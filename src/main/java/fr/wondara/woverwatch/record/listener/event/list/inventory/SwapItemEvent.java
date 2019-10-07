package fr.wondara.woverwatch.record.listener.event.list.inventory;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import fr.wondara.woverwatch.util.ItemSerializer;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Arrays;
import java.util.List;

public class SwapItemEvent extends EventTransformer<PlayerSwapHandItemsEvent> {

    public SwapItemEvent() {
        super(PlayerSwapHandItemsEvent.class);
    }

    @Override
    public boolean shouldBeRecordedFor(PlayerSwapHandItemsEvent event, RecordingPlayer player) {
        return event.getPlayer() == player.getPlayer();
    }

    @Override
    public ActionContainer record(PlayerSwapHandItemsEvent event) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("leftItem", ItemSerializer.serializeItem(event.getOffHandItem()));
        container.set("rightItem", ItemSerializer.serializeItem(event.getMainHandItem()));
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        net.minecraft.server.v1_9_R1.ItemStack left = CraftItemStack.asNMSCopy(ItemSerializer.deserializeItem(container.get("leftItem")));
        npc.getEntity().inventory.extraSlots[0] = left;
        net.minecraft.server.v1_9_R1.ItemStack right = CraftItemStack.asNMSCopy(ItemSerializer.deserializeItem(container.get("rightItem")));
        npc.getEntity().inventory.setItem(npc.getEntity().inventory.itemInHandIndex, right);
        return Arrays.asList(new PacketPlayOutEntityEquipment(npc.getEntity().getId(), EnumItemSlot.OFFHAND, left),
                new PacketPlayOutEntityEquipment(npc.getEntity().getId(), EnumItemSlot.MAINHAND, right));
    }

}
