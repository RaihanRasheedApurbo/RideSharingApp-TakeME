const express = require('express');
const route = express.Router();

const verify = require('../middleware/verifyToken');
//const services = require('../services/render');

const ownerController = require('../controller/ownerController');
const driverController = require('../controller/driverController');
const passengerController = require('../controller/passengerController');
const vehicleController = require('../controller/vehicleController');
const rideController = require('../controller/rideController');
const driverPoolController = require('../controller/driverPoolController');
const dummyController = require('../controller/dummyController');


route.get('/', (req, res) => {
    res.render('index');
});

// API
route.post('/api/owner/register', ownerController.register);
route.post('/api/owner/login', ownerController.login);
route.get('/api/owner/dashboard', verify, ownerController.showDashboard);
route.get('/api/owner/vehicles', verify, ownerController.showVehicleDetails);
route.get('/api/owner/vehicle/id/:id', verify, ownerController.showVehicleInfo); //will be deprecated lated
route.put('/api/owner/vehicle/id/:id', verify, ownerController.updateVehicleInfo);
route.get('/api/owner/vehicle/id/:id/status', verify, ownerController.showVehicleStatus);
route.get('/api/owner/vehicle/id/:id/rideHistory', verify, ownerController.showRideHistory);
route.post('/api/owner/addDriver', verify, ownerController.addDriverToVehicle); //under construction
route.get('/api/owner/getAll', ownerController.getAllOwners); //test purpose only

route.post('/api/driver/register', driverController.register);
route.post('/api/driver/login', driverController.login);
route.get('/api/driver/dashboard', verify, driverController.showDashboard);
route.get('/api/driver/vehicle', verify, driverController.showVehicleInfo);
route.get('/api/driver/rideHistory', verify, driverController.showRideHistory);
route.get('/api/driver/earning', verify, driverController.showEarning);
route.post('/api/driver/vehicle/location', verify, driverController.updateLocation);
route.get('/api/driver/vehicleID', driverController.findByVehicleID); //under construction, might be unnecessary
route.get('/api/driver/getAll', driverController.getAllDrivers); //test purpose only

route.post('/api/passenger/register', passengerController.register);
route.post('/api/passenger/login', passengerController.login);
route.get('/api/passenger/dashboard', verify, passengerController.showDashboard);
route.post('/api/passenger/addRide', verify, rideController.addRide); //under construction
route.get('/api/passenger/getAll', passengerController.getAllPassengers); //test purpose only


route.get('/api/driver/search', verify, driverPoolController.lookForPassenger);
route.get('/api/driver/stopSearch', verify, driverPoolController.stopPassengerSearch);
route.post('/api/driver/startRide', verify, driverPoolController.startRide);
route.post('/api/driver/cancelMatch', verify, driverPoolController.cancelMatch);
route.post('/api/driver/endRide', verify, driverPoolController.endRide);
route.post('/api/passenger/cancelMatch', verify, driverPoolController.cancelMatch);
route.post('/api/passenger/endRide', verify, driverPoolController.endRide);
route.post('/api/passenger/searchDriver', verify, driverPoolController.lookForDriver);
route.post('/api/passenger/acceptDriver', verify, driverPoolController.acceptDriver); //under construction


route.post('/api/vehicle/register', verify, vehicleController.addVehicle); //under construction, might be unnecessary
route.get('/api/vehicle/id/', vehicleController.find); //under construction, might be unnecessary
route.get('/api/vehicle/ownerID/', vehicleController.findByOwnerID); //under construction, might be unnecessary
route.get('/api/vehicle/driverID/:id', vehicleController.findByDriverID); //under construction, might be unnecessary
route.get('/api/vehicle/getAll', vehicleController.getAllVehicles); //test purpose only


route.post('/api/ride/add', verify, rideController.addRide); //under construction, might be unnecessary
route.get('/api/ride/id/', rideController.find); //under construction, might be unnecessary
route.get('/api/ride/passengerID/', rideController.findByPassengerID); //under construction, might be unnecessary
route.get('/api/ride/driverID/', rideController.findByDriverID); //under construction, might be unnecessary
route.get('/api/ride/vehicleID/', rideController.findByVehicleID); //under construction, might be unnecessary
route.get('/api/ride/getAll', rideController.getAllRides); //test purpose only


route.get('/api/driver/pool', driverPoolController.showPool); //test purpose only
route.delete('/api/driver/pool', driverPoolController.deletePool); //test purpose only



//dummy routes only for test purposes
route.get('/api/dummy', dummyController.get);
route.post('/api/dummy', dummyController.set);

route.post('/api/dummy/owner/reqTest', dummyController.reqTest);


module.exports = route;