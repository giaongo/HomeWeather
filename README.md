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

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) installed on your machine
- Android device or emulator for testing

OR 
- Android phone

### Installation

1. Clone the repository to your local machine using `git clone`.
2. Open the project in Android Studio.
3. Build and run the project on your Android device or emulator.
4. Contact a developers for env file

OR

- Install [Insert APK file here]

## Usage

HomeWeather is recommended to be used as a stationary device at your home, office or room. 

## Features

HomeWeather allows users to:

- Measure humidity level and temperature inside a room with an external bluetooth sensor
- Compare the humidity levels and temperatures inside your room with your living area with GPS internal sensor and data fetched from the OpenWeather API.
- Allow users to see humidity level and temperature statistic over a certain period.
- Voice alert users when inside humidity level or temperature drops too low or rises too high and allow user to add additional info on every alert log.
- Allow users to scan QR code to view our github page and download the app apk

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
Link to video here


