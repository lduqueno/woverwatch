package fr.wondara.woverwatch.record.listener.event.list.entity;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.npc.NPC;
import fr.wondara.woverwatch.replay.player.EntityLink;
import fr.wondara.woverwatch.util.LocationSerializer;
import net.minecraft.server.v1_9_R1.*;
import net.minecraft.server.v1_9_R1.Entity;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class AbstractEntitySpawnEvent<T extends Event> extends EventTransformer<T> {

    public AbstractEntitySpawnEvent(Class<T> eventClass) {
        super(eventClass);
        shouldRunSync = true;
    }

    public abstract void addCustomData(T event, ActionContainer data);
    public abstract ImmutablePair<Integer, Integer> getObjectData(Entity entity);
    public abstract Entity spawnEntity(EntityType type, World world, ActionContainer data, ReplayTask task);

    protected ActionContainer addDefaultData(T event, Location location, EntityType type, Vector velocity, int currentId) {
        ActionContainer container = new ActionContainer(actionClass);
        container.set("location", LocationSerializer.serializeLocation(location));
        container.set("type", type.getEntityClass().getSimpleName());
        container.set("velocity", velocity.getX() + ";" + velocity.getY() + ";" + velocity.getZ());
        container.set("oldId", currentId);
        ActionContainer data = new ActionContainer(String.class);
        addCustomData(event, data);
        container.set("data", data);
        return container;
    }

    @Override
    public List<Packet<PacketListenerPlayOut>> transformIntoPackets(ActionContainer container, ReplayTask task, NPC npc) {
        ActionContainer data = container.getAsContainer("data");
        EntityType type = null;
        String rawType = container.get("type");
        for (EntityType types : EntityType.values())
            if (types.getEntityClass().getSimpleName().equals(rawType)) {
                type = types;
                break;
            }
        if (type == null) {
            System.out.println("Unable to find EntityType from class name :  " + rawType + "!");
            return Collections.emptyList();
        }
        World world = ((CraftWorld) task.getReplay().getMap()).getHandle();
        Entity entity = spawnEntity(type, world, data, task);
        if (entity == null) {
            System.out.println("Unable to spawn Entity " + rawType + "!");
            return Collections.emptyList();
        }
        Location location = LocationSerializer.deserializeLocation(container.get("location"), Bukkit.getWorld(world.getWorld().getName()));
        if (location == null)
            return Collections.emptyList();
        entity.world = world;
        entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        String[] split = container.get("velocity").split(";");
        entity.getBukkitEntity().setVelocity(new Vector(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
        int oldId = container.getAsInt("oldId");
        ImmutablePair<Integer, Integer> objectData = getObjectData(entity);
        task.getEntitiesToRemove().add(new EntityLink(oldId, entity));
        world.entityList.add(entity);
        WorldServer nmsWorld = ((WorldServer) world);
        EntityTracker tracker = nmsWorld.getTracker();
        int e = nmsWorld.getMinecraftServer().getPlayerList().d();
        PersonalEntityTracker entitytrackerentry = new PersonalEntityTracker(task, entity, 64, e, entity instanceof EntityItem ? 20 : 10, true);
        try {
            Field list = tracker.getClass().getDeclaredField("c");
            list.setAccessible(true);
            ((Set<EntityTrackerEntry>) list.get(tracker)).add(entitytrackerentry);
        } catch (Exception e2) {
            System.out.println("error ");
            e2.printStackTrace();
        }
        tracker.trackedEntities.a(entity.getId(), entitytrackerentry);
        entitytrackerentry.scanPlayers(Collections.singletonList(((CraftPlayer) task.getViewer()).getHandle()));
        return Arrays.asList(new PacketPlayOutSpawnEntity(entity, objectData.left, objectData.right), new PacketPlayOutEntityMetadata(
                entity.getId(), entity.getDataWatcher(), true), new PacketPlayOutEntity.PacketPlayOutEntityLook());
    }

    public static class PersonalEntityTracker extends EntityTrackerEntry {

        private ReplayTask task;

        public PersonalEntityTracker(ReplayTask task, Entity entity, int i, int j, int k, boolean flag) {
            super(entity, i, j, k, flag);
            this.task = task;
        }

        @Override
        public void updatePlayer(EntityPlayer entityplayer) {
            if (entityplayer.getUniqueID().toString().equals(task.getViewer().getUniqueId().toString()))
                super.updatePlayer(entityplayer);
        }

        @Override
        public void track(List<EntityHuman> list) {
            if(list.size() != 1 || !list.get(0).getUniqueID().toString().equals(task.getViewer().getUniqueId().toString()))
                list = Collections.singletonList(((CraftPlayer) task.getViewer()).getHandle());
            super.track(list);
        }

        @Override
        public void clear(EntityPlayer entityplayer) {
            if (entityplayer.getUniqueID().toString().equals(task.getViewer().getUniqueId().toString()))
                super.clear(entityplayer);
        }
    }

}
