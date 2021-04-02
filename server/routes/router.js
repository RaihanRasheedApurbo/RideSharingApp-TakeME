const express = require('express');
const route = express.Router();

const verify = require('./verifyToken');
//const services = require('../services/render');

const ownerController = require('../controller/ownerController');
//const driverController = require('../controller/driverController');
//const passengerController = require('../controller/passengerController');
//const rideHistoryController = require('../controller/riderHistoryController');


// API
route.post('/api/owner/register', ownerController.register);
route.post('/api/owner/login', ownerController.login);
route.get('/api/owner/dashboard', verify, ownerController.showDashboard);



module.exports = route;