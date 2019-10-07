package fr.wondara.woverwatch.record.listener.event.list.inventory;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EatedEvent extends AbstractUpdateHandEvent<PlayerItemConsumeEvent> {

    public EatedEvent() {
        super(PlayerItemConsumeEvent.class);
    }

    @Override
    public ActionContainer packetToSendInstantly(PlayerItemConsumeEvent action) {
        return new ActionContainer(actionClass);
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        return container.isEmpty() ? Collections.singletonList(new PacketPlayOutNamedSoundEffect(SoundEffects.bC, SoundCategory.PLAYERS, npc
                .getEntity().locX, npc.getEntity().locY, npc.getEntity().locZ, 0.5F, new Random().nextFloat() * 0.1F + 0.9F)) :
                super.transformIntoPackets(container, task, npc);
    }

}
