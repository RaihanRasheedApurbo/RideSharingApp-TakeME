const mongoose = require('mongoose');

var PassengerSchema = new mongoose.Schema({
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
        type : String,
        required: true
    },
    nid : {
        type : String,
        required: true
    },
    instID : {
        type : String
    },
    couponID : {
        type : String
    }
})

module.exports = mongoose.model('passenger', PassengerSchema);