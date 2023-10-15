const mongoose = require('mongoose');
const pointSchema = require('./point');

const VehicleSchema = new mongoose.Schema({
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
        type: pointSchema,
        index: '2dsphere' // Create a special 2dsphere index on `location`
    }
})

module.exports = mongoose.model('vehicle', VehicleSchema);