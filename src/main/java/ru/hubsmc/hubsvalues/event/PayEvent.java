package ru.hubsmc.hubsvalues.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PayEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private OfflinePlayer player;
    private OfflinePlayer target;
    private boolean cancelled;
    private int amount;

    public PayEvent(OfflinePlayer player, OfflinePlayer target, int amount) {
        this.player = player;
        this.target = target;
        this.amount = amount;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public OfflinePlayer getPlayer(){
        return player;
    }

    public OfflinePlayer getTarget() {
        return target;
    }

    public int getAmount() {
        return amount;
    }

}
