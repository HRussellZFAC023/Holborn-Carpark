const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const db_connection = require('./server/javascripts/db_connection');
//main routes declaration


//API routes declaration
const carParksRoute = require('./server/routes/api/carparks');
const ticketsRoute = require('./server/routes/api/tickets');

var app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

//main routes
app.use('/',function(req, res){
    res.sendFile(__dirname + '/public/HTML/index.html');
});

//API routes
const api_name = '/api';
app.use(api_name + '/carparks', carParksRoute);
app.use(api_name + '/tickets', ticketsRoute);

module.exports = app;
