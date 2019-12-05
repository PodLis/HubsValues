package ru.hubsmc.hubsvalues.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.hubsmc.hubsvalues.HubsValues;
import ru.hubsmc.hubsvalues.board.App;

import static ru.hubsmc.hubsvalues.api.API.savePlayerData;

public class LeaveEvent implements Listener {

    private App app;

    public LeaveEvent(App app) {
        this.app = app;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        savePlayerData(event.getPlayer());
        if(app == null) {
            return;
        }
        app.unregisterHolder(event.getPlayer());
        HubsValues.boardMap.remove(event.getPlayer());
        event.getPlayer().setScoreboard(HubsValues.EMPTY_BOARD);
    }

}