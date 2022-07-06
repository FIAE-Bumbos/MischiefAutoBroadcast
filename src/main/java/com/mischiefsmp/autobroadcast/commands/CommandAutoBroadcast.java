package com.mischiefsmp.autobroadcast.commands;

import com.mischiefsmp.autobroadcast.MischiefAutoBroadcast;
import com.mischiefsmp.core.cmdinfo.CMDInfo;
import com.mischiefsmp.core.cmdinfo.CMDInfoManager;
import com.mischiefsmp.core.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class CommandAutoBroadcast implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0 || args[0].equals("help")) {
            CMDInfoManager infoManager = new CMDInfoManager(MischiefAutoBroadcast.getInstance());
            MischiefAutoBroadcast.getLangManager().sendString(sender, "available-cmds");
            for(CMDInfo info : infoManager.getCMDHelp(sender, "autobroadcast")) {
                sender.sendMessage(info.usage());
            }
        } else if(args.length == 1 && args[0].equals("reload")) {
            MischiefAutoBroadcast.getInstance().init();
            sender.sendMessage("Plugin reloaded!");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
