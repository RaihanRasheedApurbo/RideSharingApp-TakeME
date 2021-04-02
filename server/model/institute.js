const mongoose = require('mongoose');

var InstituteSchema = new mongoose.Schema({
    name : {
        type : String,
        required: true
    },
    discountRate : {
        type : Number,
        required: true
    },
    paymentRate : {
        type: Number,
        required: true
    },
    userList : {
        type: Array,
        required: true
    }
})

module.exports = mongoose.model('institute', InstituteSchema);