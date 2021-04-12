const Ride = require('../model/ride');
const jwt = require('jsonwebtoken');

exports.addRide = (req, res) => {
    // validate request
    if(!req.body){
        return res.status(400).send({ message : "Content can not be emtpy!"});
    }
    
    // new Ride
    const ride = new Ride({
        driverID : req.body.driverID,
        passengerID : req.body.passengerID,
        vehicleID : req.body.vehicleID,
        duration : req.body.duration,
        fare : req.body.fare,
        source : req.body.source,
        destination : req.body.destination
    });

    // save ride in the database
    ride.save()
        .then(data => {
            res.send(data)
            //res.redirect('/add-user');
        })
        .catch(err =>{
            res.status(500).send({
                message : err.message || "Some error occurred while creating a create operation"
            });
        });
}

//find
exports.find = (req, res) => {
    Ride.findById(req.body._id)
    .then(data =>{
        if(!data){
            res.status(404).send({ message : "No ride to show" });
        }else{
            //console.log(data)
            res.send(data);    
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving ride with id " + req.body._id});
    });
}


//find ride using driverID
exports.findByDriverID = (req, res) => {
    Ride.find({ 'driverID': req.body._id })
    .then(data =>{
        if(data.length <= 0){
            res.status(404).send({ message : "No ride to show" });
        }else{
            //console.log(data)
            res.send(data);    
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving ride with driverID " + req.body._id});
    });
}

//find ride using passengerID
exports.findByPassengerID = (req, res) => {
    Ride.find({ 'passengerID': req.body._id })
    .then(data =>{
        if(data.length <= 0){
            res.status(404).send({ message : "No ride to show" });
        }else{
            //console.log(data)
            res.send(data);    
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving ride with passengerID " + req.body._id});
    });
}

//find ride using vehicleID
exports.findByVehicleID = (req, res) => {
    if(req.body.duration) {
        let duration = req.body.duration;
        
        let d = new Date();
        let start = new Date(d.getFullYear(), d.getMonth(), d.getDate()-duration).toISOString();
        let end = d.toISOString();
        
        Ride.find({ 'vehicleID': req.body._id, 'time': {$lt: end}, 'time': {$gt: start} })
        .then(data =>{
            if(data.length <= 0){
                res.send({ message : "No ride to show" });
            }else{
                res.send(data);
            }
        })
        .catch(err =>{
            res.status(500).send({ message: err.message || "Error retrieving ride with vehicleID " + req.body._id});
        });
    }
    else {
        Ride.find({ 'vehicleID': req.body._id })
        .then(data =>{
            if(data.length <= 0){
                res.status(404).send({ message : "No ride to show" });
            }else{
                //console.log(data)
                res.send(data);    
            }
        })
        .catch(err =>{
            res.status(500).send({ message: err.message || "Error retrieving ride with vehicleID " + req.body._id});
        });
    }
}

exports.getAllRides = (req, res) => {
    Ride.find({})
    .then(data => {
        res.send(data);
    })
    .catch(err => {
        res.status(400).send(err);
    });
}