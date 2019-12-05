package ru.hubsmc.hubsvalues.board;

import java.util.ArrayList;

public class Row {

    private ArrayList<String> lines;
    private String line;
    private int interval, updateTime = 1;
    private int current = 0;

    public boolean static_line = false;
    public boolean active = false;

    public Row(ArrayList<String> lines, int interval)
    {
        this.lines = lines;
        this.interval = interval;

        if (lines.size() == 1) {
            static_line = true;
        }

        line = replaceColor(lines.get(0));
    }

    public void update()
    {
        if (static_line) return;
        if (updateTime == interval) {
            active = true;
            current++;
            if (current >= lines.size())
                current = 0;
            line = replaceColor(lines.get(current));
            updateTime = 1;
            return;
        }
        updateTime++;
    }

    public String getLine() {
        return this.line;
    }

    private String replaceColor(String s) {
        return s.replace("&", "\u00a7");
    }

}
