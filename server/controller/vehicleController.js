const Vehicle = require('../model/vehicle');
const Owner = require('../model/owner');

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
                console.log('req.body');
                console.log(req.body);
                console.log('req.data');
                console.log(req.data);
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
    Vehicle.find({ 'driverID': req.body._id })
    .then(data =>{
        if(data.length <= 0){
            res.status(404).send({ message : "No vehicle to show" });
        }else{
            //console.log(data)
            res.send(data);    
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving vehicle with driverID " + req.body._id});
    });
}