package com.yuki920.statsify.command;

import com.yuki920.statsify.Statsify;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ClearCacheCommand extends CommandBase {
    @Override
    public String getCommandName() { return "cleartabcache"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/cleartabcache"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText("\u00a7r[\u00a7bF\u00a7r] \u00a7aTab cache has been wiped"));
        Statsify.clearPlayerSuffixes();
    }

    @Override
    public int getRequiredPermissionLevel() { return 0; }
}