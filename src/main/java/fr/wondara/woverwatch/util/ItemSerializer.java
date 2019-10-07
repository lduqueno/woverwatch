package fr.wondara.woverwatch.util;

import net.minecraft.server.v1_9_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

public class ItemSerializer {

    public static String serializeItem(ItemStack item) {
        StringBuilder string = new StringBuilder();
        if (item == null)
            return string.append("none").toString();
        string.append(item.getTypeId()).append(";");
        string.append(item.getData().getData()).append(";");
        string.append(item.getDurability()).append(";");
        string.append(!item.getEnchantments().isEmpty());
        if (item.getTypeId() == 438) {
            net.minecraft.server.v1_9_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);
            NBTTagCompound tagCompound = stack.getTag();
            if (tagCompound != null && !tagCompound.getString("Potion").isEmpty())
                string.append(";").append(tagCompound.getString("Potion").replace("minecraft:", ""));
        }
        return string.toString();
    }

    public static ItemStack deserializeItem(String string) {
        if (string.equals("none"))
            return null;
        String[] split = string.split(";");
        Material material = Material.getMaterial(Integer.parseInt(split[0]));
        ItemStack item = new ItemStack(material);
        item.setData(new MaterialData(material, Byte.parseByte(split[1])));
        item.setDurability(Short.parseShort(split[2]));
        if (Boolean.parseBoolean(split[3]))
            item.addUnsafeEnchantment(Enchantment.LURE, 1);
        if(split.length == 5){
            net.minecraft.server.v1_9_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);
            NBTTagCompound tagCompound = stack.getTag();
            if(tagCompound == null)
                tagCompound = new NBTTagCompound();
            tagCompound.setString("Potion", "minecraft:" + split[4]);
            stack.setTag(tagCompound);
            item = CraftItemStack.asBukkitCopy(stack);
        }
        return item;
    }
}
