const Ride = require('../model/ride');
const jwt = require('jsonwebtoken');

exports.addRide = (req, res) => {
    // validate request
    if(!req.body){
        return res.status(400).send({ message : "Content can not be emtpy!"});
    }
    
    // new Ride
    const ride = new Ride({
        model : req.body.model,
        type : req.body.type,
        regNo : req.body.regNo,
        ownerID : req.body.ownerID,
        driverID : req.body.driverID,
        capacity : req.body.capacity
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