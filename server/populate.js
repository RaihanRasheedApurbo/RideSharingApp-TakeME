const Chance = require("chance");
const mongoose = require('mongoose');
const axios = require("axios");

const Owner = require('./model/owner');
const Driver = require('./model/driver');
const Vehicle = require('./model/vehicle');
const Passenger = require('./model/passenger');
const pointSchema = require("./model/point");
const Ride = require('./model/ride');

let chance = new Chance();
const base_address = 'https://take-me-backend.herokuapp.com/';
const address = 'http://localhost:3000/api/';

let header_data = {
    'auth-token': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDZkZWE5NTg3NDA1NTI0YjhkZjVlMDYiLCJpYXQiOjE2MTgwNzExNTZ9.j81iCN2-GLtL1PpKnZJUcVoLR8HFopCn0TWevP6yWAA'
}

let loginCred = {
    email: "kevdeh@cebsukhaw.iq",
    password: "PxNzO6cv^0jE!mZ@v"
}

let locationData = [
    {
        name: "Mirpur 10 Bus Stop",
        latitude: 23.8074814467324,
        longitude: 90.36861863275331
    },
    {
        name: "BUET",
        latitude: 23.726763402320714, 
        longitude: 90.39265156876566
    }
]

function generateDriver() {
    return {
        'name': chance.name(),
        'email': chance.email(),
        'password': chance.string(),
        'phone': chance.phone({ formatted: false }),
        'gender': chance.gender(),
        'address': {
            'street': chance.street(),
            'city': chance.city(),
            'country': 'Bangladesh'
        },
        'nid': chance.ssn({ dashes: false }),
        'licenseNo': chance.ssn({ dashes: false })
    };
}

function generatePassenger() {
    return {
        name: chance.name(),
        email: chance.email(),
        password: chance.string(),
        phone: chance.phone({ formatted: false }),
        gender: chance.gender(),
        address: {
            street: chance.street(),
            city: chance.city(),
            country: chance.country({ full: true })
        },
        nid: chance.ssn({ dashes: false })
    };
}

function generateOwner() {
    return {
        name: chance.name(),
        email: chance.email(),
        password: chance.string(),
        phone: chance.phone({ formatted: false }),
        gender: chance.gender(),
        address: {
            street: chance.street(),
            city: chance.city(),
            country: chance.country({ full: true })
        },
        nid: chance.ssn({ dashes: false }),
        vehicleList: []
    };
}

function generateVehicle() {
    return {
        model: chance.pickone(['BMW', 'Toyota', 'Mercedes-benz', 'Audi', 'Mitshubishi']),
        type: chance.pickone(['Standard', 'Premium', 'Delux']),
        regNo: chance.ssn({ dashes: false }),
        capacity: chance.pickone([2, 4, 7])      
    };
}

function driverPopulate() {
    let driverData = generateDriver();
    //console.log(driverData);
    
    axios.post(address+'driver/register', driverData)
    .then( res => {
        console.log(res.data);
    })
    .catch( err => {
        console.log(err);
    });    
}

function ownerPopulate() {
    let ownerData = generaterOwner();
    //console.log(ownerData);
    
    axios.post(address+'owner/register', ownerData)
    .then( res => {
        console.log(res.body);
    })
    .catch( err => {
        console.log(err);
    });
}

function passengerPopulate() {
    let passengerData = generatePassenger();
    //console.log(passengerData);
    
    axios.post(address+'passenger/register', passengerData)
    .then( res => {
        //console.log(res.body);
    })
    .catch( err => {
        console.log(err);
    });   
}

function vehiclePopulate() {
    let vehicleData = generateVehicle();
    //console.log(vehicleData);
    
    axios.post(address+'vehicle/register', vehicleData, {headers: header_data})
    .then( res => {
        console.log(res);
    })
    .catch( err => {
        console.log(err);
    });
}

function ownerPopulateV2() {
    let ownerData = {
        name: chance.name(),
        email: chance.email(),
        password: chance.string(),
        phone: chance.phone({ formatted: false }),
        gender: chance.gender(),
        address: {
            street: chance.street(),
            city: chance.city(),
            country: chance.country({ full: true })
        },
        nid: chance.ssn({ dashes: false }),
        vehicleList: []
    };
    
    //console.log(ownerData);
    
    axios.post(address+'owner/register', ownerData)
    .then( res => {
        //console.log(res.data);
        let ownerCred = {
            email: res.data.email,
            password: res.data.password  
        }
        axios.post(address+'owner/login', ownerCred)
        .then(res => {
            console.log(res.headers['auth-token']);
            header_data['auth-token'] = res.headers['auth-token'];
            let n = Math.floor(Math.random() * 5)+1;
            for (let index = 0; index < n; index++) {
                vehiclePopulate();
            }

            axios.get(address+'owner/vehicles', {headers: header_data})
            .then( data => {
                console.log(data);
                console.log(data.length);
            })
        })
        .catch( err => {
            console.log(err);
        });

    })
    .catch( err => {
        console.log(err);
    });
}

function show() {
    axios.post(address+'owner/login', loginCred)
        .then(res => {
            console.log(res.headers['auth-token']);
            header_data['auth-token'] = res.headers['auth-token'];
            axios.get(address+'owner/vehicles', {headers: header_data})
            .then(res => {
                console.log(res.data);
            })
            .catch(err => {
                console.log(err);
            });
        })
        .catch( err => {
            console.log(err);
        });
}


function driverAssign() {
    getDrivers = axios.get(address+'driver/getAll');
    getOwners = axios.get(address+'owner/getAll');

    Promise.all([getDrivers, getOwners])
    .then(data => {
        drivers = data[0].data;
        owners = data[1].data;
        let count = 0;

        for (let index = 0; index < owners.length; index++) {
            let owner = owners[index];
            let ownerVehicleList = owner.vehicleList;

            let ownerCred = {
                email: owner.email,
                password: owner.password  
            };

            for (let j = 0; j < ownerVehicleList.length; j++) {
                const vehicleID = ownerVehicleList[j];
                
                let driverIndex = null;
                while(true) {
                    driverIndex = Math.floor(Math.random() * drivers.length);
                    if(drivers[driverIndex].vehicleID == undefined) break;
                } 
                const driverID = drivers[driverIndex]._id; 
                
                //console.log(vehicleID, typeof(vehicleID));
                //console.log(driverID, typeof(driverID));
                //console.log("\n");

                let info = {
                    vehicleID, driverID 
                };

                axios.post(address+'owner/login', ownerCred)
                .then(res => {
                    header_data['auth-token'] = res.headers['auth-token'];
        
                    axios.post(address+'owner/addDriver', info, {headers: header_data})
                    .then( data => {
                        console.log("success", count++);
                    })
                })
                .catch( err => {
                    console.log(err);
                });
            }
        }
    })
    .catch( err=> {
        console.log(err);
    });
}

function vehicleLocationUpdate() {
    let count = 0;
    axios.get(address+'owner/getAll')
    .then(data => {
        owners = data.data;
        //console.log(owners);
        for (let index = 0; index < owners.length; index++) {
            let owner = owners[index];
            let ownerVehicleList = owner.vehicleList;
    
            let ownerCred = {
                email: owner.email,
                password: owner.password  
            };
    
            for (let j = 0; j < ownerVehicleList.length; j++) {
                const vehicleID = ownerVehicleList[j];
                
                //console.log(vehicleID, typeof(vehicleID));
                const latitude = chance.floating({min: 23.72, max: 23.85, fixed: 9});
                const longitude = chance.floating({min: 90.35, max: 90.4, fixed: 9});
                    
                const newLocation = {
                    type: 'Point',
                    coordinates: [ longitude, latitude ]
                };
                console.log(newLocation);
    
                axios.post(address+'owner/login', ownerCred)
                .then(res => {
                    header_data['auth-token'] = res.headers['auth-token'];
        
                    axios.put(address+'owner/vehicle/id/'+vehicleID, {location: newLocation}, {headers: header_data})
                    .then( data => {
                        console.log("success", count++);
                    })
                    .catch(err => {
                        console.log("update error", err.message);
                    })
                });
            }
        }
    })
    .catch(err => {
        console.log(err);
    });
}

async function putDriverOnPool() {
    try {
        let poolInfo = axios.get(address+'driver/pool');
        let driverInfo = axios.get(address+'driver/getAll');
        let info = await Promise.all([poolInfo, driverInfo]);
        
        let pool = info[0].data.pool;
        let drivers = info[1].data;
        let driverCount = drivers.length;
        
        let successCount = 0;
        while (true) {
            let driverIndex = Math.floor(Math.random() * driverCount);
            let driver = drivers[driverIndex];
            if(driver.vehicleID) console.log("valid ", driver._id);
            if(driver.vehicleID && pool.filter(d => d.driverID === driver._id).length === 0) {
                let driverCred = {
                    email: driver.email,
                    password: driver.password  
                };
                let loginInfo = await axios.post(address+'driver/login', driverCred);
                
                header_data['auth-token'] = loginInfo.headers['auth-token'];
        
                let searchData = await axios.get(address+'driver/search', {headers: header_data});
                if(searchData.data.message.includes("added to pool")) successCount++;
                if(successCount == 10) break;
            } 
        }
        console.log(successCount);
    } catch (error) {
        console.log({message: error.message});
    }
}

function driverCheck() {
    let count = 0;
    axios.get(address+'driver/getAll')
    .then(data => {
        drivers = data.data;
        //console.log(owners);
        for (let index = 0; index < drivers.length; index++) {
            let driver = drivers[index];
            let driverID = driver._id;
            let bodyInfo = {
                '_id': driverID
            };
            
            axios.get(address+'vehicle/driverID/'+driverID)
            .then(res => {
                driverData = res.data;
                if(driverData.length == 1) {console.log("okay", count++);}
                if(driverData.length != 1) {
                    console.log("driverID: " + driverID);
                    console.log(driverData);
                }
                console.log("----------------------------------");
            })
            .catch(err => {
                console.log(err.message);
            });
        }
    })
    .catch(err => {
        console.log(err);
    });    
}

//calculated geo distance between two geo coordinate
function calculateDistance(lat1, lon1, lat2, lon2) {
    //console.log(lat1, lon1, lat2, lon2);
    const R = 6371e3; // metres
    const φ1 = lat1 * Math.PI/180; // φ, λ in radians
    const φ2 = lat2 * Math.PI/180;
    const Δφ = (lat2-lat1) * Math.PI/180;
    const Δλ = (lon2-lon1) * Math.PI/180;

    const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
            Math.cos(φ1) * Math.cos(φ2) *
            Math.sin(Δλ/2) * Math.sin(Δλ/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

    const d = R * c; // in metres

    //console.log(d);
    return d;
}

function generateRandomDate() {
    let date = new Date();
    let differ = Math.floor(Math.random() * 30);
    let hour = Math.floor(Math.random() * (23-0)) + 0;
    let minute = Math.floor(Math.random() * 59);
    let second = Math.floor(Math.random() * 59);
    let time = new Date(date.getFullYear(), date.getMonth(), date.getDate()-differ, hour, minute, second);
    
    return time;
}

async function generateRide(driverID, vehicleID, passengerID) {
    /*console.log(typeof(driverID), typeof(vehicleID), typeof(passengerID));
    let getDriverInfo = axios.get(address+'driver/getAll/');
    let getVehicleInfo = axios.get(address+'vehicle/getAll/');
    let getPassengerInfo = axios.get(address+'passenger/getAll/');
    let info = await Promise.all([getDriverInfo, getVehicleInfo, getPassengerInfo]);
    let allDrivers = info[0].data;
    let allVehicles = info[1].data;
    let allPassengers = info[2].data;

    console.log(2);
    let driverInfo = allDrivers.find(x => x._id == driverID);
    let vehicleInfo = allVehicles.find(x => x._id == vehicleID);
    let passengerInfo = allPassengers.find(x => x._id == passengerID);
    
    console.log(driverInfo, vehicleInfo, passengerInfo);*/

    let lat, lon;
    
    lat = chance.floating({min: 23.72, max: 23.86, fixed: 9});
    lon = chance.floating({min: 90.35, max: 90.42, fixed: 9});
    let pickUpPoint = [lat, lon];

    lat = chance.floating({min: 23.72, max: 23.86, fixed: 9});
    lon = chance.floating({min: 90.35, max: 90.42, fixed: 9});
    let currentLocation = [lat, lon];
    
    let status = "ended";
    //console.log(pickUpPoint, currentLocation, status);
    let dist = calculateDistance(pickUpPoint[0], pickUpPoint[1], currentLocation[0], currentLocation[1]); 
    //let total = dist/100.0;  //change the total based on ...
    
    let time = generateRandomDate();
    let duration = (dist/1000)*7;
    duration = Math.round((duration + Number.EPSILON) * 1000) / 1000;
    
    let total = (dist/1000)*25 + duration*0.01;
    total = Math.round((total + Number.EPSILON) * 100) / 100;

    let rating = 4 + Math.random();
    rating = Math.round((rating + Number.EPSILON) * 100) / 100;
    
    let ride = {
        driverID: driverID,
        passengerID: passengerID,
        vehicleID: vehicleID,
        time: time,
        duration: duration,
        fare: total,
        distance: dist,
        source: pickUpPoint,
        destination: currentLocation,
        status: status,
        rating: rating
    };
    //console.log(ride);
    return ride;
}

async function generatedRideHistory() {
    try {
        let info, drivers = [], passengers = [];
        let allPassengers = axios.get(base_address+'api/passenger/getAll/');
        let allDrivers = axios.get(base_address+'api/driver/getAll/');
        
        info = await Promise.all([allPassengers, allDrivers]);
        passengers = info[0].data;
        drivers = info[1].data;
        
        /*passengers = [
            {
                _id: mongoose.Types.ObjectId('607478178c29c1408cfad290')
            },
            {
                _id: mongoose.Types.ObjectId('607478178c29c1408cfad292')
            }
        ];

        drivers = [
            {
                _id: mongoose.Types.ObjectId('607478178c29c1408cfad298'),
                vehicleID: mongoose.Types.ObjectId('6074779be70efe2e24c95ce8')
            },
            {
                _id: mongoose.Types.ObjectId('607478178c29c1408cfad295'),
                vehicleID: mongoose.Types.ObjectId('6074779be70efe2e24c95ce5')
            },
            {
                _id: mongoose.Types.ObjectId('607478188c29c1408cfad2b0'),
                vehicleID: mongoose.Types.ObjectId('6074779be70efe2e24c95ce6')
            },
            {
                _id: mongoose.Types.ObjectId('607478198c29c1408cfad2c1'),
                vehicleID: mongoose.Types.ObjectId('6074779be70efe2e24c95ce7')
            }
        ];*/
        let passengerCount = passengers.length
        let driverCount = drivers.length;

        let passengerIndex, driverIndex;
        let successCount = 0;
        for (let i = 0; i < 10; i++) {
            passengerIndex = Math.floor(Math.random() * passengerCount);
            driverIndex = Math.floor(Math.random() * driverCount);
            
            let passenger = passengers[passengerIndex];
            let driver = drivers[driverIndex];

            if(driver.vehicleID) {
                successCount++;
                let rideEntry = await generateRide(driver._id, driver.vehicleID, passenger._id);
                //console.log("rideEntry:\n", rideEntry);
                let savedRide = await axios.post(address+'ride/add', rideEntry);
                //console.log("savedEntry:\n", savedRide.data);
            }
        }
        console.log(successCount);
    } catch (error) {
        console.log("error ocurred", error.message);
    }
}

async function vehicleTypeUpdate() {
    try {
        let info, vehicles = [];
        info = await axios.get(address+'vehicle/getAll/');
        vehicles = info.data;

        const BUDGET = "budget";
        const ECONOMY = "economy";
        const PREMIUM = "premium";

        let vehicleType = "premium";

        for (let i = 0; i < vehicles.length; i++) {
            const vehicle = vehicles[i];
            console.log(vehicle.type);
            if(vehicle.type === "Standard") vehicleType = ECONOMY;
            else if(vehicle.type === "Premium") vehicleType = BUDGET;
            else if(vehicle.type === "Delux") vehicleType = PREMIUM;
            
            const postBody = {
                _id: vehicle._id,
                type: vehicleType
            };
            
            let savedEntry = await axios.post(address+'vehicle/typeUpdate', postBody);
            console.log(savedEntry.data);
        }
    } catch (error) {
        console.log(error.message);
    }
}

async function rateRides() {
    try {
        let info = await Promise.all([
            axios.get(address+'ride/getAll'),
            axios.get(address+'passenger/getAll')
        ])
        let rides = info[0].data.data;
        let passengers = info[1].data;
        console.log(rides[0]);
    
        for (let i = 0; i < rides.length; i++) {
            const ride = rides[i];
            const rideID = ride._id;
            const passengerID = ride.passengerID;
    
            const passenger = passengers.filter(p => p._id == passengerID)[0];
            let passengerCred = {
                email: passenger.email,
                password: passenger.password 
            };
            let loginInfo = await axios.post(address+'passenger/login', passengerCred);
            
            header_data['auth-token'] = loginInfo.headers['auth-token'];
    
            let rating = 4+Math.random();

            let updatedEntry = await axios.post(address+'passenger/ride/'+rideID, {rating: rating}, {headers: header_data});
            console.log(updatedEntry.data.rating);
        }
    } catch (error) {
        console.log(error.message);    
    }
} 

//show();
//driverCheck();
//vehicleLocationUpdate();

//vehicleTypeUpdate();

//generateRide(10, 20, 30);
generatedRideHistory();
//putDriverOnPool();
//rateRides();