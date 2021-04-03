const Chance = require("chance");
const axios = require("axios");

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
    
    axios({
        method: 'post',
        url: address+'driver/register',
        data: driverData
    })
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
            country: chance.country()
        },
        nid: chance.ssn({ dashes: false }),
        vehicleList: []
    };
    
    console.log(ownerData);
    
    axios({
        method: 'post',
        url: address+'owner/register',
        data: ownerData
    })
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
            country: chance.country()
        },
        nid: chance.ssn({ dashes: false })
    };
    
    console.log(passengerData);
    
    axios({
        method: 'post',
        url: address+'passenger/register',
        data: passengerData
    })
    .then( res => {
        console.log(res.data);
    })
    .catch( err => {
        console.log(err);
    });   
}

function vehiclePopulate(oid, did) {
    let vehicleData = {
        model: chance.pickone(['BMW', 'Toyota', 'Mercedes-benz', 'Audi', 'Mitshubishi']),
        type: chance.pickone(['Standard', 'Premium', 'Delux']),
        regNo: chance.ssn({ dashes: false }),
        capacity: chance.pickone([2, 4, 7]),
        ownerID: oid,
        driverID: did        
    };
    
    console.log(vehicleData);
    
    axios({
        method: 'post',
        url: address+'vehicle/register',
        header: {
            'auth-token': eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDY4ZTUwYjA3Yzg3NzNhNjhiNDRkOTEiLCJpYXQiOjE2MTc0ODcyMzh9.Xf7AgHOgFLV0CLL6M-sexfdIB9fajWwMH-JNk0SucT4
        },
        data: vehicleData
    })
    .then( res => {
        console.log(res.data);
    })
    .catch( err => {
        console.log(err);
    });
}

//driverPopulate();
//ownerPopulate();
//passengerPopulate();
//console.log('hello');


//oid = "6068e50b07c8773a68b44d91"
//did = "6068e3183e137d3570c920c6"
//vehiclePopulate(oid, did);

