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
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Owner',
        required: true
    },
    driverID : {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Driver'
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