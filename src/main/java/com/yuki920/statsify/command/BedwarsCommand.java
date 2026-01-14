package com.yuki920.statsify.command;

import com.yuki920.statsify.api.StatsAPI;
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

public class BedwarsCommand extends CommandBase {

    @Override
    public String getCommandName() { return "bw"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/bw <username>"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r]\u00a7cInvalid usage!\u00a7r Use /bw \u00a75<username>\u00a7r"));
            return;
        }

        final String username = args[0];
        new Thread(() -> {
            try {
                final String stats = StatsAPI.fetchPlayerStatsForCommand(username);
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] " + stats)));
            } catch (IOException e) {
                Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cFailed to fetch stats for: \u00a7r" + username)));
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

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}