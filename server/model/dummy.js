const mongoose = require('mongoose');

var DummySchema = new mongoose.Schema({
    item : {
        type: Object
    }
})

module.exports = mongoose.model('dummy', DummySchema);