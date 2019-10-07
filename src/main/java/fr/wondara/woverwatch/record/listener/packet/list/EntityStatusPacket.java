package fr.wondara.woverwatch.record.listener.packet.list;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.packet.PacketTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketListenerPlayOut;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityStatus;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class EntityStatusPacket extends PacketTransformer<PacketPlayOutEntityStatus> {

    public EntityStatusPacket() {
        super(PacketPlayOutEntityStatus.class);
    }

    @Override
    public boolean shouldBeRecordedFor(PacketPlayOutEntityStatus packet, RecordingPlayer player) {
        return getField(packet, "a").equals(player.getPlayer().getEntityId());
    }

    @Override
    public ActionContainer record(PacketPlayOutEntityStatus packet) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("status", getField(packet, "b"));
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        return Collections.singletonList(new PacketPlayOutEntityStatus(npc.getEntity(), container.getAsByte("status")));
    }

}
