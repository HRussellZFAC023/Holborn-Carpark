const global_vars = require('../javascripts/gloval_variables');
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
    () => { console.log("Connected successfully to the database!"); },
    err => { throw err;}
);
