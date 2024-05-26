package org.yetiman.yetisutils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final YETIsUtils plugin;

    public ReloadCommand(YETIsUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.reloadWarningsConfig();
            sender.sendMessage("YETIsUtils configurations reloaded.");
            return true;
        }
        return false;
    }
}
