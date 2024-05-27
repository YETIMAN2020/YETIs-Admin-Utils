package org.yetiman.yetisutils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportMenuCommand implements CommandExecutor {
    private final YETIsUtils plugin;
    private final ReportHandler reportHandler;

    public ReportMenuCommand(YETIsUtils plugin, ReportHandler reportHandler) {
        this.plugin = plugin;
        this.reportHandler = reportHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        openReportMenu(player);

        return true;
    }

    private void openReportMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "Player Reports");

        List<Report> allReports = reportHandler.getAllReports();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        for (Report report : allReports) {
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + "Report by " + report.getReporter());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GOLD + "Issue: " + report.getIssue());
                lore.add(ChatColor.AQUA + "Date: " + dateFormatter.format(report.getDate()));
                meta.setLore(lore);
                paper.setItemMeta(meta);
            }
            inv.addItem(paper);
        }

        ItemStack closeButton = createGuiItem(Material.BARRIER, ChatColor.RED + "Close Report");
        inv.setItem(53, closeButton);

        player.openInventory(inv);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> metaLore = new ArrayList<>();
            for (String loreEntry : lore) {
                metaLore.add(loreEntry);
            }
            meta.setLore(metaLore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
