package com.strixmc.souls.utilities;

import com.strixmc.souls.Security;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;

public class GlobalListeners implements Listener {

    private Security plugin;

    public GlobalListeners(Security plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            manager.removeMember(p.getName());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            if (!manager.getMember(p.getName()).isVerified() || !manager.getMember(p.getName()).isRegistered()) {
                final Location loc = p.getLocation();
                final double x = p.getLocation().getX();
                final double z = p.getLocation().getZ();
                final double toX = event.getTo().getX();
                final double toZ = event.getTo().getZ();
                if (x != toX || z != toZ) {
                    event.setCancelled(true);
                    p.teleport(loc);
                }
            }
        }
    }

    @EventHandler
    public void interact(final PlayerInteractEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            if (!manager.getMember(p.getName()).isVerified() || !manager.getMember(p.getName()).isRegistered()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void entityinteract(final PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            if (!manager.getMember(p.getName()).isVerified() || !manager.getMember(p.getName()).isRegistered()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            if (!manager.getMember(p.getName()).isVerified() || !manager.getMember(p.getName()).isRegistered()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void commands(final PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            if (!manager.getMember(p.getName()).isVerified() || !manager.getMember(p.getName()).isRegistered()) {
                final ArrayList<String> cmds = new ArrayList<>();
                cmds.add("pin");
                cmds.add("setpin");
                final String command = event.getMessage().split("/")[1].split(" ")[0];
                if (!cmds.contains(command.toLowerCase())) {
                    event.setCancelled(true);
                } else {
                    event.setCancelled(false);
                }
            }
        }
    }

    @EventHandler
    public void click(final InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            if (!manager.getMember(p.getName()).isVerified() || !manager.getMember(p.getName()).isRegistered()) {
                event.setCancelled(true);
                p.updateInventory();
            }
        }
    }

}
