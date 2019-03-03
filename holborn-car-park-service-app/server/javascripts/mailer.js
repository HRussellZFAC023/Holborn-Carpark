const mailer = require("nodemailer");

/**
 * Set Transport protocol to SMTP
 */
module.exports.smtpTransport = mailer.createTransport({
    service: "Gmail",
    auth: {
        user: "holbornreporting@gmail.com",
        pass: "&EaY89gWD"
    }
});



// mail = {
//     from: 'Holborn auto reports <holbornreporting@gmail.com>',
//     to: 'holbornreporting@gmail.com',
//     subject: '',
//     html: ''
// };
//
// smtpTransport.sendMail(mail, function(error, response){
//     if(error){
//         console.log(error);
//     }else{
//         console.log("Message sent: " + response.message);
//     }
//
//     smtpTransport.close();
// });