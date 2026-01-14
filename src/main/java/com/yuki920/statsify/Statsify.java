package com.yuki920.statsify;

import com.yuki920.statsify.api.StatsAPI;
import com.yuki920.statsify.command.*; // コマンドパッケージをインポート
import com.yuki920.statsify.config.ModConfig;
import com.yuki920.statsify.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
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

@Mod(modid = Statsify.MODID, name = Statsify.NAME, version = Statsify.VERSION)
public class Statsify {
    public static final String MODID = "statsify";
    public static final String NAME = "Stats Mod";
    public static final String VERSION = "1.0";

    // 外部コマンドからアクセスするため、staticかつクリア可能にしておく
    private static final Map<String, List<String>> playerSuffixes = new HashMap<>();
    
    private final Minecraft mc = Minecraft.getMinecraft();
    private List<String> onlinePlayers = new ArrayList<>();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModConfig.getInstance().load();
        MinecraftForge.EVENT_BUS.register(this);
        
        // コマンド登録
        ClientCommandHandler.instance.registerCommand(new BedwarsCommand());
        ClientCommandHandler.instance.registerCommand(new MinFkdrCommand());
        ClientCommandHandler.instance.registerCommand(new BwModeCommand());
        ClientCommandHandler.instance.registerCommand(new ToggleTagsCommand());
        ClientCommandHandler.instance.registerCommand(new StatsifyCommand());
        ClientCommandHandler.instance.registerCommand(new TablistToggleCommand());
        ClientCommandHandler.instance.registerCommand(new ClearCacheCommand());
        ClientCommandHandler.instance.registerCommand(new SetUrchinKeyCommand());
        ClientCommandHandler.instance.registerCommand(new UrchinTagsToggleCommand());
        ClientCommandHandler.instance.registerCommand(new AutoWhoToggleCommand());
        ClientCommandHandler.instance.registerCommand(new TabFormatSetCommand());
        ClientCommandHandler.instance.registerCommand(new SetHypixelKeyCommand());
    }

    // キャッシュクリア用メソッド (ClearCacheCommandから呼ばれる)
    public static void clearPlayerSuffixes() {
        playerSuffixes.clear();
    }
    
    // タブリストへのデータ登録用メソッド
    public static void sendToTablist(String playerName, String fkdr, String stars) {
        if (playerName != null && fkdr != null && stars != null) {
            playerSuffixes.put(playerName, Arrays.asList(stars, fkdr));
        }
    }

    @SubscribeEvent
    public void onChat(final ClientChatReceivedEvent event) {
        catchAndIgnoreNullPointerException(() -> {
            String message = event.message.getUnformattedText();
            if (ModConfig.getInstance().autowho) {
                if (message.contains("Protect your bed and destroy the enemy beds.") && !(message.contains(":")) && !(message.contains("SHOUT"))) {
                    mc.thePlayer.sendChatMessage("/who");
                }
            }
            if (message.startsWith("ONLINE:")) {
                String playersString = message.substring("ONLINE:".length()).trim();
                String[] players = playersString.split(",\\s*");
                onlinePlayers = new ArrayList<>(Arrays.asList(players));
                if (Objects.equals(ModConfig.getInstance().mode, "bws")) {
                    checkStatsRatelimitless();
                } else {
                    mc.thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cPlancke is discontinued, use BWS."));
                }
                if (ModConfig.getInstance().urchin) {
                    checkUrchinTags();
                }
            }

            if (message.startsWith(" ") && message.contains("Opponent:")) {
                final String username = TextUtil.parseUsername(message);
                new Thread(() -> {
                    try {
                        String stats = StatsAPI.checkDuels(username);
                        mc.thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] " + stats));
                    } catch (IOException e) {
                        mc.thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7c" + username + " is possibly nicked.\u00a7r"));
                    }
                }).start();
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderTabList(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.PLAYER_LIST) return;
        if (!ModConfig.getInstance().tabstats) return;
        
        // mc変数はインスタンスフィールドを使用
        if (mc.thePlayer == null || mc.thePlayer.sendQueue == null) return;

        Collection<NetworkPlayerInfo> playerInfoList = mc.thePlayer.sendQueue.getPlayerInfoMap();
        if (playerInfoList.isEmpty()) return;

        for (NetworkPlayerInfo playerInfo : playerInfoList) {
            if (playerInfo == null || playerInfo.getGameProfile() == null) continue;

            String playerName = playerInfo.getGameProfile().getName();
            List<String> suffixv = playerSuffixes.get(playerName);

            if (suffixv != null && suffixv.size() >= 2) {
                String[] tabData = getTabDisplayName2(playerName);
                String team = tabData[0], name = tabData[1];

                if (!name.endsWith("\u30fb" + suffixv.get(1))) {
                    String teamColor = team.length() >= 2 ? team.substring(0, 2) : "";
                    String newDisplayName;
                    String format = ModConfig.getInstance().tabFormat;
                    
                    if (format.equals("bracket_star_name_dot_fkdr")) {
                        newDisplayName = team + "\u00a77[" + suffixv.get(0) + "\u00a77] " + teamColor + name + "\u30fb" + suffixv.get(1);
                    } else if (format.equals("star_dot_name_dot_fkdr")) {
                        newDisplayName = team + suffixv.get(0) + "\u30fb" + teamColor + name + "\u30fb" + suffixv.get(1);
                    } else if (format.equals("name_dot_fkdr")) {
                        newDisplayName = team + teamColor + name + "\u30fb" + suffixv.get(1);
                    } else {
                        newDisplayName = team + "\u00a77[" + suffixv.get(0) + "\u00a77] " + teamColor + name + "\u30fb" + suffixv.get(1);
                    }
                    playerInfo.setDisplayName(new ChatComponentText(newDisplayName));
                }
            }
        }
    }

    private String getTabDisplayName(String playerName) {
        ScorePlayerTeam playerTeam = mc.theWorld.getScoreboard().getPlayersTeam(playerName);
        if (playerTeam == null) return playerName;
        int length = playerTeam.getColorPrefix().length();
        if (length == 10) return playerTeam.getColorPrefix() + playerName + playerTeam.getColorSuffix();
        if (length == 8) return playerTeam.getColorPrefix() + playerName;
        return playerName;
    }

    private String[] getTabDisplayName2(String playerName) {
        ScorePlayerTeam playerTeam = mc.theWorld.getScoreboard().getPlayersTeam(playerName);
        if (playerTeam == null) return new String[]{"", playerName, ""};
        int length = playerTeam.getColorPrefix().length();
        if (length == 10) return new String[]{playerTeam.getColorPrefix(), playerName, playerTeam.getColorSuffix()};
        if (length == 8) return new String[]{playerTeam.getColorPrefix(), playerName, ""};
        return new String[]{"", playerName, ""};
    }

    private void checkUrchinTags() {
        catchAndIgnoreNullPointerException(() -> {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            for (final String playerName : onlinePlayers) {
                executor.submit(() -> {
                    try {
                        StatsAPI.fetchUUID(playerName);
                        final String tags = StatsAPI.fetchUrchinTags(playerName)
                                .replace("sniper", "\u00a74\u00a7lSniper")
                                .replace("blatant_cheater", "\u00a74\u00a7lBlatant Cheater")
                                .replace("closet_cheater", "\u00a7e\u00a7lCloset Cheater")
                                .replace("confirmed_cheater", "\u00a74\u00a7lConfirmed Cheater");

                        if (ModConfig.getInstance().tags && !tags.isEmpty()) {
                            mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                    new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7c\u26a0 \u00a7r" + getTabDisplayName(playerName) + " \u00a7ris \u00a7ctagged\u00a7r for: " + tags)
                            ));
                        }
                    } catch (final IOException e) {
                        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] Failed to fetch tags for: " + playerName + " | " + e.getMessage())
                        ));
                    }
                });
            }
            executor.shutdown();
        });
    }

    private void checkStatsRatelimitless() {
        catchAndIgnoreNullPointerException(() -> {
            final int MAX_THREADS = 20;
            int poolSize = Math.min(onlinePlayers.size(), MAX_THREADS);
            final ExecutorService executor = Executors.newFixedThreadPool(poolSize);

            for (final String playerName : onlinePlayers) {
                executor.submit(() -> {
                    try {
                        final String stats = StatsAPI.fetchBedwarsStats(playerName);
                        if (!stats.isEmpty()) {
                            mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                    new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] " + stats)
                            ));
                        }
                    } catch (IOException e) {
                        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(
                                new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] Failed to fetch stats for: " + playerName + " | [UpstreamCSR] ")
                        ));
                    }
                });
            }

            executor.shutdown();

            new Thread(() -> {
                try {
                    if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r]\u00a7a Checks completed.")));
                    } else {
                        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r]\u00a7c Timeout waiting for completion.")));
                    }
                } catch (final InterruptedException e) {
                    mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cError while waiting: " + e.getMessage())));
                }
            }).start();
        });
    }

    private void catchAndIgnoreNullPointerException(Runnable runnable) {
        try {
            runnable.run();
        } catch (NullPointerException e) {
        }
    }
}