package com.strixmc.souls.commands;

import com.strixmc.souls.Security;
import com.strixmc.souls.utilities.MembersManager;
import com.strixmc.souls.utilities.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PinCommand implements CommandExecutor {

    private Security plugin;

    public PinCommand(Security plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("soul.command.pin")) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("NoPermissions")));
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("Usage").replace("%command%", label)));
            } else {
                if (!plugin.getManager().getMember(p.getName()).isRegistered()) {
                    p.sendMessage(Utils.c(plugin.getConfig().getString("Unregistered")));
                    return true;
                }

                if (plugin.getManager().getMember(p.getName()).isVerified()) {
                    p.sendMessage(Utils.c(plugin.getConfig().getString("Logged")));
                    return true;
                }

                if (!Utils.isNumeric(args[0])) {
                    p.sendMessage(Utils.c(plugin.getConfig().getString("NotNumber")));
                    return true;
                }

                long pin = Long.parseLong(args[0]);

                MembersManager manager = plugin.getManager();

                if (manager.getMember(p.getName()).getPin() != pin) {
                    p.sendMessage(Utils.c(plugin.getConfig().getString("Incorrect")));
                    if (plugin.getConfig().getBoolean("Kick-Wrong")) {
                        p.kickPlayer(Utils.c(plugin.getConfig().getString("Incorrect")));
                    }
                    return true;
                }

                manager.getMember(p.getName()).setVerified(true);
                p.sendMessage(Utils.c(plugin.getConfig().getString("Correct")));

            }
        }
        return false;
    }
}
