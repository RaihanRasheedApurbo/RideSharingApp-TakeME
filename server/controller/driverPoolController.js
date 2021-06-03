const Driver = require('../model/driver');
const Vehicle = require('../model/vehicle');
const Passenger = require('../model/passenger');
const DriverPool = require('../model/driverPool');

//passengerSearch
exports.lookForPassenger = (req, res) => {
    DriverPool.findOne({'driverID': req.data._id})
    .then(data => {
        if(data) {
            if(data.passengerID) {
                
                DriverPool.findOneAndDelete({'driverID': req.data._id})
                .then(data => {
                    
                    Passenger.findById(data.passengerID)
                    .then(passengerData => {
                        let passengerInfo = {
                            passengerData: passengerData,
                            pickUpPoint : data.pickUpPoint
                        };

                        console.log(passengerInfo);

                        res.status(200).send({"message": "you have been matched", passengerInfo});
                    })
                    .catch(err => {
                        res.status(500).send({message: err.message});
                    });

                })
                .catch(err => {
                    res.status(500).send({"message": err.message});
                });
            }
            else {
                res.status(200).send({"message": "No match found"});
            }
        }
        else {
            console.log("new entry");
            //getting driverInfo
            const getDriverInfo = Driver.findById(req.data._id);
            const getVehicleInfo = Vehicle.findOne({'driverID': req.data._id});
            
            Promise.all([getDriverInfo, getVehicleInfo])
            .then(data => {
                const driverEntry = new DriverPool({
                    driverID : req.data._id,
                    driverInfo : data[0],
                    vehicleLocation : data[1].location,
                    vehicleInfo: data[1]
                }); 
                //console.log(driverEntry);
                
                // adding driverEntry to driverPool
                driverEntry.save()
                .then(data => {
                    res.status(200).send({ "message": `driverID ${req.data._id} has been added to pool`});
                });
            })
            .catch(err => {
                //console.log(err);
                res.status(500).send({ message: err.message });
            });
        }
    })
    .catch(err => {
        console.log(err);
        res.status(500).send({message: err.message});
    });
}

exports.stopPassengerSearch = (req, res) => {
    DriverPool.findOneAndDelete({'driverID': req.data._id})
        .then(data => {
            res.status(200).send({"message": `DriverID ${req.data._id} has been removed from pool`});
        })
        .catch(err => {
            res.status(500).send({"message": err.message});
        });
}

function calculateDistance(lat1, lon2, lat2, lon2) {
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

    return d;
}

//driverSearch
exports.lookForDriver = (req, res) => {
    if(req.query.latitude && req.query.longitude) {
        const lat1 = parseFloat(req.query.latitude);
        const lon1 = parseFloat(req.query.longitude);
        console.log(lat1, lon1);

        DriverPool.find({
            vehicleLocation : {
                $near: {
                    $geometry: {
                        type: 'Point',
                        coordinates: [ lon1 , lat1 ]
                    },
                    $maxDistance: 10000
                }
            }
        })
        .then(data => {
            res.status(200).send(data);
        })
        .catch(err => {
            console.log(err);
            res.status(500).send(err);
        });
    }
    else {
        res.status(400).send({message: "empty query parameter"});
    }
    
}

//acceptDriver via get req
exports.acceptDriverOld = (req, res) => {
    if(req.query.passengerID && req.query.driverID) {
        const passengerID = req.query.passengerID;
        const driverID = req.query.driverID;
        console.log(passengerID, driverID);

        const filter = {driverID: req.query.driverID};
        const updateInfo = {passengerID: req.query.passengerID};

        DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true })
        .then(data => {
            res.status(200).send(data);
        })
        .catch(err => {
            res.status(500).send(err);
        });
    }
    else {
        res.status(400).send({message: "empty query parameter"});
    }
}

//acceptDriver via post req
exports.acceptDriver = (req, res) => {
    if(req.body.driverID && req.body.pickUpPoint) {
        const passengerID = req.data._id;
        const driverID = req.body.driverID;
        const pickUpPoint = JSON.parse(req.body.pickUpPoint);
        //console.log(passengerID, driverID, req.body.pickUpPoint);

        const filter = {driverID : driverID};
        const updateInfo = {passengerID: passengerID, pickUpPoint: pickUpPoint};
        //console.log(updateInfo);

        DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true })
        .then(data => {
            //console.log(data);
            res.status(200).send(data);
        })
        .catch(err => {
            //console.log(err);
            res.status(500).send(err);
        });
    }
    else {
        res.status(400).send({message: "empty body parameter"});
    }
}


exports.showPool = (req, res) => {
    DriverPool.find({})
    .then(data => {
        d = [];
        data.forEach(item => {
            obj = {
                'driverID': item.driverID, 
                'location': item.vehicleLocation.coordinates, 
                'passengerInfo': {
                    'passengerID': item.passengerID,
                    'pickUpPoint': item.pickUpPoint
                }
            };
            d.push(obj);
        })
        console.log(d);
        res.status(200).send(d);
    })
    .catch(err => {
        res.status(500).send(err);
    })
}