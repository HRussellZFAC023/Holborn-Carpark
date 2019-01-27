// var MongoClient = require('mongodb').MongoClient;
//
// var state = {
//     db: null,
// };
//
// exports.connect = function(url, done) {
//     if (state.db) return done();
//
//     MongoClient.connect(url, function(err, client) {
//         if (err) return done(err);
//         state.db =  client.db('notif');
//         done()
//     })
// };
//
// exports.get = function() {
//     return state.db;
// };
//
// exports.close = function(done) {
//     if (state.db) {
//         state.db.close(function(err, result) {
//             state.db = null;
//             state.mode = null;
//             done(err);
//         })
//     }
// };
let global_vars = require('../javascripts/gloval_variables');
//var User = require("/public/data_model/User");
//var fs = require('fs');
var mongoose = require('mongoose');
var mongoUri = global_vars.databaseUri;
//configuring mongoose
var mongoOpt = {
            useNewUrlParser: true,
            ssl: false,
            sslValidate: false,
            //sslKey: fs.readFileSync(global_vars.sslKey),
            //sslCert: fs.readFileSync(global_vars.sslCert),
            //sslCa: fs.readFileSync('/Users/vladalboiu/Certificates/mongodb-cert.crt')
    };
mongoose.connect(mongoUri, mongoOpt, function (err) {
    if(err) throw err;
    console.log("Connected successfully to the database!");
    // var newUser = new User({
    //     username: "vl231",
    //     password: "mypasw"
    // });
    // newUser.save(function (err) {
    //     if(err) throw err;
    //     User.findOne({ username: 'vl231' }, function(err, user) {
    //         if (err) throw err;
    //         user.comparePassword('mypasw', function (err,isMatch) {
    //             if (err) throw err;
    //             console.log('mypasw', isMatch);
    //         });
    //     });
    // });

});
mongoose.set('useCreateIndex', true);
//
// var db = mongoose.connection;
// db.on('error', console.error.bind(console, 'connection error:'));
// db.once('open', function() {
//     console.log("Connected successfully to the database!");
// });
// var Notification = mongoose.model("Notification", notificationSchema);
// var newNotif = new Notification({
//         _id: new mongoose.Types.ObjectId(),
//         title:'New notification using mongoose',
//         description:'Yussssssss'
// });
// newNotif.save(function (error) {
//     if(error) throw  error;
//     console.log('Notification successfully saved.');
// });

