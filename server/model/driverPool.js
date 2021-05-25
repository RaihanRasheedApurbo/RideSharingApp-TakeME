const mongoose = require('mongoose');
const pointSchema = require('./point');

const DriverPoolSchema = new mongoose.Schema({
    driverID : {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
        ref: 'Driver'
    },
    passengerID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Passenger'
    },
    location: {
        type: pointSchema,
        index: '2dsphere' // Create a special 2dsphere index on `location`
    },
    vehicleInfo : {
        type: Object,
        required: true
    }
});

module.exports = mongoose.model('driverPool', DriverPoolSchema);