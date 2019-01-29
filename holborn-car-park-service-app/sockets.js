const verif = require("./server/javascripts/verify");
const sockets = {};

sockets.init = function (server) {
    // socket.io setup
    const io = require('socket.io').listen(server);
    io.sockets.on('connection', function (socket) {
        console.log('Socket connected: ' + socket.handshake.address);
        socket.on('authorise', function (_idCarPark) {
            verif.VerifyClientAuth(_idCarPark, function (code, desc) {
                console.log("Auth: code: "+ code + " desc: " + desc);
                if(code !== 200) socket.disconnect();
            });
        });


    });
    io.sockets.on('hi', function () {
        console.log("hi");
    });
    io.sockets.on('disconnect', function (socket) {
        console.log('Socket disconnected: ' +  socket.handshake.address);

    });

};

module.exports = sockets;