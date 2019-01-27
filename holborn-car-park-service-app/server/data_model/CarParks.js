var mongoose = require('mongoose');
var Schema = mongoose.Schema;


var carParksSchema = new  Schema({
    name: {type: String, required: true},
    price: Schema.Types.Double,
});

var CarParks = mongoose.model('CarParks', carParksSchema);
module.exports = CarParks;