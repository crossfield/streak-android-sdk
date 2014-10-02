# Streak game Android SDK

This lightweight and open-source SDK implements a best-practices PrePlay Streak game integration.

## Overview
  This SDK contains an Android Activity used to host a WebView pointing to your game.

## Getting Started

  - Put the `streak_sdk_android_0.1.jar` into your "libs" folder
  - Add into your manifest the INTERNET permission

        <uses-permission android:name="android.permission.INTERNET" />

  - Add the activity to your manifest:

        <activity android:name="com.preplay.streak.PreplayStreakActivity"
                  android:configChanges="keyboard|orientation|screenSize|screenLayout" />

  - Setup your game id by adding into your resources:

        <string name="preplay_streak_gameid">your_app_id</string>

  **or** from your code by calling:

        PreplayStreakActivity.setGameId("your_app_id")

  - Launch this activity from your code by calling:

        startActivity(new Intent(this, PreplayStreakActivity.class));

## Example Usage
  You can look at a sample integration of the SDK in the sample/ subdirectory

## License

* [MIT](http://opensource.org/licenses/MIT)

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/preplay/streak-android-sdk/pulls).

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.