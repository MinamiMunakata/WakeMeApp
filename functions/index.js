// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// Listens for new messages added to /Receiver/{UID}/{chatRoomId}/notification/{pushId}
exports.sendNotification = functions.database.ref('/Receivers/{UID}/{chatRoomId}/notifications/{pushId}')
    .onCreate((snap, context) => {
        const val = snap.val();
        console.log('snap', val);
        const title = val.title;
        const body = val.body;
        const topic = val.topic;

        // The topic name can be optionally prefixed with "/topics/".
        var payload = {
            notification: {
            title: title,
            body: body
            },
            topic: topic
        };

        // Send a message to devices subscribed to the provided topic.
        return admin.messaging().send(payload)
            .then((response) => {
                // Response is a message ID string.
                return console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                return console.log('Error sending message:', error);
            });
    });

