# WakeMeApp

###### 🚧 This app is still a WIP 🚧

###### [■■■■■■■■■□]&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;90% Completed!

## Index

- [Overview](#overview)
- [Objective](#objective)
- [Target](#target)
- [Features](#features)
- [Specification](#specification)
- [Architecture](#architecture)
- [Data Architecture](#data-architecture)
- [Contributor](#contributor)
  - [UI design](#ui-design)
- [Note](#note)

## Overview

WakeMeApp is the app for those who are often late for school, work, and anywhere you should arrive early or on time.  
This app helps you wake up on time and be able to check if your friends are awake or oversleeping.  
**NEVER let anyone call you a _SLEEPY HEAD!_**

![UI Design Image](../media/readme.png?raw=true)
Designed by [Natsumi Kobayashi](https://github.com/coooopeeeer)  
👉 [Go to see details](#contributor) 👀

## Objective

- Help users wake up on time.
- Show the sleep status depending on the set wake up time, and update it with the last-seen time.
- Allow users to send a message right after checking if his/her friends overslept.

## Target

- People who often sleep over
- People who often text their friends about their attendance.

## Features

- Set an alarm.
- Share the time you want to wake up.
- Send a notification after the time to check if the user is awake.
- Show the status if the user oversleeps.
- Show friend list in order of sleep status, with oversleeping friends listed first.
- Send and receive messages to friends.
- Store all messages.
- Track the last-seen status.
- Create an account with Email and Password.
- Facebook login.
- Configure your profile. (Name/ Profile picture/ Email/ Passward)
- Search for friends by Email.

## Specification

#### Language:
- Java
- JavaScript
#### Libraries:
- Android Support Library
- [Picasso](https://github.com/square/picasso)
- [Glide](https://github.com/bumptech/glide)
- [CircleImageView](https://github.com/hdodenhof/CircleImageView)
- [Firebase](https://firebase.google.com/)
  - Authentication
  - Cloud Storage
  - Realtime Database
  - Cloud Functions
  - Cloud Messaging
  - Crashlytics

## Architecture

✏️ TBA

## Data Architecture
#### Firebase
\* UID == Firebase Authentication user UID

```
root
  ├ ChatRoomIDList
  |  └ {UID}
  |     └ {chatRoomId}
  |        └ [User object] *See a 'Users' reference below.
  |
  ├ ChatRooms
  |  └ {chatRoomId}
  |     |[ChatRoom object]
  |     ├ id (== chatRoomId)
  |     └ memberList
  |
  ├ FriendIDList
  |  └ {UID}
  |     └ {UID}: true
  |
  ├ Messages
  |  └ {chatRoomId}
  |     └ {pushId}
  |        |[Message object]
  |        ├ id (== pushId)
  |        ├ text
  |        ├ senderId
  |        ├ createdAt
  |        └ isSeen
  |
  ├ ReceiverPaths
  |  └ {UID}
  |     └ {chatRoomId}: "xxxxxxxxxxxx"
  |
  └ Users
     └ {UID}
         |[User object]
         ├ id (== UID)
         ├ name
         ├ icon
         ├ email
         ├ lastLogin
         └ wakeUpTime
             |[WakeUpTime object]
             ├ alarmOn
             ├ repeatModeOn
             ├ mon
             ├ tue
             ├ wed
             ├ thu
             ├ fri
             ├ sat
             ├ sun
             ├ hourOfDay
             ├ minute
             └ wakeUpTimeInMillis
```

## Contributor

Designer: [Natsumi Kobayashi](https://github.com/coooopeeeer)

> ### UI Design
>
> <img src="../media/friend_list.png?raw=true" width="200px" alt="Friend List Page Image"><img src="../media/chat.png?raw=true" width="200px" alt="Chat Page Image" hspace="50"><img src="../media/mypage.png?raw=true" width="200px" alt="Mypage Image">

Thank you [@Natsumi](https://github.com/coooopeeeer) for designing all layouts of WakeMeApp. The design is really well-organized, user-friendly, and beautiful.  
When I first told you the idea, you immidiately understood the consepts and gave me a lot of advice as a designer. Whenever I asked you for additional design work, you were always flexible and dealt with my requests.  
Thank you so much for your help, and please let me know if I can return the favor!

## Note

⚠️The `google-services.json` from Firebase which is nessesary to run this app is not included in this repository for security reasons.
