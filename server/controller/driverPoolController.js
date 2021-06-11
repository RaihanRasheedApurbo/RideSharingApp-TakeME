const Driver = require('../model/driver');
const Vehicle = require('../model/vehicle');
const Passenger = require('../model/passenger');
const DriverPool = require('../model/driverPool');
const axios = require("axios");

//passengerSearch
exports.lookForPassenger = (req, res) => {
    DriverPool.findOne({'driverID': req.data._id})
    .then(data => {
        if(data) {
            if(data.passengerInfo) {
                let passengerInfo = data.passengerInfo;
                res.status(200).send({"message": "you have been matched", passengerInfo});
                /*DriverPool.findOneAndDelete({'driverID': req.data._id})
                .then(data => {
                    let passengerInfo = data.passengerInfo;
                    
                    res.status(200).send({"message": "you have been matched", passengerInfo});
                })
                .catch(err => {
                    res.status(500).send({"message": err.message});
                });*/
            }
            else {
                let n = Math.floor(Math.random()*10);
                console.log("n: ", n);
                if(n%2) {
                    let passengers = ["607478178c29c1408cfad290", "607478178c29c1408cfad292", "607478178c29c1408cfad297", "607478188c29c1408cfad29b", "607478188c29c1408cfad2a1"];

                    let passengerID = passengers[Math.floor((Math.random() * passengers.length))];

                    let lat = Math.random() * (23.7 - 23.4) + 23.4;
                    let lon = Math.random() * (90.6 - 90.4) + 90.4;
                    let pickUpPoint = [lat, lon];

                    Passenger.findById(passengerID)
                    .then(passengerData => {
                        let passengerInfo = {
                            passengerData: passengerData,
                            pickUpPoint: pickUpPoint 
                        }

                        const filter = {driverID : driverID};
                        const updateInfo = {passengerInfo: passengerInfo};
                        //console.log(updateInfo);

                        DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true })
                        .then(data => {
                            console.log(data);
                            //res.status(200).send(data);
                        })
                        .catch(err => {
                            console.log(err);
                            res.status(500).send(err);
                        });
                    })
                    .catch(err => {
                        console.log(err);
                        res.status(500).send(err);
                    });
                } 


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

//stop searching for passenger
exports.stopPassengerSearch = (req, res) => {
    DriverPool.findOneAndDelete({'driverID': req.data._id})
        .then(data => {
            res.status(200).send({"message": `DriverID ${req.data._id} has been removed from pool`});
        })
        .catch(err => {
            res.status(500).send({"message": err.message});
        });
}

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

    return d;
}

const driverWithLocation = (lat, lon) => {
    DriverPool.find({
        vehicleLocation : {
            $near: {
                $geometry: {
                    type: 'Point',
                    coordinates: [ lon , lat ]
                },
                $maxDistance: 5000
            }
        }
    });
}

//driverSearch
exports.lookForDriver = (req, res) => {
    let passengerID = req.data._id;
                
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
                    $maxDistance: 5000
                }
            }
        })
        .then(data => {
            
            if(data.length > 0) {
                
                let driverID = data[0].driverID;
                let passengerLat = data[0].vehicleLocation.coordinates[1];
                let passengerLon = data[0].vehicleLocation.coordinates[0];
                let pickUpPoint = [passengerLat, passengerLon];
                let dist = calculateDistance(lat1, lon1, passengerLat, passengerLon);
                console.log(dist);
                
                Passenger.findById(passengerID)
                .then(passengerData => {
                    let passengerInfo = {
                        passengerData: passengerData,
                        pickUpPoint: pickUpPoint 
                    }

                    const filter = {driverID : driverID};
                    const updateInfo = {passengerInfo: passengerInfo};
                    //console.log(updateInfo);

                    DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true })
                    .then(data => {
                        console.log(data);
                        let obj = {
                            'distance': dist/1000,
                            'vehicleLocation': data.vehicleLocation.coordinates, 
                            'driverName': data.driverInfo.name,
                            'vehicle': data.vehicleInfo.model
                        };
                        res.status(200).send(obj);
                    })
                    .catch(err => {
                        console.log(err);
                        res.status(500).send(err);
                    });
                })
                .catch(err => {
                    console.log(err);
                    res.status(500).send(err);
                });
                

                /*
                let d = [];
                data.forEach(item => {
                    //console.log(item.vehicleInfo.model);
                    let lat2 = item.vehicleLocation.coordinates[1];
                    let lon2 = item.vehicleLocation.coordinates[0];
                    let dist = calculateDistance(lat1, lon1, lat2, lon2);
                    console.log(dist);
                    obj = {
                        'distance': dist/1000,
                        'vehicleLocation': item.vehicleLocation.coordinates, 
                        'driverName': item.driverInfo.name,
                        'vehicle': item.vehicleInfo.model
                    };
                    d.push(obj);
                });
                d.push({total: data.length});*/
            } else {
                //console.log(data);
                res.status(200).send({message: "no preferable driver found"});
            }
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

//acceptDriver via post req
exports.acceptDriver = (req, res) => {
    if(req.body.driverID && req.body.pickUpPoint) {
        const passengerID = req.data._id;
        const driverID = req.body.driverID;
        
        let pickUpPoint = null;
        try {
            console.log("JSON parsing");
            pickUpPoint = JSON.parse(req.body.pickUpPoint);
        }
        catch(err) {
            console.log("err... so no parsing");
            pickUpPoint = req.body.pickUpPoint;
        }
        console.log(passengerID, driverID, req.body.pickUpPoint);

        Passenger.findById(passengerID)
        .then(passengerData => {
            let passengerInfo = {
                passengerData: passengerData,
                pickUpPoint: pickUpPoint 
            }

            const filter = {driverID : driverID};
            const updateInfo = {passengerInfo: passengerInfo};
            //console.log(updateInfo);

            DriverPool.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true })
            .then(data => {
                console.log(data);
                res.status(200).send(data);
            })
            .catch(err => {
                console.log(err);
                res.status(500).send(err);
            });
        })
        .catch(err => {
            console.log(err);
            res.status(500).send(err);
        });
    }
    else {
        res.status(400).send({message: "empty body parameter"});
    }
}

//show pool
exports.showPool = (req, res) => {
    DriverPool.find({})
    .then(data => {
        d = [];
        data.forEach(item => {
            obj = {
                'driverID': item.driverID, 
                'driverName': item.driverInfo.name,
                'vehicle': item.vehicleInfo.model,
                'location': item.vehicleLocation.coordinates
            };
            if(item.passengerInfo) obj['passengerInfo'] = item.passengerInfo;
            d.push(obj);
        })
        d.push({total: data.length});
        console.log(d);
        res.status(200).send(d);
    })
    .catch(err => {
        console.log(err);
        res.status(500).send(err);
    })
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

//passenger assign
function passengerAssign(driverID) {
    let tokens = ["eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDc0NzgxNzhjMjljMTQwOGNmYWQyOTAiLCJpYXQiOjE2MjI1NjQ3NDZ9.JZAM2JfO-QuVD5qbL0wQ7ptsifX3KQEe0kzsWQYo9bA",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDc0NzgxNzhjMjljMTQwOGNmYWQyOTIiLCJpYXQiOjE2MjI1NjUxMjZ9.S_pl-rQ-lxw6Dc9QM6B4jW6WUGhHdZYGxjd-E4Scsa4",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDc0NzgxNzhjMjljMTQwOGNmYWQyOTciLCJpYXQiOjE2MjI1NjUxNzB9.Eq9h22EUefCVY9eQSIYI1S0c_VvA3ywW7oSGmviAjyk",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDc0NzgxODhjMjljMTQwOGNmYWQyOTkiLCJpYXQiOjE2MjI1NjUyMTB9.BrdCkVeT1cdFd1VhrgD7IwKxHDEpkkfIswH2trXcdiE",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDc0NzgxODhjMjljMTQwOGNmYWQyYTEiLCJpYXQiOjE2MjI1NjUyNTd9.697B5W-LF-5su6jV5vvOdQkOj4WMWuGLWFpJ5CnjBug",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDc0NzgxODhjMjljMTQwOGNmYWQyYTUiLCJpYXQiOjE2MjI1NjUyOTJ9.UulbXYNHCxt7u0O7Scj48umJmquWXq7dPdGEhcI-F7k"
    ];

    let token = tokens[Math.floor((Math.random() * tokens.length))];

    let lat = Math.random() * (23.7 - 23.4) + 23.4;
    let lon = Math.random() * (90.6 - 90.4) + 90.4;
    let pickUpPoint = [lat, lon];

    let header_data = {
        'auth-token': token
    };

    let postBody = {
        driverID,
        pickUpPoint
    };

    console.log(token);
    console.log(postBody);

    //let address = 'https://take-me-backend.herokuapp.com';
    let address = "http://localhost:3000";
    axios.post(address+'/api/passenger/acceptDriver', postBody, {headers: header_data})
        .then( data => {
            console.log("success\n", data.data);
        })
        .catch(err => {
            console.log(err.message);
        });

}