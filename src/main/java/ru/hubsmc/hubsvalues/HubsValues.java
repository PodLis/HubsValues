package ru.hubsmc.hubsvalues;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import ru.hubsmc.hubsvalues.api.API;
import ru.hubsmc.hubsvalues.api.DataStore;
import ru.hubsmc.hubsvalues.board.App;
import ru.hubsmc.hubsvalues.board.ScoreboardHolder;
import ru.hubsmc.hubsvalues.commands.ConvertCommand;
import ru.hubsmc.hubsvalues.commands.ManaCommand;
import ru.hubsmc.hubsvalues.commands.PayCommand;
import ru.hubsmc.hubsvalues.commands.TopCommand;
import ru.hubsmc.hubsvalues.event.JoinEvent;
import ru.hubsmc.hubsvalues.event.LeaveEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static ru.hubsmc.hubsvalues.api.API.*;

public final class HubsValues extends JavaPlugin {

    public static final String CHAT_PREFIX = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "HV" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN;
    public static int START_MANA;
    public static int START_REGEN;
    public static boolean LOAD_VALUES_ON_JOIN;

    public static Scoreboard EMPTY_BOARD;
    public static App app;
    public static Map<Player, ScoreboardHolder> boardMap;

    private static HubsValues instance;
    private static boolean online;

    private FileConfiguration configuration;
    private File file;

    private BukkitScheduler onlineScheduler, offlineScheduler;

    private DataStore dataStore;

    @Override
    public void onEnable() {
        instance = this;
        boardMap = new HashMap<>();

        loadFiles();
        loadBoard();

        onlineScheduler = getServer().getScheduler();
        offlineScheduler = getServer().getScheduler();
        startTask();

        try {
            getServer().getPluginManager().registerEvents(new LeaveEvent(app), this);
            getServer().getPluginManager().registerEvents(new JoinEvent(), this);

            ManaCommand manaCommand = new ManaCommand();
            getCommand("mana").setExecutor(manaCommand);
            getCommand("mana").setTabCompleter(manaCommand);

            PayCommand payCommand = new PayCommand();
            getCommand("pay").setExecutor(payCommand);
            getCommand("pay").setTabCompleter(payCommand);

            ConvertCommand convertCommand = new ConvertCommand();
            getCommand("convert").setExecutor(convertCommand);
            getCommand("convert").setTabCompleter(convertCommand);

            TopCommand topCommand = new TopCommand();
            getCommand("dollartop").setExecutor(topCommand);

            Commands commands = new Commands();
            getCommand("hubsval").setExecutor(commands);
            getCommand("hubsval").setTabCompleter(commands);

            online = true;
            logConsole("Successfully enabled.");
        } catch (Throwable e) {
            e.printStackTrace();
            logConsole(Level.WARNING, "There was an error.");
        }

    }

    @Override
    public void onDisable() {
        try {
            online = false;
            unloadBoard();
            cancelTask();
            for (Player player : Bukkit.getOnlinePlayers()) {
                savePlayerData(player);
            }
            dataStore.closeConnections();
            logConsole("Successfully disabled.");
        } catch (Throwable e) {
            e.printStackTrace();
            logConsole(Level.WARNING, "There was an error.");
        }
    }

    void startTask() {
        onlineScheduler.scheduleSyncRepeatingTask(this, API::increaseAllOnlineMana, 0L, 1200L);
        long offlinePeriod = 1200L * configuration.getInt("regen.offline_coefficient");
        offlineScheduler.scheduleSyncRepeatingTask(this, API::increaseAllOfflineMana, 0L, offlinePeriod);
    }

    void cancelTask() {
        onlineScheduler.cancelTasks(this);
        offlineScheduler.cancelTasks(this);
    }

    void loadFiles() {
        try {
            if (file == null) {
                file = new File(getFolder(), "config.yml");
            }

            if (file.exists()) {
                configuration = YamlConfiguration.loadConfiguration(file);
                configuration.load(file);
                reloadConfig();
            } else {
                saveResource("config.yml", false);
                configuration = YamlConfiguration.loadConfiguration(file);
                logConsole("The 'config.yml' file successfully created!");
            }

            dataStore = new DataStore();
            dataStore.prepareToWork("jdbc:mariadb://localhost/" + configuration.getString("sql.database"),
                    configuration.getString("sql.user"),
                    configuration.getString("sql.password"));

            START_MANA = configuration.getInt("mana.start_amount");
            START_REGEN = configuration.getInt("regen.start_amount");
            LOAD_VALUES_ON_JOIN = configuration.getBoolean("load-values-on-join");

            EMPTY_BOARD = getServer().getScoreboardManager().getNewScoreboard();

            // small protection, if somebody (once) will join on the server before plugin completely loaded
            for (Player player : Bukkit.getOnlinePlayers()) {
                loadPlayerData(player, START_MANA, START_REGEN);
            }

        } catch (Throwable e) {
            e.printStackTrace();
            logConsole(Level.WARNING, "There was a file error.");
        }
    }

    private File getFolder() {
        File folder = getDataFolder();
        if (!folder.exists() && folder.mkdir()) {
            logConsole("Folder recreated");
        }
        return folder;
    }

    public void loadBoard() {
        app = new App(configuration.getConfigurationSection("board"));
        app.runTaskTimer(this, 1L, 2L);
    }

    public void unloadBoard() {
        boardMap.clear();
        app.cancel();
    }

    public static HubsValues getInstance() {
        return instance;
    }

    public static boolean isOnline() {
        return online;
    }

    public void logConsole(String info) {
        logConsole(Level.INFO, info);
    }

    public void logConsole(Level level, String message) {
        Bukkit.getLogger().log(level, "[HubsValues] " + message);
    }

}
