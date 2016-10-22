/*****************************************************************************
 * Util.java
 *****************************************************************************
 * Copyright © 2011-2012 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.lau.vlcdemo.R;

public class Util {
    public final static String TAG = "VLC/Util";
    public final static boolean hasNavBar;
    /** A set of utility functions for the VLC application */

    static {
        HashSet<String> devicesWithoutNavBar = new HashSet<String>();
        devicesWithoutNavBar.add("HTC One V");
        devicesWithoutNavBar.add("HTC One S");
        devicesWithoutNavBar.add("HTC One X");
        devicesWithoutNavBar.add("HTC One XL");
        hasNavBar = isICSOrLater() && !devicesWithoutNavBar.contains(android.os.Build.MODEL);
    }

    /** Print an on-screen message to alert the user */
    public static void toaster(Context context, int stringId, int duration) {
        Toast.makeText(context, stringId, duration).show();
    }

    public static void toaster(Context context, int stringId) {
        toaster(context, stringId, Toast.LENGTH_SHORT);
    }

    public static File URItoFile(String URI) {
        return new File(Uri.decode(URI).replace("file://",""));
    }

    public static String URItoFileName(String URI) {
        int sep = URI.lastIndexOf('/');
        int dot = URI.lastIndexOf('.');
        String name = dot >= 0 ? URI.substring(sep + 1, dot) : URI;
        return Uri.decode(name);
    }

    public static String PathToURI(String path) {
        String URI;
        try {
            URI = LibVLC.getInstance().nativeToURI(path);
        } catch (LibVlcException e) {
            URI = "";
        }
        return URI;
    }

    public static String stripTrailingSlash(String _s) {
        String s = _s;
        if( s.endsWith("/") && s.length() > 1 )
            s = s.substring(0,s.length()-1);
        return s;
    }

    public static String readAsset(String assetName, String defaultS) {
        try {
            InputStream is = VLCApplication.getAppResources().getAssets().open(assetName);
            BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF8"));
            StringBuilder sb = new StringBuilder();
            String line = r.readLine();
            if(line != null) {
                sb.append(line);
                line = r.readLine();
                while(line != null) {
                    sb.append('\n');
                    sb.append(line);
                    line = r.readLine();
                }
            }
            return sb.toString();
        } catch (IOException e) {
            return defaultS;
        }
    }

    /**
     * Convert time to a string
     * @param millis e.g.time/length from file
     * @return formated string (hh:)mm:ss
     */
    public static String millisToString(long millis) {
        boolean negative = millis < 0;
        millis = java.lang.Math.abs(millis);

        millis /= 1000;
        int sec = (int) (millis % 60);
        millis /= 60;
        int min = (int) (millis % 60);
        millis /= 60;
        int hours = (int) millis;

        String time;
        DecimalFormat format = (DecimalFormat)NumberFormat.getInstance(Locale.US);
        format.applyPattern("00");
        if (millis > 0) {
            time = (negative ? "-" : "") + hours + ":" + format.format(min) + ":" + format.format(sec);
        } else {
            time = (negative ? "-" : "") + min + ":" + format.format(sec);
        }
        return time;
    }

    public static Bitmap scaleDownBitmap(Context context, Bitmap bitmap, int width) {
        if (bitmap != null) {
            final float densityMultiplier = context.getResources().getDisplayMetrics().density;
            int w = (int) (width * densityMultiplier);
            int h = (int) (w * bitmap.getHeight() / ((double) bitmap.getWidth()));
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
        }
        return bitmap;
    }

    public static Bitmap cropBorders(Bitmap bitmap, int width, int height)
    {
        int top = 0;
        for (int i = 0; i < height / 2; i++) {
            int pixel1 = bitmap.getPixel(width / 2, i);
            int pixel2 = bitmap.getPixel(width / 2, height - i - 1);
            if ((pixel1 == 0 || pixel1 == -16777216) &&
                (pixel2 == 0 || pixel2 == -16777216)) {
                top = i;
            } else {
                break;
            }
        }

        int left = 0;
        for (int i = 0; i < width / 2; i++) {
            int pixel1 = bitmap.getPixel(i, height / 2);
            int pixel2 = bitmap.getPixel(width - i - 1, height / 2);
            if ((pixel1 == 0 || pixel1 == -16777216) &&
                (pixel2 == 0 || pixel2 == -16777216)) {
                left = i;
            } else {
                break;
            }
        }

        if (left >= width / 2 - 10 || top >= height / 2 - 10)
            return bitmap;

        // Cut off the transparency on the borders
        return Bitmap.createBitmap(bitmap, left, top,
                (width - (2 * left)), (height - (2 * top)));
    }

    public static String getValue(String string, int defaultId)
    {
        return (string != null && string.length() > 0) ?
                string : VLCApplication.getAppContext().getString(defaultId);
    }

    public static void setItemBackground(View v, int position) {
        v.setBackgroundResource(position % 2 == 0
                ? R.drawable.background_item1
                : R.drawable.background_item2);
    }

    public static int convertPxToDp(int px) {
        WindowManager wm = (WindowManager)VLCApplication.getAppContext().
                getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        float logicalDensity = metrics.density;
        int dp = Math.round(px / logicalDensity);
        return dp;
    }

    public static int convertDpToPx(int dp) {
        return Math.round(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                VLCApplication.getAppResources().getDisplayMetrics())
                         );
    }

    public static boolean isGingerbreadOrLater()
    {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean isHoneycombOrLater()
    {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isICSOrLater()
    {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasNavBar()
    {
        return hasNavBar;
    }

    private static String errorMsg = null;
    private static boolean isCompatible = false;
    public static String getErrorMsg() {
        return errorMsg;
    }
    public static boolean hasCompatibleCPU()
    {
        // If already checked return cached result
        if(errorMsg != null) return isCompatible;

        Properties properties = new Properties();
        try {
            properties.load(new ByteArrayInputStream(Util.readAsset("env.txt", "").getBytes("UTF-8")));
        } catch (IOException e) {
            // Shouldn't happen if done correctly
            e.printStackTrace();
            errorMsg = "IOException whilst reading compile flags";
            isCompatible = false;
            return false;
        }

        String CPU_ABI = android.os.Build.CPU_ABI;
        String CPU_ABI2 = "none";
        if(android.os.Build.VERSION.SDK_INT >= 8) { // CPU_ABI2 since 2.2
            try {
                CPU_ABI2 = (String)android.os.Build.class.getDeclaredField("CPU_ABI2").get(null);
            } catch (Exception e) { }
        }

        String ANDROID_ABI = properties.getProperty("ANDROID_ABI");
        boolean NO_NEON = properties.getProperty("NO_NEON","0").equals("1");
        boolean NO_FPU = properties.getProperty("NO_FPU","0").equals("1");
        boolean NO_ARMV6 = properties.getProperty("NO_ARMV6","0").equals("1");
        boolean hasNeon = false, hasFpu = false, hasArmV6 = false, hasArmV7 = false;
        boolean hasX86 = false;

        if(CPU_ABI.equals("x86")) {
            hasX86 = true;
        } else if(CPU_ABI.equals("armeabi-v7a") ||
                  CPU_ABI2.equals("armeabi-v7a")) {
            hasArmV7 = true;
            hasArmV6 = true; /* Armv7 is backwards compatible to < v6 */
        } else if(CPU_ABI.equals("armeabi") ||
                  CPU_ABI2.equals("armeabi")) {
            hasArmV6 = true;
        }

        try {
            FileReader fileReader = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            while((line = br.readLine()) != null) {
                if(!hasArmV7 && line.contains("ARMv7")) {
                    hasArmV7 = true;
                    hasArmV6 = true; /* Armv7 is backwards compatible to < v6 */
                }
                if(!hasArmV7 && !hasArmV6 && line.contains("ARMv6"))
                    hasArmV6 = true;
                // "clflush size" is a x86-specific cpuinfo tag.
                // (see kernel sources arch/x86/kernel/cpu/proc.c)
                if(line.contains("clflush size"))
                    hasX86 = true;
                // TODO: MIPS - "microsecond timers"; see arch/mips/kernel/proc.c
                if(!hasNeon && line.contains("neon"))
                    hasNeon = true;
                if(!hasFpu && line.contains("vfp"))
                    hasFpu = true;
            }
            fileReader.close();
        } catch(IOException ex){
            ex.printStackTrace();
            errorMsg = "IOException whilst reading cpuinfo flags";
            isCompatible = false;
            return false;
        }

        // Enforce proper architecture to prevent problems
        if(ANDROID_ABI.equals("x86") && !hasX86) {
            errorMsg = "x86 build on non-x86 device";
            isCompatible = false;
            return false;
        } else if(hasX86 && ANDROID_ABI.contains("armeabi")) {
            errorMsg = "ARM build on x86 device";
            isCompatible = false;
            return false;
        }

        if(ANDROID_ABI.equals("armeabi-v7a") && !hasArmV7) {
            errorMsg = "ARMv7 build on non-ARMv7 device";
            isCompatible = false;
            return false;
        }
        if(ANDROID_ABI.equals("armeabi")) {
            if(!NO_ARMV6 && !hasArmV6) {
                errorMsg = "ARMv6 build on non-ARMv6 device";
                isCompatible = false;
                return false;
            } else if(!NO_FPU && !hasFpu) {
                errorMsg = "FPU-enabled build on non-FPU device";
                isCompatible = false;
                return false;
            }
        }
        if(ANDROID_ABI.equals("armeabi") || ANDROID_ABI.equals("armeabi-v7a")) {
            if(!NO_NEON && !hasNeon) {
                errorMsg = "NEON build on non-NEON device";
                isCompatible = false;
                return false;
            }
        }
        errorMsg = null;
        isCompatible = true;
        return true;
    }

    public static boolean isPhone(){
        TelephonyManager manager = (TelephonyManager)VLCApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            return false;
        }else{
            return true;
        }
    }
}
