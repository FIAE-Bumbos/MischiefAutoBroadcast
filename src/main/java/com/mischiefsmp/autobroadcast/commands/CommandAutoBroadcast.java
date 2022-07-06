package com.mischiefsmp.autobroadcast.commands;

import com.mischiefsmp.autobroadcast.MischiefAutoBroadcast;
import com.mischiefsmp.autobroadcast.config.PluginConfig;
import com.mischiefsmp.core.LangManager;
import com.mischiefsmp.core.cmdinfo.CMDInfo;
import com.mischiefsmp.core.cmdinfo.CMDInfoManager;
import com.mischiefsmp.core.config.ConfigManager;
import com.mischiefsmp.core.utils.MCUtils;
import com.mischiefsmp.core.utils.MathUtils;
import com.mischiefsmp.core.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandAutoBroadcast implements CommandExecutor, TabCompleter {
    private final CMDInfoManager infoManager;
    private final HashMap<String, CMDInfo> cmdInfo;
    private final LangManager lm;
    private final PluginConfig cfg;

    public CommandAutoBroadcast() {
        infoManager = new CMDInfoManager(MischiefAutoBroadcast.getInstance());
        lm = MischiefAutoBroadcast.getLangManager();
        cmdInfo = infoManager.getAllCMDs();
        cfg = MischiefAutoBroadcast.getPluginConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 0 || args[0].equals("help")) {
            if(!isAllowed(sender, "autobroadcast.help"))
                return true;

            lm.sendString(sender, "available-cmds");
            for(CMDInfo info : infoManager.getCMDHelp(sender, "autobroadcast")) {
                sender.sendMessage(info.usage());
            }
        } else if(args.length == 1 && args[0].equals("reload")) {
            if(!isAllowed(sender, "autobroadcast.reload"))
                return true;

            MischiefAutoBroadcast.getInstance().init();
            sender.sendMessage("Plugin reloaded!");
        } else if(args.length > 1 && args[0].equals("add")) {
            if(!isAllowed(sender, "autobroadcast.add"))
                return true;

            StringBuilder message = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                if(i != 1)
                    message.append(" ");
                message.append(args[i]);
            }
            cfg.addMessage(message.toString());
            ConfigManager.save(cfg);
            lm.sendString(sender, "msg-added");
            MischiefAutoBroadcast.getInstance().ensureMessageTask();
        } else if(args.length == 1 && args[0].equals("list")) {
            if(!isAllowed(sender, "autobroadcast.list"))
                return true;

            if(cfg.messageSize() == 0) {
                lm.sendString(sender, "no-msgs");
                return true;
            }
            lm.sendString(sender, "msg-list");
            for(int i = 0; i < cfg.messageSize(); i++) {
                String message = "[%s] %s";
                message = String.format(message, i, cfg.getMessage(i));
                sender.sendMessage(message);
            }
        } else if(args.length == 2 && args[0].equals("remove")) {
            if(!isAllowed(sender, "autobroadcast.remove"))
                return true;

            if(!MathUtils.isInteger(args[1])) {
                lm.sendString(sender, "no-int");
                return true;
            }
            int id = Integer.parseInt(args[1]);
            if(cfg.messageSize() <= id || id < 0) {
                lm.sendString(sender, "bad-id");
                return true;
            }

            cfg.removeMessage(id);
            lm.sendString(sender, "msg-removed");
            ConfigManager.save(cfg);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        if(args.length == 1) {
            if(isAllowed(sender, "autobroadcast.help", false))
                list.add("help");
            if(isAllowed(sender, "autobroadcast.list", false))
                list.add("list");
            if(isAllowed(sender, "autobroadcast.add", false))
                list.add("add");
            if(isAllowed(sender, "autobroadcast.remove", false))
                list.add("remove");
            if(isAllowed(sender, "autobroadcast.reload", false))
                list.add("reload");
        } else if(args.length == 2 && args[0].equals("remove")) {
            if(!isAllowed(sender, "autobroadcast.remove", false))
                return list;

            int size = MischiefAutoBroadcast.getPluginConfig().messageSize();
            for(int i = 0; i < size; i++) {
                list.add(Integer.toString(i));
            }
        }

        Utils.removeTabArguments(args, list);
        return list;
    }

    private boolean isAllowed(CommandSender sender, String id, boolean printOnDeny) {
        if(MCUtils.isAllowed(sender, cmdInfo.get(id).permission()))
            return true;
        else
            if(printOnDeny)
                sender.sendMessage(lm.getString(sender, "no-perm"));
        return false;
    }

    private boolean isAllowed(CommandSender sender, String id) {
        return isAllowed(sender, id, true);
    }

}
