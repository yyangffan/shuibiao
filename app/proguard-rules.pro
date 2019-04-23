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
-dontoptimize
-ignorewarnings
-keeppackagenames com.baidu.**
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod

-dontwarn com.baidu.**
-dontwarn com.baidu.navisdk.**
-dontwarn com.baidu.navi.**

-keep class com.baidu.** { *; }
-keep interface com.baidu.** { *; }

-keep class vi.com.gdi.** { *; }

-dontwarn com.google.protobuf.**
-keep class com.google.protobuf.** { *;}
-keep interface com.google.protobuf.** { *;}

-dontwarn com.google.android.support.v4.**
-keep class com.google.android.support.v4.** { *; }
-keep interface com.google.android.support.v4.app.** { *; }
-keep public class * extends com.google.android.support.v4.**
-keep public class * extends com.google.android.support.v4.app.Fragment
#gson混淆
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.jiayuan.shuibiao.** { *; }
#greendao混淆
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**
#EventBus混淆
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
