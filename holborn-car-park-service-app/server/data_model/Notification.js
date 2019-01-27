const mongoose = require("mongoose");

const notificationSchema = mongoose.Schema({
    _id: mongoose.Types.ObjectId,
    title : {
        type: String,
        required : true
    },
    description : String,
    created: {
        type: Date,
        default: Date.now
    }
});
let Notification = mongoose.model("Notification", notificationSchema);
module.exports = Notification;