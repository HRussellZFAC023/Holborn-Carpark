var mongoose = require('mongoose');
var Schema = mongoose.Schema;


var ticketsSchema = new  Schema({
    date_in : {type: Date, default: Date.now()},
    _id_carPark: {type:Schema.Types.ObjectId, ref: "CarPark"}
});

var Tickets = mongoose.model('Tickets', ticketsSchema);
module.exports = Tickets;