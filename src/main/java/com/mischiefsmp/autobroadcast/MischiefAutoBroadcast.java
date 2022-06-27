package com.mischiefsmp.autobroadcast;

import com.mischiefsmp.autobroadcast.config.PluginConfig;
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

    private static int currentMessage = 0;

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

        int delay = 20 * /*60 **/ pluginConfig.getTime();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, MischiefAutoBroadcast::broadcast, delay, delay);
    }

    public static void broadcast() {
        ArrayList<String> messages = pluginConfig.getMessages();
        if(currentMessage > messages.size() - 1)
            currentMessage = 0;

        Bukkit.getServer().broadcastMessage(String.format("%s %s", pluginConfig.getTitle(), pluginConfig.getMessages().get(currentMessage)));

        currentMessage++;
    }

    @Override
    public void onDisable() { }
}
