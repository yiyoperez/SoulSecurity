package com.strixmc.souls.commands;

import com.strixmc.souls.Security;
import com.strixmc.souls.utilities.MembersManager;
import com.strixmc.souls.utilities.PIN;
import com.strixmc.souls.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoulCommand implements CommandExecutor {

    private Security plugin;

    public SoulCommand(Security plugin) {
        this.plugin = plugin;
    }

    private void sendMessage(CommandSender sender, String text) {
        String message = Utils.c(text);
        if (sender instanceof Player) {
            sender.sendMessage(message);
        } else {
            sender.sendMessage(ChatColor.stripColor(message));
        }
    }

    private void sendHelp(CommandSender sender, String command) {
        for (String s : plugin.getConfig().getStringList("Help-Message")) {
            sendMessage(sender, s.replace("%command%", command));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("soul.admin")) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("NoPermissions")));
                return true;
            }
        }
        if (args.length >= 1) {
            PIN pin = plugin.getPin();
            MembersManager manager = plugin.getManager();
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(sender, label);
            } else if (args[0].equalsIgnoreCase("unregister")) {
                if (args.length == 2) {
                    String target = args[1];
                    if (!pin.containsMember(target)) {
                        sendMessage(sender, "&cThat player is not in the database.");
                        return true;
                    }
                    pin.unregisterMember(target);

                    if (manager.containsMember(target)) {
                        manager.removeMember(target);
                    }

                    sendMessage(sender, "&cPlayer has been removed.");

                    if (Bukkit.getPlayer(target) != null) {
                        Bukkit.getPlayer(target).kickPlayer(Utils.c("&cYou have been unregistered from PIN Security."));
                    }

                } else {
                    sendMessage(sender, "&cUsage: /" + label + " unregister <Player>");
                }
            } else if (args[0].equalsIgnoreCase("changepin")) {
                if (args.length == 3) {
                    String target = args[1];

                    if (!pin.containsMember(target)) {
                        sendMessage(sender, "&cThat player is not in the database.");
                        return true;
                    }

                    if (!Utils.isNumeric(args[2])) {
                        sendMessage(sender, plugin.getConfig().getString("NotNumber"));
                        return true;
                    }

                    long newpin = Long.parseLong(args[2]);
                    int length = args[2].length();
                    if (length > plugin.getConfig().getInt("Lenght.Max") || length < plugin.getConfig().getInt("Lenght.Min")) {
                        sendMessage(sender, plugin.getConfig().getString("PinLenght"));
                        return true;
                    }

                    pin.setPIN(target, newpin);
                    sendMessage(sender, "Member " + pin.getMember(target) + " PIN has been changed to " + newpin);
                    if (Bukkit.getPlayer(target) != null) {
                        Bukkit.getPlayer(target).sendMessage(Utils.c("&cYou PIN has been changed!"));
                        manager.getMember(Bukkit.getPlayer(target).getName()).setPin(newpin);
                        manager.getMember(Bukkit.getPlayer(target).getName()).setVerified(false);
                    }

                } else {
                    sendMessage(sender, "&cUsage: /" + label + " changepin <Player> <PIN>");
                }
            } else {
                sendHelp(sender, label);
            }
        } else {
            sendHelp(sender, label);
        }
        return false;
    }
}
