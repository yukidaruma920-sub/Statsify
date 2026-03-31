package com.yuki920.statsify.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HypixelApiClient {

    /** Mojang API（+ minetools フォールバック）でUUIDを取得する */
    public static String fetchUUID(String username) {
        try {
            String urlString = "https://api.minecraftservices.com/minecraft/profile/lookup/name/" + username;
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String response = readResponse(connection);
                String uuid = extractUUID(response);
                return uuid != null ? uuid : "NICKED";
            }

            if (responseCode == 404) return "NICKED";

            if (responseCode == 429) {
                // Rate limited - minetools へフォールバック
                urlString = "https://api.minetools.eu/uuid/" + username;
                connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("GET");
                String response = readResponse(connection);
                if (response.contains("\"id\": null")) return "NICKED";
                String[] parts = response.split("\"id\":\"");
                return parts.length > 1 ? parts[1].split("\"")[0] : "NICKED";
            }

        } catch (Exception ignored) {
        }
        return "NICKED";
    }

    /** Hypixel v2 API からプレイヤーデータをJSON文字列で取得する */
    public static String fetchHypixelPlayer(String uuid, String apiKey) throws IOException {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IOException("Hypixel API key not set. Use /hypixelkey <key>");
        }

        String urlString = "https://api.hypixel.net/v2/player?uuid=" + uuid + "&key=" + apiKey;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();
        if (responseCode == 429) throw new IOException("Rate limited");
        if (responseCode == 403) throw new IOException("Invalid API key");
        if (responseCode != 200) throw new IOException("HTTP Error: " + responseCode);

        return readResponse(connection);
    }

    /** Urchin API からタグ情報を取得する */
    public static String fetchUrchinTags(String playerName, String urchinKey) throws IOException {
        String tagsURL = "https://urchin.ws/player/" + playerName + "?key=" + urchinKey + "&sources=MANUAL";
        HttpURLConnection connection = (HttpURLConnection) new URL(tagsURL).openConnection();
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("URCHIN: " + responseCode);
        }

        String response = readResponse(connection);
        if (!response.isEmpty()) {
            try {
                String regex = "\"type\":\"(.*?)\".*?\"reason\":\"(.*?)\".*?\"added_on\":\"(.*?)\"";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(response);
                if (matcher.find()) {
                    String type = matcher.group(1);
                    String reason = matcher.group(2);
                    return "\u00a7r" + type + ". \u00a7rReason: \u00a76" + reason;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return "";
    }

    /** Nadeshiko API からプレイヤーデータを取得する */
    public static String fetchNadeshikoData(String uuid) {
        try {
            String urlString = "https://nadeshiko.io/player/" + uuid + "/network";
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String responseString = readResponse(connection);
                Pattern pattern = Pattern.compile("playerData = JSON.parse\\(decodeURIComponent\\(\"(.*?)\"\\)\\)");
                Matcher matcher = pattern.matcher(responseString);
                if (matcher.find()) {
                    return URLDecoder.decode(matcher.group(1), "UTF-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /** Mojang セッションサーバーからスキン情報を取得し、デフォルトスキンかどうかを判定する */
    public static boolean isDefaultSkin(String uuid) {
        String[] defaultSkinIDs = {
                "a3bd16079f764cd541e072e888fe43885e711f98658323db0f9a6045da91ee7a ",
                "b66bc80f002b10371e2fa23de6f230dd5e2f3affc2e15786f65bc9be4c6eb71a",
                "e5cdc3243b2153ab28a159861be643a4fc1e3c17d291cdd3e57a7f370ad676f3",
                "f5dddb41dcafef616e959c2817808e0be741c89ffbfed39134a13e75b811863d",
                "4c05ab9e07b3505dc3ec11370c3bdce5570ad2fb2b562e9b9dd9cf271f81aa44",
                "31f477eb1a7beee631c2ca64d06f8f68fa93a3386d04452ab27f43acdf1b60cb",
                "6ac6ca262d67bcfb3dbc924ba8215a18195497c780058a5749de674217721892",
                "1abc803022d8300ab7578b189294cce39622d9a404cdc00d3feacfdf45be6981",
                "daf3d88ccb38f11f74814e92053d92f7728ddb1a7955652a60e30cb27ae6659f",
                "fece7017b1bb13926d1158864b283b8b930271f80a90482f174cca6a17e88236"
        };

        try {
            String urlString = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String response = readResponse(connection);
                String[] parts = response.split("\"value\" : \"");
                String value = parts[1].split("\"")[0];
                byte[] decodedBytes = Base64.getDecoder().decode(value);
                String valueJson = new String(decodedBytes);

                for (String id : defaultSkinIDs) {
                    if (valueJson.toLowerCase().contains(id.toLowerCase())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ─── 共通ユーティリティ ───────────────────────────────

    private static String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private static String extractUUID(String response) {
        if (response.contains("Couldn't")) return "NICKED";
        String[] parts = response.split("\"");
        return parts.length >= 5 ? parts[3] : null;
    }
}
