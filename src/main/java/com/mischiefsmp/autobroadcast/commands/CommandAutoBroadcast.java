package com.mischiefsmp.autobroadcast.commands;

import com.mischiefsmp.autobroadcast.MischiefAutoBroadcast;
import com.mischiefsmp.autobroadcast.config.PluginConfig;
import com.mischiefsmp.core.LangManager;
import com.mischiefsmp.core.cmdinfo.CMDInfo;
import com.mischiefsmp.core.cmdinfo.CMDInfoManager;
import com.mischiefsmp.core.config.ConfigManager;
import com.mischiefsmp.core.utils.MathUtils;
import com.mischiefsmp.core.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CommandAutoBroadcast implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LangManager langManager = MischiefAutoBroadcast.getLangManager();
        PluginConfig cfg = MischiefAutoBroadcast.getPluginConfig();
        if(args.length == 0 || args[0].equals("help")) {
            CMDInfoManager infoManager = new CMDInfoManager(MischiefAutoBroadcast.getInstance());
            langManager.sendString(sender, "available-cmds");
            for(CMDInfo info : infoManager.getCMDHelp(sender, "autobroadcast")) {
                sender.sendMessage(info.usage());
            }
        } else if(args.length == 1 && args[0].equals("reload")) {
            MischiefAutoBroadcast.getInstance().init();
            sender.sendMessage("Plugin reloaded!");
        } else if(args.length > 1 && args[0].equals("add")) {
            StringBuilder message = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                if(i != 1)
                    message.append(" ");
                message.append(args[i]);
            }
            cfg.getMessages().add(message.toString());
            ConfigManager.save(cfg);
            langManager.sendString(sender, "msg-added");
        } else if(args.length == 1 && args[0].equals("list")) {
            if(cfg.getMessages().size() == 0) {
                langManager.sendString(sender, "no-msgs");
                return true;
            }
            langManager.sendString(sender, "msg-list");
            for(int i = 0; i < cfg.getMessages().size(); i++) {
                String message = "[%s] %s";
                message = String.format(message, i, cfg.getMessages().get(i));
                sender.sendMessage(message);
            }
        } else if(args.length == 2 && args[0].equals("remove")) {
            if(!MathUtils.isInteger(args[1])) {
                langManager.sendString(sender, "no-int");
                return true;
            }
            int id = Integer.parseInt(args[1]);
            if(cfg.getMessages().size() <= id || id < 0) {
                langManager.sendString(sender, "bad-id");
                return true;
            }

            cfg.getMessages().remove(id);
            langManager.sendString(sender, "msg-removed");
            ConfigManager.save(cfg);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if(args.length == 1) {
            list.add("help");
            list.add("list");
            list.add("add");
            list.add("remove");
            list.add("reload");
        } else if(args.length == 2 && args[0].equals("remove")) {
            int size = MischiefAutoBroadcast.getPluginConfig().getMessages().size();
            for(int i = 0; i < size; i++) {
                list.add(Integer.toString(i));
            }
        }

        Utils.removeTabArguments(args, list);
        return list;
    }
}
