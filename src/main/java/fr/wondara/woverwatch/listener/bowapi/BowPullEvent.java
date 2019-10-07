package fr.wondara.woverwatch.listener.bowapi;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class BowPullEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private boolean pulling;

    public BowPullEvent(final Player player, boolean pulling){
        super(player);
        this.pulling = pulling;
    }

    public boolean isPulling(){
        return pulling;
    }

    public final static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public final HandlerList getHandlers(){
        return handlers;
    }

}