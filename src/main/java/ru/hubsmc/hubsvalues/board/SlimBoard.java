package ru.hubsmc.hubsvalues.board;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SlimBoard {

    public Scoreboard board;
    private Objective objective;

    private HashMap<Integer, String> cache = new HashMap<>();

    public SlimBoard(Plugin plugin, Player player, int linecount)
    {
        this.board = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.objective = board.registerNewObjective("sb1", "sb2");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName("...");

        int score = linecount;
        for (int i = 0; i < linecount; i++) {
            Team t = board.registerNewTeam(i + "");
            t.addEntry(ChatColor.values()[i] + "");

            objective.getScore(ChatColor.values()[i] + "").setScore(score);

            score--;
        }

        player.setScoreboard(board);
    }

    public void setTitle(String string) {
        if (string == null) string = "";

        if (cache.containsKey(-1) && cache.get(-1).equals(string)) {
            return;
        }
        cache.remove(-1);
        cache.put(-1, string);
        objective.setDisplayName(string);
    }

    public void setLine(int line, String string) {

        Team t = board.getTeam((line) + "");
        if (string == null) string = "";

        if(cache.containsKey(line) && cache.get(line).equals(string)) {
            return;
        }
        cache.remove(line);
        cache.put(line, string);

        string = prep(string);
        ArrayList<String> parts;
        parts = convertIntoPieces(string);

        t.setPrefix(parts.get(0));
        t.setSuffix(parts.get(1));

    }

    private String prep(String color) {
        ArrayList<String> parts;
        parts = convertIntoPieces(color);
        return parts.get(0) + "§f" +  getLastColor(parts.get(0)) + parts.get(1);
    }

    private String getLastColor(String s) {
        return ChatColor.getLastColors(s);
    }

    private ArrayList<String> convertIntoPieces(String s) {
        ArrayList<String> parts = new ArrayList<>();

        if(ChatColor.stripColor(s).length() > 64) {
            parts.add(s.substring(0, 64));

            String s2 = s.substring(64);
            if(s2.length() > 64)
                s2 = s2.substring(0, 64);
            parts.add(s2);
        } else {
            parts.add(s);
            parts.add("");
        }

        return parts;
    }

}
