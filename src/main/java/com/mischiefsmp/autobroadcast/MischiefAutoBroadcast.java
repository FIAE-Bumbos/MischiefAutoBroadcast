package com.mischiefsmp.autobroadcast;

import com.mischiefsmp.autobroadcast.commands.CommandAutoBroadcast;
import com.mischiefsmp.autobroadcast.config.PluginConfig;
import com.mischiefsmp.core.LangManager;
import com.mischiefsmp.core.config.ConfigManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;

public class MischiefAutoBroadcast extends JavaPlugin {
    @Getter
    private static MischiefAutoBroadcast instance;
    @Getter
    private static PluginConfig pluginConfig;
    @Getter
    private static LangManager langManager;

    private static int currentMessage = 0;
    private static int schedulerID = -1;

    public void ensureCore(boolean required) {
        if(getServer().getPluginManager().getPlugin("MischiefCore") != null) return;
        getLogger().log(Level.INFO, "Downloading MischiefCore...");
        File pluginFile = new File(new File("").getAbsolutePath() + File.separator + "plugins" + File.separator + "MischiefCore.jar");
        try {
            InputStream in = new URL("https://github.com/MischiefSMP/MischiefCore/releases/latest/download/MischiefCore.jar").openStream();
            Files.copy(in, pluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            getServer().getPluginManager().enablePlugin(getServer().getPluginManager().loadPlugin(pluginFile));
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error downloading MischiefCore! Message: " + e.getMessage());
            if(required) getServer().shutdown();
        }
        getLogger().log(Level.INFO, "Done downloading MischiefCore!");
    }

    @Override
    public void onEnable() {
        ensureCore(true);

        instance = this;
        pluginConfig = new PluginConfig(this);
        langManager = new LangManager(this, pluginConfig.getLanguages(), pluginConfig.getDefaultLanguage());

        getCommand("autobroadcast").setExecutor(new CommandAutoBroadcast());
        init();
    }

    public void init() {
        ConfigManager.init(pluginConfig);

        if (schedulerID != -1)
            cancelMessageTask();
        ensureMessageTask();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if(!(pluginConfig.getMessages() != null && pluginConfig.getMessages().size() != 0))
                getServer().broadcastMessage(langManager.getString("no-msgs"));
        });
    }

    public static void broadcast() {
        ArrayList<String> messages = pluginConfig.getMessages();

        //If for some reason we reach this just cancel it
        if(messages.size() == 0) {
            getInstance().cancelMessageTask();
            return;
        }

        if(currentMessage > messages.size() - 1)
            currentMessage = 0;

        Bukkit.getServer().broadcastMessage(String.format("%s %s", pluginConfig.getTitle(), pluginConfig.getMessages().get(currentMessage)));

        currentMessage++;
    }

    public void ensureMessageTask() {
        if(schedulerID == -1 && pluginConfig.getMessages().size() != 0) {
            int delay = 20 * pluginConfig.getTime();
            schedulerID = getServer().getScheduler().scheduleSyncRepeatingTask(this, MischiefAutoBroadcast::broadcast, delay, delay);
        }
    }

    public void cancelMessageTask() {
        getServer().getScheduler().cancelTask(schedulerID);
    }

    @Override
    public void onDisable() { }
}
