-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field
-keep class android.support.v7.widget.SearchView { *; }
-keep public class * extends android.support.v7.preference.Preference
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
# Design library
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**
-keep class .R
-keep class **.R$* { <fields>; }
-dontobfuscate

##---------------Begin: proguard configuration for Gson ----------
-keep public class com.google.gson.**
-keep public class com.google.gson.** { public private protected *; }
-keepattributes *Annotation*,Signature
-keep class com.mypackage.ActivityMonitor.ClassMultiPoints.** { *; }
-keep public class nl.ecci.hamers.users.ActivityMonitor$ClassMultiPoints     { public protected *; }
-keep public class nl.ecci.hamers.users.ActivityMonitor$ClassMultiPoints$ClassPoints { public protected *; }
-keep public class nl.ecci.hamers.users.ActivityMonitor$ClassMultiPoints$ClassPoints$ClassPoint { public protected *; }
# To support Enum type of class members
-keepclassmembers enum * { *; }
##---------------End: proguard configuration for Gson ----------
