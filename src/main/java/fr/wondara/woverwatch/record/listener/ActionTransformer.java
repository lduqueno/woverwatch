package fr.wondara.woverwatch.record.listener;

import com.google.common.collect.Maps;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PacketListenerPlayOut;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public abstract class ActionTransformer<T> {

    private static Map<Class<?>, ActionTransformer<?>> TRANSFORMERS = Maps.newHashMap();

    protected Class<T> actionClass;
    protected boolean shouldRunSync = false;
    protected boolean isPerPlayer = true;

    public ActionTransformer(Class<T> actionClass) {
        this.actionClass = actionClass;
    }

    protected abstract void register();

    public abstract boolean shouldBeRecordedFor(T action, RecordingPlayer player);

    public abstract ActionContainer record(T action);

    public abstract List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc);

    protected boolean isInRange(Location location, Location center) {
        return location.getWorld() == center.getWorld() && location.distance(center) <= 15;
    }

    protected double roundPos(double pos) {
        return Math.round(pos * 100) / 100.00D;
    }

    public Class<T> getActionClass() {
        return actionClass;
    }

    public boolean isShouldRunSync() {
        return shouldRunSync;
    }

    public boolean isPerPlayer() {
        return isPerPlayer;
    }

    public static void registerTransformer(ActionTransformer<?> transformer) {
        if (findTransformer(transformer.getActionClass().getSimpleName()) != null)
            throw new IllegalArgumentException("Transformer " + transformer.getActionClass().getSimpleName() + " already registered!");
        TRANSFORMERS.put(transformer.getActionClass(), transformer);
        transformer.register();
    }

    public static ActionTransformer<?> findTransformer(String className) {
        Map.Entry<Class<?>, ActionTransformer<?>> e = TRANSFORMERS.entrySet().stream().filter(entry -> entry.getKey().getSimpleName().equals(className)).findFirst().orElse(null);
        return e == null ? null : e.getValue();
    }

}
