package com.strixmc.souls.commands;

import com.strixmc.souls.Security;
import com.strixmc.souls.utilities.MembersManager;
import com.strixmc.souls.utilities.PIN;
import com.strixmc.souls.utilities.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPin implements CommandExecutor {

    private Security plugin;

    public SetPin(Security plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("soul.command.setpin")){
                p.sendMessage(Utils.c(plugin.getConfig().getString("NoPermissions")));
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("Usage").replace("%command%", label)));
            } else {

                if (plugin.getManager().getMember(p.getName()).isRegistered()) {
                    p.sendMessage(Utils.c(plugin.getConfig().getString("Registered")));
                    return true;
                }

                if (!Utils.isNumeric(args[0])) {
                    p.sendMessage(Utils.c(plugin.getConfig().getString("NotNumber")));
                    return true;
                }

                long pin = Long.parseLong(args[0]);
                int length = args[0].length();
                if (length > plugin.getConfig().getInt("Lenght.Max") || length < plugin.getConfig().getInt("Lenght.Min")) {
                    p.sendMessage(Utils.c(plugin.getConfig().getString("PinLenght")));
                    return true;
                }

                MembersManager manager = plugin.getManager();
                PIN PIN = plugin.getPin();
                manager.getMember(p.getName()).setRegistered(true);
                PIN.registerMember(p.getName(), pin);
                manager.getMember(p.getName()).setPin(pin);

                p.sendMessage(Utils.c(plugin.getConfig().getString("Created").replace("%pin%", String.valueOf(pin))));
            }
        }
        return false;
    }
}
