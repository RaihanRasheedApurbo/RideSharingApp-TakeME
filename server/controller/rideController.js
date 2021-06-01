const Ride = require('../model/ride');
const jwt = require('jsonwebtoken');
const mongoose = require('mongoose');

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
    const id = mongoose.Types.ObjectId(req.body._id);
    //const id = mongoose.Types.ObjectId(req.data._id);
    if(req.body.duration) {
        let duration = req.body.duration;
        
        let d = new Date();
        let start = new Date(d.getFullYear(), d.getMonth(), d.getDate()-duration).toISOString();
        let end = d.toISOString();
        
        const getRideInfo = Ride.find({ 'driverID': id, 'time': {$gte: start, $lte: end} });
        const getTotalEarning = Ride.aggregate([
            { $match : { 'driverID': id, 'time': {$gte: new Date(start), $lte: new Date(end)} } },
            { $group: { '_id': '$driverID', 'total': {$sum: '$fare'}}}
        ]);

        Promise.all([getRideInfo, getTotalEarning])
        .then(data => {
            res.send(data);
            //console.log(data);
        })
        .catch(err => {
            res.status(500).send({ message: err.message });
        });
    }
    else {
        Ride.find({ 'driverID': id })
        .then(data =>{
            //console.log(data)
            res.send(data); 
        })
        .catch(err =>{
            res.status(500).send({ message: err.message });
        });
    }
}

//find ride using passengerID
exports.findByPassengerID = (req, res) => {
    const id = mongoose.Types.ObjectId(req.body._id);
    //const id = mongoose.Types.ObjectId(req.data._id);
    if(req.body.duration) {
        let duration = req.body.duration;
        
        let d = new Date();
        let start = new Date(d.getFullYear(), d.getMonth(), d.getDate()-duration).toISOString();
        let end = d.toISOString();
        
        const getRideInfo = Ride.find({ 'passengerID': id, 'time': {$gte: start, $lte: end} });
        const getTotalEarning = Ride.aggregate([
            { $match : { 'passengerID': id, 'time': {$gte: new Date(start), $lte: new Date(end)} } },
            { $group: { '_id': '$passengerID', 'total': {$sum: '$fare'}}}
        ]);

        Promise.all([getRideInfo, getTotalEarning])
        .then(data => {
            res.send(data);
            //console.log(data);
        })
        .catch(err => {
            res.status(500).send({ message: err.message });
        })
    }
    else {
        Ride.find({ 'passengerID': id })
        .then(data =>{
            //console.log(data)
            res.send(data); 
        })
        .catch(err =>{
            res.status(500).send({ message: err.message });
        });
    }
}

//find ride using vehicleID
exports.findByVehicleID = (req, res) => {
    if(req.body.duration) {
        let duration = req.body.duration;
        
        let d = new Date();
        let start = new Date(d.getFullYear(), d.getMonth(), d.getDate()-duration).toISOString();
        let end = d.toISOString();
        
        const getRideInfo = Ride.find({ 'vehicleID': mongoose.Types.ObjectId(req.body._id), 'time': {$gte: start, $lte: end} });
        const getTotalEarning = Ride.aggregate([
            { $match : { 'vehicleID': mongoose.Types.ObjectId(req.body._id), 'time': {$gte: new Date(start), $lte: new Date(end)} } },
            { $group: { '_id': '$vehicleID', 'total': {$sum: '$fare'}}}
        ]);

        Promise.all([getRideInfo, getTotalEarning])
        .then(data => {
            res.send(data);
            //console.log(data);
        })
        .catch(err => {
            res.status(500).send({ message: err.message });
        })
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