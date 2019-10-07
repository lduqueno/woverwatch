package fr.wondara.woverwatch.record.listener.event.list.inventory;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import org.bukkit.event.player.PlayerDropItemEvent;

public class DropItemEvent extends AbstractUpdateHandEvent<PlayerDropItemEvent> {

    public DropItemEvent() {
        super(PlayerDropItemEvent.class);
    }

    @Override
    public ActionContainer packetToSendInstantly(PlayerDropItemEvent action) {
        return null;
    }
}
