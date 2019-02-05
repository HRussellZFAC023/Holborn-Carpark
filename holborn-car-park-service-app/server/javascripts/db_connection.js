let global_vars = require('./global_variables');
const debug = require('debug')('holborn-car-park-service-app: mongo');
//var fs = require('fs'); //for file certificate

const mongoose = require('mongoose');
const mongoUri = global_vars.databaseUri;

//configuring mongoose
const mongoOpt = {
            useNewUrlParser: true,
            ssl: false,
            sslValidate: false,
            //sslKey: fs.readFileSync(global_vars.sslKey),
            //sslCert: fs.readFileSync(global_vars.sslCert),
            //sslCa: fs.readFileSync('Certificates/mongodb-cert.crt')
    };

mongoose.set('useCreateIndex', true);
mongoose.set('useFindAndModify', false);

//start a database connection
mongoose.connect(mongoUri, mongoOpt).then(
    () => { debug("Connected successfully to the database!"); },
    err => { throw err;}
);
