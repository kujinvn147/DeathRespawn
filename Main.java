package com.deathrespawn.explorer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Main extends JavaPlugin implements Listener {
	public static Plugin plugin;
	private static Economy econ = null;
	public static EconomyResponse r;
	String cslprefix = "[DeathRespawn] ";

	public static Plugin getPlugin() {
		return plugin;
	}

	public void loadingConfiguration() {
		String prefix = "prefix";
		plugin.getConfig().addDefault(prefix, "&7[&cDeathRespawn&7] &r> ");

		String respawn = "command-respawn-location";
		plugin.getConfig().addDefault(respawn, "spawn");

		String bl = "take-money";
		plugin.getConfig().addDefault(bl, Boolean.valueOf(true));
		String money = "money-will-take";
		plugin.getConfig().addDefault(money, Double.valueOf(10000));

		String sound = "sound.death-sound";
		plugin.getConfig().addDefault(sound, "ENTITY_VILLAGER_HURT");
		String sound2 = "sound.counting-down";
		plugin.getConfig().addDefault(sound2, "BLOCK_NOTE_PLING");
		String sound3 = "sound.time-out";
		plugin.getConfig().addDefault(sound3, "ENTITY_PLAYER_LEVELUP");

		String msg1 = "msg.3-second";
		plugin.getConfig().addDefault(msg1, "&aBạn sẽ được hồi sinh sau &c3 &agiây nữa!");
		String msg2 = "msg.2-second";
		plugin.getConfig().addDefault(msg2, "&aBạn sẽ được hồi sinh sau &c2 &agiây nữa!");
		String msg3 = "msg.1-second";
		plugin.getConfig().addDefault(msg3, "&aBạn sẽ được hồi sinh sau &c1 &agiây nữa!");
		String msg4 = "msg.time-out";
		plugin.getConfig().addDefault(msg4, "&aBạn sẽ được hồi sinh sau &c0 &agiây nữa!");
		String msg5 = "msg.death-event";
		plugin.getConfig().addDefault(msg5, "&cBạn đã chết!");
		String msg6 = "msg.respawn-message";
		plugin.getConfig().addDefault(msg6, "&aBạn đã được hồi sinh!");

		String reload = "reload";
		plugin.getConfig().addDefault(reload, "&aPlugin đã được reload");
		String noperm = "no-perm";
		plugin.getConfig().addDefault(noperm, "&cBạn không có quyền để sử dụng lệnh này!");

		String title = "title.3-second";
		plugin.getConfig().addDefault(title, "&a&lHỒI SINH SAU &c&l3");
		String title2 = "title.2-second";
		plugin.getConfig().addDefault(title2, "&a&lHỒI SINH SAU &c&l2");
		String title3 = "title.1-second";
		plugin.getConfig().addDefault(title3, "&a&lHỒI SINH SAU &c&l1");
		String title4 = "title.0-second";
		plugin.getConfig().addDefault(title4, "&a&lHỒI SINH SAU &c&l0");
		String title5 = "title.time-out";
		plugin.getConfig().addDefault(title5, "&e&lHẾT GIỜ");

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
	}

	PluginDescriptionFile pdf = getDescription();

	public void onEnable() {
		ConsoleCommandSender console = getServer().getConsoleSender();
		if (setupEconomy()) {
			plugin = this;
			getServer().getPluginManager().enablePlugin(this);
			console.sendMessage(this.cslprefix + ChatColor.GOLD
					+ "Da tim thay Economy! Plugin se duoc khoi chay trong vai giay nua!!");
			getServer().getPluginManager().registerEvents(this, this);
			loadingConfiguration();
			console.sendMessage(this.cslprefix + ChatColor.GREEN + "Plugin da duoc bat!");
			console.sendMessage(
					this.cslprefix + ChatColor.DARK_PURPLE + "Made by " + ChatColor.AQUA + pdf.getAuthors());
			console.sendMessage(
					this.cslprefix + ChatColor.DARK_PURPLE + "Version " + ChatColor.RESET + this.pdf.getVersion());
			console.sendMessage(this.cslprefix + ChatColor.DARK_PURPLE + "Donate " + ChatColor.AQUA + pdf.getWebsite());
		} else if (!setupEconomy()) {
			console.sendMessage(this.cslprefix + ChatColor.RED
					+ "Khong tim thay Economy! Hay chac chan rang Essential da duoc cai dat de setup voi Vault moi co the khoi chay plugin!");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = (Economy) rsp.getProvider();
		return econ != null;
	}

	public void onDisable() {
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			final Player p = e.getEntity();
			final Location loc = p.getLocation();

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					if (p.isOnline()) {
						p.spigot().respawn();
						p.setHealth(p.getMaxHealth());
						p.setFoodLevel(20);
					}
				}
			}, 0L);

			if (!(p.hasPermission("dr.respawn"))) {
				p.setGameMode(GameMode.SPECTATOR);
				p.setAllowFlight(true);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						p.playSound(loc, Sound.valueOf(Main.this.getConfig().getString("sound.death-sound")), 4.0F,
								1.0F);
						p.sendMessage(
								ChatColor.translateAlternateColorCodes('&', Main.this.getConfig().getString("prefix"))
										+ ChatColor.translateAlternateColorCodes('&',
												Main.this.getConfig().getString("msg.death-event")));
					}
				}, 40L);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						p.playSound(loc, Sound.valueOf(Main.this.getConfig().getString("sound.counting-down")), 4.0F,
								1.0F);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								Main.this.getConfig().getString("msg.3-second")));
						Titles.sendTitle(p,
								ChatColor.translateAlternateColorCodes('&',
										Main.this.getConfig().getString("title.3-second")),
								"", Integer.valueOf(0), Integer.valueOf(20), Integer.valueOf(0));
					}
				}, 60L);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						p.playSound(loc, Sound.valueOf(Main.this.getConfig().getString("sound.counting-down")), 4.0F,
								1.0F);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								Main.this.getConfig().getString("msg.2-second")));
						Titles.sendTitle(p,
								ChatColor.translateAlternateColorCodes('&',
										Main.this.getConfig().getString("title.2-second")),
								"", Integer.valueOf(0), Integer.valueOf(20), Integer.valueOf(0));
					}
				}, 80L);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						p.playSound(loc, Sound.valueOf(Main.this.getConfig().getString("sound.counting-down")), 4.0F,
								1.0F);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								Main.this.getConfig().getString("msg.1-second")));
						Titles.sendTitle(p,
								ChatColor.translateAlternateColorCodes('&',
										Main.this.getConfig().getString("title.1-second")),
								"", Integer.valueOf(0), Integer.valueOf(20), Integer.valueOf(0));
					}
				}, 100L);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						p.playSound(loc, Sound.valueOf(Main.this.getConfig().getString("sound.counting-down")), 4.0F,
								1.0F);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								Main.this.getConfig().getString("msg.time-out")));
						Titles.sendTitle(p,
								ChatColor.translateAlternateColorCodes('&',
										Main.this.getConfig().getString("title.0-second")),
								"", Integer.valueOf(0), Integer.valueOf(20), Integer.valueOf(0));
					}
				}, 120L);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						p.playSound(loc, Sound.valueOf(Main.this.getConfig().getString("sound.time-out")), 4.0F, 1.0F);
						Titles.sendTitle(p,
								ChatColor.translateAlternateColorCodes('&',
										Main.this.getConfig().getString("title.time-out")),
								"", Integer.valueOf(0), Integer.valueOf(20), Integer.valueOf(20));
					}
				}, 140L);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						p.setGameMode(GameMode.SURVIVAL);
						p.setAllowFlight(false);
						p.performCommand(Main.this.getConfig().getString("command-respawn-location"));
						p.sendMessage(
								ChatColor.translateAlternateColorCodes('&', Main.this.getConfig().getString("prefix"))
										+ ChatColor.translateAlternateColorCodes('&',
												Main.this.getConfig().getString("msg.respawn-message")));
					}
				}, 160L);
			} else if (p.hasPermission("dr.respawn")) {
				p.setGameMode(GameMode.SURVIVAL);
				p.setAllowFlight(false);
				p.performCommand(Main.this.getConfig().getString("command-respawn-location"));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.this.getConfig().getString("prefix"))
						+ ChatColor.translateAlternateColorCodes('&',
								Main.this.getConfig().getString("msg.respawn-message")));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		final Location loc = p.getLocation();
		if (((e.getPlayer() instanceof Player)) && (p.getGameMode() == GameMode.SPECTATOR)) {
			p.setHealth(p.getMaxHealth());
			p.setGameMode(GameMode.SURVIVAL);
			p.setAllowFlight(false);
			p.setFoodLevel(20);

			double money = econ.getBalance(p.getName());
			if (getConfig().getBoolean("take-money")) {
				if (money >= getConfig().getDouble("money-will-take")) {
					r = econ.withdrawPlayer(p, getConfig().getDouble("money-will-take"));
					if ((p.isOnline()) && r.transactionSuccess()) {
						p.setHealth(p.getMaxHealth());
						p.setFoodLevel(20);
						p.setAllowFlight(false);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								getConfig().getString("prefix") + ChatColor.translateAlternateColorCodes('&',
										"&cBạn vừa thoát ra khi đang trong trạng thái hồi sinh. Tịch thu &e"
												+ getConfig().getDouble("money-will-take") + "$&c của bạn")));
					} else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
								+ ChatColor.RED + ChatColor.RED
								+ "Đã có lỗi xảy ra, vui lòng báo cáo với quản trị viên");
						p.playSound(loc, Sound.ITEM_SHIELD_BREAK, 4.0F, 1.0F);
						return;
					}
				}
				if (money < getConfig().getDouble("money-will-take")) {
					r = econ.withdrawPlayer(p, econ.getBalance(p.getName()));
					if ((p.isOnline()) && (r.transactionSuccess())) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
								+ ChatColor.translateAlternateColorCodes('&',
										"&cBạn không đủ &e" + getConfig().getDouble("money-will-take")
												+ "$&c. Tịch thu toàn bộ số tiền hiện có của bạn"));
						p.playSound(loc, Sound.ITEM_SHIELD_BREAK, 4.0F, 1.0F);
					} else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
								+ ChatColor.RED + ChatColor.RED
								+ "Đã có lỗi xảy ra, vui lòng báo cáo với quản trị viên");
						p.playSound(loc, Sound.ITEM_SHIELD_BREAK, 4.0F, 1.0F);
						return;
					}
				}
			} else if (!getConfig().getBoolean("take-money")) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
						+ ChatColor.RED + "Bạn vừa thoát ra khi đang trong trạng thái hồi sinh");
				p.playSound(loc, Sound.ITEM_SHIELD_BREAK, 4.0F, 1.0F);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCmd(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.SPECTATOR) {
			if (!p.isOp()) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
						+ ChatColor.RED + "Bạn không thể sử dụng lệnh vào lúc này!");
			} else if (p.isOp()) {
				e.setCancelled(false);
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		ConsoleCommandSender console = Bukkit.getConsoleSender();
		if (!(sender instanceof Player)) {
			console.sendMessage(this.cslprefix + ChatColor.RED + "Lenh chi co the su dung trong tro choi!");
		} else {
			Player p = (Player) sender;
			Location loc = p.getLocation();
			if (cmd.getName().equalsIgnoreCase("deathrespawn")) {
				if ((args.length == 1) && (args[0].equalsIgnoreCase("reload"))) {
					if (!p.hasPermission("dr.reload")) {
						p.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_HURT, 4.0F, 1.0F);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
								+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("no-perm")));
						return true;
					}
					reloadConfig();
					saveConfig();
					p.playSound(loc, Sound.BLOCK_LEVER_CLICK, 4.0F, 1.0F);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
							+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("reload")));
					return true;
				}
				if (((args.length != 1) || (!args[0].equalsIgnoreCase("reload"))) && (args.length != 1)) {
					p.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_HURT, 4.0F, 1.0F);
					p.sendMessage(
							ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix")) + ChatColor.RED
									+ "Không rõ yêu cầu! Nếu bạn muốn reload, vui lòng dùng lệnh /deathrespawn reload");
					return true;
				}
			}
		}
		return true;
	}
}
