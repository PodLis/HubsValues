package ru.hubsmc.hubsvalues.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.hubsmc.hubsvalues.HubsValues;
import ru.hubsmc.hubsvalues.Permissions;
import ru.hubsmc.hubsvalues.event.PayEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import static ru.hubsmc.hubsvalues.api.API.*;

public class PayCommand implements CommandExecutor, TabCompleter {

    private HubsValues plugin;

    public PayCommand() {
        this.plugin = HubsValues.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase("pay")) {

                if (!(sender instanceof Player)) {
                    sendPrefixMessage(sender, "Payer must be a player :)");
                    return true;
                }

                Player player = (Player) sender;

                if (!player.hasPermission(Permissions.Perm.PAY.getPerm())) {
                    sendPrefixMessage(player, "У тебя нет привилегии, чтобы использовать&6 " + label);
                    return true;
                }
                if (args.length < 2) {
                    sendPrefixMessage(player, "Мало аргументов! Попробуй: /" + label + " <игрок> <сумма перевода>");
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sendPrefixMessage(player, "У нас нельзя платить буквами!");
                    return true;
                }

                if (args[0].equals("*")) {
                    if (!player.hasPermission(Permissions.Perm.PAY_ALL.getPerm())) {
                        sendPrefixMessage(player, "У тебя нет привилегии, чтобы платить всем игрокам!");
                        return true;
                    }
                    sendPrefixMessage(player, "Сорян, автор плагина не успел доделать перевод всем игрокам...");
                    return true;
                }

                Player receiver = Bukkit.getPlayer(args[0]);

                if (receiver == null) {
                    sendPrefixMessage(player, "Этот игрок не онлайн!");
                    return true;
                }

                if (player.getUniqueId().toString().equals(receiver.getUniqueId().toString())) {
                    sendPrefixMessage(player, "Ты не можешь заплатить самому себе!");
                    return true;
                }

                PayEvent event = new PayEvent(player, receiver, amount);
                Bukkit.getServer().getPluginManager().callEvent(event);
                boolean isReceiverOnline;

                if (!event.isCancelled()) {
                    if (takeDollars(player, amount) == 0){
                        sendPrefixMessage(player, "Ты хочешь перевести больше, чем у тебя есть!");
                        return true;
                    }
                    isReceiverOnline = addDollars(receiver, amount);
                } else {
                    return true;
                }

                sendPrefixMessage(player, "Ты перевёл &e" + amount + "$&f игроку " + receiver.getName() + ". Теперь у тебя &e" + getDollars(player) + "$&f.");
                if (isReceiverOnline && receiver.getPlayer() != null) {
                    sendPrefixMessage(receiver.getPlayer(), "Игрок " + player.getName() + " перевёл тебе &e" + amount + "$&f. Теперь у тебя &e" + getDollars(receiver) + "$&f.");
                }

                return true;

            }
        } catch (Throwable e) {
            e.printStackTrace();
            plugin.logConsole(Level.WARNING, "Some troubles with command 'pay'.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completionList = new ArrayList<>();
        String partOfCommand;
        List<String> cmds = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                cmds.add(player.getName());
            }
            cmds.add("*");
            partOfCommand = args[0];

            StringUtil.copyPartialMatches(partOfCommand, cmds, completionList);
            Collections.sort(completionList);
            return completionList;
        }

        if (args.length == 2) {
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

    private String replaceColor(String s) {
        return s.replace("&", "\u00a7");
    }

    private void sendPrefixMessage(CommandSender sender, String message) {
        sender.sendMessage(HubsValues.CHAT_PREFIX + replaceColor(message));
    }

}
