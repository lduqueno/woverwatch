package fr.wondara.woverwatch;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class PlayerInfo {

    private String name;
    private UUID uuid;
    private ItemStack mainHand, otherHand;
    private ItemStack[] armor;
    private Location location;
    private List<PotionEffect> potionEffects;
    private String skinValue, skinSignature;

    public PlayerInfo(String name, UUID uuid, ItemStack mainHand, ItemStack otherHand, ItemStack[] armor, Location location,
                      List<PotionEffect> potionEffects, String skinValue, String skinSignature) {
        this.name = name;
        this.uuid = uuid;
        this.mainHand = mainHand;
        this.otherHand = otherHand;
        this.armor = armor;
        this.location = location;
        this.potionEffects = potionEffects;
        this.skinValue = skinValue;
        this.skinSignature = skinSignature;
    }

    public void clear() {
        this.mainHand = null;
        this.otherHand = null;
        this.armor = null;
        this.location = null;
        this.skinValue = null;
        this.skinSignature = null;
        this.potionEffects.clear();
    }

    public String getName() {
        return this.name;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public ItemStack getStartItemInMainHand() {
        return this.mainHand;
    }

    public ItemStack getStartItemInOtherHand() {
        return this.otherHand;
    }

    public ItemStack[] getStartArmors() {
        return this.armor;
    }

    public void setStartMainHand(ItemStack mainHand) {
        this.mainHand = mainHand;
    }

    public void setStartOtherHand(ItemStack otherHand) {
        this.otherHand = otherHand;
    }

    public void setStartArmors(ItemStack[] armor) {
        this.armor = armor;
    }

    public Location getStartLocation() {
        return location;
    }

    public void setStartLocation(Location location) {
        this.location = location;
    }

    public List<PotionEffect> getStartPotionEffects() {
        return potionEffects;
    }

    public void setStartPotionEffects(List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public String getSkinValue() {
        return skinValue;
    }

    public void setSkinValue(String skinValue) {
        this.skinValue = skinValue;
    }

    public String getSkinSignature() {
        return skinSignature;
    }

    public void setSkinSignature(String skinSignature) {
        this.skinSignature = skinSignature;
    }

    public String toString() {
        return "PlayerInfo{name='" + this.name + '\'' + ", uuid=" + this.uuid + ", mainHand=" + this.mainHand + "," +
                " otherHand=" + this.otherHand + ", armor=" + Arrays.toString(this.armor) + ", potionEffects=" + this.potionEffects + '}';
    }

}
