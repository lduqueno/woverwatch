package fr.wondara.woverwatch.record.listener.event.list.block;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.util.CraftMagicNumbers;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlockPlaceEvent extends EventTransformer<org.bukkit.event.block.BlockPlaceEvent> {

    public BlockPlaceEvent() {
        super(org.bukkit.event.block.BlockPlaceEvent.class);
    }

    @Override
    public boolean shouldBeRecordedFor(org.bukkit.event.block.BlockPlaceEvent event, RecordingPlayer player) {
        return isInRange(event.getBlockPlaced().getLocation(), event.getPlayer().getLocation());
    }

    @Override
    public ActionContainer record(org.bukkit.event.block.BlockPlaceEvent event) {
        ActionContainer container = new ActionContainer(actionClass);
        Block block = event.getBlockPlaced();
        container.set("x", block.getLocation().getBlockX());
        container.set("y", block.getLocation().getBlockY());
        container.set("z", block.getLocation().getBlockZ());
        container.set("type", block.getType().getId());
        container.set("data", block.getData());
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        double x = container.getAsDouble("x");
        double y = container.getAsDouble("y");
        double z = container.getAsDouble("z");
        Material type = Material.getMaterial(container.getAsInt("type"));
        byte data = container.getAsByte("data");
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(npc.getEntity().world, new BlockPosition(x, y, z));
        packet.block = CraftMagicNumbers.getBlock(type.getId()).fromLegacyData(data);
        return Collections.singletonList(packet);
    }

}
