package com.strixmc.souls.commands;

import com.strixmc.souls.Security;
import com.strixmc.souls.utilities.MembersManager;
import com.strixmc.souls.utilities.PIN;
import com.strixmc.souls.utilities.Utils;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@ACommand(names = "setpin", desc = "Allows you to register your security PIN.", permission = "soul.command.setpin", permissionMessage = "Sorry, but you do not have admin permissions to perform this command.")
@Usage(usage = "/<command> <pin>")
public class SetPin implements CommandClass {

    private Security plugin;

    public SetPin(Security plugin) {
        this.plugin = plugin;
    }

    @ACommand(names = "", desc = "Allows you to register your security PIN.", permission = "soul.command.setpin", permissionMessage = "Sorry, but you do not have admin permissions to perform this command.")
    public boolean command(@Injected CommandSender sender, @Default("Missing") @Named("pin") String pin) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (plugin.getManager().getMember(p.getName()).isRegistered()) {
            p.sendMessage(Utils.c(plugin.getConfig().getString("Registered")));
            return true;
        }

        if (!pin.equals("Missing")) {

            if (!Utils.isNumeric(pin)) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("NotNumber")));
                return true;
            }

            long registerPIN = Long.parseLong(pin);
            int length = pin.length();
            if (length > plugin.getConfig().getInt("Lenght.Max") || length < plugin.getConfig().getInt("Lenght.Min")) {
                p.sendMessage(Utils.c(plugin.getConfig().getString("PinLenght")));
                return true;
            }

            MembersManager manager = plugin.getManager();
            PIN PIN = plugin.getPin();
            manager.getMember(p.getName()).setRegistered(true);
            PIN.registerMember(p.getName(), registerPIN);
            manager.getMember(p.getName()).setPin(registerPIN);

            p.sendMessage(Utils.c(plugin.getConfig().getString("Created").replace("%pin%", String.valueOf(registerPIN))));
            return true;
        }

        return false;
    }
}
