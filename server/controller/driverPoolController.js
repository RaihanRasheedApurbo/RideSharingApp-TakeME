const Ride = require('../model/ride');
const Driver = require('../model/driver');
const Vehicle = require('../model/vehicle');
const Passenger = require('../model/passenger');
const DriverPool = require('../model/driverPool');

const mongoose = require('mongoose');


const MATCHED = "matched";
const SEARCHING = "searching";
const RIDING = "riding";
const DENIED = "denied";
const CANCELLED = "cancelled";

const DRIVER = "driver";
const PASSENGER = "passenger";


//function to array parse
function arrayParse(arr) {
    console.log("arrayParse here: ", arr);
    try {
        arr = JSON.parse(arr);
        return arr;
    } catch (error) {
        console.log(error, arr);
        return arr;
    }
}

//function to pretify driver and vehicleInfo
function pretifyDriverInfo(data) {
    console.log(data);
    return {
        driverID: data.driverID,
        driverName: data.driverInfo.name,
        driverRating: data.driverInfo.rating,
        vehicleName: data.vehicleInfo.model,
        vehicleType: data.vehicleInfo.type,
        vehicleLocation: data.vehicleInfo.location.coordinates
    }
}

//calculated geo distance between two geo coordinate
function calculateDistance(lat1, lon1, lat2, lon2) {
    console.log(lat1, lon1, lat2, lon2);
    const R = 6371e3; // metres
    const φ1 = lat1 * Math.PI/180; // φ, λ in radians
    const φ2 = lat2 * Math.PI/180;
    const Δφ = (lat2-lat1) * Math.PI/180;
    const Δλ = (lon2-lon1) * Math.PI/180;

    const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
            Math.cos(φ1) * Math.cos(φ2) *
            Math.sin(Δλ/2) * Math.sin(Δλ/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    const d = R * c; // in metres

    console.log(d);
    return d;
}

//find the nearest driver
function getNearestDriver(lat, lon, maxDist) {
    return DriverPool.find({
        status : SEARCHING,
        vehicleLocation : {
            $near: {
                $geometry: {
                    type: 'Point',
                    coordinates: [ lon , lat ]
                },
                $maxDistance: maxDist
            }
        }
    });
}

//passenger match with driver
async function matchPassenger(driverID, passengerID, pickUpPoint, dropOutPoint) {
    try {
        passengerData = await Passenger.findById(passengerID);
        let passengerInfo = {
            passengerData: passengerData,
            pickUpPoint: pickUpPoint,
            dropOutPoint: dropOutPoint,
        };
        //console.log(passengerData);

        const filter = {driverID : driverID};
        const updateInfo = {passengerInfo: passengerInfo, status: MATCHED, passengerID: passengerData._id};
        console.log(updateInfo);

        return DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true });
    } catch (error) {
        console.log(error);
        return error;
    }
}

//passengerSearchAlt
exports.lookForPassenger = async(req, res) => {
    try {
        let driverID = req.data._id;
        let data = await DriverPool.findOne({'driverID': driverID});
        
        if(data) {
            if(data.passengerInfo) {
                let passengerInfo = data.passengerInfo;
                res.status(200).send({"message": "you have been matched", passengerInfo, status: data.status});
            }
            else {
                if(data.status === CANCELLED) {
                    let removedData = await DriverPool.findOneAndDelete({'driverID': req.data._id});
                    res.status(200).send({"message": "passenger cancelled the ride", status: data.status, removedData});
                } else {
                    let n = Math.floor(Math.random()*10);
                    if(n%2) {
                        //let passengers = ["607478178c29c1408cfad290", "607478178c29c1408cfad292", "607478178c29c1408cfad297", "607478188c29c1408cfad29b", "607478188c29c1408cfad2a1"];
                        let passengers = ["607478178c29c1408cfad290"];
                        
                        let passengerID = passengers[Math.floor((Math.random() * passengers.length))];

                        let lat = Math.random() * (23.87 - 23.72) + 23.72;
                        let lon = Math.random() * (90.426 - 90.345) + 90.345;
                        let pickUpPoint = [lat, lon];

                        lat = Math.random() * (23.87 - 23.72) + 23.72;
                        lon = Math.random() * (90.426 - 90.345) + 90.345;
                        let dropOutPoint = [lat, lon];

                        entryData = await matchPassenger(driverID, passengerID, pickUpPoint, dropOutPoint);
                        if(entryData.passengerInfo) res.status(200).send({"message": "you should get one in the next call", entryData});
                        else throw new Error("couldn't update entry");
                    } 
                    else {
                        res.status(200).send({"message": "No match found "+n});
                    }
                }
            }
        }
        else {
            console.log("new entry");
            //getting driverInfo
            const getDriverInfo = Driver.findById(req.data._id);
            const getVehicleInfo = Vehicle.findOne({'driverID': req.data._id});
            
            data = await Promise.all([getDriverInfo, getVehicleInfo]);
            const driverEntry = new DriverPool({
                driverID : req.data._id,
                driverInfo : data[0],
                vehicleLocation : data[1].location,
                vehicleInfo: data[1],
                status: SEARCHING
            });

            entryInfo = await driverEntry.save();
            res.status(200).send({ "message": `driverID ${req.data._id} has been added to pool`, entryInfo});
        }
    } catch (error) {
        console.log(error); 
        res.status(500).send({message: error.message});
    }
}

//stop searching for passenger
exports.stopPassengerSearch = (req, res) => {
    DriverPool.findOneAndDelete({'driverID': req.data._id})
        .then(data => {
            if(data && Object.keys(data).length) res.status(200).send({"message": `DriverID ${req.data._id} has been removed from pool`});
            else res.status(200).send({"message": "DriverID has already been removed from pool"})
        })
        .catch(err => {
            res.status(500).send({"message": err.message});
        });
}

exports.cancelMatch = async(req, res) => {
    try {
        let entity = req.body.entity;
        let filter, updateInfo;
        if(entity === DRIVER) {
            filter = {"driverID": req.data._id, "status": MATCHED};
            updateInfo = {
                $set: {"status": DENIED},
                $unset: {
                    "driverID": "",
                    "vehicleInfo": "",
                    "vehicleLocation": "",
                    "driverInfo": "",
                }
            }
        } 
        if(entity === PASSENGER) {
            filter = {"passengerID": req.data._id, "status": MATCHED};
            updateInfo = {
                $set: {"status": CANCELLED},
                $unset: {
                    "passengerID": "",
                    "passengerInfo": "",
                }
            }
        }
        console.log(filter, updateInfo);
        entryData = await DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true });    
        
        if(entity && entryData && Object.keys(entryData).length) res.send({message: "You cancelled the ride", entryData});
        else throw new Error("This call was wrongfully made");
    } catch (error) {
        console.log(error);
        res.status(500).send({message: error.message});
    }
}

exports.startRide = async(req, res) => {
    try {
        let filter = {"driverID": req.data._id, "status": MATCHED};
        let updateInfo = {
            $set: {"status": RIDING, "startTime": Date.now()}
        }
        entryData = await DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true });    
        
        if(entryData && Object.keys(entryData).length) res.send({message: "Ride started", entryData});
        else throw new Error("This call was wrongfully made");
    } catch (error) {
        res.status(500).send({message: error.message});
    }
}

exports.endRide = async(req, res) => {
    try {
        let entity = req.body.entity;
        let filter = {status: "nothing"};

        if(entity === DRIVER) filter = {"driverID": req.data._id, "status": RIDING};
        else if(entity === PASSENGER) filter = {"passengerID": req.data._id, "status": RIDING};
        console.log(req.body.location);
        let currentLocation = arrayParse(req.body.location);
        let entryData = await DriverPool.findOne(filter);
        
        if(entryData && Object.keys(entryData).length) {
            let pickUpPoint = entryData.passengerInfo.pickUpPoint;
            console.log(pickUpPoint, currentLocation);
            let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], currentLocation[0], currentLocation[1]); 
            let total = dist/100.0;
            
            let d1 = Date.now();
            let d2 = new Date(entryData.startTime);
            let duration = (d1-d2)/1000;
            
            let ride = new Ride({
                driverID: entryData.driverID,
                passengerID: entryData.passengerID,
                vehicleID: entryData.vehicleInfo._id,
                duration: duration,
                fare: total,
                distance: dist,
                source: pickUpPoint,
                destination: currentLocation
            });
            console.log(ride);
            //res.status(200).send({ride});

            //let rideEntry = ride.save();
            let removedEntry = DriverPool.findOneAndDelete(filter);

            let info = await Promise.all([removedEntry])
            res.status(200).send({ride});
        } 
        else throw new Error("This call was wrongfully made"); 
    } catch (error) {
        res.status(500).send({message: error.message});
    }
}

//driverSearchWithDist
exports.lookforNearestDriver = async(req, res) => {
    
    try {
        let passengerID = req.data._id;
        let pickUpPoint = arrayParse(req.body.pickUpPoint);
        let dropOutPoint = arrayParse(req.body.dropOutPoint);
        //console.log(pickUpPoint, dropOutPoint, typeof(pickUpPoint), typeof(dropOutPoint));

        let entry = await DriverPool.findOne({passengerID: passengerID});
        if(entry && entry.driverID !== undefined) {
            console.log("exists");
            let driverLocation = entry.vehicleInfo.location.coordinates;
            let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], driverLocation[1], driverLocation[0]);
                
            let driverInfo = pretifyDriverInfo(entry);
            return res.status(200).send({"message": "Driver Matched", "distance": dist, driverInfo});
        }
        else if(entry && entry.status === DENIED) {
            console.log("should i delete now?");
            let removedData = await DriverPool.findOneAndDelete({passengerID: passengerID});
            return res.status(200).send({message: "driver denied the ride", status: data.status, removedData});
        }
        else {
            let drivers = await getNearestDriver(pickUpPoint[0], pickUpPoint[1], 5000);
            if(drivers.length > 0) {
                let driverID = drivers[0].driverID;
                let driverLocation = drivers[0].vehicleLocation.coordinates;
                let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], driverLocation[1], driverLocation[0]);
                console.log(dist);

                entryData = await matchPassenger(driverID, passengerID, pickUpPoint, dropOutPoint);
                
                if(entryData && Object.keys(entryData).length) {
                    let driverInfo = pretifyDriverInfo(entryData);
                    res.status(200).send({"message": "Driver Matched", "distance": dist, driverInfo});
                } 
                else throw new Error("couldn't update entry");
            } else {
                //if(n%2) res.status(200).send({message: "no preferable driver found"});
                console.log("custom match");
                let driverID = mongoose.Types.ObjectId("607478178c29c1408cfad295");
                
                let getDriverData = Driver.findById(driverID);
                let getPassengerData = Passenger.findById(passengerID);
                let getVehicleData = Vehicle.findOne({driverID: driverID});

                let data = await Promise.all([getDriverData, getPassengerData, getVehicleData]);
                let driverInfo = data[0];
                let vehicleInfo = data[2];
                let vehicleLocation = data[2].location;
                let passengerInfo = {
                    passengerData: data[1],
                    pickUpPoint: pickUpPoint,
                    dropOutPoint: dropOutPoint
                }
                
                let entry = new DriverPool({
                    driverID: driverID,
                    passengerID: passengerID,
                    driverInfo: driverInfo,
                    passengerInfo: passengerInfo,
                    vehicleInfo: vehicleInfo,
                    vehicleLocation: vehicleLocation,
                    status: MATCHED
                });
                
                let entryInfo = await entry.save();
                driverInfo = pretifyDriverInfo(entryInfo);
                
                let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], vehicleLocation.coordinates[1], vehicleLocation.coordinates[0]);
                return res.status(200).send({"message": "Driver Matched", "distance": dist, driverInfo});
            }
        }
        
    } catch (error) {
        res.status(200).send({message: error});
    }
}

//acceptDriver via post req
exports.acceptDriver = async(req, res) => {
    try {
        const passengerID = req.data._id;
        const driverID = req.body.driverID;
        
        let pickUpPoint = arrayParse(req.body.pickUpPoint);
        console.log(passengerID, driverID, req.body.pickUpPoint);
        
        entryData = await matchPassenger(driverID, passengerID, pickUpPoint, dropOutPoint);
        if(entryData.passengerInfo) res.status(200).send({"message": "Driver Matched", entryData});
        else throw new Error("couldn't update entry");
    } catch (error) {
        console.log(error);
        res.status(500).send({message: error});
    }
}

//show pool
exports.showPool = async(req, res) => {
    function pretify(item) {
        //console.log(item);
        try {
            let obj = {};
            if(item.driverID) {
                obj['driverID'] = item.driverID;
                obj['driverName'] = item.driverInfo.name;
                obj['vehicle'] = item.vehicleInfo.model;
                obj['vehicleLocation'] = item.vehicleLocation.coordinates;
            }
            if(item.passengerID) {
                obj['passengerID'] = item.passengerID;
                obj['passengerName'] = item.passengerInfo.passengerData.name;
                obj['pickUpPoint'] = item.passengerInfo.pickUpPoint;
                obj['dropOutPoint'] = item.passengerInfo.dropOutPoint;
            }
            obj['status'] = item.status;
            return obj;
        } catch (error) {
            console.log("oops", error);
            return "oops";
        }
    }

    try {
        let data = await DriverPool.find({});
        let pool = data.map(pretify);
        res.status(200).send({pool, 'total': pool.length});
    } catch (error) {
        console.log(error);
        res.status(500).send(error);
    }
}

//deletePool
exports.deletePool = (req, res) => {
    DriverPool.deleteMany({})
    .then(data => {
        res.status(200).send(data);
    })
    .catch(err => {
        console.log(err);
        res.status(500).send(err);
    });
}