package com.yuki920.statsify.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class StatsifyCommand extends CommandBase {
    @Override
    public String getCommandName() { return "st"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/st"; }

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
        sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73/hypixelkey <apikey>:\u00a7b Set your Hypixel API Key.\u00a7r"));
        sender.addChatMessage(new ChatComponentText(""));
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}