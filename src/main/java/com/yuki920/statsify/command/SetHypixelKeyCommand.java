package com.yuki920.statsify.command;

import com.yuki920.statsify.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class SetHypixelKeyCommand extends CommandBase {
    @Override
    public String getCommandName() { return "hypixelkey"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/hypixelkey <apikey>"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /hypixelkey <apikey>\u00a7r"));
            return;
        }
        ModConfig.getInstance().hypixelApiKey = args[0];
        ModConfig.getInstance().save();
        sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aHypixel API Key set successfully!"));
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}