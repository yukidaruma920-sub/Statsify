package com.yuki920.statsify.util;

public class TextUtil {
    public static String getColorCode(String colorName) {
        if (colorName == null) return "\u00a7c";
        switch (colorName.toUpperCase()) {
            case "BLACK": return "\u00a70";
            case "DARK_BLUE": return "\u00a71";
            case "DARK_GREEN": return "\u00a72";
            case "DARK_AQUA": return "\u00a73";
            case "DARK_RED": return "\u00a74";
            case "DARK_PURPLE": return "\u00a75";
            case "GOLD": return "\u00a76";
            case "GRAY": return "\u00a77";
            case "DARK_GRAY": return "\u00a78";
            case "BLUE": return "\u00a79";
            case "GREEN": return "\u00a7a";
            case "AQUA": return "\u00a7b";
            case "RED": return "\u00a7c";
            case "LIGHT_PURPLE": return "\u00a7d";
            case "YELLOW": return "\u00a7e";
            case "WHITE": return "\u00a7f";
            default: return "\u00a7c";
        }
    }
    
    public static String parseUsername(String str) {
        str = str.trim();
        String[] words = str.split("\\s+");
        return words.length > 0 ? words[words.length - 1] : "";
    }
}