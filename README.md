# WakeMeApp

🚧 This app is still a WIP 🚧

## Overview

WakeMeApp is the app for those who are often late for school, work, and anything you should be there before or on time.  
This app lets you wake up on time and be able to check if your friends get up or sleep over.  
**NEVER let anyone call you a _SLEEPY HEAD!_**

## Objective

- Let the user wake up on time.
- Show the sleep-over status depends on the time to wake up the user set and update it with the last-seen time.
- Allow the user to send a message right after checking if his/her friends sleep over.

## Target

- People who often sleep over
- People who often text their friend's about the attendance.

## Features

- Set an alarm.
- Share the time you want to wake up.
- Send a notification after the time to check if the user get up.
- Show the status if the user sleep over.
- Show friend list in order which the user who doesn't get up yet comes first.
- Send and receive messages to friend-users.
- Store all messages.
- Track the last seen status.
- Create an account with Email and Passward.
- Facebook login.
- Configure your profile. (Name/ Profile picture/ Email/ Passward)
- Search friend-users by Email.

## Specification

Language: Java  
Libraries:

- Android Support Library
- [Picasso](https://github.com/square/picasso)
- [CircleImageView](https://github.com/hdodenhof/CircleImageView)
- [Firebase](https://firebase.google.com/)
  - Authentication
  - Cloud Storage
  - Realtime Database

## Architecture

✏️ TBA

## Data Architecture

✏️ WIP

##### Firebase

```
root
  ├ xxxx
  |  └ xxxx
  |     └ xxxx
  |
  ├ xxxx
  |  └ xxxx
  |     └ xxxx
  |        ├ xxxx
  |        ├ xxxx
  |        ├ xxxx
  |        ├ xxxx
  |        ├ xxxx
  |        └ xxxx
  |
  ├ xxxx
  |  └ xxxx
  |     └ xxxx
  |        ├ xxxx
  |        ├ xxxx
  |        └ xxxx
  |
  └ xxxx
     └ xxxx
        └ xxxx
           ├ xxxx
           ├ xxxx
           ├ xxxx
           ├ xxxx
           ├ xxxx
           └ xxxx
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
