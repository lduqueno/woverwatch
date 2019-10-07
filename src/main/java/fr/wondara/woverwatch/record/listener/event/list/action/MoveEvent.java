package fr.wondara.woverwatch.record.listener.event.list.action;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.List;

public class MoveEvent extends EventTransformer<PlayerMoveEvent> {

    public MoveEvent() {
        super(PlayerMoveEvent.class);
    }

    @Override
    public boolean shouldBeRecordedFor(PlayerMoveEvent event, RecordingPlayer player) {
        return event.getPlayer() == player.getPlayer();
    }

    @Override
    public ActionContainer record(PlayerMoveEvent event) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("x", roundPos(event.getTo().getX() - event.getFrom().getX()));
        container.set("y", roundPos(event.getTo().getY() - event.getFrom().getY()));
        container.set("z", roundPos(event.getTo().getZ() - event.getFrom().getZ()));
        container.set("yaw", event.getTo().getYaw());
        container.set("pitch", event.getTo().getPitch());
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        double x = container.getAsDouble("x");
        double y = container.getAsDouble("y");
        double z = container.getAsDouble("z");
        float yaw = container.getAsFloat("yaw");
        float pitch = container.getAsFloat("pitch");
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook p = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(npc.getEntity().getId(),
                (long) ((short) (((x + npc.getEntity().locX) * 32.0 - npc.getEntity().locX * 32.0) * 128.0)),
                (long) ((short) (((y + npc.getEntity().locY) * 32.0 - npc.getEntity().locY * 32.0) * 128.0)),
                (long) ((short) (((z + npc.getEntity().locZ) * 32.0 - npc.getEntity().locZ * 32.0) * 128.0)),
                this.toAngle(yaw), this.toAngle(pitch), npc.getEntity().onGround);
        npc.getEntity().setPositionRotation(npc.getEntity().locX + x, npc.getEntity().locY + y, npc.getEntity().locZ + z, yaw, pitch);
        return Arrays.asList(p, new PacketPlayOutEntityHeadRotation(npc.getEntity(), this.toAngle(container.getAsFloat("yaw"))));
    }

    private byte toAngle(float value) {
        return (byte) (value * 256.0f / 360.0f);
    }

}
