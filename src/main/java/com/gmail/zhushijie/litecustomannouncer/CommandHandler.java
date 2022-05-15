package com.gmail.zhushijie.litecustomannouncer;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = LiteCustomAnnouncer.INSTANCE.getConfig();
        FileConfiguration aconfig = LiteCustomAnnouncer.Announcementsconfig.getConfig();
        FileConfiguration tconfig = LiteCustomAnnouncer.TimingAnnounceconfig.getConfig();
        FileConfiguration mconfig = LiteCustomAnnouncer.Messagesconfig.getConfig();
        String prefix = aconfig.getString("announcement-prefix").replace("&","§");
        String pluginprefix = mconfig.getString("plugin-prefix").replace("&","§");
            if (command.getName().equalsIgnoreCase("litecustomannouncer")) {
                if (args.length < 1) { //若只输入了个/lcar则弹出插件帮助
                    if (!(sender instanceof Player)) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"litecustomannouncer help");
                    } else {
                        Player player = (Player) sender;
                        player.performCommand("litecustomannouncer help");
                    }
                } else if (Objects.equals(args[0], "switch")) { //开关公告推送的指令 lcar switch
                    if (args.length != 1) {
                        sender.sendMessage(pluginprefix + mconfig.getString("args-incorrect").replace("&","§"));
                    } else {
                        if (Objects.equals(config.getString("enable-plugin"), "true")) {
                            config.set("enable-plugin","false");
                            LiteCustomAnnouncer.INSTANCE.saveConfig();
                            LiteCustomAnnouncer.i = 0;
                            sender.sendMessage(pluginprefix + mconfig.getString("set-switch-success").replace("&","§"));
                        } else if (Objects.equals(config.getString("enable-plugin"), "false")) {
                            config.set("enable-plugin","true");
                            LiteCustomAnnouncer.INSTANCE.saveConfig();
                            LiteCustomAnnouncer.i = 0;
                            sender.sendMessage(pluginprefix + mconfig.getString("set-switch-success").replace("&","§"));
                        }
                    }
                } else if (Objects.equals(args[0], "send")) { //用指令直接发送公告的指令 lcar send <公告文字>
                    String announcement = args[1].replace("&","§");
                    if (Objects.equals(config.getString("announce-message"), "true")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(prefix + announcement);
                        }
                    }
                    if (Objects.equals(config.getString("announce-title"), "true")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendTitle(prefix.replace(" ",""), announcement, 10, config.getInt("title-time") * 20, 20);
                        }
                    }
                    if (Objects.equals(config.getString("announce-bossbar"), "true")) {
                        BossBar bossBar = Bukkit.createBossBar(prefix + announcement, BarColor.valueOf(config.getString("bossbar-color")), BarStyle.SOLID);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            bossBar.addPlayer(player);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    bossBar.removePlayer(player);
                                    cancel();
                                }
                            }.runTaskTimer(LiteCustomAnnouncer.INSTANCE,config.getInt("bossbar-time") * 20,20L);
                        }
                    }
                } else if (Objects.equals(args[0], "add")) { //新增公告的指令 lcar add <公告文字>
                    if (args.length != 2) {
                        sender.sendMessage(pluginprefix + mconfig.getString("args-incorrect").replace("&","§"));
                    } else {
                        List<String> announcements = aconfig.getStringList("announcements");
                        announcements.add(args[1]);
                        aconfig.set("announcements",announcements);
                        LiteCustomAnnouncer.Announcementsconfig.saveConfig();
                        LiteCustomAnnouncer.i = 0;
                        sender.sendMessage(pluginprefix + mconfig.getString("set-add-success").replace("&","§").replace("<公告文字>",args[1]));
                    }
                } else if (Objects.equals(args[0], "remove")) { //删除指定公告的指令 lcar remove <公告文字>
                    if (args.length != 2) {
                        sender.sendMessage(pluginprefix + mconfig.getString("args-incorrect").replace("&","§"));
                    } else {
                        List<String> announcements = aconfig.getStringList("announcements");
                        announcements.remove(args[1]);
                        aconfig.set("announcements",announcements);
                        LiteCustomAnnouncer.Announcementsconfig.saveConfig();
                        LiteCustomAnnouncer.i = 0;
                        sender.sendMessage(pluginprefix + mconfig.getString("set-remove-success").replace("&","§").replace("<公告文字>",args[1]));
                    }
                } else if (Objects.equals(args[0], "tadd")) { //新增定时公告的指令 lcar tadd <公告文字>
                    if (args.length != 2) {
                        sender.sendMessage(pluginprefix + mconfig.getString("args-incorrect").replace("&","§"));
                    } else {
                        List<String> announcements = tconfig.getStringList("announcements-timing");
                        announcements.add(args[1]);
                        tconfig.set("announcements-timing",announcements);
                        LiteCustomAnnouncer.TimingAnnounceconfig.saveConfig();
                        LiteCustomAnnouncer.i = 0;
                        sender.sendMessage(pluginprefix + mconfig.getString("tset-add-success").replace("&","§").replace("<公告文字>",args[1]));
                    }
                } else if (Objects.equals(args[0], "tremove")) { //删除指定公告的指令 lcar tremove <公告文字>
                    if (args.length != 2) {
                        sender.sendMessage(pluginprefix + mconfig.getString("args-incorrect").replace("&","§"));
                    } else {
                        List<String> announcements = tconfig.getStringList("announcements-timing");
                        announcements.remove(args[1]);
                        tconfig.set("announcements-timing",announcements);
                        LiteCustomAnnouncer.TimingAnnounceconfig.saveConfig();
                        LiteCustomAnnouncer.i = 0;
                        sender.sendMessage(pluginprefix + mconfig.getString("tset-remove-success").replace("&","§").replace("<公告文字>",args[1]));
                    }
                } else if (Objects.equals(args[0], "reload")) { //重载插件配置的指令 lcar reload
                    LiteCustomAnnouncer.INSTANCE.reloadConfig();
                    LiteCustomAnnouncer.INSTANCE.saveConfig();
                    LiteCustomAnnouncer.Announcementsconfig.reloadConfig();
                    LiteCustomAnnouncer.Announcementsconfig.saveConfig();
                    LiteCustomAnnouncer.TimingAnnounceconfig.reloadConfig();
                    LiteCustomAnnouncer.TimingAnnounceconfig.saveConfig();
                    LiteCustomAnnouncer.Messagesconfig.reloadConfig();
                    LiteCustomAnnouncer.Messagesconfig.saveConfig();
                    LiteCustomAnnouncer.i = 0;
                    sender.sendMessage(pluginprefix + mconfig.getString("reload-success").replace("&","§"));
                } else if (Objects.equals(args[0], "help")) { //查看插件帮助的指令 lcar help
                    List<String> helpmessage = mconfig.getStringList("help-message");
                    int i = 0;
                    for (int length = helpmessage.size(); i < length; i++) {
                        sender.sendMessage(helpmessage.get(i).replace("&","§"));
                    }
                } else { //若指令输入错误
                    if (!(sender instanceof Player)) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),"litecustomannouncer help");
                    } else {
                        Player player = (Player) sender;
                        player.performCommand("litecustomannouncer help");
                    }
                }
            }
        return true;
    }
}