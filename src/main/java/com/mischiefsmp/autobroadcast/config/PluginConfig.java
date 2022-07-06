package com.mischiefsmp.autobroadcast.config;

import com.mischiefsmp.core.config.ConfigFile;
import com.mischiefsmp.core.config.ConfigManager;
import com.mischiefsmp.core.config.ConfigValue;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class PluginConfig extends ConfigFile {
    @Getter
    @ConfigValue(path = "language")
    private String defaultLanguage;

    @Getter
    @ConfigValue(path = "languages")
    private ArrayList<String> languages;

    @Getter
    @ConfigValue(path = "title")
    private String title;

    @Getter
    @ConfigValue(path = "time")
    private int time;

    @ConfigValue(path = "messages")
    private ArrayList<String> messages;

    public PluginConfig(Plugin plugin) {
        super(plugin, "config.yml", "config.yml");
        reload();
    }

    public void reload() {
        ConfigManager.init(this);
    }

    public int messageSize() {
        ensureMessages();
        return messages.size();
    }

    public void addMessage(String message) {
        ensureMessages();
        messages.add(message);
    }

    public void removeMessage(int index) {
        ensureMessages();
        messages.remove(index);
    }

    public String getMessage(int index) {
        ensureMessages();
        return messages.get(index);
    }

    private void ensureMessages() {
        if(messages == null)
            messages = new ArrayList<>();
    }
}
