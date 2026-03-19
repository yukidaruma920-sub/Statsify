package com.yuki920.statsify.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yuki920.statsify.util.FormatUtil;
import net.minecraft.util.EnumChatFormatting;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatsParser {

    /**
     * Hypixel API のJSONレスポンスをパースしてチャット表示用の文字列を返す。
     * タブリストへの書き込み（sendToTablist）は呼び出し元で行う。
     *
     * @return 表示文字列。フィルターに引っかかった場合は空文字列。
     */
    public static ParsedStats parseBedwarsStats(String jsonResponse) {
        try {
            JsonObject root = new JsonParser().parse(jsonResponse).getAsJsonObject();
            if (!root.get("success").getAsBoolean() || root.get("player").isJsonNull()) {
                return null; // nicked
            }

            JsonObject player = root.getAsJsonObject("player");
            String displayName = player.has("displayname") ? player.get("displayname").getAsString() : "Unknown";

            if (!player.has("stats") || !player.getAsJsonObject("stats").has("Bedwars")) {
                return new ParsedStats(displayName, null, null, null, null, 0, 0, 0, 0, 0, 0);
            }

            JsonObject bedwars = player.getAsJsonObject("stats").getAsJsonObject("Bedwars");

            int level = 0;
            if (player.has("achievements") && player.getAsJsonObject("achievements").has("bedwars_level")) {
                level = player.getAsJsonObject("achievements").get("bedwars_level").getAsInt();
            }

            int finalKills  = getInt(bedwars, "final_kills_bedwars", 0);
            int finalDeaths = getInt(bedwars, "final_deaths_bedwars", 1);
            int wins        = getInt(bedwars, "wins_bedwars", 0);
            int losses      = getInt(bedwars, "losses_bedwars", 1);
            int winstreak   = getInt(bedwars, "winstreak", 0);

            double fkdr = (double) finalKills / finalDeaths;
            double wlr  = (double) wins / losses;

            String fkdrColor = FormatUtil.getFkdrColor(fkdr);
            String wlrColor  = FormatUtil.getWlrColor(wlr);
            String formattedFkdr = FormatUtil.formatDouble(fkdr);
            String formattedWlr  = FormatUtil.formatDouble(wlr);
            String formattedStars = FormatUtil.formatStars(String.valueOf(level));

            return new ParsedStats(
                    displayName,
                    formattedStars,
                    fkdrColor + formattedFkdr,
                    wlrColor + formattedWlr,
                    winstreak > 0 ? FormatUtil.formatWinstreak(String.valueOf(winstreak)) : "",
                    level, fkdr, wlr, winstreak, finalKills, finalDeaths
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Hypixel APIのプレイヤーオブジェクトからランク文字列を組み立てる */
    public static String getFormattedRank(JsonObject player) {
        if (player.has("rank") && !player.get("rank").isJsonNull()) {
            String rank = player.get("rank").getAsString();
            if (rank.equals("STAFF")) return "\u00a7c[\u00a76\u12de\u00a7c] ";
            if (rank.equals("YOUTUBER")) {
                if (player.has("prefix") && !player.get("prefix").isJsonNull()) {
                    if ("\u00a7d[PIG\u00a7b+++\u00a7d]".equals(player.get("prefix").getAsString())) {
                        return "\u00a7d[PIG\u00a7b+++\u00a7d] ";
                    }
                }
                return "\u00a7c[\u00a7fYOUTUBE\u00a7c] ";
            }
        }

        if (player.has("monthlyPackageRank") && !player.get("monthlyPackageRank").isJsonNull()) {
            if ("SUPERSTAR".equals(player.get("monthlyPackageRank").getAsString())) {
                String plusColor    = player.has("rankPlusColor")    ? FormatUtil.getColorCode(player.get("rankPlusColor").getAsString())    : "\u00a7c";
                String monthlyColor = player.has("monthlyRankColor") ? FormatUtil.getColorCode(player.get("monthlyRankColor").getAsString()) : "\u00a76";
                return monthlyColor + "[MVP" + plusColor + "++" + monthlyColor + "] ";
            }
        }

        if (player.has("newPackageRank") && !player.get("newPackageRank").isJsonNull()) {
            String plusColor = player.has("rankPlusColor") ? FormatUtil.getColorCode(player.get("rankPlusColor").getAsString()) : "\u00a7c";
            switch (player.get("newPackageRank").getAsString()) {
                case "MVP_PLUS": return "\u00a7b[MVP" + plusColor + "+\u00a7b] ";
                case "MVP":      return "\u00a7b[MVP] ";
                case "VIP_PLUS": return "\u00a7a[VIP\u00a76+\u00a7a] ";
                case "VIP":      return "\u00a7a[VIP] ";
            }
        }

        return "\u00a77";
    }

    /** タグ文字列を生成する */
    public static String buildTags(String name, String uuid, int stars, double fkdr, int ws, int finals, int fdeaths) {
        StringBuilder totaltags = new StringBuilder();

        // N: Suspicious name
        String[] suswords = {"msmc","kikin","g0ld","Fxrina_","MAL_","fer_","ly_","tzi_","Verse_",
                "uwunova","Anas_","MyloAlt_","rayl_","mchk_","HellAlts_","disruptive",
                "solaralts_","G0LDALTS_","unwilling","predicative"};
        boolean suswordcheck = false;
        for (String keyword : suswords) {
            if (name.toLowerCase().contains(keyword.toLowerCase())) { suswordcheck = true; break; }
        }
        if (suswordcheck || Pattern.compile("\\d.*\\d.*\\d.*\\d").matcher(name).find()) {
            totaltags.append(EnumChatFormatting.YELLOW).append("N \u00a7r");
        }

        // W: Winstreak while low star
        if (stars <= 6 && ws >= 1) totaltags.append(EnumChatFormatting.GREEN).append("W \u00a7r");

        // F: High FKDR when low star
        if (stars <= 6 && fkdr >= 4) totaltags.append(EnumChatFormatting.DARK_RED).append("F \u00a7r");

        // SK: Default skin
        if (HypixelApiClient.isDefaultSkin(uuid)) totaltags.append(EnumChatFormatting.DARK_AQUA).append("SK \u00a7r");

        // NL: New login
        String playerData = HypixelApiClient.fetchNadeshikoData(uuid);
        Pattern timestampPattern = Pattern.compile("\"first_login\":(\\d+),");
        Matcher timestampMatcher = timestampPattern.matcher(playerData);
        if (timestampMatcher.find()) {
            long timestamp = Long.parseLong(timestampMatcher.group(1));
            Calendar current = Calendar.getInstance();
            Calendar login   = Calendar.getInstance();
            current.setTimeInMillis(System.currentTimeMillis());
            current.set(Calendar.HOUR_OF_DAY, 0); current.set(Calendar.MINUTE, 0);
            current.set(Calendar.SECOND, 0);      current.set(Calendar.MILLISECOND, 0);
            login.setTime(new Date(timestamp));
            login.set(Calendar.HOUR_OF_DAY, 0);   login.set(Calendar.MINUTE, 0);
            login.set(Calendar.SECOND, 0);         login.set(Calendar.MILLISECOND, 0);

            long diff = current.getTimeInMillis() - login.getTimeInMillis();
            if (Math.abs(diff) <= 24L * 60 * 60 * 1000) {
                totaltags.append(EnumChatFormatting.RED).append("NL \u00a7r");
            }
        }

        // 0F: 0 finals 0 final deaths
        if (finals == 0 && fdeaths == 0) totaltags.append(EnumChatFormatting.RED).append("0F \u00a7r");

        return totaltags.toString();
    }

    // ─── ヘルパー ────────────────────────────────────────────

    private static int getInt(JsonObject obj, String key, int defaultVal) {
        return obj.has(key) ? obj.get(key).getAsInt() : defaultVal;
    }

    // ─── ParsedStats ─────────────────────────────────────────

    /** パース結果を保持する値オブジェクト */
    public static class ParsedStats {
        public final String displayName;
        public final String formattedStars;   // null = no bedwars stats
        public final String coloredFkdr;
        public final String coloredWlr;
        public final String coloredWinstreak; // "" = no winstreak
        public final int level;
        public final double fkdr;
        public final double wlr;
        public final int winstreak;
        public final int finalKills;
        public final int finalDeaths;

        public ParsedStats(String displayName, String formattedStars,
                           String coloredFkdr, String coloredWlr, String coloredWinstreak,
                           int level, double fkdr, double wlr, int winstreak,
                           int finalKills, int finalDeaths) {
            this.displayName      = displayName;
            this.formattedStars   = formattedStars;
            this.coloredFkdr      = coloredFkdr;
            this.coloredWlr       = coloredWlr;
            this.coloredWinstreak = coloredWinstreak;
            this.level            = level;
            this.fkdr             = fkdr;
            this.wlr              = wlr;
            this.winstreak        = winstreak;
            this.finalKills       = finalKills;
            this.finalDeaths      = finalDeaths;
        }
    }
}
