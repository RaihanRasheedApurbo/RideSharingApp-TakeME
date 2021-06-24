const mongoose = require('mongoose');
const pointSchema = require('./point');

const DriverPoolSchema = new mongoose.Schema({
    driverID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Driver'
    },
    passengerID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Passenger'
    },
    vehicleInfo : {
        type: Object
    },
    driverInfo : {
        type: Object
    },
    passengerInfo : {
        type: Object
    },
    vehicleLocation : {
        type: pointSchema,
        index: '2dsphere' // Create a special 2dsphere index on `vehicleLocation`
    },
    status : {
        type : String
    },
    startTime : {
        type : Date,
        default: Date.now
    },
    rideInfo : {
        type: Object
    }
});

module.exports = mongoose.model('driverPool', DriverPoolSchema);