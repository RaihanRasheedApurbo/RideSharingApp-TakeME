const mongoose = require('mongoose');
var DriverPoolSchema = new mongoose.Schema({
    driverID : {
        type: mongoose.Schema.Types.ObjectId,
        required: true,
        ref: 'Driver'
    },
    vehicleInfo : {
        type: Object,
        required: true
    },
    passengerID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Passenger'
    }
})

module.exports = mongoose.model('driverPool', DriverPoolSchema);