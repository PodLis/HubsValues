package ru.hubsmc.hubsvalues.board;

import org.bukkit.entity.Player;
import ru.hubsmc.hubsvalues.HubsValues;

import static ru.hubsmc.hubsvalues.api.API.*;

public class ScoreboardHolder {

    private App app;
    public Player player;

    private SlimBoard slim;

    private String vPlayer = "", vDollars = "", vHubixes = "", vMana = "", vMax = "", vRegen = "";

    public ScoreboardHolder(App app, Player player) {

        this.player = player;
        updateValues();

        this.app = app;

        slim = new SlimBoard(HubsValues.getInstance(), player, app.getRows().size());

        app.registerHolder(this);
    }

    public void update() {

        slim.setTitle(app.getTitle().getLine());

        int count = 0;
        for(Row row : app.getRows()) {
            String line = row.getLine();
            if (line.contains("%")) {
                slim.setLine(count, replaceValues(line));
            } else {
                slim.setLine(count, line);
            }
            count++;
        }
    }

    private String replaceValues(String s) {
        return s.replace("%player%", vPlayer)
                .replace("%dollars%", vDollars)
                .replace("%hubixes%", vHubixes)
                .replace("%mana%", vMana)
                .replace("%max%", vMax)
                .replace("%regen%", vRegen);
    }

    public void updateValues() {
        vPlayer = "" + player.getDisplayName();
        vDollars = "" + getDollars(player);
        vHubixes = "" + getHubixes(player);
        vMana = "" + getMana(player);
        vMax = "" + getMaxMana(player);
        vRegen = "" + getRegenMana(player);
    }

}
