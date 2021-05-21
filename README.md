# take-me-backend

back-end server for Take Me App  
[Heroku Link](http://take-me-backend.herokuapp.com "Take Me Homepage") 
`http://take-me-backend.herokuapp.com`  


## Owner Register

**API** `/api/owner/register`  
I receive  

```javascript
{
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
}
```

You receive  
`{ message: "registration successful" }`  

## Owner Login

**API** `/api/owner/login`  
I receive  

```javascript
{
    email: 'ucnovle@fa.bo',
    password: '#aCcCL5P%Nh6%X'
}
```

You receive  
`{ message: "login successful" }`  
and
`{'auth-token': token}` inside **res.headers**  

## Owner dashboard

**API** `/api/owner/dashboard`  
***This is a GET request***  
I receive  
`{'auth-token': token}` inside **req.headers**

You receive  

```javascript
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

**API** `/api/owner/vehicles`  
***This is a GET request***  
I receive  
`{'auth-token': token}` inside **req.headers**  

You receive  

```javascript
{
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
}
```

## Vehicle Info  
**API** ***GET*** `/api/owner/vehicle/id/{id}` where `{id}` should be replaced by `vehicleID` of that vehicle  
you recieve  

```javascript
{
    "vehicleInfo": {
        "_id": "6074779be70efe2e24c95ce5",
        "model": "Audi",
        "type": "Delux",
        "regNo": "253512805",
        "capacity": 2,
        "ownerID": "6074779ae70efe2e24c95cd5",
        "__v": 0,
        "driverID": "607478178c29c1408cfad295"
    },
    "data": []
}  
```  
**Optional Parameter** `driver` and `duration`  
**Example**  
`/api/owner/vehicle/id/{id}?driver={driverValue}`  
`api/owner/vehicle/id/{id}?duration={duration}`  
`/api/owner/vehicle/id/{id}?driver=true&duration={duration}`  

replace  
`{id}` by `vehicleID` of that vehicle  
`{driverValue}` by `true` or `required`  
`{duration}` by and `int` like `1`, `7` or `30`   

the `data` portion of response will contain proper response for each of the query  
***driverInfo*** will be returned as an object  
***rideHistory*** will be returned as an array object  
***driverInfo*** will be returned as an array object  

**Example**
```javascript
{
    "vehicleInfo": {
        "_id": "6074779be70efe2e24c95ce5",
        "model": "Audi",
        "type": "Delux",
        "regNo": "253512805",
        "capacity": 2,
        "ownerID": "6074779ae70efe2e24c95cd5",
        "__v": 0,
        "driverID": "607478178c29c1408cfad295"
    },
    "data": [
        {
            "rating": 5,
            "_id": "607478178c29c1408cfad295",
            "name": "Bryan Bass",
            "email": "bisuca@tuprij.tg",
            "password": "csbffihS@e53oN^]2",
            "phone": "5374897507",
            "gender": "Male",
            "address": {
                "street": "Mevawo Place",
                "city": "Zutahohu",
                "country": "Bangladesh"
            },
            "nid": "342866391",
            "licenseNo": "258451589",
            "__v": 0,
            "vehicleID": "6074779be70efe2e24c95ce5"
        },
        [
            {
                "_id": "60a8098d2d450e379c956dfc",
                "driverID": "607478178c29c1408cfad295",
                "passengerID": "607478188c29c1408cfad2b5",
                "vehicleID": "6074779be70efe2e24c95ce5",
                "duration": 44,
                "fare": 57.16,
                "source": {
                    "name": "652 Besvep Key",
                    "latitude": 23.1217021,
                    "longitude": 90.5235118
                },
                "destination": {
                    "name": "360 Etocus Grove",
                    "latitude": 23.5203627,
                    "longitude": 90.8889861
                },
                "time": "2021-05-21T19:27:09.211Z",
                "__v": 0
            }
        ],
        [
            {
                "_id": "6074779be70efe2e24c95ce5",
                "total": 57.16
            }
        ]
    ]
}
```


## Vehicle Earning

***use the one above***  

**API** `/api/ride/vehicleID`  
I receive  
either  

```javascript
{
  _id: <vehicleID>
  duration: <duration_in_days> //eg: 1 or 7 or 30
}
```

and you recieve  

```javascript
{
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
}
```

An **Array** of **Two** objects  
First object contains **Array** of ride history for last `duration` days  
Second object contains `id` of the vehicle and `total` earning  

or  

```javascript
{
  _id: <vehicleID>
}
```

This also works sending `duration` as `0` or `null`  
and you receive  

```javascript
{
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
}
```

An **Array** of **One** object  
only containing **Array** of complete ride history with `_id` `vehicleID`

## Dummy API  

**API** `/api/dummy/`  
***This is a GET request***  
I receive nothing haha  
you receive  

```javascript
{
    <object>
}
```

**API** `/api/dummy/`  
***This is a POST request***  
I receive  

```javascript
{
    <object>
}
```

you receive  

```javascript
{
    <object>
}
```

## Test
**API** `/api/owner/getAll`  
**API** `/api/ride/getAll`  
**API** `/api/passenger/getAll`  
**API** `/api/drivr/getAll`  

***These are GET requests***  
to get all the data  