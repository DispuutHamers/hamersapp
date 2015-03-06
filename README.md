# README #
## How do I get set up? ##
How to import v7-appcompat libraries:
http://stackoverflow.com/questions/18025942/how-do-i-add-a-library-android-support-v7-appcompat-in-intellij-idea

### Requisites ###
* SDK: Android support library
* SDK: Android support repository
* SDK: Google Play Services
* SDK: Google repository
* v7-appcompat (support) libraries (as module)
* Preference fragment (v4-support) ( https://github.com/kolavar/android-support-v4-preferencefragment )


### Signing ###
http://developer.android.com/tools/publishing/app-signing.html
Passwords are on the wiki.
Signing was automated by Intellij (rather than command-line tools)

You have to download the new ProGuard (the version shipped with the SDK is from 2011..,
http://sourceforge.net/projects/proguard/files/proguard/ ) and replace [SDK]/tools/proguard with the new version.
Copy the following items from the old ProGuard dir to the new one:

* proguard-android.txt
* proguard-android-optimize.txt
* proguard-project.txt
