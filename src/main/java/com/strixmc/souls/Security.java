package com.strixmc.souls;

import com.strixmc.souls.commands.PinCommand;
import com.strixmc.souls.commands.SetPin;
import com.strixmc.souls.commands.SoulCommand;
import com.strixmc.souls.utilities.*;
import com.strixmc.souls.utilities.menusystem.PlayerMenuUtilityManager;
import fr.xephi.authme.api.v3.AuthMeApi;
import me.fixeddev.ebcm.*;
import me.fixeddev.ebcm.bukkit.BukkitAuthorizer;
import me.fixeddev.ebcm.bukkit.BukkitCommandManager;
import me.fixeddev.ebcm.bukkit.BukkitMessager;
import me.fixeddev.ebcm.bukkit.parameter.provider.BukkitModule;
import me.fixeddev.ebcm.parameter.provider.ParameterProviderRegistry;
import me.fixeddev.ebcm.parametric.ParametricCommandBuilder;
import me.fixeddev.ebcm.parametric.ReflectionParametricCommandBuilder;
import com.strixmc.souls.utilities.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Security extends JavaPlugin {

    private MembersManager manager;
    private PIN pin;
    private AuthMeHook authMeHook;
    private PlayerMenuUtilityManager playerMenuUtilityManager;

    @Override
    public void onEnable() {
        int pluginId = 7648;
        new MetricsLite(this, pluginId);

        createConfig();
        initInstances();

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        pin.savePlayers(this);
    }

    private void registerCommands() {
        ParametricCommandBuilder builder = new ReflectionParametricCommandBuilder();

        Authorizer authorizer = new BukkitAuthorizer();
        ParameterProviderRegistry providerRegistry = ParameterProviderRegistry.createRegistry();
        Messager messager = new BukkitMessager();
        CommandManager commandManager = new SimpleCommandManager(authorizer, messager, providerRegistry);
        providerRegistry.installModule(new BukkitModule());

        BukkitCommandManager bukkitCommandManager = new BukkitCommandManager(commandManager, this.getName());

        List<Command> commands = new ArrayList<>();
        commands.addAll(builder.fromClass(new SoulCommand(this)));
        commands.addAll(builder.fromClass(new SetPin(this)));
        commands.addAll(builder.fromClass(new PinCommand(this)));

        bukkitCommandManager.registerCommands(commands);
    }

    private void registerListeners() {
        if (getServer().getPluginManager().isPluginEnabled("AuthMe")) {
            getServer().getPluginManager().registerEvents(new AuthMeListeners(this), this);
            authMeHook.initializeHook();
            Bukkit.getLogger().info("AuthMe has been found. Using it!");
        } else {
            getServer().getPluginManager().registerEvents(new NormalListeners(this), this);
        }
        getServer().getPluginManager().registerEvents(new GlobalListeners(this), this);
    }

    private void initInstances() {

        UpdateChecker.init(this, 77520).requestUpdateCheck().whenComplete((result, exception) -> {
            if (result.requiresUpdate()) {
                this.getLogger().info(String.format("An update is available! SoulSecurity %s may be downloaded on SpigotMC", result.getNewestVersion()));
                return;
            }

            UpdateChecker.UpdateReason reason = result.getReason();
            if (reason == UpdateChecker.UpdateReason.UP_TO_DATE) {
                this.getLogger().info(String.format("Your version of SoulSecurity (%s) is up to date!", result.getNewestVersion()));
            } else if (reason == UpdateChecker.UpdateReason.UNRELEASED_VERSION) {
                this.getLogger().info(String.format("Your version of SoulSecurity (%s) is more recent than the one publicly available. Are you on a development build?", result.getNewestVersion()));
            } else {
                this.getLogger().warning("Could not check for a new version of SoulSecurity. Reason: " + reason);
            }
        });

        pin = new PIN(this);
        pin.loadMembers(this);
        manager = new MembersManager();
        authMeHook = new AuthMeHook();
        this.playerMenuUtilityManager = new PlayerMenuUtilityManager();

        securityScheduler();
        notifySheduler();
    }

    private void securityScheduler() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                //Bukkit.broadcastMessage(Utils.c("&eHOOK &7" + authMeHook.isHookActive() + "&f, &eAuth &7" + authMeHook.authMeApi.isAuthenticated(player) + "&f, &eMember &7" + manager.containsMember(player.getName()) + "&f, &ePin &7" + pin.containsMember(player.getName()) + "&f, &eOP &7" + player.isOp()));
                if (authMeHook.isHookActive()) {
                    if (authMeHook.authMeApi.isAuthenticated(player)) {
                        if ((pin.containsMember(player.getName()) && !manager.containsMember(player.getName()) || player.isOp() || !pin.containsMember(player.getName()) && !manager.containsMember(player.getName())) && player.hasPermission("soul.staff")) {
                            Member member = new Member(player.getPlayer(), pin.containsMember(player.getName()), pin.getPIN(player.getName()));
                            manager.addMember(member);
                        }
                    }
                } else {
                    if ((pin.containsMember(player.getName()) && !manager.containsMember(player.getName()) || player.isOp() || !pin.containsMember(player.getName()) && !manager.containsMember(player.getName())) && player.hasPermission("soul.staff")) {
                        Member member = new Member(player.getPlayer(), pin.containsMember(player.getName()), pin.getPIN(player.getName()));
                        manager.addMember(member);
                    }
                }
            }
        }, 0L, 20L);
    }

    private void notifySheduler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (manager.containsMember(player.getName())) {
                    Member member = manager.getMember(player.getName());
                    if (!member.isRegistered()) {
                        player.sendMessage(Utils.c(getConfig().getString("Setup")));
                        return;
                    } else {
                        if (!member.isVerified()) {
                            player.sendMessage(Utils.c(getConfig().getString("Notify")));
                        }
                    }
                }
            }
        }, 0L, 20L * getConfig().getInt("Notifications-Interval"));
    }

    private void createConfig() {
        Bukkit.getLogger().info("Loading config.yml");
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile, Arrays.asList("Members"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
    }

    public PlayerMenuUtilityManager getPlayerMenuUtilityManager() {
        return playerMenuUtilityManager;
    }

    public MembersManager getManager() {
        return manager;
    }

    public PIN getPin() {
        return pin;
    }

    public AuthMeHook getAuthMeHook() {
        return authMeHook;
    }

    public class AuthMeHook {
        private AuthMeApi authMeApi;

        public void initializeHook() {
            authMeApi = AuthMeApi.getInstance();
        }

        public boolean registerPlayer(String name, String password) {
            if (authMeApi != null) { // check that the API is loaded
                return authMeApi.registerPlayer(name, password);
            }
            return false;
        }

        public boolean isNameRegistered(String name) {
            return authMeApi != null && authMeApi.isRegistered(name);
        }

        public boolean isHookActive() {
            return authMeApi != null;
        }

        public void removeAuthMeHook() {
            authMeApi = null;
        }
    }
}
