package com.yuki920.statsify.command;

import com.yuki920.statsify.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class BwModeCommand extends CommandBase {
    @Override
    public String getCommandName() { return "bwmode"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/bwmode <plancke/bws>"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1 || (!args[0].equalsIgnoreCase("plancke") && !args[0].equalsIgnoreCase("bws"))) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /bwmode <bws>"));
            return;
        }
        ModConfig.getInstance().mode = args[0].toLowerCase();
        ModConfig.getInstance().save();
        sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aMode set to: " + ModConfig.getInstance().mode));
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}