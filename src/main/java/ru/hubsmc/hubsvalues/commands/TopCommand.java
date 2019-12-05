package ru.hubsmc.hubsvalues.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.hubsmc.hubsvalues.HubsValues;

public class TopCommand implements CommandExecutor {

    private HubsValues plugin;

    public TopCommand() {
        this.plugin = HubsValues.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(HubsValues.CHAT_PREFIX + "Автор ещё не закончил топ. Ждите...");
        return true;
    }
}
