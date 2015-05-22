# README #
## How do I get set up? ##
### Requisites ###
* SDK: Android support library
* SDK: Android support repository
* SDK: Google Play Services
* SDK: Google repository
* Gradle

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
