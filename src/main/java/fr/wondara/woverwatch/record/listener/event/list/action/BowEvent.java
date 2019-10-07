package fr.wondara.woverwatch.record.listener.event.list.action;

import fr.wondara.woverwatch.listener.bowapi.BowPullEvent;
import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.List;

public class BowEvent extends EventTransformer<BowPullEvent> {

    public BowEvent() {
        super(BowPullEvent.class);
        shouldIgnoreCancelled = true;
    }

    @Override
    public boolean shouldBeRecordedFor(BowPullEvent event, RecordingPlayer player) {
        return event.getPlayer() == player.getPlayer();
    }

    @Override
    public ActionContainer record(BowPullEvent event) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("pulling", event.isPulling());
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        if (container.getAsBoolean("pulling"))
            npc.getEntity().c(EnumHand.MAIN_HAND);
        else
            npc.getEntity().cz();
        return Collections.singletonList(new PacketPlayOutEntityMetadata(npc.getEntity().getId(), npc.getEntity().getDataWatcher(), true));
    }

}
