package fr.wondara.woverwatch.record.listener.packet.list;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.packet.PacketTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class PotionRemovePacket extends PacketTransformer<PacketPlayOutRemoveEntityEffect> {

    public PotionRemovePacket() {
        super(PacketPlayOutRemoveEntityEffect.class);
    }

    @Override
    public boolean shouldBeRecordedFor(PacketPlayOutRemoveEntityEffect packet, RecordingPlayer player) {
        return getField(packet, "a").equals(player.getPlayer().getEntityId());
    }

    @Override
    public ActionContainer record(PacketPlayOutRemoveEntityEffect packet) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("effectId", MobEffectList.getId((MobEffectList) getField(packet, "b")));
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        MobEffectList effect = MobEffectList.fromId(container.getAsInt("effectId"));
        npc.getEntity().effects.remove(effect);
        try {
            Method method = npc.getEntity().getClass().getSuperclass().getSuperclass().getDeclaredMethod("F");
            method.setAccessible(true);
            method.invoke(npc.getEntity());
        } catch (Exception e) {
        }
        return Arrays.asList(new PacketPlayOutRemoveEntityEffect(npc.getEntity().getId(), effect), new PacketPlayOutEntityMetadata(npc.getEntity()
                .getId(), npc.getEntity().getDataWatcher(), true));
    }

}
