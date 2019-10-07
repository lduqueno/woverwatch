package fr.wondara.woverwatch;

import fr.wondara.woverwatch.command.CommandReplay;
import fr.wondara.woverwatch.listener.bowapi.BowListener;
import fr.wondara.woverwatch.record.listener.event.EventTransformer;
import fr.wondara.woverwatch.listener.BaseListener;
import fr.wondara.woverwatch.listener.armorapi.ArmorListener;
import fr.wondara.woverwatch.record.listener.event.list.action.*;
import fr.wondara.woverwatch.record.listener.event.list.block.BlockBreakEvent;
import fr.wondara.woverwatch.record.listener.event.list.block.BlockPlaceEvent;
import fr.wondara.woverwatch.record.listener.event.list.entity.*;
import fr.wondara.woverwatch.record.listener.event.list.inventory.*;
import fr.wondara.woverwatch.record.listener.packet.PacketTransformer;
import fr.wondara.woverwatch.record.listener.packet.list.EntityStatusPacket;
import fr.wondara.woverwatch.record.listener.packet.list.PotionRemovePacket;
import org.bukkit.plugin.java.JavaPlugin;

public final class Woverwatch extends JavaPlugin {

    private static Woverwatch instance;
    private Timeline timeline;

    //Current bugs :
    //normal potion held list show default potion material

    @Override
    public void onEnable() {
        instance = this;
        this.timeline = new Timeline();
        saveDefaultConfig();
        registerCommands();
        registerListeners();
        registerTransformers();
    }

    private void registerCommands(){
        getCommand("replay").setExecutor(new CommandReplay());
    }

    private void registerListeners(){
        getServer().getPluginManager().registerEvents(new BaseListener(), this);
        getServer().getPluginManager().registerEvents(new BowListener(), this);
        getServer().getPluginManager().registerEvents(new ArmorListener(getConfig().getStringList("blockedRightClickEquipArmor")), this);
    }

    private void registerTransformers(){
        EventTransformer.registerTransformer(new InteractEvent());
        EventTransformer.registerTransformer(new MoveEvent());
        EventTransformer.registerTransformer(new SneakEvent());
        EventTransformer.registerTransformer(new SprintEvent());
        EventTransformer.registerTransformer(new BowEvent());
        EventTransformer.registerTransformer(new ProjectileEvent());
        EventTransformer.registerTransformer(new EntitySpawnEvent());
        EventTransformer.registerTransformer(new TeleportEvent());
        EventTransformer.registerTransformer(new PotionEvent());
        EventTransformer.registerTransformer(new ArmorEquipEvent());
        EventTransformer.registerTransformer(new PickupItemEvent());
        EventTransformer.registerTransformer(new DropItemEvent());
        EventTransformer.registerTransformer(new HeldItemEvent());
        EventTransformer.registerTransformer(new SwapItemEvent());
        EventTransformer.registerTransformer(new EatedEvent());
        EventTransformer.registerTransformer(new BlockPlaceEvent());
        EventTransformer.registerTransformer(new BlockBreakEvent());

        PacketTransformer.registerTransformer(new PotionRemovePacket());
        PacketTransformer.registerTransformer(new EntityStatusPacket());
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    public static Woverwatch getInstance() {
        return instance;
    }
}
