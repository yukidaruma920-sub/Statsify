package com.yuki920.statsify.commands;

import com.yuki920.statsify.Statsify;
import com.yuki920.statsify.util.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Statsify の全コマンドを内部クラスとして提供するホルダークラス。
 * 各コマンドは Statsify.init() から ClientCommandHandler へ登録される。
 */
public class Commands {

    // ─── /bw <username> ─────────────────────────────────────

    public static class BedwarsCommand extends CommandBase {
        private final Statsify mod;
        public BedwarsCommand(Statsify mod) { this.mod = mod; }

        @Override public String getCommandName() { return "bw"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/bw <username>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r]\u00a7cInvalid usage!\u00a7r Use /bw \u00a75<username>\u00a7r"));
                return;
            }
            final String username = args[0];
            new Thread(() -> {
                try {
                    final String stats = mod.fetchPlayerStatsForCommand(username);
                    Minecraft.getMinecraft().addScheduledTask(() ->
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] " + stats)));
                } catch (IOException e) {
                    Minecraft.getMinecraft().addScheduledTask(() ->
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cFailed to fetch stats for: \u00a7r" + username)));
                }
            }).start();
        }

        @Override
        public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
            if (args.length == 1) {
                Collection<NetworkPlayerInfo> playerInfoMap = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
                List<String> playerNames = playerInfoMap.stream()
                        .map(info -> info.getGameProfile().getName())
                        .collect(Collectors.toList());
                return getListOfStringsMatchingLastWord(args, playerNames.toArray(new String[0]));
            }
            return null;
        }
    }

    // ─── /minfkdr <number> ──────────────────────────────────

    public static class MinFkdrCommand extends CommandBase {
        private final Statsify mod;
        private final ConfigManager config;
        public MinFkdrCommand(Statsify mod, ConfigManager config) { this.mod = mod; this.config = config; }

        @Override public String getCommandName() { return "minfkdr"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/minfkdr <number>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /minfkdr <number>\u00a7r"));
                return;
            }
            try {
                config.minFkdr = Integer.parseInt(args[0]);
                config.save();
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aMinimum FKDR set to: " + config.minFkdr));
            } catch (NumberFormatException e) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid number! Use an integer value."));
            }
        }
    }

    // ─── /bwmode <bws> ──────────────────────────────────────

    public static class BwModeCommand extends CommandBase {
        private final ConfigManager config;
        public BwModeCommand(ConfigManager config) { this.config = config; }

        @Override public String getCommandName() { return "bwmode"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/bwmode <plancke/bws>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1 || (!args[0].equalsIgnoreCase("plancke") && !args[0].equalsIgnoreCase("bws"))) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /bwmode <bws>"));
                return;
            }
            config.mode = args[0].toLowerCase();
            config.save();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aMode set to: " + config.mode));
        }
    }

    // ─── /bwtags <info|on|off> ───────────────────────────────

    public static class ToggleTagsCommand extends CommandBase {
        private final ConfigManager config;
        public ToggleTagsCommand(ConfigManager config) { this.config = config; }

        @Override public String getCommandName() { return "bwtags"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/bwtags <info/on/off>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1 || (!args[0].equalsIgnoreCase("info") && !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /bwtags <info/on/off>"));
                return;
            }
            String arg = args[0].toLowerCase();
            if (arg.equals("info")) {
                sender.addChatMessage(new ChatComponentText("\u00a7r\u00a7b\u00a7lfon\u00a79\u00a7lta\u00a73\u00a7line\u00a7r"));
                sender.addChatMessage(new ChatComponentText("\u00a7r\u00a7bmade by\u00a7e melissalmao\u00a7r"));
                sender.addChatMessage(new ChatComponentText(""));
                sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73Tags are in early development and WILL slow down checking stats.\u00a7r"));
                sender.addChatMessage(new ChatComponentText("\u00a7r        N = Suspicious name (kikin, mchk, msmc, 4+ number in name..)\n" +
                        "        W = Winstreak while being low star (1+ WS when 1-6 star)\n" +
                        "        F = Suspiciously high fkdr while being low star (4+ when 1 - 6 star)\n" +
                        "        SK = Default skin\n" +
                        "        NL = New login (today or yesterday first login)\n" +
                        "        0F = 0 final kills 0 final deaths\u00a7r"));
                sender.addChatMessage(new ChatComponentText(""));
            } else {
                config.tags = arg.equals("on");
                config.save();
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aTags toggled: " + arg));
            }
        }
    }

    // ─── /tabstats <on|off> ──────────────────────────────────

    public static class TablistToggleCommand extends CommandBase {
        private final ConfigManager config;
        public TablistToggleCommand(ConfigManager config) { this.config = config; }

        @Override public String getCommandName() { return "tabstats"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/tabstats <on/off>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1 || (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /tabstats <on/off>"));
                return;
            }
            config.tabstats = args[0].equalsIgnoreCase("on");
            config.save();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aTabstats toggled: " + args[0].toLowerCase()));
        }
    }

    // ─── /cleartabcache ──────────────────────────────────────

    public static class ClearCacheCommand extends CommandBase {
        @Override public String getCommandName() { return "cleartabcache"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/cleartabcache"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            Statsify.playerSuffixes.clear();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aTab cache has been wiped"));
        }
    }

    // ─── /urchinkey <apikey> ─────────────────────────────────

    public static class SetUrchinKeyCommand extends CommandBase {
        private final ConfigManager config;
        public SetUrchinKeyCommand(ConfigManager config) { this.config = config; }

        @Override public String getCommandName() { return "urchinkey"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/urchinkey <apikey>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /urchinkey <apikey>\u00a7r"));
                return;
            }
            config.urchinkey = args[0];
            config.save();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aUrchin API Key set to: " + args[0]));
        }
    }

    // ─── /hypixelkey <apikey> ────────────────────────────────

    public static class SetHypixelKeyCommand extends CommandBase {
        private final ConfigManager config;
        public SetHypixelKeyCommand(ConfigManager config) { this.config = config; }

        @Override public String getCommandName() { return "hypixelkey"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/hypixelkey <apikey>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /hypixelkey <apikey>\u00a7r"));
                return;
            }
            config.hypixelApiKey = args[0];
            config.save();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aHypixel API Key set successfully!"));
        }
    }

    // ─── /urchin <on|off> ────────────────────────────────────

    public static class UrchinTagsToggleCommand extends CommandBase {
        private final ConfigManager config;
        public UrchinTagsToggleCommand(ConfigManager config) { this.config = config; }

        @Override public String getCommandName() { return "urchin"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/urchin <on/off>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1 || (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /urchin <on/off>"));
                return;
            }
            config.urchin = args[0].equalsIgnoreCase("on");
            config.save();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aUrchinAPI toggled: " + args[0].toLowerCase()));
        }
    }

    // ─── /bwautowho <on|off> ─────────────────────────────────

    public static class AutoWhoToggleCommand extends CommandBase {
        private final ConfigManager config;
        public AutoWhoToggleCommand(ConfigManager config) { this.config = config; }

        @Override public String getCommandName() { return "bwautowho"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/bwautowho <on/off>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1 || (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /bwautowho <on/off>"));
                return;
            }
            config.autowho = args[0].equalsIgnoreCase("on");
            config.save();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aautoWHO: " + args[0].toLowerCase()));
        }
    }

    // ─── /tabformat <1-3> ────────────────────────────────────

    public static class TabFormatSetCommand extends CommandBase {
        private final ConfigManager config;
        public TabFormatSetCommand(ConfigManager config) { this.config = config; }

        @Override public String getCommandName() { return "tabformat"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/tabformat <preset>"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length != 1) {
                sender.addChatMessage(new ChatComponentText("\u00a7r\u00a7b\u00a7lfon\u00a79\u00a7lta\u00a73\u00a7line\u00a7r"));
                sender.addChatMessage(new ChatComponentText("\u00a7r\u00a7bmade by\u00a7e melissalmao\u00a7r"));
                sender.addChatMessage(new ChatComponentText(""));
                sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73Use /tabformat <number> to select a preset.\u00a7r"));
                sender.addChatMessage(new ChatComponentText("\u00a7r        1) \u00a7dP \u00a76[200\u272b] \u00a7dFontaine \u00a78| \u00a7a1.36\n" +
                        "\u00a7r        2) \u00a7dP \u00a76200\u272b \u00a78| \u00a7dFontaine \u00a78| \u00a7a1.36\n" +
                        "\u00a7r        3) \u00a7dP \u00a7dFontaine \u00a78| \u00a7a1.36\n"));
                sender.addChatMessage(new ChatComponentText(""));
                return;
            }
            switch (args[0]) {
                case "1":
                    config.tabFormat = "bracket_star_name_dot_fkdr";
                    config.save();
                    sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a72Tablist format set to 1."));
                    break;
                case "2":
                    config.tabFormat = "star_dot_name_dot_fkdr";
                    config.save();
                    sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a72Tablist format set to 2."));
                    break;
                case "3":
                    config.tabFormat = "name_dot_fkdr";
                    config.save();
                    sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a72Tablist format set to 3."));
                    break;
                default:
                    sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid value. 1-3."));
            }
        }
    }

    // ─── /st (ヘルプ) ─────────────────────────────────────────

    public static class StatsifyCommand extends CommandBase {
        @Override public String getCommandName() { return "st"; }
        @Override public String getCommandUsage(ICommandSender sender) { return "/st"; }
        @Override public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a7b\u00a7lfon\u00a79\u00a7lta\u00a73\u00a7line\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a7bmade by\u00a7e melissalmao\u00a7r"));
            sender.addChatMessage(new ChatComponentText(""));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/bw <username>:\u00a7b Manually check bedwars stats of a player.\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/minfkdr <value>:\u00a7b Set minimum FKDR to show on running /who. Default -1.\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/bwtags <info/on/off>:\u00a7b Toggle tags on /who (on/off) or view information (info). Default off. [indev]\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/tabstats <on/off>:\u00a7b Toggle printing stats on tablist. Default on.\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/tabformat <1-3>:\u00a7b Edit the way stats show on your tablist. /tabformat for info.\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/cleartabcache:\u00a7b Clear stats cache of players if you're having issues.\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/urchin <on/off>:\u00a7b Toggle Urchin API on and off. Default off.\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/urchinkey <key>:\u00a7b Set your urchin API key (discord.gg/urchin)\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/who:\u00a7b Check and print the stats of the players in your lobby.\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/bwautowho:\u00a7b Automatically run /who on game start. Default on.\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/hypixelkey <apikey>:\u00a7b Set your Hypixel API Key (https://developer.hypixel.net/dashboard/).\u00a7r"));
            sender.addChatMessage(new ChatComponentText(""));
        }
    }
}
