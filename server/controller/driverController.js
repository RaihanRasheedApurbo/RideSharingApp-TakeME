const Driver = require('../model/driver');
const jwt = require('jsonwebtoken');

exports.register = (req, res) => {
    // validate request
    if(!req.body){
        return res.status(400).send({ message : "Content can not be emtpy!"});
    }
    
    Driver.findOne({ email: req.body.email })
        .then(data => {
            if(data) {
                //Email already Exists
                //console.log(data);
                return res.status(400).send({ message: "Email already Exists", data: data});
            }
            else {
                // new Driver
                const driver = new Driver({
                    name : req.body.name,
                    email : req.body.email,
                    password : req.body.password,
                    phone : req.body.phone,
                    gender: req.body.gender,
                    address : req.body.address,
                    nid : req.body.nid,
                    licenseNo : req.body.licenseNo
                });

                // save driver in the database
                driver.save()
                    .then(data => {
                        res.send(data);
                        //res.send({ message: "registration succesful" });
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
    Driver.findOne({'email': req.body.email, 'password': req.body.password})
    .then(data =>{
        if(!data){
            res.status(404).send({ message : "Invalid Email or Password" });
        }else{
            //console.log(data)
            const token = jwt.sign({_id: data._id}, process.env.TOKEN_SECRET);
            res.header('auth-token', token).send({ message: "login successful", data });
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving driver with email " + req.body.email});
    });
}

//dashboard
exports.showDashboard = (req, res) => {
    const id = req.data._id;
    Driver.findById(id)
    .then(data =>{
        if(!data){
            res.status(404).send({ message : "Driver Not found with id" + id})
        }else{
            //console.log(data)
            res.send(data);
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving driver with id " + id})
    })
}

//maybe will delete these later
exports.getAllDrivers = (req, res) => {
    Driver.find({})
    .then( data => {
        res.send(data);
    })
    .catch( err => {
        res.status(400).send(err);
    });
}


//find Driver using vehicleID
exports.findByVehicleID = (req, res) => {
    Driver.find({ 'vehicleID': req.body._id })
    .then(data =>{
        if(data.length <= 0){
            res.status(404).send({ message : "No Driver with vehicleID: " + req.body._id });
        }else{
            //console.log(data)
            res.send(data);    
        }
    })
    .catch(err =>{
        res.status(500).send({ message: err.message || "Error retrieving Driver with vehicleID: " + req.body._id});
    });
}