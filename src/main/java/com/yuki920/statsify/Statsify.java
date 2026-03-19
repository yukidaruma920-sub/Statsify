package com.yuki920.statsify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yuki920.statsify.api.HypixelApiClient;
import com.yuki920.statsify.api.StatsParser;
import com.yuki920.statsify.commands.Commands;
import com.yuki920.statsify.util.ConfigManager;
import com.yuki920.statsify.util.FormatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(modid = Statsify.MODID, name = Statsify.NAME, version = Statsify.VERSION)
public class Statsify {

    public static final String MODID   = "statsify";
    public static final String NAME    = "Stats Mod";
    public static final String VERSION = "1.0";

    /** タブリストに表示するプレイヤー別サフィックス（stars, fkdr）。static でイベント間共有 */
    public static final Map<String, List<String>> playerSuffixes = new HashMap<>();

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ConfigManager config = new ConfigManager();
    private Set<String> onlinePlayers = new HashSet<>();

    // ─── 初期化 ──────────────────────────────────────────────

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        config.load();
        MinecraftForge.EVENT_BUS.register(this);

        ClientCommandHandler.instance.registerCommand(new Commands.BedwarsCommand(this));
        ClientCommandHandler.instance.registerCommand(new Commands.MinFkdrCommand(this, config));
        ClientCommandHandler.instance.registerCommand(new Commands.BwModeCommand(config));
        ClientCommandHandler.instance.registerCommand(new Commands.ToggleTagsCommand(config));
        ClientCommandHandler.instance.registerCommand(new Commands.StatsifyCommand());
        ClientCommandHandler.instance.registerCommand(new Commands.TablistToggleCommand(config));
        ClientCommandHandler.instance.registerCommand(new Commands.ClearCacheCommand());
        ClientCommandHandler.instance.registerCommand(new Commands.SetUrchinKeyCommand(config));
        ClientCommandHandler.instance.registerCommand(new Commands.UrchinTagsToggleCommand(config));
        ClientCommandHandler.instance.registerCommand(new Commands.AutoWhoToggleCommand(config));
        ClientCommandHandler.instance.registerCommand(new Commands.TabFormatSetCommand(config));
        ClientCommandHandler.instance.registerCommand(new Commands.SetHypixelKeyCommand(config));
    }

    // ─── チャットイベント ─────────────────────────────────────

    @SubscribeEvent
    public void onChat(final ClientChatReceivedEvent event) {
        catchAndIgnoreNullPointerException(() -> {
            String message = event.message.getUnformattedText();

            // ゲーム開始検知
            if (message.contains("Protect your bed and destroy the enemy beds.")
                    && !message.contains(":") && !message.contains("SHOUT")) {
                playerSuffixes.clear();
                onlinePlayers.clear();
                if (config.autowho) {
                    mc.thePlayer.sendChatMessage("/who");
                }
            }

            // /who 応答
            if (message.startsWith("ONLINE:")) {
                String playersString = message.substring("ONLINE:".length()).trim();
                onlinePlayers = new HashSet<>(Arrays.asList(playersString.split(",\\s*")));

                if (Objects.equals(config.mode, "bws")) {
                    checkStatsRatelimitless();
                } else {
                    mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(
                            "\u00a7r[\u00a7bF\u00a7r] \u00a7cPlancke is discontinued, use BWS."));
                }
                if (config.urchin) {
                    checkUrchinTags();
                }
            }

            // デュエル対戦相手
            if (message.startsWith(" ") && message.contains("Opponent:")) {
                final String username = parseUsername(message);
                new Thread(() -> {
                    try {
                        String stats = checkDuels(username);
                        Minecraft.getMinecraft().thePlayer.addChatMessage(
                                new net.minecraft.util.ChatComponentText("\u00a7r[\u00a7bF\u00a7r] " + stats));
                    } catch (IOException e) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(
                                new net.minecraft.util.ChatComponentText(
                                        "\u00a7r[\u00a7bF\u00a7r] \u00a7c" + username + " is possibly nicked.\u00a7r"));
                    }
                }).start();
            }
        });
    }

    // ─── タブリスト描画イベント ───────────────────────────────

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderTabList(RenderGameOverlayEvent.Post event) {
        // Future reference, I have no idea what the FUCK did i do here. It works, but idk how. So I wont be improving this.
        // does not work on viaforge 1.20 / 1.19
        if (event.type != RenderGameOverlayEvent.ElementType.PLAYER_LIST) return;
        if (!config.tabstats || !isBedwars()) return;

        if (mc.thePlayer == null || mc.thePlayer.sendQueue == null) return;
        Collection<NetworkPlayerInfo> playerInfoList = mc.thePlayer.sendQueue.getPlayerInfoMap();
        if (playerInfoList.isEmpty()) return;

        for (NetworkPlayerInfo playerInfo : playerInfoList) {
            if (playerInfo == null || playerInfo.getGameProfile() == null) continue;

            String playerName = playerInfo.getGameProfile().getName();
            List<String> suffixv = playerSuffixes.get(playerName);

            String[] tabData = getTabDisplayName2(playerName);
            String team = tabData[0], name = tabData[1];
            String teamColor = team.length() >= 2 ? team.substring(0, 2) : "";

            String newDisplayName;

            if (suffixv != null && suffixv.size() >= 2) {
                if (!name.endsWith(" \u00a78| " + suffixv.get(1))) {
                    if (config.tabFormat.equals("bracket_star_name_dot_fkdr")) {
                        newDisplayName = team + suffixv.get(0) + "\u00a7r " + teamColor + name + " \u00a78| " + suffixv.get(1);
                    } else if (config.tabFormat.equals("star_dot_name_dot_fkdr")) {
                        newDisplayName = team + suffixv.get(0) + " \u00a78|\u00a7r " + teamColor + name + " \u00a78| " + suffixv.get(1);
                    } else if (config.tabFormat.equals("name_dot_fkdr")) {
                        newDisplayName = team + teamColor + name + " \u00a78|\u00a7r " + suffixv.get(1);
                    } else {
                        newDisplayName = team + suffixv.get(0) + "\u00a7r " + teamColor + name + " \u00a78| " + suffixv.get(1);
                    }
                    playerInfo.setDisplayName(new net.minecraft.util.ChatComponentText(newDisplayName));
                }
            } else if (onlinePlayers.contains(playerName)) {
                newDisplayName = team + "\u00a78[\u00a75NICK\u00a78]\u00a7r " + teamColor + name;
                playerInfo.setDisplayName(new net.minecraft.util.ChatComponentText(newDisplayName));
            }
        }
    }

    // ─── 公開ユーティリティ ───────────────────────────────────

    public static void sendToTablist(String playerName, String fkdr, String stars) {
        if (playerName != null && fkdr != null && stars != null) {
            playerSuffixes.put(playerName, Arrays.asList(stars, fkdr));
        }
    }

    public static String parseUsername(String str) {
        str = str.trim();
        String[] words = str.split("\\s+");
        return words.length > 0 ? words[words.length - 1] : "";
    }

    // ─── stat取得（/bw コマンド用） ──────────────────────────

    public String fetchPlayerStatsForCommand(String playerName) throws IOException {
        try {
            String uuid = HypixelApiClient.fetchUUID(playerName);
            if (uuid == null || uuid.equals("NICKED")) {
                return playerName + " \u00a7c is possibly nicked.";
            }

            String jsonResponse = HypixelApiClient.fetchHypixelPlayer(uuid, config.hypixelApiKey);
            JsonObject root = new JsonParser().parse(jsonResponse).getAsJsonObject();

            if (!root.get("success").getAsBoolean() || root.get("player").isJsonNull()) {
                return playerName + " \u00a7cis possibly nicked.";
            }

            JsonObject player = root.getAsJsonObject("player");
            String displayedName = player.has("displayname") ? player.get("displayname").getAsString() : playerName;
            String formattedRank = StatsParser.getFormattedRank(player);

            if (!player.has("stats") || !player.getAsJsonObject("stats").has("Bedwars")) {
                return formattedRank + displayedName + " \u00a7chas no Bedwars stats.";
            }

            StatsParser.ParsedStats s = StatsParser.parseBedwarsStats(jsonResponse);
            if (s == null) return playerName + " \u00a7cis possibly nicked.";
            if (s.formattedStars == null) return formattedRank + displayedName + " \u00a7chas no Bedwars stats.";

            return formattedRank + displayedName + " \u00a7r" + s.formattedStars
                    + " \u00a7rFKDR: " + s.coloredFkdr + " \u00a7rWLR: " + s.coloredWlr;

        } catch (Exception e) {
            e.printStackTrace();
            return playerName + " \u00a7cError fetching stats.";
        }
    }

    // ─── stat取得（/who 一括チェック用） ─────────────────────

    public String fetchBedwarsStats(String playerName) throws IOException {
        try {
            String uuid = getUUIDFromName(playerName);
            if (uuid == null || uuid.equals("NICKED")) {
                return getTabDisplayName(playerName) + " \u00a7cis possibly nicked.";
            }

            String jsonResponse = HypixelApiClient.fetchHypixelPlayer(uuid, config.hypixelApiKey);
            return parseAndFormatStats(jsonResponse, playerName, uuid);
        } catch (Exception e) {
            return EnumChatFormatting.RED + playerName + " is possibly nicked.";
        }
    }

    private String parseAndFormatStats(String jsonResponse, String playerName, String uuid) {
        try {
            JsonObject root = new JsonParser().parse(jsonResponse).getAsJsonObject();
            if (!root.get("success").getAsBoolean() || root.get("player").isJsonNull()) {
                return playerName + " \u00a7cis possibly nicked.";
            }

            JsonObject player = root.getAsJsonObject("player");
            String displayName = player.has("displayname") ? player.get("displayname").getAsString() : playerName;

            if (!player.has("stats") || !player.getAsJsonObject("stats").has("Bedwars")) {
                return displayName + " \u00a7chas no Bedwars stats.";
            }

            StatsParser.ParsedStats s = StatsParser.parseBedwarsStats(jsonResponse);
            if (s == null || s.formattedStars == null) return displayName + " \u00a7chas no Bedwars stats.";

            // フィルター
            if (s.fkdr < config.minFkdr) return "";

            // タブリストへ書き込み
            if (config.tabstats) {
                sendToTablist(playerName, s.coloredFkdr + " \u00a78| " + s.coloredWlr, s.formattedStars);
            }

            // タグ生成
            if (config.tags) {
                String tagsValue = StatsParser.buildTags(playerName, uuid, s.level, s.fkdr, s.winstreak, s.finalKills, s.finalDeaths);
                if (tagsValue.endsWith(" ")) tagsValue = tagsValue.substring(0, tagsValue.length() - 1);

                if (s.coloredWinstreak.isEmpty()) {
                    return getTabDisplayName(playerName) + " \u00a7r" + s.formattedStars + "\u00a7r\u00a77 |\u00a7r FKDR: " + s.coloredFkdr + "\u00a7r\u00a77 |\u00a7r WLR: " + s.coloredWlr + " \u00a7r\u00a77|\u00a7r [ " + tagsValue + " ]";
                } else {
                    return getTabDisplayName(playerName) + " \u00a7r" + s.formattedStars + "\u00a7r\u00a77 |\u00a7r FKDR: " + s.coloredFkdr + "\u00a7r\u00a77 |\u00a7r WLR: " + s.coloredWlr + " \u00a7r\u00a77|\u00a7r WS: " + s.coloredWinstreak + "\u00a7r [ " + tagsValue + " ]";
                }
            } else {
                if (s.coloredWinstreak.isEmpty()) {
                    return getTabDisplayName(playerName) + " \u00a7r" + s.formattedStars + "\u00a7r\u00a77 |\u00a7r FKDR: " + s.coloredFkdr + "\u00a7r\u00a77 |\u00a7r WLR: " + s.coloredWlr + "\u00a7r";
                } else {
                    return getTabDisplayName(playerName) + " \u00a7r" + s.formattedStars + "\u00a7r\u00a77 |\u00a7r FKDR: " + s.coloredFkdr + "\u00a7r\u00a77 |\u00a7r WLR: " + s.coloredWlr + " \u00a7r\u00a77|\u00a7r WS: " + s.coloredWinstreak + "\u00a7r";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return playerName + " \u00a7cError parsing stats.";
        }
    }

    // ─── /who 一括チェック（並列） ────────────────────────────

    private void checkStatsRatelimitless() {
        catchAndIgnoreNullPointerException(() -> {
            final int MAX_THREADS = 20;
            int poolSize = Math.min(onlinePlayers.size(), MAX_THREADS);
            final ExecutorService executor = Executors.newFixedThreadPool(poolSize);

            for (final String playerName : onlinePlayers) {
                executor.submit(() -> {
                    try {
                        final String stats = fetchBedwarsStats(playerName);
                        if (!stats.isEmpty()) {
                            mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                    new net.minecraft.util.ChatComponentText("\u00a7r[\u00a7bF\u00a7r] " + stats)));
                        }
                    } catch (IOException e) {
                        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                new net.minecraft.util.ChatComponentText("\u00a7r[\u00a7bF\u00a7r] Failed to fetch stats for: " + playerName + " | [UpstreamCSR] ")));
                    }
                });
            }

            executor.shutdown();
            new Thread(() -> {
                try {
                    if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                new net.minecraft.util.ChatComponentText("\u00a7r[\u00a7bF\u00a7r]\u00a7a Checks completed.")));
                    } else {
                        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                new net.minecraft.util.ChatComponentText("\u00a7r[\u00a7bF\u00a7r]\u00a7c Timeout waiting for completion.")));
                    }
                } catch (InterruptedException e) {
                    mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                            new net.minecraft.util.ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cError while waiting: " + e.getMessage())));
                }
            }).start();
        });
    }

    // ─── Urchin タグ一括チェック ──────────────────────────────

    private void checkUrchinTags() {
        catchAndIgnoreNullPointerException(() -> {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            for (final String playerName : onlinePlayers) {
                executor.submit(() -> {
                    try {
                        HypixelApiClient.fetchUUID(playerName);
                        final String tags = HypixelApiClient.fetchUrchinTags(playerName, config.urchinkey)
                                .replace("sniper", "\u00a74\u00a7lSniper")
                                .replace("blatant_cheater", "\u00a74\u00a7lBlatant Cheater")
                                .replace("closet_cheater", "\u00a7e\u00a7lCloset Cheater")
                                .replace("confirmed_cheater", "\u00a74\u00a7lConfirmed Cheater");

                        if (!tags.isEmpty()) {
                            mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                    new net.minecraft.util.ChatComponentText(
                                            "\u00a7r[\u00a7bF\u00a7r] \u00a7c\u26a0 \u00a7r" + getTabDisplayName(playerName) + " \u00a7ris \u00a7ctagged\u00a7r for: " + tags)));
                        }
                    } catch (final IOException e) {
                        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                new net.minecraft.util.ChatComponentText(
                                        "\u00a7r[\u00a7bF\u00a7r] Failed to fetch tags for: " + playerName + " | " + e.getMessage())));
                    }
                });
            }
            executor.shutdown();
        });
    }

    // ─── デュエル stats ───────────────────────────────────────

    private String checkDuels(String playerName) throws IOException {
        String url = "https://plancke.io/hypixel/player/stats/" + playerName;
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        StringBuilder responseText = new StringBuilder();
        try (java.io.InputStream inputStream = connection.getInputStream();
             java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) responseText.append(line);
        }

        String response = responseText.toString();
        if (connection.getResponseCode() == java.net.HttpURLConnection.HTTP_NOT_FOUND) {
            return playerName + " is \u00a7cnicked\u00a7r";
        }

        Pattern namePattern = Pattern.compile("(?<=content=\"Plancke\" /><meta property=\"og:locale\" content=\"en_US\" /><meta property=\"og:description\" content=\").+?(?=\")");
        Matcher nameMatcher = namePattern.matcher(response);
        String displayedName = nameMatcher.find() ? nameMatcher.group() : "Unknown";

        String playerrank = "";
        String[] parts = displayedName.trim().split("\\s+", 2);
        if (parts.length > 0 && parts[0].startsWith("[") && parts[0].endsWith("]")) {
            playerrank = FormatUtil.formatRank(parts[0]) + " ";
        }

        String regex = "<tr><td>Classic 1v1</td><td>([\\d,]+)</td><td>([\\d,]+)</td><td>([\\d.,]+)</td><td>([\\d,]+)</td><td>([\\d,]+)</td><td>([\\d.,]+)</td><td>([\\d.,]+)</td><td>([\\d.,]+)</td></tr>";
        Matcher matcher = Pattern.compile(regex).matcher(response);
        if (matcher.find()) {
            String ClassicStats = "\n\u00a7aKills:\u00a7r " + matcher.group(1) + " \u00a7cDeaths:\u00a7r " + matcher.group(2) + " (\u00a7d" + matcher.group(3) + "\u00a7r) "
                    + "\n" + "\u00a7bW:\u00a7r " + matcher.group(4) + " \u00a7cL: \u00a7r" + matcher.group(5) + " (\u00a7d" + matcher.group(6) + "\u00a7r)";
            return playerrank + playerName + "\u00a7r (Classic 1v1)" + ClassicStats;
        } else {
            return playerrank + playerName + " \u00a7chas no Classic Duels stats.\u00a7r";
        }
    }

    // ─── Scoreboard ユーティリティ ────────────────────────────

    public String getUUIDFromName(String playerName) {
        for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (info.getGameProfile().getName().equalsIgnoreCase(playerName)) {
                return String.valueOf(info.getGameProfile().getId());
            }
        }
        return null;
    }

    private String getTabDisplayName(String playerName) {
        ScorePlayerTeam playerTeam = Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(playerName);
        if (playerTeam == null) return playerName;
        int length = playerTeam.getColorPrefix().length();
        if (length == 10) return playerTeam.getColorPrefix() + playerName + playerTeam.getColorSuffix();
        if (length == 8)  return playerTeam.getColorPrefix() + playerName;
        return playerName;
    }

    private String[] getTabDisplayName2(String playerName) {
        ScorePlayerTeam playerTeam = Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(playerName);
        if (playerTeam == null) return new String[]{"", playerName, ""};
        int length = playerTeam.getColorPrefix().length();
        if (length == 10) return new String[]{playerTeam.getColorPrefix(), playerName, playerTeam.getColorSuffix()};
        if (length == 8)  return new String[]{playerTeam.getColorPrefix(), playerName, ""};
        return new String[]{"", playerName, ""};
    }

    private boolean isBedwars() {
        if (mc.theWorld == null) return false;
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) return false;
        ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebarObjective == null) return false;
        String title = EnumChatFormatting.getTextWithoutFormattingCodes(sidebarObjective.getDisplayName());
        return title.toUpperCase().contains("BED WARS");
    }

    private void catchAndIgnoreNullPointerException(Runnable runnable) {
        try {
            runnable.run();
        } catch (NullPointerException ignored) {
        }
    }
}
