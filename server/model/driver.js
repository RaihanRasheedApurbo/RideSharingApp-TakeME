const mongoose = require('mongoose');

var DriverSchema = new mongoose.Schema({
    name : {
        type : String,
        required: true
    },
    email : {
        type: String,
        required: true,
        unique: true
    },
    password : {
        type: String,
        required: true,
        max: 32,
        min: 8
    },
    phone : {
        type : String,
        required: true,
        unique: true
    },
    address : {
        type : Object,
        required: true
    },
    nid : {
        type : String,
        required: true
    },
    licenseNo : {
        type : String,
        required: true
    },
    rating : {
        type : Number,
        required: true
    },
    vehicleID : {
        type : String,
        required: true
    }
})

module.exports = mongoose.model('driver', DriverSchema);