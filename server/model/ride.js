const mongoose = require('mongoose');

var RideSchema = new mongoose.Schema({
    driverID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Driver',
        required: true
    },
    passengerID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Passenger',
        required: true
    },
    vehicleID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Vehicle',
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