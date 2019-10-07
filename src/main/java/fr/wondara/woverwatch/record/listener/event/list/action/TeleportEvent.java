package fr.wondara.woverwatch.record.listener.event.list.action;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TeleportEvent extends EventTransformer<PlayerTeleportEvent> {

    public TeleportEvent() {
        super(PlayerTeleportEvent.class);
    }

    @Override
    public boolean shouldBeRecordedFor(PlayerTeleportEvent event, RecordingPlayer player) {
        return event.getPlayer() == player.getPlayer() && event.getTo().getWorld() == player.getRecord().getWorld();
    }

    @Override
    public ActionContainer record(PlayerTeleportEvent event) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("x", event.getTo().getX());
        container.set("y", event.getTo().getY());
        container.set("z", event.getTo().getZ());
        container.set("yaw",  event.getTo().getYaw());
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
        npc.getEntity().setPositionRotation(x, y, z, yaw, pitch);
        return Collections.singletonList(new PacketPlayOutEntityTeleport(npc.getEntity()));
    }

}
