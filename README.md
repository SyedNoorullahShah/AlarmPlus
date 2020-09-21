# Alarm Plus

## About this project
Alarm Plus is an android alarm clock app for heavy sleepers which, instead of a stop button, lets users dismiss the alarm either by playing a tic-tac-toe or by walking some steps.

## Built using
- **Tools:** Android SDK
- **IDE:** Android Studio
- **Language:** Java
- **Libraries:-**
  - **Database:** [Realm](https://realm.io/blog/realm-for-android/)
  - **Memory Leak Detection:** [LeakCanary](https://github.com/square/leakcanary)
  - **Alarm Scheduling:-** [AlarmManager](https://developer.android.com/reference/android/app/AlarmManager)

## App Functionalities
The user can:-
- Set basic alarm settings:- 
  - Set ringtone 
  - Set Alarm label text 
  - enable/disable snooze
- As well as select one of the two alarm dismiss mode options:
  1. **Game mode:** Set **difficulty level** and **number of rounds**
  2. **Step Counter mode:** Set **number of steps** user has to take for stopping the alarm
  
### App Screenshots
[These screenshots ](https://drive.google.com/drive/folders/1NC33JKtK4bBbliuFCLxWD3I7hqGGYi7e?usp=sharing)show the overview of the app flow.

## TODO
Reschedule alarms of the AlarmManager that were removed due to system reboot.
