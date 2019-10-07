package fr.wondara.woverwatch.record.listener.event.list.entity;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PotionEvent extends EventTransformer<PotionSplashEvent> {

    public PotionEvent() {
        super(PotionSplashEvent.class);
        shouldRunSync = true;
    }

    @Override
    public boolean shouldBeRecordedFor(PotionSplashEvent event, RecordingPlayer player) {
        return event.getAffectedEntities().contains(player.getPlayer());
    }

    @Override
    public ActionContainer record(PotionSplashEvent event) {
        ActionContainer container = new ActionContainer(actionClass);
        if(event.getEntity().getEffects().isEmpty())
            return container;
        PotionEffect potion = event.getEntity().getEffects().iterator().next();
        container.set("effectId", potion.getType().getId());
        container.set("duration", potion.getDuration());
        container.set("amplifier", potion.getAmplifier());
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        if(container.isEmpty())
            return Collections.emptyList();
        MobEffect effect = new MobEffect(MobEffectList.fromId(container.getAsInt("effectId")),
                container.getAsInt("duration"), container.getAsInt("amplifier"));
        npc.getEntity().effects.put(effect.getMobEffect(), effect);
        try {
            Method method = npc.getEntity().getClass().getSuperclass().getSuperclass().getDeclaredMethod("F");
            method.setAccessible(true);
            method.invoke(npc.getEntity());
        } catch (Exception e) {
        }
        return Arrays.asList(new PacketPlayOutEntityEffect(npc.getEntity().getId(), effect), new PacketPlayOutEntityMetadata(npc.getEntity()
                .getId(), npc.getEntity().getDataWatcher(), true));
    }

}
