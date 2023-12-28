package org.hello.punishplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PunishPlugin extends JavaPlugin implements CommandExecutor {

    private static JavaPlugin pluginInstance;

    @Override
    public void onEnable() {
        this.getCommand("punish").setExecutor(this);
        pluginInstance = this;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /punish <player> <effectCode> <hours>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        int effectCode;
        try {
            effectCode = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid effect code.");
            return true;
        }

        int hours;
        try {
            hours = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid hours value.");
            return true;
        }

        applyPunishment(target, effectCode, hours * 3600); // Convert hours to seconds
        sender.sendMessage(ChatColor.GREEN + target.getName() + " has been punished.");

        return true;
    }

    private void applyPunishment(Player player, int effectCode, int durationSeconds) {
        switch (effectCode) {
            case 1:
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, durationSeconds, 255));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, durationSeconds, 255));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, durationSeconds, 255));
                break;
            // Add more cases if needed for different punishment types
            default:
                break;
        }

        // Schedule a task to remove the effects after the duration expires
        new BukkitRunnable() {
            @Override
            public void run() {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                player.removePotionEffect(PotionEffectType.SLOW);
            }
        }.runTaskLater(pluginInstance, durationSeconds * 20); // Convert seconds to ticks
    }
}
