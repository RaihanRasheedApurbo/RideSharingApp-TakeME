const Owner = require('../model/owner');
const Driver = require('../model/driver');
const Vehicle = require('../model/vehicle');
const Ride = require('../model/ride');
const jwt = require('jsonwebtoken');
const mongoose = require('mongoose');

const driverController = require('../controller/driverController');
const vehicleController = require('./vehicleController');
const rideController = require('./rideController'); 

const secret = process.env.TOKEN_SECRET || "TakeMeSecret";

exports.register = (req, res) => {
    // validate request
    if(!req.body){
        return res.status(400).send({ message : "Content can not be emtpy!"});
    }
    
    Owner.findOne({ email: req.body.email })
        .then(data => {
            if(data) {
                //Email already Exists
                //console.log(data);
                return res.status(400).send({ message: "Email already Exists" });
            }
            else {
                // new Owner
                const owner = new Owner({
                    name : req.body.name,
                    email : req.body.email,
                    password : req.body.password,
                    phone : req.body.phone,
                    gender: req.body.gender,
                    address : req.body.address,
                    nid : req.body.nid,
                    vehicleList : req.body.vehicleList
                });

                // save owner in the database
                owner.save()
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
    Owner.findOne({'email': req.body.email, 'password': req.body.password})
    .then(data =>{
        if(!data){
            res.status(404).send({ message : "Invalid Email or Password" });
        }else{
            //console.log(data)
            const token = jwt.sign({_id: data._id}, secret);
            res.header('auth-token', token).send({ message: 'login successful', data });
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving owner with email " + req.body.email});
    });
}

//dashboard
exports.showDashboard = (req, res) => {
    const id = req.data._id;
    Owner.findById(id)
    .then(data =>{
        if(!data){
            res.status(404).send({ message : "Owner Not found with id" + id})
        }else{
            //console.log(data)
            res.send(data);
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving owner with id " + id})
    });
}

//vehicleInfoFunction
exports.showVehicleDetails = (req, res) => {
    const id = req.data._id;
    Owner.findById(id)
    .then(data => {
        if(!data){
            res.status(404).send({ message : "Owner Not found with id" + id})
        }else{
            //console.log(data)
            Vehicle.find({ _id: data.vehicleList })
            .then(data => {
                res.send(data);
                //console.log(data);
            })
            .catch(err => {
                console.log(err);
            })
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving owner with id " + id})
    });

}

exports.showVehicleInfo = (req, res) => {
    const ownerID = req.data._id;
    const vehicleID = req.params.id;
    
    const driver = req.query.driver;
    const duration = parseInt(req.query.duration);
    
    Vehicle.findOne({_id: vehicleID, ownerID: ownerID})
    .then(data => {
        const vehicleInfo = data;
        
        const promises = [];
        if(driver !== undefined) {
            //console.log("hello");
            const getDriverInfo = Driver.findById(vehicleInfo.driverID);
            promises.push(getDriverInfo);
        }
        if(!isNaN(duration)) {
            //console.log("helloo");
            let d = new Date();
            let start = new Date(d.getFullYear(), d.getMonth(), d.getDate()-duration).toISOString();
            let end = d.toISOString();

            console.log("start: ", start);
            console.log("end: ", end);
            
            const getRideInfo = Ride.find({ 'vehicleID': vehicleInfo._id, 'time': {$gte: start, $lte: end} });
            const getTotalEarning = Ride.aggregate([
                { $match : { 'vehicleID': vehicleInfo._id, 'time': {$gte: new Date(start), $lte: new Date(end)} } },
                { $group: { '_id': '$vehicleID', 'total': {$sum: '$fare'}}}
            ]);

            promises.push(getRideInfo, getTotalEarning);
        }

        Promise.all(promises)
        .then(data => {
            res.send({vehicleInfo, data});
            //console.log({vehicleInfo, data});
        })
        .catch(err => {
            res.status(500).send({ message: err.message });
        });
    })
    .catch(err => {
        //console.log(err);
        res.status(500).send({ message: err.message});
    });
}

//vehicleUpdate
exports.updateVehicleInfo =(req, res) => {
    const ownerID = req.data._id;
    const vehicleID = req.params.id;
    
    const filter = {
        _id: mongoose.Types.ObjectId(vehicleID),
        ownerID: mongoose.Types.ObjectId(ownerID)
    }
    const updateInfo = req.body;

    Vehicle.findOneAndUpdate(filter, updateInfo, { useFindAndModify: false, new: true })
    .then(data => {
        res.status(200).send(data);
    })
    .catch(err => {
        res.status(500).send(err);
    })
}

//driverAddFunction
exports.addDriverToVehicle = (req, res) => {
    const oid = mongoose.Types.ObjectId(req.data._id);
    const vid = mongoose.Types.ObjectId(req.body.vehicleID);
    const did = mongoose.Types.ObjectId(req.body.driverID);

    Owner.findOne({_id: oid, vehicleList:  vid})
    .then(data => {
        if(data) {
            //console.log(data);
            
            vehicleUpdate = Vehicle.findByIdAndUpdate(vid, {driverID: did}, { useFindAndModify: false, new: true });
            driverUpdate = Driver.findByIdAndUpdate(did, {vehicleID: vid}, { useFindAndModify: false, new: true }); 

            Promise.all([vehicleUpdate, driverUpdate])
            .then(data => {
                //console.log(data);
                res.send(data);
            }).catch(err => {
                res.status(500).send({message: err.message});
            });
        }
        else {
            res.status(404).send({ message : "Owner Not found with vehicleID " + vid});
        } 
    })
    .catch( err => {
        res.status(500).send({ message: err.message });
    });
}

exports.getAllOwners = (req, res) => {
    Owner.find({})
    .then(data => {
        res.send(data);
    })
    .catch(err => {
        res.status(400).send(err);
    });
}