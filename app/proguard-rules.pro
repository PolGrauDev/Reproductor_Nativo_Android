# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Coil 3 matches custom Fetcher.Factory/Keyer components to requests via reflection over
# each class's generic type parameter (Class.getGenericInterfaces/Signature attribute).
# R8's default rules strip that metadata and can rename these classes, which silently
# breaks album art loading in release builds (isMinifyEnabled/isShrinkResources) without
# any crash — the fetcher simply never gets picked for AlbumArtRequest.
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*

-keep class coil3.** { *; }
-keep interface coil3.** { *; }

-keep class * implements coil3.fetch.Fetcher$Factory { *; }
-keep class * implements coil3.key.Keyer { *; }
-keep class * implements coil3.decode.Decoder$Factory { *; }

-keep class com.PolGrauDev.reproductor_nativo_android.data.AlbumArtRequest { *; }
-keep class com.PolGrauDev.reproductor_nativo_android.data.AlbumArtFetcher { *; }
-keep class com.PolGrauDev.reproductor_nativo_android.data.AlbumArtFetcher$Factory { *; }
-keep class com.PolGrauDev.reproductor_nativo_android.data.AlbumArtKeyer { *; }