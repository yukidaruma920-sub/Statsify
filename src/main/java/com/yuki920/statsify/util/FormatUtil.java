package com.yuki920.statsify.util;

import java.text.DecimalFormat;

public class FormatUtil {

    public static String formatStars(String text) {
        String color = "\u00a77";

        int Stars = Integer.parseInt(text);
        if (Stars < 100) {
            color = "\u00a77";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 100 && Stars < 200) {
            color = "\u00a7f";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 200 && Stars < 300) {
            color = "\u00a76";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 300 && Stars < 400) {
            color = "\u00a7b";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 400 && Stars < 500) {
            color = "\u00a72";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 500 && Stars < 600) {
            color = "\u00a73";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 600 && Stars < 700) {
            color = "\u00a74";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 700 && Stars < 800) {
            color = "\u00a7d";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 800 && Stars < 900) {
            color = "\u00a79";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 900 && Stars < 1000) {
            color = "\u00a75";
            return color + "[" + text + "\u272b]";
        }
        if (Stars >= 1000 && Stars < 1100) {
            String[] digit = text.split("");
            return "\u00a7c[\u00a76" + digit[0] + "\u00a7e" + digit[1] + "\u00a7a" + digit[2] + "\u00a7b" + digit[3] + "\u00a7d\u272b\u00a75]";
        }
        if (Stars >= 1100 && Stars < 1200) {
            String[] digit = text.split("");
            return "\u00a77[\u00a7f" + digit[0] + digit[1] + digit[2] + digit[3] + "\u272a\u00a77]";
        }
        if (Stars >= 1200 && Stars < 1300) {
            String[] digit = text.split("");
            return "\u00a77[\u00a7e" + digit[0] + digit[1] + digit[2] + digit[3] + "\u00a76\u272a\u00a77]";
        }
        if (Stars >= 1300 && Stars < 1400) {
            String[] digit = text.split("");
            return "\u00a77[\u00a7b" + digit[0] + digit[1] + digit[2] + digit[3] + "\u00a73\u272a\u00a77]";
        }
        if (Stars >= 1400 && Stars < 1500) {
            String[] digit = text.split("");
            return "\u00a77[\u00a7a" + digit[0] + digit[1] + digit[2] + digit[3] + "\u00a72\u272a\u00a77]";
        }
        if (Stars >= 1500 && Stars < 1600) {
            String[] digit = text.split("");
            return "\u00a77[\u00a73" + digit[0] + digit[1] + digit[2] + digit[3] + "\u00a79\u272a\u00a77]";
        }
        if (Stars >= 1600 && Stars < 1700) {
            String[] digit = text.split("");
            return "\u00a77[\u00a7c" + digit[0] + digit[1] + digit[2] + digit[3] + "\u00a74\u272a\u00a77]";
        }
        if (Stars >= 1700 && Stars < 1800) {
            String[] digit = text.split("");
            return "\u00a77[\u00a7d" + digit[0] + digit[1] + digit[2] + digit[3] + "\u00a75\u272a\u00a77]";
        }
        if (Stars >= 1800 && Stars < 1900) {
            String[] digit = text.split("");
            return "\u00a77[\u00a77[\u00a79" + digit[0] + digit[1] + digit[2] + digit[3] + "\u00a71\u272a\u00a77]";
        }
        if (Stars >= 1900 && Stars < 2000) {
            String[] digit = text.split("");
            return "\u00a77[\u00a75" + digit[0] + digit[1] + digit[2] + digit[3] + "\u00a78\u272a\u00a77]";
        }
        if (Stars >= 2000 && Stars < 2100) {
            String[] digit = text.split("");
            return "\u00a78[\u00a77" + digit[0] + "\u00a7f" + digit[1] + digit[2] + "\u00a77" + digit[3] + "\u269d\u00a78]";
        }
        if (Stars >= 2100 && Stars < 2200) {
            String[] digit = text.split("");
            return "\u00a7f[" + digit[0] + "\u00a7e" + digit[1] + digit[2] + "\u00a76" + digit[3] + "\u269d]";
        }
        if (Stars >= 2200 && Stars < 2300) {
            String[] digit = text.split("");
            return "\u00a76[" + digit[0] + "\u00a7f" + digit[1] + digit[2] + "\u00a7b" + digit[3] + "\u00a73\u269d]";
        }
        if (Stars >= 2300 && Stars < 2400) {
            String[] digit = text.split("");
            return "\u00a75[" + digit[0] + "\u00a7d" + digit[1] + digit[2] + "\u00a76" + digit[3] + "\u00a7e\u269d]";
        }
        if (Stars >= 2400 && Stars < 2500) {
            String[] digit = text.split("");
            return "\u00a7b[" + digit[0] + "\u00a7f" + digit[1] + digit[2] + "\u00a77" + digit[3] + "\u269d\u00a78]";
        }
        if (Stars >= 2500 && Stars < 2600) {
            String[] digit = text.split("");
            return "\u00a7f[" + digit[0] + "\u00a7a" + digit[1] + digit[2] + "\u00a72" + digit[3] + "\u269d]";
        }
        if (Stars >= 2600 && Stars < 2700) {
            String[] digit = text.split("");
            return "\u00a74[" + digit[0] + "\u00a7c" + digit[1] + digit[2] + "\u00a7d" + digit[3] + "\u269d]";
        }
        if (Stars >= 2700 && Stars < 2800) {
            String[] digit = text.split("");
            return "\u00a7e[" + digit[0] + "\u00a7f" + digit[1] + digit[2] + "\u00a78" + digit[3] + "\u269d]";
        }
        if (Stars >= 2800 && Stars < 2900) {
            String[] digit = text.split("");
            return "\u00a7a[" + digit[0] + "\u00a72" + digit[1] + digit[2] + "\u00a76" + digit[3] + "\u269d\u00a7e]";
        }
        if (Stars >= 2900 && Stars < 3000) {
            String[] digit = text.split("");
            return "\u00a7b[" + digit[0] + "\u00a73" + digit[1] + digit[2] + "\u00a79" + digit[3] + "\u269d\u00a71]";
        }
        if (Stars >= 3000 && Stars < 3100) {
            String[] digit = text.split("");
            return "\u00a7e[" + digit[0] + "\u00a76" + digit[1] + digit[2] + "\u00a7c" + digit[3] + "\u269d\u00a74]";
        }
        if (Stars >= 3100 && Stars < 3200) {
            String[] digit = text.split("");
            return "\u00a79[" + digit[0] + "\u00a73" + digit[1] + digit[2] + "\u00a76" + digit[3] + "\u2725\u00a7e]";
        }
        if (Stars >= 3200 && Stars < 3300) {
            String[] digit = text.split("");
            return "\u00a7c[\u00a74" + digit[0] + "\u00a77" + digit[1] + digit[2] + "\u00a74" + digit[3] + "\u00a7c\u2725]";
        }
        if (Stars >= 3300 && Stars < 3400) {
            String[] digit = text.split("");
            return "\u00a79[" + digit[0] + digit[1] + "\u00a7d" + digit[2] + "\u00a7c" + digit[3] + "\u2725\u00a74]";
        }
        if (Stars >= 3400 && Stars < 3500) {
            String[] digit = text.split("");
            return "\u00a72[\u00a7a" + digit[0] + "\u00a7d" + digit[1] + digit[2] + "\u00a75" + digit[3] + "\u2725\u00a72]";
        }
        if (Stars >= 3500 && Stars < 3600) {
            String[] digit = text.split("");
            return "\u00a7c[" + digit[0] + "\u00a74" + digit[1] + digit[2] + "\u00a72" + digit[3] + "\u00a7a\u2725]";
        }
        if (Stars >= 3600 && Stars < 3700) {
            String[] digit = text.split("");
            return "\u00a7a[" + digit[0] + digit[1] + "\u00a7b" + digit[2] + "\u00a79" + digit[3] + "\u2725\u00a71]";
        }
        if (Stars >= 3700 && Stars < 3800) {
            String[] digit = text.split("");
            return "\u00a74[" + digit[0] + "\u00a7c" + digit[1] + digit[2] + "\u00a7b" + digit[3] + "\u00a73\u2725]";
        }
        if (Stars >= 3800 && Stars < 3900) {
            String[] digit = text.split("");
            return "\u00a71[" + digit[0] + "\u00a79" + digit[1] + "\u00a75" + digit[2] + digit[3] + "\u00a7d\u2725\u00a71]";
        }
        if (Stars >= 3900 && Stars < 4000) {
            String[] digit = text.split("");
            return "\u00a7c[" + digit[0] + "\u00a7a" + digit[1] + digit[2] + "\u00a73" + digit[3] + "\u00a79\u2725]";
        }
        if (Stars >= 4000 && Stars < 4100) {
            String[] digit = text.split("");
            return "\u00a75[" + digit[0] + "\u00a7c" + digit[1] + digit[2] + "\u00a76" + digit[3] + "\u2725\u00a7e]";
        }
        if (Stars >= 4100 && Stars < 4200) {
            String[] digit = text.split("");
            return "\u00a7e[" + digit[0] + "\u00a76" + digit[1] + "\u00a7c" + digit[2] + "\u00a7d" + digit[3] + "\u2725\u00a75]";
        }
        if (Stars >= 4200 && Stars < 4300) {
            String[] digit = text.split("");
            return "\u00a71[\u00a79" + digit[0] + "\u00a73" + digit[1] + "\u00a7b" + digit[2] + "\u00a7f" + digit[3] + "\u00a77\u2725]";
        }
        if (Stars >= 4300 && Stars < 4400) {
            String[] digit = text.split("");
            return "\u00a70[\u00a75" + digit[0] + "\u00a78" + digit[1] + digit[2] + "\u00a75" + digit[3] + "\u2725\u00a70]";
        }
        if (Stars >= 4400 && Stars < 4500) {
            String[] digit = text.split("");
            return "\u00a72[" + digit[0] + "\u00a7a" + digit[1] + "\u00a7e" + digit[2] + "\u00a76" + digit[3] + "\u00a75\u2725\u00a7d]";
        }
        if (Stars >= 4500 && Stars < 4600) {
            String[] digit = text.split("");
            return "\u00a7f[" + digit[0] + "\u00a7b" + digit[1] + digit[2] + "\u00a73" + digit[3] + "\u2725]";
        }
        if (Stars >= 4600 && Stars < 4700) {
            String[] digit = text.split("");
            return "\u00a73[\u00a7b" + digit[0] + "\u00a7e" + digit[1] + digit[2] + "\u00a76" + digit[3] + "\u00a7d\u2725\u00a75]";
        }
        if (Stars >= 4700 && Stars < 4800) {
            String[] digit = text.split("");
            return "\u00a7f[\u00a74" + digit[0] + "\u00a7c" + digit[1] + digit[2] + "\u00a79" + digit[3] + "\u00a71\u2725\u00a79]";
        }
        if (Stars >= 4800 && Stars < 4900) {
            String[] digit = text.split("");
            return "\u00a75[" + digit[0] + "\u00a7c" + digit[1] + "\u00a76" + digit[2] + "\u00a7e" + digit[3] + "\u00a7b\u2725\u00a73]";
        }
        if (Stars >= 4900 && Stars < 5000) {
            String[] digit = text.split("");
            return "\u00a72[\u00a7a" + digit[0] + "\u00a7f" + digit[1] + digit[2] + "\u00a7a" + digit[3] + "\u2725\u00a72]";
        }
        if (Stars >= 5000) {
            String[] digit = text.split("");
            return "\u00a74[" + digit[0] + "\u00a75" + digit[1] + "\u00a79" + digit[2] + digit[3] + "\u00a71\u2725\u00a70]";
        }
        return "NaN";
    }

    public static String formatWinstreak(String text) {
        String color = "\u00a7r";
        int winstreak = Integer.parseInt(text);
        if (winstreak >= 5 && winstreak < 10) color = "\u00a7b";
        if (winstreak >= 10 && winstreak < 20) color = "\u00a76";
        if (winstreak >= 20) color = "\u00a74";
        return color + text;
    }

    public static String getFkdrColor(double fkdr) {
        if (fkdr < 0.5) return "\u00a77";
        if (fkdr < 1) return "\u00a7f";
        if (fkdr < 2) return "\u00a7a";
        if (fkdr < 4) return "\u00a72";
        if (fkdr < 6) return "\u00a7e";
        if (fkdr < 9) return "\u00a76";
        if (fkdr < 12) return "\u00a7c";
        if (fkdr < 15) return "\u00a74";
        if (fkdr < 20) return "\u00a7d";
        return "\u00a75";
    }

    public static String getWlrColor(double wlr) {
        if (wlr < 0.2) return "\u00a77";
        if (wlr < 0.5) return "\u00a7f";
        if (wlr < 1) return "\u00a7a";
        if (wlr < 2) return "\u00a72";
        if (wlr < 4) return "\u00a7e";
        if (wlr < 7) return "\u00a76";
        if (wlr < 10) return "\u00a7c";
        if (wlr < 15) return "\u00a74";
        if (wlr < 20) return "\u00a7d";
        return "\u00a75";
    }

    public static String formatRank(String rank) {
        return rank.replace("[VIP]", "\u00a7a[VIP]")
                   .replace("[VIP+]", "\u00a7a[VIP\u00a76+\u00a7a]")
                   .replace("[MVP]", "\u00a7b[MVP]")
                   .replace("[MVP+]", "\u00a7b[MVP\u00a7c+\u00a7b]")
                   .replace("[MVP++]", "\u00a76[MVP\u00a7c++\u00a76]");
    }

    public static String getColorCode(String colorName) {
        switch (colorName.toUpperCase()) {
            case "BLACK":        return "\u00a70";
            case "DARK_BLUE":    return "\u00a71";
            case "DARK_GREEN":   return "\u00a72";
            case "DARK_AQUA":    return "\u00a73";
            case "DARK_RED":     return "\u00a74";
            case "DARK_PURPLE":  return "\u00a75";
            case "GOLD":         return "\u00a76";
            case "GRAY":         return "\u00a77";
            case "DARK_GRAY":    return "\u00a78";
            case "BLUE":         return "\u00a79";
            case "GREEN":        return "\u00a7a";
            case "AQUA":         return "\u00a7b";
            case "RED":          return "\u00a7c";
            case "LIGHT_PURPLE": return "\u00a7d";
            case "YELLOW":       return "\u00a7e";
            case "WHITE":        return "\u00a7f";
            default:             return "\u00a7c";
        }
    }

    public static String formatDouble(double value) {
        return new DecimalFormat("#.##").format(value);
    }
}
