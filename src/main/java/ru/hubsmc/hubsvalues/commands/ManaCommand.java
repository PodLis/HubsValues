package ru.hubsmc.hubsvalues.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.hubsmc.hubsvalues.HubsValues;

import java.util.List;
import java.util.logging.Level;

import static ru.hubsmc.hubsvalues.api.API.*;

public class ManaCommand implements CommandExecutor, TabCompleter {

    private HubsValues plugin;

    public ManaCommand() {
        this.plugin = HubsValues.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("mana")) {

                if (!(sender instanceof Player)) {
                    sendPrefixMessage(sender, "Mana user must be a player");
                    return true;
                }

                Player player = (Player) sender;
                sendPrefixMessage(sender, "Твоя мана-статистика:");
                sendMessage(player, "Текущее значение маны: " + getMana(player));
                sendMessage(player, "Максимальное значение маны: " + getMaxMana(player));
                sendMessage(player, "Регенерация маны в минуту: " + getRegenMana(player));

            }
        } catch (Throwable e) {
            e.printStackTrace();
            plugin.logConsole(Level.WARNING, "Some troubles with mana commands.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
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
