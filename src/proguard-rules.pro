# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.oseamiya.reloadly.Reloadly {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/oseamiya/reloadly/repack'
-flattenpackagehierarchy
-dontpreverify
