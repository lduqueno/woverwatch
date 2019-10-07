package fr.wondara.woverwatch.replay.npc;

import com.mojang.authlib.GameProfile;

import java.util.UUID;

import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_9_R1.WorldSettings;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.MinecraftServer;
import net.minecraft.server.v1_9_R1.PlayerInteractManager;
import net.minecraft.server.v1_9_R1.WorldServer;

public class PlayerBuilder {

    private MinecraftServer server;
    private WorldServer world;
    private UUID uuid;
    private GameProfile profile;
    private PlayerInteractManager manager;
    private WorldSettings.EnumGamemode mode;
    private int ping;
    private EntityPlayer player;

    public PlayerBuilder(String name, UUID uuid, String skinValue, String skinSignature) {
        this.server = MinecraftServer.getServer();
        this.world = this.server.getWorldServer(0);
        this.uuid = UUID.randomUUID();
        this.profile = new GameProfile(this.uuid, name);
        this.profile.getProperties().clear();
        Property property = new Property("textures", skinValue, skinSignature);
        this.profile.getProperties().put("textures", property);
        this.manager = new PlayerInteractManager(this.world);
        this.mode = WorldSettings.EnumGamemode.SURVIVAL;
        this.ping = 0;
    }

    public PlayerBuilder withGameMode(WorldSettings.EnumGamemode mode) {
        this.mode = mode;
        return this;
    }

    public EntityPlayer create() {
        this.player = new EntityPlayer(this.server, this.world, this.profile, this.manager);
        this.player.a(this.mode);
        this.player.ping = this.ping;

        return this.player;
    }
}
