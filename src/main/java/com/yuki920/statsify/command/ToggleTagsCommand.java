package com.yuki920.statsify.command;

import com.yuki920.statsify.config.ModConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ToggleTagsCommand extends CommandBase {
    @Override
    public String getCommandName() { return "bwtags"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/bwtags <info/on/off>"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1 || (!args[0].equalsIgnoreCase("info") && !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off"))) {
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7cInvalid usage! Use /bwtags <info/on/off>"));
            return;
        }

        String arg = args[0].toLowerCase();
        if (arg.equalsIgnoreCase("info")) {
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
            boolean enable = arg.equalsIgnoreCase("on");
            ModConfig.getInstance().tags = enable;
            ModConfig.getInstance().save();
            sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aTags toggled: " + arg));
        }
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}