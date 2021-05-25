const mongoose = require('mongoose');
const pointSchema = require('./point');

const RideSchema = new mongoose.Schema({
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
        location : {
            type: pointSchema
        },
        required: true
    },
    destination : {
        type : Object,
        name : {
            type: String,
            required: true
        },
        location : {
            type: pointSchema
        },
        required: true
    }
})

module.exports = mongoose.model('ride', RideSchema);