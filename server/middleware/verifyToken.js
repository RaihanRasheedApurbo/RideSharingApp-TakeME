const jwt = require('jsonwebtoken');
const secret = process.env.TOKEN_SECRET || "TakeMeSecret";

module.exports = function (req, res, next) {
    /*const bearerHeader = req.header('authorization');
    if(typeof bearerHeader !== 'undefined') {
        const bearerData = authData.split(' ');
        const bearerToken = bearerData[1];
        
        jwt.verify(bearerToken, process.env.TOKEN_SECRET, (err, AuthData) => {
            if(err) {
                res.status(400).send({ message: err.message || "invalid Token" });
            }
            else {
                req.data = AuthData;
                next();
            }
        });
    }
    else {
        res.status(401).send({ messsage: "Access Denied" });
    }*/
    
    const token = req.header('auth-token');
    //console.log("token: ", token);
    if(!token) {
        let headers = req.header;
        return res.status(401).send({ messsage: "Access Denied", header: headers });
    }
    try{
        const verified = jwt.verify(token, secret);
        req.data = verified;
        next();
    }catch (err) {
        let headers = req.header;
        let message = err.message + "invalid";
        res.status(400).send({ message: message, header: headers });
    }
}
