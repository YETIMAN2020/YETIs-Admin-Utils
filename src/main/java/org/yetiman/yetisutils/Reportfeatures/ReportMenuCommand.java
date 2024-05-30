package org.yetiman.yetisutils.Reportfeatures;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportMenuCommand implements CommandExecutor {
    private final ReportHandler reportHandler;
    private final ReportGUI reportGUI;

    public ReportMenuCommand(ReportHandler reportHandler, ReportGUI reportGUI) {
        this.reportHandler = reportHandler;
        this.reportGUI = reportGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        reportGUI.openReportMenu(player);
        return true;
    }
}
