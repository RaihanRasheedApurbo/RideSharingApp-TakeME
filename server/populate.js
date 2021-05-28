const Chance = require("chance");
const axios = require("axios");

const Owner = require('./model/owner');
const Driver = require('./model/driver');
const Vehicle = require('./model/vehicle');
const pointSchema = require("./model/point");

let chance = new Chance();
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

function generateRide(did, pid, vid) {
    return {
        driverID: did,
        passengerID: pid,
        vehicleID: vid,
        duration: chance.minute(),
        fare: chance.floating({min: 40, max: 500, fixed: 2}),
        source: {
            name: chance.address(),
            latitude: chance.floating({min: 23.1, max: 23.9, fixed: 7}),
            longitude: chance.floating({min: 90.1, max: 90.9, fixed: 7})
        },
        destination: {
            name: chance.address(),
            latitude: chance.floating({min: 23.1, max: 23.9, fixed: 7}),
            longitude: chance.floating({min: 90.1, max: 90.9, fixed: 7})
        }
    };
}

function ridePopulate(rideCount) {
    getDrivers = axios.get(address+'driver/getAll');
    getPassengers = axios.get(address+'passenger/getAll');

    Promise.all([getDrivers, getPassengers])
    .then(data => {
        drivers = data[0].data;
        passengers = data[1].data;
        
        for (let index = 0; index < rideCount; index++) {
            let passengerIndex = Math.floor(Math.random() * passengers.length);
            
            let passengerCred = {
                email: passengers[passengerIndex].email,
                password: passengers[passengerIndex].password  
            };

            let driverIndex = null;
            while(true) {
                driverIndex = Math.floor(Math.random() * drivers.length);
                if(drivers[driverIndex].vehicleID) break;
            }
            
            const passengerID = passengers[passengerIndex]._id;
            const driverID = drivers[driverIndex]._id; 
            const vehicleID = drivers[driverIndex].vehicleID;

            let rideInfo = generateRide(driverID, passengerID, vehicleID);
            console.log("rideInfo: ", rideInfo);
            
            axios.post(address+'passenger/login', passengerCred)
                .then(res => {
                    header_data['auth-token'] = res.headers['auth-token'];
        
                    axios.post(address+'passenger/addRide', rideInfo, {headers: header_data})
                    .then( data => {
                        console.log("success", index);
                    })
                    .catch(err => {
                        console.log(err);
                    })
                })
                .catch( err => {
                    console.log(err);
                });
        }
    })
    .catch( err=> {
        console.log(err);
    });
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
                const latitude = chance.floating({min: 23.7, max: 23.85, fixed: 9});
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

function makeDriverAvailable() {
    let count = 0;
    axios.get(address+'driver/getAll')
    .then(data => {
        drivers = data.data;
        //console.log(owners);
        for (let index = 0; index < drivers.length; index++) {
            let n = Math.floor(Math.random() * 16);
            if (n%3) continue;


            let driver = drivers[index];
            
            let driverCred = {
                email: driver.email,
                password: driver.password  
            };
            
            axios.post(address+'driver/login', driverCred)
            .then(res => {
                header_data['auth-token'] = res.headers['auth-token'];
    
                axios.get(address+'driver/search', {headers: header_data})
                .then( data => {
                    console.log("success", count++, data.data);
                })
                .catch(err => {
                    console.log("update error", err.message);
                })
            });
        }
    })
    .catch(err => {
        console.log(err);
    });
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

//driverAssign();


//get a random driver to put on the vehicle

/*let n = 40;
for (let index = 0; index < n; index++) {
    driverPopulate();
    passengerPopulate();
}*/

//show();
//ridePopulate(10);
//driverCheck();
//makeDriverAvailable();
//vehicleLocationUpdate();

/*let duration = 60;
let d = new Date();
let start = new Date(d.getFullYear(), d.getMonth(), d.getDate()-duration).toISOString();
let end = d.toISOString();

console.log(start);
console.log(end);*/
//console.log(generateOwner());