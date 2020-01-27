package ru.hubsmc.hubsvalues;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.hubsmc.hubsvalues.board.ScoreboardHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static ru.hubsmc.hubsvalues.api.API.*;

public class Commands implements CommandExecutor, TabCompleter {

    private HubsValues plugin;
    private FileConfiguration config;

    Commands() {
        this.plugin = HubsValues.getInstance();
        this.config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("hubsval")) {


                if (args.length == 0)
                {
                    sendPrefixMessage(sender, "&b&l Info");
                    sendMessage(sender, "&5Version:&a " + plugin.getDescription().getVersion());
                    sendMessage(sender, "&5Author, created by:&a Rosenboum");
                }


                else if (args[0].equalsIgnoreCase("help"))
                {
                    if (!sender.hasPermission(Permissions.Perm.HELP.getPerm())) {
                        sendPrefixMessage(sender, "You have no permissions to use&6 " + args[0]);
                        return true;
                    }
                    sendPrefixMessage(sender, "Commands:");
                    sendMessage(sender, "/" + label + " reload&7 - Reloads the plugin.");
                    sendMessage(sender, "/" + label + " check <player> [value]&7 - Checks player's balance.");
                    sendMessage(sender, "/" + label + " set <player> <value> <amount>&7 - Sets player's balance.");
                    sendMessage(sender, "/" + label + " add <player> <value> <amount>&7 - Add value to player's balance.");
                    sendMessage(sender, "/" + label + " remove <player> <value> <amount>&7 - Remove value from player's balance.");
                    sendMessage(sender, "/" + label + " pay <player> <value> <amount>&7 - Pay value to another player.");
                    sendMessage(sender, "/" + label + " top <value>&7 - Shown the top of the players in that value.");
                    return true;
                }


                else if (args[0].equalsIgnoreCase("update"))
                {

                    if (!(sender instanceof Player)) {
                        sendPrefixMessage(sender, "Updater must be a player");
                        return true;
                    }

                    Player player = (Player) sender;

                    loadPlayerData(player);
                    HubsValues.boardMap.put(player, new ScoreboardHolder(HubsValues.app, player));

                    return true;
                }


                else if (args[0].equalsIgnoreCase("reload"))
                {
                    if (sender instanceof Player && !sender.hasPermission(Permissions.Perm.RELOAD.getPerm())) {
                        sendPrefixMessage(sender, "You have no permissions to use&6 " + args[0]);
                        return true;
                    }

                    plugin.cancelTask();
                    plugin.unloadBoard();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        savePlayerData(player);
                    }

                    plugin.loadFiles();
                    plugin.loadBoard();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        loadPlayerData(player);
                        HubsValues.boardMap.put(player, new ScoreboardHolder(HubsValues.app, player));
                    }
                    plugin.startTask();

                    sendPrefixMessage(sender, "Plugin reloaded.");

                    return true;
                }


                else if (args[0].equalsIgnoreCase("check"))
                {

                    if (sender instanceof Player && !sender.hasPermission(Permissions.Perm.CHECK.getPerm())) {
                        sendPrefixMessage(sender, "You have no permissions to use&6 " + args[0]);
                        return true;
                    }

                    if (args.length < 2) {
                        sendPrefixMessage(sender, "Usage: &7/" + label + " check <player> [value]");
                        return true;
                    }

                    if (args.length == 2) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                        if (checkDataExist(offlinePlayer.getUniqueId().toString())) {
                            sendPrefixMessage(sender, "Player's values:");
                            for (String type : config.getStringList("economy-types")) {
                                sendMessage(sender, config.getString(type + ".name") + ": " + config.getString(type + ".color") + getValueFromName(offlinePlayer, type));
                            }
                            return true;
                        }

                        sendPrefixMessage(sender, "That player is not define.");
                        return true;

                    }

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    String valueType = args[2].toLowerCase();

                    if (checkDataExist(offlinePlayer.getUniqueId().toString()) && config.getStringList("economy-types").contains(valueType)) {
                        sendMessage(sender, config.getString(valueType + ".name") + ": " + config.getString(valueType + ".color") + getValueFromName(offlinePlayer, valueType));
                        return true;
                    }

                    sendPrefixMessage(sender, "That player or that economy is not define.");
                    return true;

                }


                else if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))
                {
                    if (sender instanceof Player && !sender.hasPermission(Permissions.Perm.CHANGE.getPerm())) {
                        sendPrefixMessage(sender,  "You have no permissions to use&6 " + args[0]);
                        return true;
                    }
                    if (args.length < 4) {
                        sendPrefixMessage(sender, "Usage: /" + label + " " + args[0] + " <player> <value> <amount>");
                        return true;
                    }

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    String valueType = args[2].toLowerCase();
                    String amount = args[3];
                    boolean wasSavedInMemory = false;

                    if (checkDataExist(offlinePlayer.getUniqueId().toString()) && config.getStringList("economy-types").contains(valueType)) {
                        try {

                            switch (args[0]) {
                                case "set":
                                {
                                    wasSavedInMemory = setValueFromName(offlinePlayer, valueType, Math.max(Integer.parseInt(amount), 0));
                                    break;
                                }
                                case "add":
                                {
                                    int beforeAmount = getValueFromName(offlinePlayer, valueType);
                                    wasSavedInMemory = setValueFromName(offlinePlayer, valueType, beforeAmount + Math.max(Integer.parseInt(amount), 0));
                                    break;
                                }
                                case "remove":
                                {
                                    int beforeAmount = getValueFromName(offlinePlayer, valueType);
                                    wasSavedInMemory = setValueFromName(offlinePlayer, valueType, Math.max(beforeAmount - Math.max(Integer.parseInt(amount), 0), 0));
                                    break;
                                }
                            }

                            if (wasSavedInMemory) {
                                sendPrefixMessage(sender, "This value was saved in memory:");
                            } else {
                                sendPrefixMessage(sender, "This value was saved in database:");
                            }
                            sendMessage(sender, config.getString(valueType + ".name") + ": " + config.getString(valueType + ".color") + getValueFromName(offlinePlayer, valueType));
                            return true;

                        } catch (NumberFormatException e) {
                            sendPrefixMessage(sender, "Not valid number");
                            return true;
                        }

                    }

                    sendPrefixMessage(sender, "That player or that economy is not define.");
                    return true;

                }


                else {
                    sender.sendMessage(replaceColor(HubsValues.CHAT_PREFIX + "unknown sub-command&6 " + args[0]));
                    return true;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            plugin.logConsole(Level.WARNING, "Some troubles with commands.");
            plugin.logConsole("oops...");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completionList = new ArrayList<>();
        String partOfCommand;
        List<String> cmds = new ArrayList<>();

        if (args.length == 1) {
            cmds = new ArrayList<>(getCmds(sender));
            partOfCommand = args[0];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                cmds.add(player.getName());
            }
            partOfCommand = args[1];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            cmds.addAll(config.getStringList("economy-types"));
            partOfCommand = args[2];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        if (args.length == 4 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            for (int i = 1; i <= 6; i++) {
                cmds.add("" + ((int)Math.pow(10, i)));
                cmds.add("" + ((int)Math.pow(10, i))*5);
            }
            partOfCommand = args[1];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        return null;
    }

    private List<String> getCmds(CommandSender sender) {
        List<String> c = new ArrayList<>();
        for (String cmd : Arrays.asList("help", "reload", "check", "set", "add", "remove")) {
            if (!sender.hasPermission("hubsval." + cmd)) {
                continue;
            }
            c.add(cmd);
        }
        return c;
    }

    private String replaceColor(String s) {
        return s.replace("&", "\u00a7");
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(replaceColor(message));
    }

    private void sendPrefixMessage(CommandSender sender, String message) {
        sender.sendMessage(HubsValues.CHAT_PREFIX + replaceColor(message));
    }

}
