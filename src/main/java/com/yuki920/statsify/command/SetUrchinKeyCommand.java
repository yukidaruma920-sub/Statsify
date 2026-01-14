package com.yuki920.statsify.command;

import com.yuki920.statsify.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class SetUrchinKeyCommand extends CommandBase {
    @Override
    public String getCommandName() { return "urchinkey"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/urchinkey <apikey>"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /urchinkey <apikey>\u00a7r"));
            return;
        }
        try {
            ModConfig.getInstance().urchinkey = args[0];
            ModConfig.getInstance().save();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aUrchin API Key set."));
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cfish."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}