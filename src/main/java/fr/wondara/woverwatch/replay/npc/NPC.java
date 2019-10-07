package fr.wondara.woverwatch.replay.npc;

import fr.wondara.woverwatch.PlayerInfo;
import fr.wondara.woverwatch.Woverwatch;
import fr.wondara.woverwatch.util.ItemSerializer;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Method;

public class NPC {

    private PlayerInfo info;
    private EntityPlayer player;
    private Location spawnLocation;

    public NPC(PlayerInfo info, Location loc) {
        this.info = info;
        this.player = new PlayerBuilder(info.getName(), info.getUUID(), info.getSkinValue(), info.getSkinSignature()).withGameMode(
                WorldSettings.EnumGamemode.SURVIVAL).create();
        this.player.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        this.spawnLocation = loc;
    }

    public void spawnFor(Player toPlayer) {
        this.player.world = ((CraftWorld) this.spawnLocation.getWorld()).getHandle();
        this.player.locX = spawnLocation.getX();
        this.player.locY = spawnLocation.getY();
        this.player.locZ = spawnLocation.getZ();
        this.player.yaw = spawnLocation.getYaw();
        this.player.pitch = spawnLocation.getPitch();
        sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, player), toPlayer);
        sendPacket(new PacketPlayOutNamedEntitySpawn(player), toPlayer);
        Bukkit.getScheduler().runTaskLater(Woverwatch.getInstance(), () -> sendPacket(new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.player), toPlayer), 5);
        sendPacket(new PacketPlayOutEntityTeleport(player), toPlayer);
        int i1 = MathHelper.d((player.yaw * 256.0f / 360.0f));
        int j1 = MathHelper.d((player.pitch * 256.0f / 360.0f));
        sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(player.getId(), (byte) i1, (byte) j1, player.onGround), toPlayer);
        ItemStack offHand = CraftItemStack.asNMSCopy(info.getStartItemInOtherHand());
        ItemStack mainHand = CraftItemStack.asNMSCopy(info.getStartItemInMainHand());
        sendPacket(new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.FEET, CraftItemStack.asNMSCopy(info.getStartArmors()[0])), toPlayer);
        sendPacket(new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(info.getStartArmors()[1])), toPlayer);
        sendPacket(new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(info.getStartArmors()[2])), toPlayer);
        sendPacket(new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(info.getStartArmors()[3])), toPlayer);
        sendPacket(new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.OFFHAND, offHand),
                toPlayer);
        player.inventory.extraSlots[0] = offHand;
        sendPacket(new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.MAINHAND, mainHand),
                toPlayer);
        player.inventory.setItem(player.inventory.itemInHandIndex, mainHand);
        player.setEquipment(EnumItemSlot.MAINHAND, mainHand);
        for(PotionEffect potion : info.getStartPotionEffects()) {
            MobEffect effect = new MobEffect(MobEffectList.fromId(potion.getType().getId()), potion.getDuration(), potion.getAmplifier());
            player.effects.put(effect.getMobEffect(), effect);
            try {
                Method method = player.getClass().getSuperclass().getSuperclass().getDeclaredMethod("F");
                method.setAccessible(true);
                method.invoke(player);
            } catch (Exception e) {
            }
            sendPacket(new PacketPlayOutEntityEffect(player.getId(), effect), toPlayer);
        }
        sendPacket(new PacketPlayOutEntityMetadata(player.getId(), player.getDataWatcher(), true), toPlayer);
    }

    public void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public EntityPlayer getEntity() {
        return this.player;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }
}