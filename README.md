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
- Configure your profile. (Name/ Email/ Password)
- Upload a profile picture from a device.
- Search for friends by Email.
- Send a notification to a backgrounded app when it gets a new message.
- Reset a password.

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
  
#### Test Tool:
- [Monkey](https://developer.android.com/studio/test/monkey)

## Architecture

✏️ TBA

## Data Architecture
#### Firebase
\* UID == Firebase Authentication user UID

```
root
  ├ Receivers
  |  └ {UID}
  |     └ {chatRoomId}
  |         ├ notifications
  |         |  └ {pushId}
  |         |      |[Notification object]
  |         |      ├ id (== pushId)
  |         |      ├ topic
  |         |      ├ title
  |         |      └ body
  |         |
  |         └ receiver: [User object] *See a 'Users' reference below.
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
> <img src="../media/sign_in.png?raw=true" width="100px" alt="Sign-in Page" hspace="50"><img src="../media/friend_list.png?raw=true" width="100px" alt="Friend-list Page"><img src="../media/chat.png?raw=true" width="100px" alt="Chat Page" hspace="50"><img src="../media/my_page.png?raw=true" width="100px" alt="Mypage">  
> <img src="../media/search_friend.png?raw=true" width="100px" alt="Search-friend Page" hspace="50"><img src="../media/add_friend.png?raw=true" width="100px" alt="Search-result Page"><img src="../media/add_friend_success.png?raw=true" width="100px" alt="Success-message Dialog" hspace="50"><img src="../media/Timer.png?raw=true" width="100px" alt="Set-time Page">  
> ##### Click to see a larger image.

Thank you [@Natsumi](https://github.com/coooopeeeer) for designing all layouts of WakeMeApp. The design is really well-organized, user-friendly, and beautiful.  
When I first told you the idea, you immediately understood the concepts and gave me a lot of advice as a designer. Whenever I asked you for additional design work, you were always flexible and dealt with my requests.
Thank you so much for your help, and please let me know if I can return the favor!

## Note

⚠️The `google-services.json` from Firebase which is necessary to run this app is not included in this repository for security reasons.
