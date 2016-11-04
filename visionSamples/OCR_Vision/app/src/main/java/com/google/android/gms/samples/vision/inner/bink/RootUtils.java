package com.google.android.gms.samples.vision.inner.bink;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by hi on 24/05/16.
 */
public class RootUtils {
    public static boolean isDeviceRooted() {
        return canExecuteSuCommand() || hasSuperuserApk() || isTestKeyBuild() || hasAccess2SuDirs();
    }

    private static boolean canExecuteSuCommand(){
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    private static boolean hasSuperuserApk(){
        return new File("/system/app/Superuser.apk").exists();
    }

    private static boolean isTestKeyBuild(){
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean hasAccess2SuDirs() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su" };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }
}
