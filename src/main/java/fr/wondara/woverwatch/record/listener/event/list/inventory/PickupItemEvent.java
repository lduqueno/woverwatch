package fr.wondara.woverwatch.record.listener.event.list.inventory;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import fr.wondara.woverwatch.replay.player.EntityLink;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketListenerPlayOut;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Collections;
import java.util.List;

public class PickupItemEvent extends AbstractUpdateHandEvent<PlayerPickupItemEvent> {

    public PickupItemEvent() {
        super(PlayerPickupItemEvent.class);
        shouldRunSync = true;
    }

    @Override
    public ActionContainer packetToSendInstantly(PlayerPickupItemEvent action) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("itemId", action.getItem().getEntityId());
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        if(container.isPresent("itemId")){
            EntityLink link = task.getEntityLinkFromOldId(container.getAsInt("itemId"));
            if(link != null)
                link.die(task.getViewer());
            return Collections.emptyList();
        }
        return super.transformIntoPackets(container, task, npc);
    }
}
