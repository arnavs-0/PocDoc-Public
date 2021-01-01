[![Logo](/.github/Untitled%20design%20(3).png)](https://github.com/arnavs-0/PocDoc)

<h1 align="center">Welcome to PocDoc 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-1.0-blue.svg?cacheSeconds=2592000" />
  <a href="https://github.com/arnavs-0/PocDoc-Public" target="_blank">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" />
  </a>
  <a href="https://www.gnu.org/licenses/gpl-3.0.en.html" target="_blank">
    <img alt="License: GPL--3.0" src="https://img.shields.io/badge/License-GPL--3.0-yellow.svg" />
  </a>
</p>

> A One Stop Medical Assistant. Features include: Diary, Symptom Checker, Allergy Finder, Hospital/Pharmacy Finder, and Medication Tracker. Implemented with the use of Firebase, Tensorflow + Keras, and Clarifai.

### 🏠 [Homepage](https://arnavs-0.github.io/PocDoc-Client/)

### ✨ [Demo](https://www.youtube.com/watch?v=5lS15bdPwvI)

## Install

There is 2 ways to install PocDoc.
* **Method 1 (Recommended)**
    * In *Releases* Download the APK
    * Drag and Drop the APK in the emulator or use this [guide](https://www.wikihow.com/Install-APK-Files-from-a-PC-on-Android) to install on a Android Device (Version 6.0 or above)

* **Method 2**
    * Fork the Repo
    * Clone the Repo
    ```git clone https://github.com/GITHUB_USERNAME/PocDoc-Public.git```
    * **Set Up Firebase**
        * Use the [Firebase Docs](https://firebase.google.com/docs/android/setup#console) to set up Firebase in your Project and Add it to Android Studio
            * **Note: Step 4 IS Required** Make sure to add your SHA-1 Key in Firebase: [Learn More on How to Find Your SHA-1 Key Here](https://stackoverflow.com/a/34223470/13826785)
        * Enable Firebase ```Authentication``` in your Console and Enable ```Email/Password Sign In``` and ```Sign In with Google```
        * Enable ```Realtime Database``` in your Console and start in ```Debug Mode```
    * **Set Up PocDoc API**
        * Follow the PocDoc API Guide [Here](https://github.com/arnavs-0/PocDoc-API#install)
        * In ```styles.xml``` Change this line:
            ```XML 
            <string name="base_api_url">YOUR_API_URL_HERE</string>
            ```
    * **Set Up Clarifai**
        * In [Clarifai](https://www.clarifai.com/) gather your model id and credentials
        * In ```FoodAllergyResult.java``` change these lines:
        ```Java
        V2Grpc.V2BlockingStub stub = V2Grpc.newBlockingStub(channel)
                    .withCallCredentials(new ClarifaiCallCredentials("YOUR_CLARIFAI_CREDENTIALS"));
        ```
        And this line:
        ```Java
        PostModelOutputsRequest.newBuilder()
                                .setModelId("YOUR_MODEL_ID")
        ```
    * **Set Up Maps API**
        * In [Google Cloud Platform](https://cloud.google.com/) Enable ```Android SDK``` & ```Places API``` for Google Maps
        * Follow this [guide](https://developers.google.com/maps/documentation/android-sdk/get-api-key) to get your API Key.
        * Replace this line in ```APIQuery.java```:
        ```Java
         private static String my_api_key="YOUR_API_KEY";
        ```
    * Run The Project


## Authors

👤 **Arnav Shah & Vishal Dattathreya**

* Github: [@arnavs-0](https://github.com/arnavs-0)
* Github: [@cmdvmd](https://github.com/cmdvmd)

## 🤝 Issues

Contributions, issues and feature requests are welcome!<br />Feel free to check [issues page](https://github.com/arnavs-0/PocDoc-Public/issues). 

## Show your support

Give a ⭐️ if this project helped you!

## Tools

* [Gradle](https://gradle.org/) - Dependency Management
* [Android Studio](https://developer.android.com/studio) - IDE
* [Firebase](https://rometools.github.io/rome/) - Backend Structures
* [Google ML](https://developers.google.com/ml-kit) - AI and ML

[![forthebadge](https://forthebadge.com/images/badges/built-for-android.svg)](https://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)
[![ForTheBadge built-by-developers](http://ForTheBadge.com/images/badges/built-by-developers.svg)](https://GitHub.com/arnavs-0/)

## 📝 License

Copyright © 2020-2021 [Arnav Shah](https://github.com/arnavs-0) & [Vishal Dattathreya](https://github.com/cmdvmd).<br />
This project is [GPL-3.0](https://www.gnu.org/licenses/gpl-3.0.en.html) licensed.
