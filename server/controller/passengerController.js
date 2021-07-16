const Passenger = require('../model/passenger');
const jwt = require('jsonwebtoken');
const mongoose = require('mongoose');

const secret = process.env.TOKEN_SECRET || "TakeMeSecret";

exports.register = (req, res) => {
    // validate request
    if(!req.body){
        return res.status(400).send({ message : "Content can not be emtpy!"});
    }
    
    Passenger.findOne({ email: req.body.email })
        .then(data => {
            if(data) {
                //Email already Exists
                //console.log(data);
                return res.status(400).send({ message: "Email already Exists"});
            }
            else {
                // new Passenger
                const passenger = new Passenger({
                    name : req.body.name,
                    email : req.body.email,
                    password : req.body.password,
                    phone : req.body.phone,
                    gender: req.body.gender,
                    address : req.body.address,
                    nid : req.body.nid
                });

                // save passenger in the database
                passenger.save()
                    .then(data => {
                        res.send(data);
                        //res.send({ message: "registration successful" });
                        //res.redirect('/add-user');
                    })
                    .catch(err =>{
                        res.status(500).send({
                            message : err.message || "Some error occurred while creating a create operation"
                        });
                    });
            }
        })
        .catch(err =>{
            res.status(500).send({ message : err.message });
        });
}

//login function
exports.login = (req, res) => {
    Passenger.findOne({'email': req.body.email, 'password': req.body.password})
    .then(data =>{
        if(!data){
            res.status(404).send({ message : "Invalid Email or Password" });
        }else{
            //console.log(data)
            const token = jwt.sign({_id: data._id}, secret);
            res.header('auth-token', token).send({ message: "login successful", data });
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving passenger with email " + req.body.email});
    });
}

//dashboard
exports.showDashboard = (req, res) => {
    const id = req.data._id;
    Passenger.findById(id)
    .then(data =>{
        if(!data){
            res.status(404).send({ message : "Passenger Not found with id" + id})
        }else{
            //console.log(data)
            res.send(data);
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving passenger with id " + id})
    })
}

//show Ride History
exports.showRideHistory = async (req, res) => {
    try {
        const passengerID = mongoose.Types.ObjectId(req.data._id);
        let filter = {'passengerID': passengerID};
        let getRideHistory = null, getTotalSpent = null;

        if(req.query.duration) {
            const duration = parseInt(req.query.duration);

            let d = new Date();
            let start = new Date(d.getFullYear(), d.getMonth(), d.getDate()-duration).toISOString();
            let end = d.toISOString();

            getRideHistory = Ride.aggregate([
                { $match : { filter, 'time': {$gte: new Date(start), $lte: new Date(end)} } }
            ]);
            getTotalSpent = Ride.aggregate([
                { $match : { filter, 'time': {$gte: new Date(start), $lte: new Date(end)} } },
                { $group: { '_id': '$passengerID', 'total': {$sum: '$fare'}}}
            ]);
        }
        else {
            getRideHistory = Ride.find({ filter });
            getTotalSpent = Ride.aggregate([
                { $group: { '_id': '$passengerID', 'total': {$sum: '$fare'}}}
            ]);
        }

        let info = await Promise.all([getRideHistory, getTotalSpent]);
        let rideHistory = info[0];
        let spent = info[1];
        res.status(200).send({ride: rideHistory, count: rideHistory.length, total: spent});
    } catch (error) {
        res.send({message: error.message});
    }   
}

exports.getAllPassengers = (req, res) => {
    Passenger.find({})
    .then(data => {
        res.send(data);
    })
    .catch(err => {
        res.status(400).send(err);
    });
}