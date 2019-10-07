package fr.wondara.woverwatch.record.listener.event.list.action;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.event.Event;

import java.util.Collections;
import java.util.List;

public abstract class AbstractActionEvent<T extends Event> extends EventTransformer<T> {

    public AbstractActionEvent(Class<T> clazz) {
        super(clazz);
    }

    protected abstract PacketPlayInEntityAction.EnumPlayerAction eventToAction(ActionContainer container);

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        int id = -1;
        switch (eventToAction(container)) {
            case START_SNEAKING: {
                id = 1;
                npc.getEntity().setSneaking(true);
                break;
            }
            case STOP_SNEAKING: {
                id = 1;
                npc.getEntity().setSneaking(false);
                break;
            }
            case START_SPRINTING: {
                id = 3;
                npc.getEntity().setSprinting(true);
                break;
            }
            case STOP_SPRINTING: {
                id = 3;
                npc.getEntity().setSprinting(false);
                break;
            }
        }
        if (id == -1)
            return Collections.emptyList();
        DataWatcher dw = npc.getEntity().getDataWatcher();
        return Collections.singletonList(new PacketPlayOutEntityMetadata(npc.getEntity().getId(), dw, true));
    }


}
