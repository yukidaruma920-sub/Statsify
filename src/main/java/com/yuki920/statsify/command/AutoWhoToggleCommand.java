package com.yuki920.statsify.command;

import com.yuki920.statsify.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class AutoWhoToggleCommand extends CommandBase {
    @Override
    public String getCommandName() { return "bwautowho"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/bwautowho <on/off>"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1 || (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /bwautowho <on/off>"));
            return;
        }
        boolean enable = args[0].equalsIgnoreCase("on");
        ModConfig.getInstance().autowho = enable;
        ModConfig.getInstance().save();
        sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aautoWHO: " + args[0].toLowerCase()));
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}