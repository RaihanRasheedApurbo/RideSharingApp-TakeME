const Vehicle = require('../model/vehicle');
const DriverPool = require('../model/driverPool');

//passengerSearch
exports.lookForPassenger = (req, res) => {
    DriverPool.findOne({'driverID': req.data._id})
    .then(data => {
        if(data) {
            if(data.passengerID) {
                console.log(data);
                DriverPool.findOneAndDelete({'driverID': req.data._id})
                .then(data => {
                    res.status(200).send({"message": "you have been matched", "passengerID": data.passengerID, entryData: data});
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
            
            //finding vehicleInfo of the driver
            getVehicleInfo = Vehicle.findOne({'driverID': req.data._id})
            .then(data => {
                const driverEntry = new DriverPool({
                    driverID : req.data._id,
                    location : data.location,
                    vehicleInfo: data
                });   
                
                // adding driverEntry to driverPool
                driverEntry.save()
                .then(data => {
                    res.send({ "message": `driverID ${req.data._id} has beend added to pool`, entryData: data});
                }); 
            });
        }
    })
    .catch(err => {
        console.log(err);
        res.status(500).send({message: err.message});
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
            location : {
                $near: {
                    $geometry: {
                        type: 'Point',
                        coordinates: [ lon1 , lat1 ]
                    },
                    $maxDistance: 1000
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

//acceptDriver
exports.acceptDriver = (req, res) => {
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


exports.showPool = (req, res) => {
    DriverPool.find({})
    .then(data => {
        d = [];
        data.forEach(item => {
            obj = {'driverID': item.driverID, 'location': item.location.coordinates, 'passengerID': item.passengerID};
            d.push(obj);
        })
        console.log(d);
        res.status(200).send(d);
    })
    .catch(err => {
        res.status(500).send(err);
    })
}