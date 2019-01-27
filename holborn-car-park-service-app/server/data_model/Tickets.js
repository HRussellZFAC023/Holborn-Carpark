const mongoose = require('mongoose');
const Schema = mongoose.Schema;


const ticketsSchema = new Schema({
    date_in: {type: Date, default: Date.now()},
    _id_carPark: {type: Schema.Types.ObjectId, ref: "CarPark"}
});

const Tickets = mongoose.model('Tickets', ticketsSchema);
module.exports = Tickets;