package com.strixmc.souls.utilities;

import com.strixmc.souls.Security;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;

public class GlobalListeners implements Listener {

    private Security plugin;

    public GlobalListeners(Security plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        if (p.isOp() || p.hasPermission("soul.admin")) {
            UpdateChecker.init(plugin, 77520).requestUpdateCheck().whenComplete((result, exception) -> {
                if (result.requiresUpdate()) {
                    p.sendMessage(String.format("An update is available! SoulSecurity %s may be downloaded on SpigotMC", result.getNewestVersion()));
                    return;
                }

                UpdateChecker.UpdateReason reason = result.getReason();
                if (reason == UpdateChecker.UpdateReason.UP_TO_DATE) {
                    p.sendMessage(String.format("Your version of SoulSecurity (%s) is up to date!", result.getNewestVersion()));
                } else if (reason == UpdateChecker.UpdateReason.UNRELEASED_VERSION) {
                    p.sendMessage(String.format("Your version of SoulSecurity (%s) is more recent than the one publicly available. Are you on a development build?", result.getNewestVersion()));
                }
            });
        }
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
    public void damage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            MembersManager manager = plugin.getManager();
            if (manager.containsMember(p.getName())) {
                if (!manager.getMember(p.getName()).isVerified() || !manager.getMember(p.getName()).isRegistered()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            if (!manager.getMember(p.getName()).isVerified() || !manager.getMember(p.getName()).isRegistered()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void inventoryDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();

        MembersManager manager = plugin.getManager();
        if (manager.containsMember(p.getName())) {
            if (!manager.getMember(p.getName()).isRegistered() || !manager.getMember(p.getName()).isVerified()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void entityInteract(PlayerInteractEntityEvent event) {
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
