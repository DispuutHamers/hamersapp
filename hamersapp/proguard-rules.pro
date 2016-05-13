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
# Android-Iconics library
-keep class .R
-keep class **.R$* {
    <fields>;
}