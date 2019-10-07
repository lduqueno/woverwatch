package fr.wondara.woverwatch.record.listener.event.list.inventory;

import fr.wondara.woverwatch.Woverwatch;
import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import fr.wondara.woverwatch.util.ItemSerializer;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import java.util.Collections;
import java.util.List;

public abstract class AbstractUpdateHandEvent<T extends PlayerEvent> extends EventTransformer<T> {

    public AbstractUpdateHandEvent(Class<T> eventClass) {
        super(eventClass);
    }

    public abstract ActionContainer packetToSendInstantly(T action);

    @Override
    public boolean shouldBeRecordedFor(T action, RecordingPlayer player) {
        if(action.getPlayer() == player.getPlayer()){ //We need to check what's the main hand item AFTER the event call, otherwise its will show the old one
            Bukkit.getScheduler().runTaskLater(Woverwatch.getInstance(), () -> { //So we call the record method one tick after!
                addActionContainer(player, record(action));
            }, 1L);
            ActionContainer toSendNow = packetToSendInstantly(action); //If there is many packets to send (one instantly) like EatedEvent
            if(toSendNow != null)                                      //(Packet sound AND one tick after, Hand item update)
                addActionContainer(player, toSendNow);                 //Send Sound packet now AND one tick after, send hand item update
        }
        return false;
    }

    @Override
    public ActionContainer record(T event) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("itemHand", ItemSerializer.serializeItem(event.getPlayer().getInventory().getItemInMainHand()));
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        ItemStack item = CraftItemStack.asNMSCopy(ItemSerializer.deserializeItem(container.get("itemHand")));
        npc.getEntity().inventory.setItem(npc.getEntity().inventory.itemInHandIndex, item);
        return Collections.singletonList(new PacketPlayOutEntityEquipment(npc.getEntity().getId(), EnumItemSlot.MAINHAND, item));
    }

    private void addActionContainer(RecordingPlayer player, ActionContainer packetToAdd){
        if(player == null || !player.isRecording() || !player.getPlayer().isOnline())
            return;
        packetToAdd.set("player", player.getRecord().getId(player));
        player.getRecord().addActionContainer(packetToAdd);
    }

}
