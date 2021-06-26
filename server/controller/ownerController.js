const Owner = require('../model/owner');
const Driver = require('../model/driver');
const Vehicle = require('../model/vehicle');
const Ride = require('../model/ride');
const DriverPool = require('../model/driverPool');
const jwt = require('jsonwebtoken');
const mongoose = require('mongoose');

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

//all vehicles
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

//show info for one vehicle
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

exports.showVehicleStatus = async(req, res) => {
    try {
        const ownerID = mongoose.Types.ObjectId(req.data._id);
        const vehicleID = mongoose.Types.ObjectId(req.params.id);

        let filter = {_id: vehicleID, ownerID: ownerID};
        vehicleInfo = await Vehicle.findOne(filter);

        let driverID = vehicleInfo.driverID;
        let getDriverInfo = Driver.findById(driverID);
        let getPoolStatus = DriverPool.findOne({driverID: driverID});
        let info = await Promise.all([getDriverInfo, getPoolStatus]);

        let driverInfo = info[0];
        let poolStatus = info[1];
        let vehicleLocation = vehicleInfo.location.coordinates;

        let passengerInfo = undefined;
        let status = undefined;
        if(poolStatus && Object.keys(poolStatus).length) {
            status = poolStatus.status;
            if(poolStatus.passengerID) {
                passengerInfo = {
                    "passengerID": poolStatus.passengerID,
                    "passengerName": poolStatus.passengerInfo.passengerData.name,
                    "pickUpPoint": poolStatus.passengerInfo.pickUpPoint,
                    "dropOutPoint": poolStatus.passengerInfo.dropOutPoint    
                }
            }
        }
        //console.log(passengerInfo);
        res.status(200).send({vehicleInfo, vehicleLocation, driverInfo, status, passengerInfo});
    } catch (error) {
        console.log(error);
        res.status(200).send({message: error.message, error});
    }
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

//ride history for one vehicle
exports.showRideHistory = (req, res) => {
    
    const ownerID = req.data._id;
    const vehicleID = req.params.id;
    const filter = {
        vehicleID: mongoose.Types.ObjectId(vehicleID)
    }

    let getRideHistory = null;

    if(req.query.duration) {
        const duration = parseInt(req.query.duration);

        let d = new Date();
        let start = new Date(d.getFullYear(), d.getMonth(), d.getDate()-duration).toISOString();
        let end = d.toISOString();

        //console.log("start: ", start);
        //console.log("end: ", end);
        
        getRideHistory = Ride.aggregate([
            { $match : { filter, 'time': {$gte: new Date(start), $lte: new Date(end)} } }
        ]);
    }
    else {
        console.log(filter);
        getRideHistory = Ride.find(filter);
    }

    getRideHistory
    .then(data => {
        res.status(200).send(data);
    })
    .catch(err => {
        //console.log(err);
        res.status(500).send({message: err.message});
    });
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