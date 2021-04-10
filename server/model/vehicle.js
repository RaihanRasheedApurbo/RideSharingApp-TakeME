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
        type: String,
        required: true,
        default: "None"
    },
    location: {
        type : Object,
        latitude: {
            type: Number,
            required: true
        },
        longitude: {
            type: Number,
            required: true
        }
    }
})

module.exports = mongoose.model('vehicle', VehicleSchema);