const Ride = require('../model/ride');
const Passenger = require('../model/passenger');
const Driver = require('../model/driver')
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
        time: req.body.time,
        duration : req.body.duration,
        fare : req.body.fare,
        distance: req.body.distance,
        source : req.body.source,
        destination : req.body.destination,
        status: req.body.status,
        rating: req.body.rating
    });

    // save ride in the database
    ride.save()
        .then(data => {
            res.send(data);
        })
        .catch(err =>{
            res.status(500).send({
                message : err.message || "Some error occurred while creating a create operation"
            });
        });
}

//rateRide by passenger
exports.rateRide = async (req, res) => {
    try {
        const passengerID = mongoose.Types.ObjectId(req.data._id);
        const _id = mongoose.Types.ObjectId(req.params.id);
        let rating = Number(req.body.rating);

        if (rating === NaN || rating === 0) throw new Error("got invalid rating as parameter");
        rating = Math.round((rating + Number.EPSILON) * 100) / 100;

        let filter = { _id, passengerID };
        let updateBody = {
            $set: {rating: rating}
        };
        let updateRideEntry = await Ride.findOneAndUpdate(filter, updateBody, { useFindAndModify: false, new: true });
        console.log("ride rating: ", updateRideEntry.rating);
        //res.send(updateRideEntry);
        
        //think how to update rating of the driver
        const driverID = updateRideEntry.driverID;
        let info = await Driver.findById(driverID);
        let driverRating = info.rating;
        let driverRides = await Ride.find({ 'driverID': driverID });
        let driverRideCount = driverRides.length;
        let newRating = (driverRating*(driverRideCount-1) + rating)/driverRideCount;
        newRating = Math.round((newRating + Number.EPSILON) * 100) / 100;
    
        let updatedDriverEntry = await Driver.findByIdAndUpdate(driverID, {$set: {rating: newRating}}, { useFindAndModify: false, new: true });
        console.log("driver new rating: ", updatedDriverEntry.rating);

        res.send(updateRideEntry);
    } catch (error) {
        console.log(error.message);
        res.send(error.message);
    }
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
        res.send({data, total: data.lenth});
    })
    .catch(err => {
        res.status(400).send(err);
    });
}