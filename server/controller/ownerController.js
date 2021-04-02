const Owner = require('../model/owner');
const jwt = require('jsonwebtoken');

exports.register = (req, res) => {
    // validate request
    if(!req.body){
        res.status(400).send({ message : "Content can not be emtpy!"});
        return;
    }
    
    Owner.findOne({ email: req.body.email })
        .then(data => {
            if(data) {
                //Email already Exists
                console.log(data);
                return res.status(400).send({ message: "Email already Exists", data: data});
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
                        res.send(data)
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
            const token = jwt.sign({_id: data._id}, process.env.TOKEN_SECRET);
            res.header('auth-token', token).send({'token': token, '_id': data._id});
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
    })
}