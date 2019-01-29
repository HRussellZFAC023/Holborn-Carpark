const mongoose = require('mongoose');
const Schema = mongoose.Schema;

//Create a car park schema
const carParksSchema = new  Schema({
    name: {type: String, required: true},
    hourly_price: Number,
    address: String,
    postcode: String
});

const CarParks = mongoose.model('CarPark', carParksSchema);
module.exports = CarParks;