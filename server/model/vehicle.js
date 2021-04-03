const mongoose = require('mongoose');

var VehicleSchema = new mongoose.Schema({
    model : {
        type : String,
        required: true
    },
    type : {
        type : String,
        required: true
    },
    capacity : {
        type : Number,
        required: true
    },
    regNo : {
        type: String,
        required: true,
        unique: true
    },
    ownerID : {
        type: String,
        required: true
    },
    driverID : {
        type: String
    }
})

module.exports = mongoose.model('vehicle', VehicleSchema);