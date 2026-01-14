package com.yuki920.statsify.command;

import com.yuki920.statsify.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class UrchinTagsToggleCommand extends CommandBase {
    @Override
    public String getCommandName() { return "urchin"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/urchin <on/off>"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1 || (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /urchin <on/off>"));
            return;
        }
        boolean enable = args[0].equalsIgnoreCase("on");
        ModConfig.getInstance().urchin = enable;
        ModConfig.getInstance().save();
        sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aUrchinAPI toggled: " + args[0].toLowerCase()));
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}