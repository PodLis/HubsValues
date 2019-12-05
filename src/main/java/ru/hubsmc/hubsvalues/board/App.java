package ru.hubsmc.hubsvalues.board;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class App extends BukkitRunnable {

    private Row title;
    private ArrayList<Row> rows = new ArrayList<>();
    public ArrayList<ScoreboardHolder> holders = new ArrayList<>();

    public App(ConfigurationSection configurationSection)
    {

        title = new Row((ArrayList<String>) configurationSection.getStringList("title.liner"), configurationSection.getInt("title.interval"));

        for (int i = 1; i < 200; i++) {
            ConfigurationSection section = configurationSection.getConfigurationSection("rows." + i);
            if (null != section) {
                Row row = new Row((ArrayList<String>) section.getStringList("liner"), section.getInt("interval"));
                rows.add(row);
            }
        }

    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public Row getTitle() {
        return title;
    }

    public void registerHolder(ScoreboardHolder holder) {
        holders.add(holder);
    }

    public void unregisterHolder(ScoreboardHolder holder) {
        holders.remove(holder);
    }

    public void unregisterHolder(Player player) {
        for (ScoreboardHolder holder : holders)
            if (holder.player == player) {
                holders.remove(holder);
                break;
            }
    }

    @Override
    public void run() {

        title.update();
        for (Row row : rows)
            row.update();

        for (ScoreboardHolder holder : holders)
            holder.update();
    }

}
