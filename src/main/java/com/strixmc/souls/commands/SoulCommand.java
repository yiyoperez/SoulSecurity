package com.strixmc.souls.commands;

import com.strixmc.souls.Security;
import com.strixmc.souls.utilities.MembersManager;
import com.strixmc.souls.utilities.PIN;
import com.strixmc.souls.utilities.Utils;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ACommand(names = {"soul", "souls", "soulsecurity"}, desc = "Main SoulSecurity management command.", permission = "soul.admin", permissionMessage = "Sorry, but you do not have admin permissions to perform this command.")
@Usage(usage = "/<command> <unregister, changepin>")
public class SoulCommand implements CommandClass {

    private Security plugin;

    public SoulCommand(Security plugin) {
        this.plugin = plugin;
    }

    @ACommand(names = "", permission = "soul.admin", permissionMessage = "Sorry, but you do not have admin permissions to perform this command.")
    public boolean command(@Injected CommandSender sender) {

        for (String s : plugin.getConfig().getStringList("Help-Message")) {
            sendMessage(sender, Utils.c(s));
        }

        return true;
    }

    @ACommand(names = "unregister", desc = "SoulSecurity unregister command.", permission = "soul.admin", permissionMessage = "Sorry, but you do not have admin permissions to perform this command.")
    @Usage(usage = "/<command> unregister <player>")
    public boolean unregisterCommand(@Injected(true) CommandSender sender, @Default("Missing") @Named("argument") String target) {

        if (!target.equals("Missing")) {
            PIN pin = plugin.getPin();
            MembersManager manager = plugin.getManager();

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
            return true;
        }
        return false;
    }

    @ACommand(names = "changepin", desc = "SoulSecurity changepin command.", permission = "soul.admin", permissionMessage = "Sorry, but you do not have admin permissions to perform this command.")
    @Usage(usage = "/<command> changepin <player> <pin>")
    public boolean changepinCommand(@Injected(true) CommandSender sender, @Default("Missing") @Named("target") String target, @Default("Missing") @Named("changed") String changed) {

        PIN pin = plugin.getPin();
        MembersManager manager = plugin.getManager();

        if (!target.equals("Missing")) {
            if (!pin.containsMember(target)) {
                sendMessage(sender, "&cThat player is not in the database.");
                return true;
            }
            if (!changed.equals("Missing")) {

                if (!Utils.isNumeric(changed)) {
                    sendMessage(sender, plugin.getConfig().getString("NotNumber"));
                    return true;
                }

                long newpin = Long.parseLong(changed);
                int length = changed.length();
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
            }
        }

        return false;
    }

    private void sendMessage(CommandSender sender, String text) {
        String message = Utils.c(text);
        if (sender instanceof Player) {
            sender.sendMessage(message);
        } else {
            sender.sendMessage(ChatColor.stripColor(message));
        }
    }

}
