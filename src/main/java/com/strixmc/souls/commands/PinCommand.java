package com.strixmc.souls.commands;

import com.strixmc.souls.Security;
import com.strixmc.souls.utilities.MembersManager;
import com.strixmc.souls.utilities.Utils;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ACommand(names = "pin", desc = "Allows you to login using your security PIN.", permission = "soul.command.pin", permissionMessage = "Sorry, but you do not have admin permissions to perform this command.")
@Usage(usage = "/<command> <pin>")
public class PinCommand implements CommandClass {

    private Security plugin;

    public PinCommand(Security plugin) {
        this.plugin = plugin;
    }


    @ACommand(names = "", desc = "Allows you to login using your security PIN.", permission = "soul.command.pin", permissionMessage = "Sorry, but you do not have admin permissions to perform this command.")
    @Usage(usage = "/<command> <pin>")
    public boolean command(@Injected CommandSender sender, @Default("Missing") @Named("pin") String pin) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;
        if (!plugin.getManager().getMember(p.getName()).isRegistered()) {
            p.sendMessage(Utils.c(plugin.getConfig().getString("Unregistered")));
            return true;
        }

        if (plugin.getManager().getMember(p.getName()).isVerified()) {
            p.sendMessage(Utils.c(plugin.getConfig().getString("Logged")));
            return true;
        }

        if (!pin.equals("Missing")) {

            if (!Utils.isNumeric(pin)) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("NotNumber")));
                return true;
            }

            long registeredPIN = Long.parseLong(pin);

            MembersManager manager = plugin.getManager();

            if (manager.getMember(p.getName()).getPin() != registeredPIN) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("Incorrect")));
                if (plugin.getConfig().getBoolean("Kick-Wrong")) {
                    p.kickPlayer(Utils.c(plugin.getConfig().getString("Incorrect")));
                }
                return true;
            }

            manager.getMember(p.getName()).setVerified(true);
            p.sendMessage(Utils.c(plugin.getConfig().getString("Correct")));
            return true;
        }

        return false;
    }
}
