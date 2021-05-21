const Dummy = require('../model/dummy');
const jwt = require('jsonwebtoken');

exports.set = (req, res) => {
    // validate request
    if(!req.body){
        return res.status(400).send({ message : "Content can not be emtpy!"});
    }
    
    Dummy.find({})
    .then(data => {
        if(data.length > 0) {
            Dummy.findOneAndUpdate({}, {$set: {item: req.body}}, { useFindAndModify: false, new: true })
            .then(data => {
                res.send(data.item);
            })
            .catch(err => {
                res.status(500).send( {message: err.message} );
            });
        }
        else {
            const dummy = new Dummy({
                item: req.body
            });
        
            dummy.save()
            .then(data => {
                res.send(data.item);
            })
            .catch(err => {
                res.status(500).send( {message: err.message} );
            });
        }
    })
    .catch(err => {
        res.status(500).send( {message: err.message} );
    });
}

exports.get = (req, res) => {
    Dummy.find({})
    .then( data => {
        res.send(data[0].item);
    })
    .catch( err => {
        res.status(500).send( {message: err.message} );
    });
}

exports.reqTest = (req, res) => {
    const reqData = {
        "params": req.params,
        "query": req.query,
        "body": req.body,
        "headers": req.headers
    };

    console.log(reqData);
    console.log(req);
    res.send(reqData);
}