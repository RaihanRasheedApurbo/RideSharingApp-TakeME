const Chance = require("chance");
const axios = require("axios");

const Owner = require('./model/owner');
const Driver = require('./model/driver');

let chance = new Chance();
const address = 'http://localhost:3000/api/';

let header_data = {
    'auth-token': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDZkZWE5NTg3NDA1NTI0YjhkZjVlMDYiLCJpYXQiOjE2MTgwNzExNTZ9.j81iCN2-GLtL1PpKnZJUcVoLR8HFopCn0TWevP6yWAA'
}

function driverPopulate() {
    let driverData = {
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
    
    //console.log(driverData);
    
    axios.post(address+'driver/register', driverData)
    .then( res => {
        //console.log(res.body);
    })
    .catch( err => {
        console.log(err.message);
    });    
}

function ownerPopulate() {
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
    
    console.log(ownerData);
    
    axios.post(address+'owner/register', ownerData)
    .then( res => {
        console.log(res.body);
    })
    .catch( err => {
        console.log(err);
    });
}

function passengerPopulate() {
    let passengerData = {
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
    
    //console.log(passengerData);
    
    axios.post(address+'passenger/register', passengerData)
    .then( res => {
        //console.log(res.data);
    })
    .catch( err => {
        console.log(err.message);
    });   
}

function vehiclePopulate() {
    let vehicleData = {
        model: chance.pickone(['BMW', 'Toyota', 'Mercedes-benz', 'Audi', 'Mitshubishi']),
        type: chance.pickone(['Standard', 'Premium', 'Delux']),
        regNo: chance.ssn({ dashes: false }),
        capacity: chance.pickone([2, 4, 7])      
    };
    
    //console.log(vehicleData);
    
    axios.post(address+'vehicle/register', vehicleData, {headers: header_data})
    .then( res => {
        //console.log("res: \n", res.body);
    })
    .catch( err => {
        console.log("err: \n", err.message);
    });
}

async function ownerPopulateV2() {
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
        console.log(res.data);
        let ownerCred = {
            email: res.data.email,
            password: res.data.password  
        }
        axios.post(address+'owner/login', ownerCred)
        .then(res => {
            header_data['auth-token'] = res.headers['auth-token'];
            let n = Math.floor(Math.random() * 5)+1;
            for (let index = 0; index < n; index++) {
                vehiclePopulate();
            }
        })
        .catch( err => {
            console.log(err.message);
        });

    })
    .catch( err => {
        console.log(err);
    });
}



//driverPopulate();
//ownerPopulate();

//passengerPopulate();


//vehiclePopulate();
//get a random driver to put on the vehicle

//test();

for (let index = 0; index < 40; index++) {
    driverPopulate();
}


