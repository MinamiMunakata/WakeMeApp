// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path functions/messages/:pushId/original
exports.addMessage = functions.https.onRequest((req, res) => {
    // Grab the text parameter.
    const original = req.query.text;
    // Push the new message into the Realtime Database using the Firebase Admin SDK.
    return admin.database().ref('functions/messages').push({original: original}).then((snapshot) => {
        // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
        return res.redirect(303, snapshot.ref.toString());
    });
});

// Listens for new messages added to /functions/messages/:pushId/original and creates an
// uppercase version of the message to /functions/messages/:pushId/uppercase
exports.makeUppercase = functions.database.ref('/functions/messages/{pushId}/original')
    .onCreate((snapshot, context) => {
        const original = snapshot.val();
        console.log('Uppercasing', context.params.pushId, original);
        const uppercase = original.toUpperCase();
        // You must return a Promise when performing asynchronous tasks inside a Functions such as
        // writing to the Firebase Realtime Database.
        // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
        return snapshot.ref.parent.child('uppercase').set(uppercase)
    });

// ------------------------
/*
    TODO:
    1. Observe firebase realtime database if a new message is added.
    2. if 1 == true -> check if it is seen.
    3. if 2 == false -> send a notification.
    */
/*
    TODO:
    1. Modify architecture. You may need a notification node in realtime database.
*/
exports.sendNotification = functions.database.ref('/functions/messages/{pushId}/original')
    .onCreate((snap, context) => {
//        const val = event.data.val();
//        console.log('helloWorld', val);
        console.log('helloWorld');
        // The topic name can be optionally prefixed with "/topics/".
        var topic = 'messages';

        var payload = {
            notification: {
            title: "新着メッセージ", // この辺りを動的に取ってくるケースが多いかな？
            body: "新着メッセージがあります"
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
