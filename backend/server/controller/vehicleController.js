const Vehicle = require('../model/vehicle');
const Owner = require('../model/owner');
const mongoose = require('mongoose');

exports.addVehicle = (req, res) => {
    // validate request
    if(!req.body){
        return res.status(400).send({ message : "Content can not be emtpy!"});
    }
    
    Vehicle.findOne({ regNo: req.body.regNo })
        .then(data => {
            if(data) {
                //Vehicle already Exists
                console.log(data);
                return res.status(400).send({ message: "Vehicle already Exists"});
            }
            else {
                // new Vehicle
                const vehicle = new Vehicle({
                    model : req.body.model,
                    type : req.body.type,
                    regNo : req.body.regNo,
                    capacity : req.body.capacity,
                    ownerID : req.data._id
                });

                // save vehicle in the database
                vehicle.save()
                    .then(vehicleData => {
                        Owner.findByIdAndUpdate(req.data._id, {$addToSet :{ "vehicleList": vehicleData }}, { useFindAndModify: false, new: true })
                        .then(ownerData => {
                            res.send({vehicleData, ownerData});
                            //res.redirect('/add-user');
                        })
                        .catch( err => {
                            res.status(500).send({ message : err.message });
                        });
                    })
                    .catch(err =>{
                        res.status(500).send({
                            message : err.message || "Some error occurred while creating a create operation"
                        });
                    });
            }
        })
        .catch(err =>{
            console.log("fjlsdldsj");
            res.status(500).send({ message : err.message });
        });
}


//find
exports.find = (req, res) => {
    Vehicle.findById(req.body._id)
    .then(data =>{
        if(!data){
            res.status(404).send({ message : "No vehicle to show" });
        }else{
            //console.log(data)
            res.send(data);    
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving vehicle with id " + req.body._id});
    });
}


//find vehicle using ownerID
exports.findByOwnerID = (req, res) => {
    Vehicle.find({ 'ownerID': req.body._id })
    .then(data =>{
        if(data.length <= 0){
            res.status(404).send({ message : "No vehicle to show" });
        }else{
            //console.log(data)
            res.send(data);    
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving vehicle with ownerID " + req.body._id});
    });
}


//find vehicle using driverID
exports.findByDriverID = (req, res) => {
    console.log(req.params.id);
    Vehicle.find({ 'driverID': req.params.id })
    .then(data =>{
        if(data.length <= 0){
            res.status(200).send({ message : "No vehicle to show" });
        }else{
            //console.log(data)
            console.log(data);
            res.status(200).send(data);    
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving vehicle with driverID " + req.body._id});
    });
}

exports.getAllVehicles = (req, res) => {
    Vehicle.find({})
    .then(data => {
        res.send(data);
    })
    .catch(err => {
        res.status(400).send(err);
    });
}

exports.updateVehicleType = async (req, res) => {
    try {
        let id = mongoose.Types.ObjectId(req.body._id);
        let vehicleType = req.body.type;

        const vehicleUpdateBody = {
            $set: {
                'type': vehicleType
            }
        };
        
        let savedEntry = await Vehicle.findByIdAndUpdate(id, vehicleUpdateBody, { useFindAndModify: false, new: true });
        res.send(savedEntry);
    } catch (error) {
        console.log(error.message);
        res.send(error.message);
    }
}