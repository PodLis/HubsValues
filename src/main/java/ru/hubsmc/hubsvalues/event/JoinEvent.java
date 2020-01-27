package ru.hubsmc.hubsvalues.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.hubsmc.hubsvalues.HubsValues;
import ru.hubsmc.hubsvalues.board.ScoreboardHolder;

import static ru.hubsmc.hubsvalues.api.API.loadPlayerData;

public class JoinEvent implements Listener {

    public JoinEvent() {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (HubsValues.LOAD_VALUES_ON_JOIN) {
            loadPlayerData(event.getPlayer());
            HubsValues.boardMap.put(event.getPlayer(), new ScoreboardHolder(HubsValues.app, event.getPlayer()));
        }
    }

}
