const Chance = require("chance");
const axios = require("axios");

const Owner = require('./model/owner');

let chance = new Chance();
const address = 'http://localhost:3000/api/';

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
    
    console.log(driverData);
    
    axios.post(address+'driver/register', driverData)
    .then( res => {
        console.log(res.data);
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
        console.log(res.data);
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
    
    console.log(passengerData);
    
    axios.post(address+'passenger/register', passengerData)
    .then( res => {
        console.log(res.data);
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
    
    console.log(vehicleData);
    
    axios.post(address+'vehicle/register', vehicleData, {headers: header_data})
    .then( res => {
        console.log("res: \n", res.data);
    })
    .catch( err => {
        console.log("err: \n", err.message);
    });
}


function test() {
    Owner.find({})
    .then( data => {
        console.log(data);
        console.log("hello");
        console.log(data.length);
    })
    .catch( err => {
        console.log("err", err);
    });
}

//driverPopulate();
//ownerPopulate();

//passengerPopulate();

const header_data = {
    'auth-token': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDZkZWE5NTg3NDA1NTI0YjhkZjVlMDYiLCJpYXQiOjE2MTgwNzExNTZ9.j81iCN2-GLtL1PpKnZJUcVoLR8HFopCn0TWevP6yWAA'
}
//vehiclePopulate();
//get a random driver to put on the vehicle

test();
