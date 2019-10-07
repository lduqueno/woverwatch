package fr.wondara.woverwatch.record.listener.event.list.inventory;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class HeldItemEvent extends AbstractUpdateHandEvent<PlayerItemHeldEvent> {

    public HeldItemEvent() {
        super(PlayerItemHeldEvent.class);
    }

    @Override
    public ActionContainer packetToSendInstantly(PlayerItemHeldEvent action) {
        return null;
    }

}
