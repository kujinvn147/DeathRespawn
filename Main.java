package death.respawn.delay;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
		plugin.getConfig().addDefault(title5, "&5&lHẾT GIỜ");

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

	}

	PluginDescriptionFile pdf = getDescription();

	@Override
	public void onEnable() {

		ConsoleCommandSender console = Bukkit.getConsoleSender();

		if (!setupEconomy()) {
			console.sendMessage(
					cslprefix + ChatColor.RED + "Khong tim thay Vault! Vui long cai dat Vault de chay plugin!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			plugin = this;

			loadingConfiguration();
			getServer().getPluginManager().registerEvents(this, this);

			console.sendMessage(cslprefix + ChatColor.GREEN + "Plugin da duoc bat!");
			console.sendMessage(cslprefix + ChatColor.DARK_PURPLE + "Made by " + ChatColor.AQUA + "Explorer");
			console.sendMessage(cslprefix + ChatColor.DARK_PURPLE + "Version " + ChatColor.RESET + pdf.getVersion());
			console.sendMessage(cslprefix + ChatColor.DARK_PURPLE + "Donate " + ChatColor.AQUA
					+ "fb.com/taocuoivichungmaycureporttao");
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
		econ = rsp.getProvider();
		return econ != null;
	}

	@Override
	public void onDisable() {
	}

	@EventHandler
	public void onDeath(final PlayerDeathEvent e) {
		final Player p = e.getEntity();
		Location loc = p.getLocation();

		if (p.isDead()) {

			p.setHealth(20.0D);
			p.setGameMode(GameMode.SPECTATOR);
			p.setAllowFlight(true);
			p.setFoodLevel(20);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					if (p.isOnline()) {
						p.spigot().respawn();
					}
				}
			}, 0L);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				public void run() {
					loc.getWorld().playSound(loc, Sound.valueOf(getConfig().getString("sound.death-sound")), 4F, 1F);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
							+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.death-event")));
				}

			}, 40L);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				public void run() {
					loc.getWorld().playSound(loc, Sound.valueOf(getConfig().getString("sound.counting-down")), 4F, 1F);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.3-second")));
					Titles.sendTitle(p,
							ChatColor.translateAlternateColorCodes('&', getConfig().getString("title.3-second")), "", 0,
							20, 0);
				}

			}, 60L);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				public void run() {
					loc.getWorld().playSound(loc, Sound.valueOf(getConfig().getString("sound.counting-down")), 4F, 1F);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.2-second")));
					Titles.sendTitle(p,
							ChatColor.translateAlternateColorCodes('&', getConfig().getString("title.2-second")), "", 0,
							20, 0);
				}

			}, 80L);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				public void run() {
					loc.getWorld().playSound(loc, Sound.valueOf(getConfig().getString("sound.counting-down")), 4F, 1F);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.1-second")));
					Titles.sendTitle(p,
							ChatColor.translateAlternateColorCodes('&', getConfig().getString("title.1-second")), "", 0,
							20, 0);
				}

			}, 100L);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				public void run() {
					loc.getWorld().playSound(loc, Sound.valueOf(getConfig().getString("sound.counting-down")), 4F, 1F);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.time-out")));
					Titles.sendTitle(p,
							ChatColor.translateAlternateColorCodes('&', getConfig().getString("title.0-second")), "", 0,
							20, 0);
				}

			}, 120L);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				public void run() {
					loc.getWorld().playSound(loc, Sound.valueOf(getConfig().getString("sound.time-out")), 4F, 1F);
					Titles.sendTitle(p,
							ChatColor.translateAlternateColorCodes('&', getConfig().getString("title.time-out")), "", 0,
							20, 20);
				}

			}, 140L);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

				public void run() {
					p.setGameMode(GameMode.SURVIVAL);
					p.setAllowFlight(false);
					p.performCommand(getConfig().getString("command-respawn-location"));
					p.sendMessage(
							ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix")) + ChatColor
									.translateAlternateColorCodes('&', getConfig().getString("msg.respawn-message")));
				}

			}, 160L);
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		Location loc = p.getLocation();

		if (e.getPlayer() instanceof Player) {
			if (p.getGameMode() == GameMode.SPECTATOR) {

				p.setHealth(20.0D);
				p.setGameMode(GameMode.SURVIVAL);
				p.setAllowFlight(false);
				p.setFoodLevel(20);

				double pl = econ.getBalance(p.getName());

				if (getConfig().getBoolean("take-money") == true) {
					if (pl >= 1000) {
						r = econ.withdrawPlayer(p, 1000.0);
						if (r.transactionSuccess()) {
							p.setHealth(20.0D);
							p.setFoodLevel(20);
							p.setAllowFlight(false);
							for (PotionEffect effect : p.getActivePotionEffects()) {
								p.removePotionEffect(effect.getType());
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
									+ ChatColor.RED + ChatColor.RED
									+ "Đã có lỗi xảy ra, không thể lấy tiền của bạn! Vui lòng báo cáo với quản trị viên");
							loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BREAK, 4F, 1F);
							return;
						}
					}
					if (pl <= 999) {
						r = econ.withdrawPlayer(p, econ.getBalance(p.getName()));
						if (p.isOnline()) {
							if (r.transactionSuccess()) {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										getConfig().getString("prefix"))
										+ ChatColor.translateAlternateColorCodes('&',
												"&cBạn không đủ &e1000$&c. Bạn sẽ phải trả nợ bằng cách khác."));
								loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BREAK, 4F, 1F);

								Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
									@Override
									public void run() {
										p.setHealth(0.0D);
										p.setFoodLevel(0);
										p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 1),
												true);
										p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1), true);
										p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1), true);
										p.sendMessage(ChatColor.translateAlternateColorCodes('&',
												getConfig().getString("prefix")) + ChatColor.RED
												+ "Bạn đã bị phạt vì tội thoát ra khi đang hồi sinh!");
										loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BREAK, 4F, 1F);
									}
								}, 20L);
							}
						}

					}
				} else if (getConfig().getBoolean("take-money") == false) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
							+ ChatColor.RED + "Bạn vừa thoát ra khi đang trong trạng thái hồi sinh");
					loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BREAK, 4F, 1F);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCmd(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.SPECTATOR) {
			if (!(p.isOp())) {
				e.setCancelled(true);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
						+ ChatColor.RED + "Bạn không thể sử dụng lệnh vào lúc này!");
			} else if (p.isOp()) {
				e.setCancelled(false);
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		ConsoleCommandSender console = Bukkit.getConsoleSender();

		if (!(sender instanceof Player)) {
			console.sendMessage(cslprefix + ChatColor.RED + "Lenh chi co the su dung trong tro choi!");
		} else {
			Player p = (Player) sender;
			Location loc = p.getLocation();

			if (cmd.getName().equalsIgnoreCase("deathrespawn")) {
				if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
					if (!(p.hasPermission("death.reload"))) {
						loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_HURT, 4F, 1F);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
								+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("no-perm")));
						return true;
					}
					reloadConfig();
					saveConfig();
					loc.getWorld().playSound(loc, Sound.BLOCK_LEVER_CLICK, 4F, 1F);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"))
							+ ChatColor.translateAlternateColorCodes('&', getConfig().getString("reload")));
					return true;
				} else if (!(args.length == 1 && args[0].equalsIgnoreCase("reload") || args.length == 1)) {
					loc.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_HURT, 4F, 1F);
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
