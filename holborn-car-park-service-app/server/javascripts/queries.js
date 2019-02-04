exports.sockets = {
   ticket_details: `SELECT tickets.date_in, tickets.paid, tickets.valid, tickets.duration, tickets.check_date_out, carparks.hour_rate 
   FROM tickets 
   INNER JOIN carparks ON tickets._carpark_id=carparks._id WHERE tickets._id =$1`
};