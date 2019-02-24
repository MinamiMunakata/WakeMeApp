// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

//// Listens for new messages added to /functions/messages/:pushId/original and creates an
//// uppercase version of the message to /functions/messages/:pushId/uppercase
//exports.makeUppercase = functions.database.ref('/functions/messages/{pushId}/original')
//    .onCreate((snapshot, context) => {
//        const original = snapshot.val();
//        console.log('Uppercasing', context.params.pushId, original);
//        const uppercase = original.toUpperCase();
//        // You must return a Promise when performing asynchronous tasks inside a Functions such as
//        // writing to the Firebase Realtime Database.
//        // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
//        return snapshot.ref.parent.child('uppercase').set(uppercase)
//    });
//
//// ------------------------

// Listens for new messages added to /Notification/{receiverId}/{chatRoomId}/{pushId}
exports.sendNotification = functions.database.ref('/Notification/{receiverId}/{chatRoomId}/{pushId}')
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
