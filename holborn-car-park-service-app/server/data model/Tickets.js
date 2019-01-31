const mongoose = require('mongoose');
const Schema = mongoose.Schema;


const ticketsSchema = new  Schema({
    date_in : Date,
    date_check_out : Date,
    date_out: Date,
    duration:{type: Number, default: 0},
    price:  {type: Number, default: 0},
    paid: {type: Boolean, default: false},
    valid: {type: Boolean, default: true},
    _id_carPark: {type:Schema.Types.ObjectId, ref: "CarPark"}
});

const Tickets = mongoose.model('Tickets', ticketsSchema);
module.exports = Tickets;