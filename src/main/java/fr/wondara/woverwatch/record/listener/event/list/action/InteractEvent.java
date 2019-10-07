package fr.wondara.woverwatch.record.listener.event.list.action;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketListenerPlayOut;
import net.minecraft.server.v1_9_R1.PacketPlayOutAnimation;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.List;

public class InteractEvent extends EventTransformer<PlayerInteractEvent> {

    public InteractEvent() {
        super(PlayerInteractEvent.class);
        shouldIgnoreCancelled = true;
    }

    @Override
    public boolean shouldBeRecordedFor(PlayerInteractEvent event, RecordingPlayer player) {
        return event.getPlayer() == player.getPlayer() && (event.getAction().name().contains("LEFT") || (
                event.getAction() == Action.RIGHT_CLICK_BLOCK && event.isBlockInHand()));
    }

    @Override
    public ActionContainer record(PlayerInteractEvent event) {
        return new ActionContainer(actionClass);
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        return Collections.singletonList(new PacketPlayOutAnimation(npc.getEntity(), 0));
    }

}
