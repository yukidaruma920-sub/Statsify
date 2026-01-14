package com.yuki920.statsify.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yuki920.statsify.Statsify;
import com.yuki920.statsify.config.ModConfig;
import com.yuki920.statsify.util.BedwarsUtil;
import com.yuki920.statsify.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatsAPI {

    public static String fetchUUID(String username) {
        try {
            String urlString = "https://api.minecraftservices.com/minecraft/profile/lookup/name/" + username;
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                }
                String uuid = extractUUID(response.toString());
                return uuid != null ? uuid : "NICKED";
            }

            if (responseCode == 404) return "NICKED";

            if (responseCode == 429) {
                urlString = "https://api.minetools.eu/uuid/" + username;
                connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                }

                if (response.toString().contains("\"id\": null")) return "NICKED";
                String[] parts = response.toString().split("\"id\":\"");
                if (parts.length > 1) {
                    return parts[1].split("\"")[0];
                } else {
                    return "NICKED";
                }
            }

        } catch (Exception ignored) {
        }

        return "NICKED";
    }

    private static String extractUUID(String response) {
        String[] parts = response.split("\"");
        if (response.contains("Couldn't")) return "NICKED";
        if (parts.length >= 5) return parts[3];
        return null;
    }

    public static String fetchBedwarsStats(String playerName) throws IOException {
        try {
            String uuid = getUUIDFromName(playerName);
            if (uuid == null || uuid.equals("NICKED")) {
                // Tabリストにいない場合、APIからUUID取得を試みる（コマンド用などに有効）
                uuid = fetchUUID(playerName); 
                if (uuid == null || uuid.equals("NICKED")) {
                    return playerName + " §cis possibly nicked.";
                }
            }
            String jsonResponse = fetchHypixelBedwarsStats(uuid);
            return parseHypixelStats(jsonResponse, playerName, uuid);
        } catch (Exception e) {
            return EnumChatFormatting.RED + playerName + " is possibly nicked.";
        }
    }

    public static String fetchPlayerStatsForCommand(String playerName) throws IOException {
        // コマンド用: 名前から強制的にUUIDを引く
        return fetchBedwarsStats(playerName);
    }

    private static String fetchHypixelBedwarsStats(String uuid) throws IOException {
        if (ModConfig.getInstance().hypixelApiKey.isEmpty()) {
            throw new IOException("Hypixel API key not set. Use /hypixelkey <key>");
        }
        String urlString = "https://api.hypixel.net/v2/player?uuid=" + uuid + "&key=" + ModConfig.getInstance().hypixelApiKey;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();
        if (responseCode == 429) throw new IOException("Rate limited");
        if (responseCode == 403) throw new IOException("Invalid API key");
        if (responseCode != 200) throw new IOException("HTTP Error: " + responseCode);

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) response.append(line);
        }
        return response.toString();
    }

    private static String parseHypixelStats(String jsonResponse, String playerName, String uuid) {
        try {
            JsonObject root = new JsonParser().parse(jsonResponse).getAsJsonObject();
            if (!root.get("success").getAsBoolean() || root.get("player").isJsonNull()) {
                return playerName + " §cis possibly nicked.";
            }

            JsonObject player = root.getAsJsonObject("player");
            String displayName = player.has("displayname") ? player.get("displayname").getAsString() : playerName;
            String formattedRank = BedwarsUtil.getFormattedRank(player); // ランク取得

            if (!player.has("stats") || !player.getAsJsonObject("stats").has("Bedwars")) {
                return formattedRank + displayName + " §chas no Bedwars stats.";
            }

            JsonObject bedwars = player.getAsJsonObject("stats").getAsJsonObject("Bedwars");
            int level = 0;
            if (player.has("achievements") && player.getAsJsonObject("achievements").has("bedwars_level")) {
                level = player.getAsJsonObject("achievements").get("bedwars_level").getAsInt();
            }
            String formattedStars = BedwarsUtil.formatStars(String.valueOf(level));

            int finalKills = bedwars.has("final_kills_bedwars") ? bedwars.get("final_kills_bedwars").getAsInt() : 0;
            int finalDeaths = bedwars.has("final_deaths_bedwars") ? bedwars.get("final_deaths_bedwars").getAsInt() : 1;
            double fkdr = (double) finalKills / finalDeaths;

            if (fkdr < ModConfig.getInstance().minFkdr) return "";

            String fkdrColor = "§7";
            if (fkdr >= 0.5 && fkdr < 1) fkdrColor = "§f";
            else if (fkdr >= 1 && fkdr < 2) fkdrColor = "§a";
            else if (fkdr >= 2 && fkdr < 3) fkdrColor = "§2";
            else if (fkdr >= 3 && fkdr < 4) fkdrColor = "§e";
            else if (fkdr >= 4 && fkdr < 6) fkdrColor = "§6";
            else if (fkdr >= 6 && fkdr < 8) fkdrColor = "§c";
            else if (fkdr >= 8 && fkdr < 10) fkdrColor = "§4";
            else if (fkdr >= 10 && fkdr < 15) fkdrColor = "§d";
            else if (fkdr > 15) fkdrColor = "§5";

            DecimalFormat df = new DecimalFormat("#.##");
            String formattedFkdr = df.format(fkdr);

            int winstreak = bedwars.has("winstreak") ? bedwars.get("winstreak").getAsInt() : 0;
            String formattedWinstreak = winstreak > 0 ? BedwarsUtil.formatWinstreak(String.valueOf(winstreak)) : "";

            String tabfkdr = fkdrColor + formattedFkdr;
            if (ModConfig.getInstance().tabstats) {
                Statsify.sendToTablist(playerName, tabfkdr, formattedStars); // メインクラスのメソッド呼び出し
            }

            if (ModConfig.getInstance().tags) {
                String tagsValue = buildTags(displayName, uuid, level, fkdr, winstreak, finalKills, finalDeaths); // displayNameを使用
                if (tagsValue.endsWith(" ")) tagsValue = tagsValue.substring(0, tagsValue.length() - 1);
                
                String baseMsg = formattedRank + displayName + " §r" + formattedStars + "§r§7 |§r FKDR: " + fkdrColor + formattedFkdr;
                if (!formattedWinstreak.isEmpty()) baseMsg += " §r§7|§r WS: " + formattedWinstreak;
                return baseMsg + "§r [ " + tagsValue + " ]";
            } else {
                String baseMsg = formattedRank + displayName + " §r" + formattedStars + "§r§7 |§r FKDR: " + fkdrColor + formattedFkdr;
                if (!formattedWinstreak.isEmpty()) baseMsg += " §r§7|§r WS: " + formattedWinstreak;
                return baseMsg + "§r";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return playerName + " §cError parsing stats.";
        }
    }

    public static String getUUIDFromName(String playerName) {
        if (Minecraft.getMinecraft().getNetHandler() == null) return null;
        for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (info.getGameProfile().getName().equalsIgnoreCase(playerName)) {
                return String.valueOf(info.getGameProfile().getId());
            }
        }
        return null;
    }

    public static String checkDuels(String playerName) throws IOException {
        String url = "https://plancke.io/hypixel/player/stats/" + playerName;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        StringBuilder responseText = new StringBuilder();
        try (InputStream inputStream = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) responseText.append(line);
        }

        String response = responseText.toString();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            return playerName + " is \u00a7cnicked\u00a7r";
        }

        Pattern namePattern = Pattern.compile("(?<=content=\"Plancke\" /><meta property=\"og:locale\" content=\"en_US\" /><meta property=\"og:description\" content=\").+?(?=\")");
        Matcher nameMatcher = namePattern.matcher(response);
        String displayedName = nameMatcher.find() ? nameMatcher.group() : "Unknown";

        String playerrank = "";
        String trimmedName = displayedName.trim();
        String[] parts = trimmedName.split("\\s+", 2);
        // ここは簡易的にBedwarsUtilのメソッドは使わずそのまま残すか、必要ならUtilへ
        if (parts.length > 0 && parts[0].startsWith("[") && parts[0].endsWith("]")) {
            playerrank = parts[0].replace("[VIP", "\u00a7a[VIP").replace("[MVP+", "\u00a7b[MVP+").replace("[MVP++", "\u00a76[MVP++") + " ";
        }

        String regex = "<tr><td>Classic 1v1</td><td>([\\d,]+)</td><td>([\\d,]+)</td><td>([\\d.,]+)</td><td>([\\d,]+)</td><td>([\\d,]+)</td><td>([\\d.,]+)</td><td>([\\d.,]+)</td><td>([\\d.,]+)</td></tr>";
        Matcher matcher = Pattern.compile(regex).matcher(response);
        if (matcher.find()) {
            String ClassicStats = "\n\u00a7aKills:\u00a7r " + matcher.group(1) + " \u00a7cDeaths:\u00a7r " + matcher.group(2) + " (\u00a7d" + matcher.group(3) + "\u00a7r) " + "\n" + "\u00a7bW:\u00a7r " + matcher.group(4) + " \u00a7cL: \u00a7r" + matcher.group(5) + " (\u00a7d" + matcher.group(6) + "\u00a7r)";
            return playerrank + playerName + "\u00a7r (Classic 1v1)" + ClassicStats;
        } else {
            return playerrank + playerName + " \u00a7chas no Classic Duels stats.\u00a7r";
        }
    }

    public static String fetchUrchinTags(String playerName) throws IOException {
        String tagsURL = "https://urchin.ws/player/" + playerName + "?key=" + ModConfig.getInstance().urchinkey + "&sources=MANUAL";
        HttpURLConnection statsConnection = (HttpURLConnection) new URL(tagsURL).openConnection();
        statsConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

        if (statsConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("URCHIN: " + statsConnection.getResponseCode());
        }

        StringBuilder responseText = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(statsConnection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) responseText.append(line);
        }

        String response = responseText.toString();
        if (!response.isEmpty()) {
            Matcher matcher = Pattern.compile("\"type\":\"(.*?)\".*?\"reason\":\"(.*?)\".*?\"added_on\":\"(.*?)\"").matcher(response);
            if (matcher.find()) {
                return "\u00a7r" + matcher.group(1) + ". \u00a7rReason: \u00a76" + matcher.group(2);
            }
        }
        return "";
    }

    private static String nadeshikoAPI(String uuid) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://nadeshiko.io/player/" + uuid + "/network").openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            if (connection.getResponseCode() == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                }
                Matcher matcher = Pattern.compile("playerData = JSON.parse\\(decodeURIComponent\\(\"(.*?)\"\\)\\)").matcher(response.toString());
                if (matcher.find()) return URLDecoder.decode(matcher.group(1), "UTF-8");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "";
    }

    private static String buildTags(String name, String uuid, int stars, double fkdr, int ws, int finals, int fdeaths) {
        String totaltags = "";
        
        // Suspicious name
        String[] suswords = {"msmc", "kikin", "g0ld", "Fxrina_", "MAL_", "fer_", "ly_", "tzi_", "Verse_", "uwunova", "Anas_", "MyloAlt_", "rayl_", "mchk_", "HellAlts_", "disruptive", "solaralts_", "G0LDALTS_", "unwilling", "predicative"};
        boolean suswordcheck = false;
        for (String keyword : suswords) {
            if (name.toLowerCase().contains(keyword.toLowerCase())) { suswordcheck = true; break; }
        }
        if (suswordcheck || Pattern.compile("\\d.*\\d.*\\d.*\\d").matcher(name).find()) totaltags += EnumChatFormatting.YELLOW + "N \u00a7r";

        // Stats tags
        if (stars <= 6 && ws >= 1) totaltags += EnumChatFormatting.GREEN + "W \u00a7r";
        if (stars <= 6 && fkdr >= 4) totaltags += EnumChatFormatting.DARK_RED + "F \u00a7r";
        if (finals == 0 && fdeaths == 0) totaltags += EnumChatFormatting.RED + "0F \u00a7r";

        // Skin check
        String[] defaultSkinIDS = {"a3bd16079f764cd541e072e888fe43885e711f98658323db0f9a6045da91ee7a ", "b66bc80f002b10371e2fa23de6f230dd5e2f3affc2e15786f65bc9be4c6eb71a", "e5cdc3243b2153ab28a159861be643a4fc1e3c17d291cdd3e57a7f370ad676f3", "f5dddb41dcafef616e959c2817808e0be741c89ffbfed39134a13e75b811863d", "4c05ab9e07b3505dc3ec11370c3bdce5570ad2fb2b562e9b9dd9cf271f81aa44", "31f477eb1a7beee631c2ca64d06f8f68fa93a3386d04452ab27f43acdf1b60cb", "6ac6ca262d67bcfb3dbc924ba8215a18195497c780058a5749de674217721892", "1abc803022d8300ab7578b189294cce39622d9a404cdc00d3feacfdf45be6981", "daf3d88ccb38f11f74814e92053d92f7728ddb1a7955652a60e30cb27ae6659f", "fece7017b1bb13926d1158864b283b8b930271f80a90482f174cca6a17e88236"};
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid).openConnection();
            if (conn.getResponseCode() == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line; while ((line = in.readLine()) != null) response.append(line);
                }
                if (response.toString().contains("\"value\" : \"")) {
                    String val = response.toString().split("\"value\" : \"")[1].split("\"")[0];
                    String skinJson = new String(Base64.getDecoder().decode(val)).toLowerCase();
                    for (String id : defaultSkinIDS) {
                        if (skinJson.contains(id.trim().toLowerCase())) { totaltags += EnumChatFormatting.DARK_AQUA + "SK \u00a7r"; break; }
                    }
                }
            }
        } catch (Exception ignored) {}

        // New Login check
        String pData = nadeshikoAPI(uuid);
        Matcher tm = Pattern.compile("\"first_login\":(\\d+),").matcher(pData);
        if (tm.find()) {
            long diff = System.currentTimeMillis() - Long.parseLong(tm.group(1));
            if (diff <= 86400000L) totaltags += EnumChatFormatting.RED + "NL \u00a7r"; // 24 hours
        }

        return totaltags;
    }
}