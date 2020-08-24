package com.gmail.zhushijie.litecustomannouncer;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LiteCustomAnnouncer extends JavaPlugin {
    public static LiteCustomAnnouncer INSTANCE;
    public LiteCustomAnnouncer() {
        INSTANCE = this;
    }

    public static int i = 0;

    public void announce() {
        FileConfiguration originalconfig = LiteCustomAnnouncer.INSTANCE.getConfig();
        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration config = LiteCustomAnnouncer.INSTANCE.getConfig();
                FileConfiguration aconfig = Announcementsconfig.getConfig();
                List<String> announcements = aconfig.getStringList("announcements");
                int length = announcements.size();
                if (Objects.equals(config.getString("enable-plugin"), "true")) {
                    String prefix = aconfig.getString("announcement-prefix").replace("&","§");
                    String announcement;
                    if (Objects.equals(config.getString("announcement-order"), "random")) {
                        announcement = announcements.get(i).replace("&","§");
                    } else {
                        announcement = announcements.get((new Random()).nextInt(announcements.size())).replace("&","§");
                    }
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
                        BossBar bossBar = Bukkit.createBossBar(prefix + announcement,BarColor.valueOf(config.getString("bossbar-color")),BarStyle.SOLID);
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
                }
                i++;
                if(i == length){
                    i = 0;
                }
            }
        }.runTaskTimer(this,originalconfig.getInt("announce-cooldown") * 20,originalconfig.getInt("announce-cooldown") * 20);
    }

    public void announce_timing() {
        new BukkitRunnable() {
            @Override
            public void run() {
                FileConfiguration config = LiteCustomAnnouncer.INSTANCE.getConfig();
                FileConfiguration tconfig = TimingAnnounceconfig.getConfig();
                if (Objects.equals(config.getString("enable-plugin"), "true")) {
                    List<String> timing_announcements = tconfig.getStringList("announcements-timing");
                    for (String timing_announcement : timing_announcements) {
                        String[] s = timing_announcement.split("::");
                        long millisnow = System.currentTimeMillis();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm");
                        String timenow = dateFormat.format(millisnow);
                        if (Objects.equals(timenow, s[0])) {
                            String prefix = tconfig.getString("timing-announcement-prefix").replace("&", "§");
                            String announcement = s[1].replace("&", "§");
                            if (Objects.equals(config.getString("announce-message"), "true")) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendMessage(prefix + announcement);
                                }
                            }
                            if (Objects.equals(config.getString("announce-title"), "true")) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendTitle(prefix.replace(" ", ""), announcement, 10, config.getInt("title-time") * 20, 20);
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
                                    }.runTaskTimer(LiteCustomAnnouncer.INSTANCE, config.getInt("bossbar-time") * 20, 20L);
                                }
                            }
                            if (Objects.equals(s[2], "1")) {
                                timing_announcements.remove(timing_announcement);
                                tconfig.set("announcements-timing",timing_announcements);
                                TimingAnnounceconfig.saveConfig();
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this,0L,1200L);
    }

    public void pluginupdater() {
        String currentversion = this.getDescription().getVersion();
        getLogger().info("正在检查更新......");
        try {
            URL url = new URL("https://raw.githubusercontent.com/main-world/LiteCustom/master/LiteCustomAnnouncer.txt");
            InputStream is = url.openStream();
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            String version = br.readLine();
            if (version.equals(currentversion)) {
                getLogger().info("插件已是最新版本!");
            } else {
                getLogger().info("检查到插件有新版本!");
                getLogger().info("请前往相应网页下载更新!");
            }
        } catch (Throwable t) {
            try {
                URL url = new URL("https://cdn.jsdelivr.net/gh/main-world/LiteCustom@update/Announcer.txt");
                InputStream is = url.openStream();
                InputStreamReader ir = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(ir);
                String version = br.readLine();
                if (version.equals(currentversion)) {
                    getLogger().info("插件已是最新版本!");
                } else {
                    getLogger().info("检查到插件有新版本!");
                    getLogger().info("请前往相应网页下载更新!");
                }
            } catch (Throwable e) {
                getLogger().info("更新检查失败!");
            }
        }
    }

    static ConfigReader Announcementsconfig;
    static ConfigReader TimingAnnounceconfig;
    static ConfigReader Messagesconfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        Announcementsconfig = new ConfigReader(this,"announcements.yml");
        Announcementsconfig.saveDefaultConfig();
        Announcementsconfig.reloadConfig();
        TimingAnnounceconfig = new ConfigReader(this,"announcements_timing.yml");
        TimingAnnounceconfig.saveDefaultConfig();
        TimingAnnounceconfig.reloadConfig();
        Messagesconfig = new ConfigReader(this,"messages.yml");
        Messagesconfig.saveDefaultConfig();
        Messagesconfig.reloadConfig();
        getLogger().info("LiteCustomAnnouncer已成功加载!");
        getLogger().info("作者:主世界");
        getLogger().info("本插件已免费发布并在Github上开源");
        pluginupdater();
        LiteCustomAnnouncer.INSTANCE.getCommand("LiteCustomAnnouncer").setExecutor(new CommandHandler());
        announce();
        announce_timing();
    }

    @Override
    public void onDisable() {
        saveConfig();
        reloadConfig();
        Announcementsconfig.saveConfig();
        Announcementsconfig.reloadConfig();
        TimingAnnounceconfig.saveConfig();
        TimingAnnounceconfig.reloadConfig();
        Messagesconfig.saveConfig();
        Messagesconfig.reloadConfig();
        getLogger().info("LiteCustomAnnouncer已成功卸载!");
    }
}