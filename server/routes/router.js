const express = require('express');
const route = express.Router();

const verify = require('./verifyToken');
//const services = require('../services/render');

const ownerController = require('../controller/ownerController');
const driverController = require('../controller/driverController');
const passengerController = require('../controller/passengerController');
const vehicleController = require('../controller/vehicleController');
const rideController = require('../controller/rideController');


// API
route.post('/api/owner/register', ownerController.register);
route.post('/api/owner/login', ownerController.login);
route.get('/api/owner/dashboard', verify, ownerController.showDashboard);


route.post('/api/driver/register', driverController.register);
route.post('/api/driver/login', driverController.login);
route.get('/api/driver/dashboard', verify, driverController.showDashboard);


route.post('/api/passenger/register', passengerController.register);
route.post('/api/passenger/login', passengerController.login);
route.get('/api/passenger/dashboard', verify, passengerController.showDashboard);



module.exports = route;