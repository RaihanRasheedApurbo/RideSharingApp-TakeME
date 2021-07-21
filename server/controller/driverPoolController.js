const Ride = require('../model/ride');
const Driver = require('../model/driver');
const Vehicle = require('../model/vehicle');
const Passenger = require('../model/passenger');
const DriverPool = require('../model/driverPool');

const mongoose = require('mongoose');
const Dummy = require('../model/dummy');


const MATCHED = "matched";
const SEARCHING = "searching";
const RIDING = "riding";
const DENIED = "denied";
const CANCELLED = "cancelled";
const ENDED = "ended";

const DRIVER = "driver";
const PASSENGER = "passenger";

const NEAR = "nearest";
const MOST_RIDE = "most_ride";
const NEW_RIDE = "new";
const EXP = "experienced";

const BUDGET = "budget";
const ECONOMY = "economy";
const PREMIUM = "premium";


//function to array parse
function arrayParse(arr) {
    console.log("arrayParse here: ", arr, typeof(arr));
    try {
        arr = JSON.parse(arr);
        return arr;
    } catch (error) {
        //console.log(error, arr);
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
        driverPhone: data.driverInfo.phone,
        vehicleName: data.vehicleInfo.model,
        vehicleType: data.vehicleInfo.type,
        vehicleLocation: data.vehicleLocation.coordinates
    }
}

function pretifyPassengerData(data) {
    console.log(data);
    return {
        _id: data._id,
        name: data.name,
        email: data.email,
        phone: data.phone
    }
}


function makeRideEntry(pickUpPoint, currentLocation, driverID, passengerID, vehicleID, startTime, total, status) {
    //console.log(pickUpPoint, currentLocation, status);
    let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], currentLocation[0], currentLocation[1]); 
    //let total = dist/100.0;  //change the total based on ...
    
    let d1 = Date.now();
    let d2 = new Date(startTime);
    let duration = (d1-d2)/60000;
    
    let fare = 0, penaltyCost = 0;
    if (status === ENDED) {
        fare = total;
        penaltyCost = 0;
    }else {
        fare = 0;
        penaltyCost = total;
    }
    fare = Math.round((fare + Number.EPSILON) * 100) / 100;
    dist = Math.round((dist + Number.EPSILON) * 1000) / 1000;
    duration = Math.round((duration + Number.EPSILON) * 1000) / 1000;
    
    let ride = {
        driverID: driverID,
        passengerID: passengerID,
        vehicleID: vehicleID,
        duration: duration,
        fare: fare,
        penaltyCost: penaltyCost,
        distance: dist,
        source: pickUpPoint,
        destination: currentLocation,
        status: status
    };
    return ride;
}

//calculated geo distance between two geo coordinate
function calculateDistance(lat1, lon1, lat2, lon2) {
    //console.log(lat1, lon1, lat2, lon2);
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
async function getNearestDriver(lat, lon, maxDist) {
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

//find the nearest driver with type
async function getNearestDriverWithType(lat, lon, maxDist, type) {
    return DriverPool.find({
        'vehicleInfo.type': type,
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

//find the driver with most rides in last 7 days
async function getMostRideDriver(passengerID, pickUpPoint, dropOutPoint, type) {
    let dataInfo = await DriverPool.aggregate([
        { 
            $match: { 
                'vehicleInfo.type': type,
                status : SEARCHING 
            } 
        },
        { 
            $lookup: {
                'from': Ride.collection.name,
                'localField': 'driverID',
                'foreignField': 'driverID',
                'as': 'rides'
            }
        },
        {
            $project: {
                "driverID": 1,
                "driverInfo": 1,
                "vehicleInfo": 1,
                "vehicleLocation": 1,
                "status": 1,
                "rideCount": { "$size": "$rides" }
            }
        },
        {   
            $sort : { 
                rideCount : 1, _id: 1 
            } 
        }
    ]);
    if(dataInfo && dataInfo.length > 0) {
        let entryData = await matchPassenger(dataInfo[0].driverID, passengerID, pickUpPoint, dropOutPoint);
        return entryData;
    }
}

async function getMostRatedDriver(passengerID, pickUpPoint, dropOutPoint, type) {
    let dataInfo = await DriverPool.aggregate([
        { 
            $match: { 
                'vehicleInfo.type': type,
                status : SEARCHING 
            } 
        },
        {   
            $sort : { 
                'driverInfo.rating' : 1, _id: 1 
            } 
        }
    ]);
    if(dataInfo && dataInfo.length > 0) {
        let entryData = await matchPassenger(dataInfo[0].driverID, passengerID, pickUpPoint, dropOutPoint);
        return entryData;
    }
}

async function scoreDriver(type, pickUpPoint, choice) {
    try {
        let poolDrivers = await DriverPool.find({ 
            'vehicleInfo.type': type,
            status : SEARCHING 
        } );
    
        console.log(poolDrivers.length);
        if(poolDrivers.length === 0) {
            console.log("no driver in the pool");
            return;
        }
        let scoreInfo = [];
        for (let index = 0; index < poolDrivers.length; index++) {
            const driver = poolDrivers[index];
            let driverID = driver.driverID;
            let driverLocation = driver.vehicleLocation.coordinates;
            let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], driverLocation[1], driverLocation[0]);
            let rating = driver.driverInfo.rating;
            
            let d = new Date(), duration = 30;
            let start = new Date(d.getFullYear(), d.getMonth(), d.getDate()-duration).toISOString();
            let end = d.toISOString();
            let rideCount = await Ride.aggregate([
                { $match : { 'driverID': driverID, 'time': {$gte: new Date(start), $lte: new Date(end)} } }
            ]);

            let rateCount = await Ride.aggregate([
                { $match : { 'driverID': driverID, 'time': {$gte: new Date(start), $lte: new Date(end)} } },
                { $group: { '_id': '$driverID', 'total': {$sum: '$rating'}}}
            ]);

            rideCount = rideCount.length;
            recentRating = rideCount === 0? 0 : rateCount[0].total/rideCount;
            console.log(rating, recentRating, rideCount);

            //discuss these weights and scores
            let weights = [0.5, 0.25, 0.25];
            let distWeight = weights[0];
            let ratingWeight = weights[1];
            let rideCountWeight = weights[2];
            
            if (choice === NEAR) {
                distWeight = weights[0];
                ratingWeight = weights[1];
                rideCountWeight = weights[2];
            } else if (choice == NEW) {
                distWeight = weights[2];
                ratingWeight = weights[1];
                rideCountWeight = weights[0];
            } else if (choice == EXP) {
                distWeight = weights[1];
                ratingWeight = weights[0];
                rideCountWeight = weights[2];
            } 
            
            let distScore = dist < 1000? 1 : Math.max(0, 1-(0.2 * Math.ceil((dist-1000)/1000)));
            let ratingScore = (rating+recentRating)/10.0;
            let rideCountScore  = rideCount < 5? 1 : Math.max(0, 1-(0.2 * Math.ceil((rideCount-5)/10)));

            console.log(distScore, ratingScore, rideCountScore);

            distScore = distScore*distWeight;
            ratingScore = ratingScore*ratingWeight;
            rideCountScore = rideCountScore*rideCountWeight;

            console.log(distScore, ratingScore, rideCountScore);

            let score = distScore+ratingScore+rideCountScore;
            score = score*100;

            let item = {
                driverID: driver.driverID,
                driverName: driver.driverInfo.name,
                vehicle: driver.vehicleInfo.model + driver.vehicleInfo.type,
                rating,
                rideCount,
                rateCount,
                dist,
                score
            }
            scoreInfo.push(item);

            //calculate the score
            let updateBody = {
                $set: { score: score }
            }
            let updatedEntry = await DriverPool.findOneAndUpdate({driverID: driverID}, updateBody, { useFindAndModify: false, new: true });
            console.log("updated score: ", updatedEntry.score);
        }
        const dummy = new Dummy({
            item: scoreInfo
        });
        let dummySave = await dummy.save();
        let topDriver = await DriverPool.aggregate([
            { $match : { 'vehicleInfo.type': type, status : SEARCHING } },
            { $sort : { 'score' : -1, '_id': 1} }
        ]);
        console.log("found driver:\n", topDriver, dummySave);
    } catch (error) {
        console.log(error.message);   
    }
}

//custom driver match(temporary for testing purpose)
async function customDriverMatch(passengerID, pickUpPoint, dropOutPoint) {
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
    let status = entryInfo.status;
    
    let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], vehicleLocation.coordinates[1], vehicleLocation.coordinates[0]);
    return {"message": "Driver Matched", "distance": dist, "status": status, driverInfo};
}

//passenger match with driver
async function matchPassenger(driverID, passengerID, pickUpPoint, dropOutPoint) {
    try {
        let passengerData = await Passenger.findById(passengerID);
        passengerData = pretifyPassengerData(passengerData);
        let passengerInfo = {
            passengerData: passengerData,
            pickUpPoint: pickUpPoint,
            dropOutPoint: dropOutPoint,
        };
        //console.log(passengerData);

        const filter = {driverID : driverID};

        let getDriverVehicle = await Vehicle.findOne(filter);
        let driverCurrentLocation = getDriverVehicle.location.coordinates;
        
        const updateInfo = {passengerInfo: passengerInfo, status: MATCHED, passengerID: passengerData._id, startTime: Date.now(), driverInitialLocation: driverCurrentLocation};
        console.log(updateInfo);

        return DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true });
    } catch (error) {
        console.log(error);
        return error;
    }
}

//passengerSearch(driver call)
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
                    res.status(200).send({"message": "No match found "+n});
                    /*if(n%2) {
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
                    }*/
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

//stop searching for passenger(driver call)
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

//cancel match with matched entity
exports.cancelMatch = async(req, res) => {
    try {
        let entity = req.body.entity;
        let cancelStatus = null;
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
            };
            cancelStatus = "Driver Cancelled";
        } 
        if(entity === PASSENGER) {
            filter = {"passengerID": req.data._id, "status": MATCHED};
            updateInfo = {
                $set: {"status": CANCELLED},
                $unset: {
                    "passengerID": "",
                    "passengerInfo": "",
                }
            };
            cancelStatus = "Passenger Cancelled";
        }
        console.log(filter, updateInfo);
        entryData = await DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false});    
        
        let pickUpDist = calculateDistance(entryData.passengerInfo.pickUpPoint[0], entryData.passengerInfo.pickUpPoint[1], entryData.driverInitialLocation[1], entryData.driverInitialLocation[0]);
        let cancelDist = calculateDistance(entryData.passengerInfo.pickUpPoint[0], entryData.passengerInfo.pickUpPoint[1], entryData.vehicleLocation.coordinates[1], entryData.vehicleLocation.coordinates[0]);
        let driverTravelledDist = calculateDistance(entryData.driverInitialLocation[1], entryData.driverInitialLocation[0], entryData.vehicleLocation.coordinates[1], entryData.vehicleLocation.coordinates[0]);
        
        let currentTime = Date.now();
        let startTime = entryData.startTime;
        let cancelTime = (currentTime-startTime)/1000;
        let estimatedArrivalTime = pickUpDist/10;
        console.log(cancelTime, estimatedArrivalTime);

        let total = 0;
        if(entity === DRIVER) {
            let fineDuration = Math.max(cancelTime - (estimatedArrivalTime/2), 0);
            total = Math.min(Math.floor(30+2*(fineDuration/10)), 50);
        }
        if(entity === PASSENGER) {
            if(cancelTime > estimatedArrivalTime*1.5) total = 0;
            else {
                if(cancelDist < pickUpDist) total = Math.min(Math.floor(driverTravelledDist/50), 50);
                else total = Math.min(Math.floor(driverTravelledDist/100), 50);
            }
        }
        
        console.log(pickUpDist, " ", cancelDist);
        rideInfo = makeRideEntry(entryData.passengerInfo.pickUpPoint, entryData.passengerInfo.dropOutPoint, entryData.driverID, entryData.passengerID, entryData.vehicleInfo._id, entryData.startTime, total, cancelStatus);
        console.log(rideInfo);

        if(entity && entryData && Object.keys(entryData).length) {
            //save the ride here
            const ride = new Ride(rideInfo);
            let savedEntry = await ride.save();
            console.log(savedEntry);

            let updateBody = {
                $set: {rideInfo: savedEntry}
            }

            let updatedEntry = undefined;

            if(entity === DRIVER) {
                let filter = {
                    "passengerID": entryData.passengerID
                }
                updatedEntry = await DriverPool.findOneAndUpdate(filter, updateBody, { useFindAndModify: false, new: true });
                console.log(updatedEntry.rideInfo);

            } 
            if(entity === PASSENGER) {
                let filter = {
                    "driverID": entryData.driverID
                }
                updatedEntry = await DriverPool.findOneAndUpdate(filter, updateBody, { useFindAndModify: false, new: true });
                console.log(updatedEntry);
            }

            if(updatedEntry === undefined)  throw new Error("ride wasn't updated");
            else res.send({message: "You cancelled the ride", penaltyCost: total, entryData: updatedEntry, rideInfo: updatedEntry.rideInfo});

            
        }else throw new Error("This call was wrongfully made");
    } catch (error) {
        console.log(error);
        res.status(200).send({message: error.message});
    }
}

//end ride with riding passenger(only driver can call)
exports.startRide = async(req, res) => {
    try {
        let filter = {"driverID": req.data._id, "status": MATCHED};
        let updateInfo = {
            $set: {"status": RIDING, "startTime": Date.now()}
        }
        entryData = await DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true });    
        
        if(entryData && Object.keys(entryData).length) res.send({message: "Ride started", entryData, dropOutPoint: entryData.passengerInfo.dropOutPoint});
        else throw new Error("This call was wrongfully made");
    } catch (error) {
        res.status(500).send({message: error.message});
    }
}

//end ride with riding entity
exports.endRide = async(req, res) => {
    try {
        let entity = req.body.entity;
        let filter = {status: "nothing"};

        if(entity === DRIVER) filter = {"driverID": req.data._id, "status": RIDING};
        console.log(req.body.location);
        let currentLocation = arrayParse(req.body.location);
        let entryData = await DriverPool.findOne(filter);
        
        if(entryData && Object.keys(entryData).length) {
            let pickUpPoint = entryData.passengerInfo.pickUpPoint;
            let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], currentLocation[0], currentLocation[1]); 
            
            let vehicleCharge = 1, vehicleType = entryData.vehicleInfo.type === undefined ? ECONOMY : entryData.vehicleInfo.type;
            if(vehicleType === ECONOMY) vehicleCharge = 1;
            else if(vehicleType === BUDGET) vehicleCharge = 1.25;
            else if(vehicleType === PREMIUM) vehicleCharge = 1.5;
            let total = (dist/1000)*25*vehicleCharge;
            let rideInfo = makeRideEntry(pickUpPoint, currentLocation, entryData.driverID, entryData.passengerID, entryData.vehicleInfo._id, entryData.startTime, total, ENDED);
            console.log(rideInfo);
            
            let updateInfo = { 
                $set: {
                    status: ENDED,
                    rideInfo: rideInfo
                },
                $unset: {
                    "driverID": "",
                    "vehicleInfo": "",
                    "vehicleLocation": "",
                    "driverInfo": "",
                }
            };
            //let rideEntry = ride.save();
            let updateEntry = DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true });;

            let info = await Promise.all([updateEntry]); //add rideEntry to Promise after final check
            res.status(200).send({rideInfo});
        } 
        else throw new Error("This call was wrongfully made"); 
    } catch (error) {
        res.status(200).send({message: error.message});
    }
}

//lookForDriver(passenger call)
exports.lookForDriver = async(req, res) => {
    try {
        let passengerID = req.data._id;
        let requirement = req.body.requirement;
        let vehicleType = req.body.vehicleType === undefined ? PREMIUM : req.body.vehicleType;
        
        let vehicleCharge = 1;
        if(vehicleType === ECONOMY) vehicleCharge = 1;
        else if(vehicleType === BUDGET) vehicleCharge = 1.25;
        else if(vehicleType === PREMIUM) vehicleCharge = 1.5;

        let pickUpPoint = arrayParse(req.body.pickUpPoint);
        let dropOutPoint = arrayParse(req.body.dropOutPoint);
        //console.log(pickUpPoint, dropOutPoint, typeof(pickUpPoint), typeof(dropOutPoint));

        scoreDriver(vehicleType, pickUpPoint, requirement);

        let entry = await DriverPool.findOne({passengerID: passengerID});
        if(entry && entry.driverID !== undefined) {
            console.log("exists");
            let driverLocation = entry.vehicleLocation.coordinates;
            let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], driverLocation[1], driverLocation[0]);
            let journeyDist = calculateDistance(pickUpPoint[0], pickUpPoint[1], dropOutPoint[0], dropOutPoint[1]);
            let estimatedCost = (journeyDist/1000.0)*25*vehicleCharge;
            console.log(journeyDist);

            let driverInfo = pretifyDriverInfo(entry);
            let status = entry.status;
            console.log("hello\n\n", dist, driverInfo);
            res.status(200).send({"message": "Driver Matched", "distance": dist, "estimatedCost": estimatedCost, "status": status, driverInfo});
        }
        else if(entry && entry.status === DENIED) {
            console.log("should i delete now?");
            let removedData = await DriverPool.findOneAndDelete({passengerID: passengerID});
            res.status(200).send({message: "driver denied the ride", status: removedData.status, removedData, rideInfo: removedData.rideInfo});
        }
        else if(entry && entry.status === ENDED) {
            console.log(entry);
            let removedData = await DriverPool.findOneAndDelete({passengerID: passengerID});
            let rideInfo = removedData.rideInfo;
            
            //save the ride
            const ride = new Ride(rideInfo);
            let savedEntry = await ride.save();
            console.log(savedEntry);
            
            res.status(200).send({message: "ride was completed", status: removedData.status, rideInfo: savedEntry});
        }
        else {
            if(requirement === NEAR) {
                console.log('hello nearest');
                let drivers = await getNearestDriverWithType(pickUpPoint[0], pickUpPoint[1], 5000, vehicleType);
                if(drivers.length > 0) {
                    let driverID = drivers[0].driverID;
                    let driverLocation = drivers[0].vehicleLocation.coordinates;
                    let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], driverLocation[1], driverLocation[0]);
                    console.log(dist);

                    let journeyDist = calculateDistance(pickUpPoint[0], pickUpPoint[1], dropOutPoint[0], dropOutPoint[1]);
                    let estimatedCost = (journeyDist/1000.0)*25*vehicleCharge;
                    console.log(journeyDist);

                    entryData = await matchPassenger(driverID, passengerID, pickUpPoint, dropOutPoint);
                    
                    if(entryData && Object.keys(entryData).length) {
                        
                        let status = entryData.status;
                        let driverInfo = pretifyDriverInfo(entryData);
                        console.log(dist, driverInfo);
                        res.status(200).send({"message": "Driver Matched", "distance": dist, "estimatedCost": estimatedCost, "status": status, driverInfo});
                    } 
                    else throw new Error("couldn't update entry");
                } else {
                    res.status(200).send({message: "no driver found"});
                    //let retBody = await customDriverMatch(passengerID, pickUpPoint, dropOutPoint);
                    //res.status(200).send(retBody);
                }
            }
            else if(requirement === MOST_RIDE) {
                console.log("hello most ride");
                entryData = await getMostRideDriver(passengerID, pickUpPoint, dropOutPoint, vehicleType);
                    
                if(entryData && Object.keys(entryData).length) {
                    //res.send(entryData);
                    let driverInfo = pretifyDriverInfo(entryData);
                    let driverLocation = entryData.vehicleInfo.location.coordinates;
                    let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], driverLocation[1], driverLocation[0]);
                    
                    let journeyDist = calculateDistance(pickUpPoint[0], pickUpPoint[1], dropOutPoint[0], dropOutPoint[1]);
                    let estimatedCost = (journeyDist/1000.0)*25*vehicleCharge;
                    console.log(journeyDist);
                    
                    let status = entryData.status;

                    res.status(200).send({"message": "Driver Matched", "distance": dist, "estimatedCost": estimatedCost, "status": status, driverInfo});
                } else {
                    res.status(200).send({message: "no driver found"});
                    //let retBody = await customDriverMatch(passengerID, pickUpPoint, dropOutPoint);
                    //res.status(200).send(retBody);
                }
            }
            else if (requirement === EXP) {
                console.log("hello rated");
                entryData = await getMostRatedDriver(passengerID, pickUpPoint, dropOutPoint, vehicleType);
                    
                if(entryData && Object.keys(entryData).length) {
                    //res.send(entryData);
                    let driverInfo = pretifyDriverInfo(entryData);
                    let driverLocation = entryData.vehicleInfo.location.coordinates;
                    let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], driverLocation[1], driverLocation[0]);
                    
                    let journeyDist = calculateDistance(pickUpPoint[0], pickUpPoint[1], dropOutPoint[0], dropOutPoint[1]);
                    let estimatedCost = (journeyDist/1000.0)*25*vehicleCharge;
                    console.log(journeyDist);
                    
                    let status = entryData.status;

                    res.status(200).send({"message": "Driver Matched", "distance": dist, "estimatedCost": estimatedCost, "status": status, driverInfo});
                } else {
                    res.status(200).send({message: "no driver found"});
                    //let retBody = await customDriverMatch(passengerID, pickUpPoint, dropOutPoint);
                    //res.status(200).send(retBody);
                }
            }
            else throw new Error("body parameter wrong");
        }

    } catch (error) {
        console.log(error);
        res.status(500).send({message: err.message});
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
                obj['vehicle'] = item.vehicleInfo.model+" "+item.vehicleInfo.type;
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

//cleanPool
exports.cleanPool = (req, res) => {
    DriverPool.deleteMany({})
    .then(data => {
        res.status(200).send(data);
    })
    .catch(err => {
        console.log(err);
        res.status(500).send(err);
    });
}
