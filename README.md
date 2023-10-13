# HomeWeather

HomeWeather is an android mobile sensor-based application utilizing DHT22 temperature-humidity sensor. This is a project developed under Metropolia University of Applied Sciences. For further information, please see below or contact one of the developers.

## Table of Contents
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [Features](#features)
- [Components](#components)
- [Application Flow](#application-flow)
- [Technology](#technology)
- [Contributing](#contributing)
- [Video Demo](#video-demo)
- [Thumbnails](#thumbnails)

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) installed on your machine
- Android device or emulator for testing

OR 
- Android phone
- Arduino board MKR1010 and DHT22 sensor. Please check this documentation for extra information:
  1. [Arduino MKR1010](https://docs.arduino.cc/hardware/mkr-wifi-1010)
  2. [DHT22 with Arduino](https://www.instructables.com/How-to-use-DHT-22-sensor-Arduino-Tutorial/)

### Installation

1. Clone the repository to your local machine using `git clone`.
2. Open the project in Android Studio.
3. Build and run the project on your Android device or emulator.
4. Contact a developer for env file

OR

- Install this [APK File](https://drive.google.com/file/d/1GehuS3wvuHBRAyPQAe7n5OE1hLzgRoBW/view?usp=drive_link) to your android phone

## Usage

HomeWeather is recommended to be used as a stationary device at your home, office or room. 

## Features

HomeWeather allows users to:

- Measure humidity level and temperature inside a room with an external bluetooth sensor
- Compare the humidity levels and temperatures inside your room with your living area with GPS internal sensor and data fetched from the OpenWeather API.
- Allow users to see humidity level and temperature statistic over a certain period.
- Voice alert + emergency call to users when inside humidity level or temperature drops too low or rises too high and allow user to add additional info on every alert log.
- Allow users to scan QR code to view our github page and download the app apk
- Application is available in English and Finnish

## Components

- Jetpack Compose
- Firebase Firestone
- Material Design 3
- Bluetooth Connection
- Foreground Service
- Animation
- QR generator
- External Arduino board with DHT22 sensor
- Open Weather API
- Voice Alert
- TexttoSpeech API
- GPS lat & long
- SharedPreference
- Emergency call
- Env file to prevent sensitive data exposure

## Application flow

- Bluetooth Service is running persistently on background even when application is closed
- Indoor temperature and humidity level are uploaded to Firebase cloud in background every hour
- Outdoor temperature and humidity level are fetched and updated hourly

## Technology

- Android Kotlin
- C/C++

## Contributing

Contributions are welcome! Here's how you can contribute to the project:

1. Fork the repository on GitHub.
2. Clone the forked repository to your local machine.
3. Make changes and commit them with descriptive commit messages.
4. Push your changes to your fork on GitHub.
5. Submit a pull request to the original repository explaining your changes.

## Video Demo

Click the video to watch

[![](https://img.youtube.com/vi/wbvtO4DTXSM/maxresdefault.jpg)](https://www.youtube.com/watch?v=wbvtO4DTXSM&ab_channel=GiaoNg%C3%B4)

## Thumbnails
<img src="https://github.com/giaongo/HomeWeather/assets/91269635/f151933e-81b4-447f-a004-2fddfc37c3db" height="500"/>
<img src="https://github.com/giaongo/HomeWeather/assets/91269635/5b11f8a8-8d7d-4e6a-87c1-35dbd50b11c6" height="500"/>
<img src="https://github.com/giaongo/HomeWeather/assets/91269635/1ed14521-c287-4d41-9798-b7f8718aa062" height="500"/>
<img src="https://github.com/giaongo/HomeWeather/assets/91269635/dd583a9d-f84a-4203-85cc-976677e60040" height="500"/>
<img src="https://github.com/giaongo/HomeWeather/assets/91269635/bc8b4a84-38e9-40b0-8ef6-e37ed691a5ab" height="500"/>
<img src="https://github.com/giaongo/HomeWeather/assets/91269635/65a67901-93c9-4d43-9f44-d2d0ff7899a9" height="500"/>
<img src="https://github.com/giaongo/HomeWeather/assets/91269635/2ab6eb65-4828-4407-b438-e16e3f67a49b" height="500"/>




