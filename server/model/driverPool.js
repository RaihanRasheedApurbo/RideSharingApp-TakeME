const mongoose = require('mongoose');
const pointSchema = require('./point');

const DriverPoolSchema = new mongoose.Schema({
    driverID : {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
        ref: 'Driver'
    },
    vehicleInfo : {
        type: Object,
        required: true
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
    }
});

module.exports = mongoose.model('driverPool', DriverPoolSchema);