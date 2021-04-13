# take-me-backend
back-end server for Take Me App

## Owner Register
**API** `/api/owner/register`<br>
I receive <br>
```
name: 'Douglas Berry',
email: 'ucnovle@fa.bo',
password: '#aCcCL5P%Nh6%X',
phone: '3225711537',
gender: 'Male',
address: {
  street: 'Zede Square',
  city: 'Lahizgun',
  country: 'Falkland Islands'
},
nid: '923896883',
vehicleList: [] //empty array
```
You receive
`{ message: "registration successful" }`
</p>

## Owner Login
**API** `/api/owner/login`<br>
I receive <br>
```
email: 'ucnovle@fa.bo',
password: '#aCcCL5P%Nh6%X',
```
You receive <br>
`{ message: "login successful" }`
and
`{'auth-token', token}` inside **res.headers**


## Owner dashboard
**API** `/api/owner/dashboard`<br>
I receive <br>
`{'auth-token', token}` inside **res.headers**

You receive <br>
```
{
    "vehicleList": [
        "6074779be70efe2e24c95cdb",
        "6074779be70efe2e24c95cdc"
    ],
    "_id": "6074779ae70efe2e24c95cd1",
    "name": "Henry McCoy",
    "email": "kevdeh@cebsukhaw.iq",
    "password": "PxNzO6cv^0jE!mZ@v",
    "phone": "5027884900",
    "gender": "Female",
    "address": {
        "street": "Fohdo Turnpike",
        "city": "Fiwmopoj",
        "country": "Paraguay"
    },
    "nid": "822626239",
    "__v": 0
}
```

## Owner vehicles
**API** `/api/owner/vehicles`<br>
I receive <br>
`{'auth-token', token}` inside **res.headers**

You receive <br>
```
[
    {
        "_id": "6074779be70efe2e24c95cdb",
        "model": "Toyota",
        "type": "Premium",
        "regNo": "111503145",
        "capacity": 2,
        "ownerID": "6074779ae70efe2e24c95cd1",
        "__v": 0,
        "driverID": "607478188c29c1408cfad29c"
    },
    {
        "_id": "6074779be70efe2e24c95cdc",
        "model": "Mitshubishi",
        "type": "Standard",
        "regNo": "751750906",
        "capacity": 4,
        "ownerID": "6074779ae70efe2e24c95cd1",
        "__v": 0,
        "driverID": "607478198c29c1408cfad2d8"
    }
]
```


## Vehicle Earning
***This part will slightly change based on jwt token and api***

**API** `/api/ride/vehicleID`
I receive <br>
either
```
{
  _id: <vehicleID>
  duration: <duration_in_days> //eg: 1 or 7 or 30
}
```
and you recieve
```
[
    [
        {
            "_id": "6074c19a4fad381308cd46e9",
            "driverID": "607478178c29c1408cfad291",
            "passengerID": "607478198c29c1408cfad2d3",
            "vehicleID": "6074779be70efe2e24c95cdb",
            "duration": 9,
            "fare": 97.81,
            "source": {
                "name": "511 Guil Point",
                "latitude": 23.3652892,
                "longitude": 90.1464935
            },
            "destination": {
                "name": "1423 Fezmo Place",
                "latitude": 23.4368054,
                "longitude": 90.7679393
            },
            "time": "2021-04-12T21:54:34.533Z",
            "__v": 0
        }
    ],
    [
        {
            "_id": "6074779be70efe2e24c95cdb",
            "total": 97.81
        }
    ]
]
```
An **Array** of **Two** objects  <br>
First object contains **Array** of ride history for last `duration` days  <br>
Second object contains `id` of the vehicle and `total` earning  <br>

or  <br>
```
{
  _id: <vehicleID>
}
```
This also works sending `duration` as `0` or `null` <br>
and you receive <br>
```
[
    {
        "_id": "6074c19a4fad381308cd46e9",
        "driverID": "607478178c29c1408cfad291",
        "passengerID": "607478198c29c1408cfad2d3",
        "vehicleID": "6074779be70efe2e24c95cdb",
        "duration": 9,
        "fare": 97.81,
        "source": {
            "name": "511 Guil Point",
            "latitude": 23.3652892,
            "longitude": 90.1464935
        },
        "destination": {
            "name": "1423 Fezmo Place",
            "latitude": 23.4368054,
            "longitude": 90.7679393
        },
        "time": "2021-04-12T21:54:34.533Z",
        "__v": 0
    }
]
```
An **Array** of **One** object <br>
only containing **Array** of ride history for last `duration` days
