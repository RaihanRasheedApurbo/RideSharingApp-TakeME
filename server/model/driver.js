const mongoose = require('mongoose');

const DriverSchema = new mongoose.Schema({
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
    gender : {
        type : String,
        required: true,
    },
    address : {
        type : Object,
        street : {
            type: String
        },
        city : {
            type: String,
            require: true
        },
        country : {
            type: String,
            require: true
        },
        required: true
    },
    nid : {
        type : String,
        required: true,
        unique: true
    },
    rating : {
        type : Number,
        default: 5,
        required: true
    },
    licenseNo : {
        type : String,
        required: true
    },
    vehicleID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Vehicle'
    }
})

module.exports = mongoose.model('driver', DriverSchema);