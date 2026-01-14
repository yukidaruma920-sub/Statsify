package com.yuki920.statsify.command;

import com.yuki920.statsify.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class TabFormatSetCommand extends CommandBase {
    @Override
    public String getCommandName() { return "tabformat"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/tabformat <preset>"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a7b\u00a7lfon\u00a79\u00a7lta\u00a73\u00a7line\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a7bmade by\u00a7e melissalmao\u00a7r"));
            sender.addChatMessage(new ChatComponentText(""));
            sender.addChatMessage(new ChatComponentText("\u00a7r\u00a73Use /tabformat <number> to select a preset.\u00a7r"));
            sender.addChatMessage(new ChatComponentText("\u00a7r        1) \u00a7dP \u00a77[\u00a76200\u272b\u00a77] \u00a7dFontaine\u30fb\u00a7f1.36\n" +
                    "\u00a7r        2) \u00a7dP \u00a76200\u272b\u30fb\u00a7dFontaine\u30fb\u00a7f1.36\n" +
                    "\u00a7r        3) \u00a7dP \u00a7dFontaine\u30fb\u00a7f1.36\n"));
            sender.addChatMessage(new ChatComponentText(""));
            return;
        }

        try {
            String value = args[0];
            if ("1".equals(value)) {
                ModConfig.getInstance().tabFormat = "bracket_star_name_dot_fkdr";
                ModConfig.getInstance().save();
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a72Tablist format set to 1."));
            } else if ("2".equals(value)) {
                ModConfig.getInstance().tabFormat = "star_dot_name_dot_fkdr";
                ModConfig.getInstance().save();
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a72Tablist format set to 2."));
            } else if ("3".equals(value)) {
                ModConfig.getInstance().tabFormat = "name_dot_fkdr";
                ModConfig.getInstance().save();
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a72Tablist format set to 3."));
            } else {
                sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid value. 1-3."));
            }
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cfish."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}