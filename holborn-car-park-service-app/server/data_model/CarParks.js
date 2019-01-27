const mongoose = require('mongoose');
const Schema = mongoose.Schema;


const carParksSchema = new Schema({
    name: {type: String, required: true},
    price: Schema.Types.Double,
});

const CarParks = mongoose.model('CarParks', carParksSchema);
module.exports = CarParks;