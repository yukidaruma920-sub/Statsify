package com.yuki920.statsify.command;

import com.yuki920.statsify.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class MinFkdrCommand extends CommandBase {
    @Override
    public String getCommandName() { return "minfkdr"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/minfkdr <number>"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /minfkdr <number>\u00a7r"));
            return;
        }
        try {
            int value = Integer.parseInt(args[0]);
            ModConfig.getInstance().minFkdr = value;
            ModConfig.getInstance().save();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aMinimum FKDR set to: " + value));
        } catch (NumberFormatException e) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid number! Use an integer value."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}