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
        type : Number,
        required: true
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