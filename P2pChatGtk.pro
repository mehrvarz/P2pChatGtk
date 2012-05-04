-injars P2pChatForGkt.jar
-injars lib/P2pChatOTR.jar
-injars lib/p2pCore.jar
-injars lib/bcprov-jdk15on-147-ext.jar
-injars lib/commons-codec-1.6.jar
-injars lib/gtk-4.1.jar
-injars lib/protobuf-java-2.3.0.jar
#-injars /home/dave/bin/scala-2.9.2-RC3/lib/scala-library.jar
-injars /usr/share/java/scala-library.jar
#-libraryjars /home/dave/bin/scala-2.9.2-RC3/lib/scala-library.jar
#-libraryjars /usr/share/java/scala-library.jar
-libraryjars <java.home>/lib/rt.jar
-libraryjars <java.home>/lib/jce.jar
-outjars P2pChatGtk.jar

-keep public class timur.p2pChat.P2pChatGtk {
    public static void main(java.lang.String[]);
}

-dontwarn scala.**
-dontwarn ext.org.bouncycastle.crypto.params.**

-keep public class org.freedesktop.** {*;}
-keep public class org.gnome.** {*;}
-keepclassmembers class org.gnome.** { *; }
-keepclassmembers class org.freedesktop.** { *; }
#-keep public class org.gnome.glib.Object {*;}
#-keep public class org.gnome.gtk.TextTag
-keepclassmembers class org.gnome.gtk.TextTag { double SCALE; }
-keepclassmembers class org.gnome.atk.AtkTextAttribute { int SCALE; }
-keepclassmembers class org.gnome.pango.Pango { double SCALE; }
-keepclassmembers class org.gnome.pango.PangoAttrType { int SCALE; }
-keepclassmembers class org.gnome.unixprint.UnixprintPrintCapabilities { int SCALE; }

-dontobfuscate
-dontoptimize
-optimizationpasses 3
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-dontskipnonpubliclibraryclassmembers
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*



############################################################### scala
-dontnote scala.Enumeration
-dontnote org.xml.sax.EntityResolver
-dontnote org.apache.james.mime4j.storage.DefaultStorageProvider

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,
                SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
    ** MODULE$;
}

-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinPool {
    long eventCount;
    int  workerCounts;
    int  runControl;
    scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode syncStack;
    scala.concurrent.forkjoin.ForkJoinPool$WaitQueueNode spareStack;
}

-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinWorkerThread {
    int base;
    int sp;
    int runState;
}

-keepclassmembernames class scala.concurrent.forkjoin.ForkJoinTask {
    int status;
}

-keepclassmembernames class scala.concurrent.forkjoin.LinkedTransferQueue {
    scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference head;
    scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference tail;
    scala.concurrent.forkjoin.LinkedTransferQueue$PaddedAtomicReference cleanMe;
}

