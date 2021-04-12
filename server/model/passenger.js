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
    instID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Institute'
    },
    couponID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Coupon'
    }
})

module.exports = mongoose.model('passenger', PassengerSchema);