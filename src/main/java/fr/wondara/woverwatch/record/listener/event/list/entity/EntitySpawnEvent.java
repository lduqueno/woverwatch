package fr.wondara.woverwatch.record.listener.event.list.entity;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.util.ItemSerializer;
import net.minecraft.server.v1_9_R1.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntitySpawnEvent extends AbstractEntitySpawnEvent<org.bukkit.event.entity.EntitySpawnEvent> {

    public EntitySpawnEvent() {
        super(org.bukkit.event.entity.EntitySpawnEvent.class);
        isPerPlayer = false;
    }

    @Override
    public boolean shouldBeRecordedFor(org.bukkit.event.entity.EntitySpawnEvent event, RecordingPlayer player) {
        return event.getEntity() instanceof org.bukkit.entity.Item && isInRange(event.getEntity().getLocation(), player.getPlayer().getLocation());
    }

    @Override
    public ActionContainer record(org.bukkit.event.entity.EntitySpawnEvent action) {
        return addDefaultData(action, action.getEntity().getLocation(), action.getEntityType(), action.getEntity().getVelocity(), action.getEntity()
                .getEntityId());
    }

    @Override
    public Entity spawnEntity(EntityType type, World world, ActionContainer data, ReplayTask task) {
        Entity entity = null;
        switch (type) {
            case DROPPED_ITEM:
                entity = new EntityItem(world);
                ((EntityItem) entity).setItemStack(CraftItemStack.asNMSCopy(ItemSerializer.deserializeItem(data.get("itemStack"))));
                break;
        }
        return entity;
    }

    @Override
    public void addCustomData(org.bukkit.event.entity.EntitySpawnEvent event, ActionContainer data) {
        switch (event.getEntityType()) {
            case DROPPED_ITEM:
                data.set("itemStack", ItemSerializer.serializeItem(((CraftItem) event.getEntity()).getItemStack()));
                break;
        }
    }

    @Override
    public ImmutablePair<Integer, Integer> getObjectData(Entity entity) {
        if (entity instanceof org.bukkit.entity.Item)
            return new ImmutablePair<>(2, 1);
        return new ImmutablePair<>(0, 0);
    }

}
