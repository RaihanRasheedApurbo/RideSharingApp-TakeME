const mongoose = require('mongoose');

var OwnerSchema = new mongoose.Schema({
    name : {
        type : String,
        required: true
    },
    email : {
        type: String,
        required: true,
        unique: true
    },
    password : {
        type: String,
        required: true,
        max: 32,
        min: 8
    },
    phone : {
        type : String,
        required: true,
        unique: true
    },
    gender : {
        type : String,
        required: true,
    },
    address : {
        type : Object,
        required: true
    },
    nid : {
        type : String,
        required: true
    },
    vehicleList : {
        type : Array,
    }
})

module.exports = mongoose.model('owner', OwnerSchema);