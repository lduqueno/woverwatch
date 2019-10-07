package fr.wondara.woverwatch.replay.player;

import fr.wondara.woverwatch.PlayerInfo;
import fr.wondara.woverwatch.record.listener.ActionContainer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public class ReplayingPlayer extends PlayerInfo {

    private EntityLink link;
    private int id;

    public ReplayingPlayer(String name, UUID uuid, ItemStack mainHand, ItemStack otherHand, ItemStack[] armor, Location location,
                           List<PotionEffect> potionEffects, String skinValue, String skinSignature, int oldEntityId, int id) {
        super(name, uuid, mainHand, otherHand, armor, location, potionEffects, skinValue, skinSignature);
        this.link = new EntityLink(oldEntityId);
        this.id = id;
    }

    public EntityLink getLink() {
        return link;
    }

    public int getId() {
        return id;
    }
}
