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
route.get('/api/owner/vehicles', verify, ownerController.showVehicleDetails);
route.post('/api/owner/addDriver', verify, ownerController.addDriverToVehicle);

route.post('/api/driver/register', driverController.register);
route.post('/api/driver/login', driverController.login);
route.get('/api/driver/dashboard', verify, driverController.showDashboard);
route.get('/api/driver/vehicleID', driverController.findByVehicleID);


route.post('/api/passenger/register', passengerController.register);
route.post('/api/passenger/login', passengerController.login);
route.get('/api/passenger/dashboard', verify, passengerController.showDashboard);


route.post('/api/vehicle/register', verify, vehicleController.addVehicle);
route.get('/api/vehicle/id/', vehicleController.find);
route.get('/api/vehicle/ownerID/', vehicleController.findByOwnerID);
route.get('/api/vehicle/driverID/', vehicleController.findByDriverID);


route.post('api/ride/add', rideController.addRide);
route.get('/api/ride/id/', rideController.find);
route.get('/api/ride/passengerID/', rideController.findByPassengerID);
route.get('/api/ride/driverID/', rideController.findByDriverID);
route.get('/api/ride/vehicleID/', rideController.findByVehicleID);


module.exports = route;