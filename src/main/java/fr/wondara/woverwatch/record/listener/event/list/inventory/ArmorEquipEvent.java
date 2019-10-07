package fr.wondara.woverwatch.record.listener.event.list.inventory;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.listener.armorapi.PlayerArmorEquipEvent;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import fr.wondara.woverwatch.util.ItemSerializer;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ArmorEquipEvent extends EventTransformer<PlayerArmorEquipEvent> {

    public ArmorEquipEvent() {
        super(PlayerArmorEquipEvent.class);
    }

    @Override
    public boolean shouldBeRecordedFor(PlayerArmorEquipEvent event, RecordingPlayer player) {
        return event.getPlayer() == player.getPlayer();
    }

    @Override
    public ActionContainer record(PlayerArmorEquipEvent event) {
        ActionContainer container =  new ActionContainer(actionClass);
        container.set("type", event.getType().getNmsName());
        container.set("item", ItemSerializer.serializeItem(event.getNewArmorPiece()));
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        return Collections.singletonList(new PacketPlayOutEntityEquipment(npc.getEntity().getId(), EnumItemSlot.a(container.get("type")),
                CraftItemStack.asNMSCopy(ItemSerializer.deserializeItem(container.get("item")))));
    }

}
