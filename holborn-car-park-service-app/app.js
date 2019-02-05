const express = require('express');
const cookieParser = require('cookie-parser');

//Database
require('./server/javascripts/db_connection');
const db = require('./server/databases/carpark_db_conn');

//main routes declaration
const noApiRoutes = require('./server/routes/noapi');

//API routes declaration
const carParksRoute = require('./server/routes/api/carparks');
const ticketsRoute = require('./server/routes/api/tickets');

const app = express();

app.use(express.json());
app.use(cookieParser());
app.use(express.urlencoded({
    extended: true
}));

//main routes
app.use(express.static(__dirname + '/public/stylesheets/')); //serve stylesheets first
app.use(noApiRoutes);

//API routes
const api_resource = '/api';
app.use(api_resource + '/carparks', carParksRoute);
app.use(api_resource + '/tickets', ticketsRoute);

//handle 404 res codes
app.use(function(req, res){
    res.status(404);

    res.format({
        'text/plain': function(){
            res.send('404 nothing to show here.');
        },

        'text/html': function(){
            res.sendFile('404.html', {root: 'public/HTML/'});
        },

        'application/json': function(){
            res.send({ message: '404 nothing to show here.' });
        },

        'default': function() {
            res.status(406).send('Not Acceptable');
        }
    });
});

module.exports = app;
