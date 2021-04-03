const mongoose = require('mongoose');

var RideSchema = new mongoose.Schema({
    driverID : {
        type: String,
        required: true
    },
    passengerID : {
        type: String,
        required: true
    },
    vehicleID : {
        type: String,
        required: true
    },
    time : {
        type : Date,
        default: Date.now
    },
    duration : {
        type : Number,
        required: true
    },
    fare : {
        type : Number,
        required: true
    },
    source : {
        type : Object,
        name : {
            type: String,
            required: true
        },
        latitude: {
            type: Number,
            required: true
        },
        longitude: {
            type: Number,
            required: true
        },
        required: true
    },
    destination : {
        type : Object,
        name : {
            type: String,
            required: true
        },
        latitude: {
            type: Number,
            required: true
        },
        longitude: {
            type: Number,
            required: true
        },
        required: true
    }
})

module.exports = mongoose.model('ride', RideSchema);