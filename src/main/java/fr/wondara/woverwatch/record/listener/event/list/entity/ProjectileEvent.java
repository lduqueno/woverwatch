package fr.wondara.woverwatch.record.listener.event.list.entity;

import fr.wondara.woverwatch.record.listener.ActionContainer;
import fr.wondara.woverwatch.record.player.RecordingPlayer;
import fr.wondara.woverwatch.replay.ReplayTask;
import fr.wondara.woverwatch.replay.player.EntityLink;
import fr.wondara.woverwatch.util.ItemSerializer;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftTippedArrow;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileEvent extends AbstractEntitySpawnEvent<ProjectileLaunchEvent> {

    public ProjectileEvent() {
        super(ProjectileLaunchEvent.class);
        isPerPlayer = false;
    }

    @Override
    public boolean shouldBeRecordedFor(ProjectileLaunchEvent event, RecordingPlayer player) {
        return event.getEntity().getShooter() instanceof Player && isInRange(event.getEntity().getLocation(), player.getPlayer().getLocation());
    }

    @Override
    public ActionContainer record(ProjectileLaunchEvent action) {
        return addDefaultData(action, action.getEntity().getLocation(), action.getEntityType(), action.getEntity().getVelocity(), action.getEntity()
                .getEntityId());
    }

    @Override
    public void addCustomData(ProjectileLaunchEvent event, ActionContainer data) {
        switch (event.getEntityType()) {
            case TIPPED_ARROW:
                data.set("potionType", ((CraftTippedArrow) event.getEntity()).getHandle().getType());
                break;
            case SPECTRAL_ARROW:
                data.set("glowingTicks", ((SpectralArrow) event.getEntity()).getGlowingTicks());
                break;
            case SPLASH_POTION:
                data.set("potionItem", ItemSerializer.serializeItem(((ThrownPotion) event.getEntity()).getItem()));
                break;
            case LINGERING_POTION:
                data.set("potionItem", ItemSerializer.serializeItem(((LingeringPotion) event.getEntity()).getItem()));
                break;
        }
        if (event.getEntity().getShooter() instanceof Player)
            data.set("shooter", ((Player) event.getEntity().getShooter()).getEntityId());
    }

    @Override
    public Entity spawnEntity(EntityType type, World world, ActionContainer data, ReplayTask task) {
        Entity entity = null;
        if (Projectile.class.isAssignableFrom(type.getEntityClass())) {
            EntityLink link = task.getEntityLinkFromOldId(data.getAsInt("shooter"));
            if (link != null) {
                EntityLiving shooter = (EntityLiving) link.getReplayEntity();
                switch (type) {
                    case ARROW:
                        entity = new EntityTippedArrow(world, shooter);
                        break;
                    case TIPPED_ARROW:
                        EntityTippedArrow tippedArrow = new EntityTippedArrow(world, shooter);
                        tippedArrow.setType(data.get("potionType"));
                        entity = tippedArrow;
                        break;
                    case SPECTRAL_ARROW:
                        EntitySpectralArrow spectralArrow = new EntitySpectralArrow(world, shooter);
                        spectralArrow.f = data.getAsInt("glowingTicks");
                        entity = spectralArrow;
                        break;
                    case SNOWBALL:
                        entity = new EntitySnowball(world, shooter);
                        break;
                    case EGG:
                        entity = new EntityEgg(world, shooter);
                        break;
                    case SPLASH_POTION:
                        entity = new EntityPotion(world, shooter, CraftItemStack.asNMSCopy(ItemSerializer.deserializeItem(data.get("potionItem"))));
                        break;
                    case LINGERING_POTION:
                        entity = new EntityPotion(world, shooter, CraftItemStack.asNMSCopy(ItemSerializer.deserializeItem(data.get("potionItem"))));
                        break;
                    case ENDER_PEARL:
                        entity = new EntityEnderPearl(world, shooter);
                        break;
                }
            }
        }
        return entity;
    }

    @Override
    public ImmutablePair<Integer, Integer> getObjectData(Entity entity) {
        if (entity instanceof EntityArrow) {
            int id = ((EntityArrow) entity).shooter.getId() + 1;
            if (entity instanceof EntitySpectralArrow)
                return new ImmutablePair<>(91, id);
            return new ImmutablePair<>(60, id);
        } else if (entity instanceof EntityPotion)
            return new ImmutablePair<>(73, 0);
        else if (entity instanceof EntitySnowball)
            return new ImmutablePair<>(61, 0);
        else if (entity instanceof EntityEgg)
            return new ImmutablePair<>(62, 0);
        else if (entity instanceof EntityEnderPearl)
            return new ImmutablePair<>(65, 0);
        return new ImmutablePair<>(0, 0);
    }

}
