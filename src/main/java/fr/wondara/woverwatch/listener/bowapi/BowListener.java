package fr.wondara.woverwatch.listener.bowapi;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class BowListener implements Listener {

    private Set<String> drawing = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDraw(PlayerInteractEvent e) {
        if (e.getItem() != null && e.getItem().getType() == Material.BOW && e.getAction().name().contains("RIGHT") &&
                (e.getPlayer().getInventory().contains(Material.ARROW) || e.getPlayer().getInventory().contains(Material.TIPPED_ARROW) ||
                e.getPlayer().getInventory().contains(Material.SPECTRAL_ARROW))) {
            drawing.add(e.getPlayer().getName());
            Bukkit.getPluginManager().callEvent(new BowPullEvent(e.getPlayer(), true));
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player shooter = (Player) e.getEntity();
            if (drawing.contains(shooter.getName()))
                stopDrawing(shooter);
        }
    }

    @EventHandler
    public void onChangeSlot(PlayerItemHeldEvent e) {
        if (drawing.contains(e.getPlayer().getName()))
            stopDrawing(e.getPlayer());
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (drawing.contains(e.getPlayer().getName()))
            stopDrawing((Player) e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (drawing.contains(e.getEntity().getName()))
            stopDrawing(e.getEntity());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (drawing.contains(e.getPlayer().getName()) && e.getItemDrop().getItemStack().equals(e.getPlayer().getInventory().getItemInMainHand()))
            stopDrawing(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (drawing.contains(e.getPlayer().getName()))
            stopDrawing(e.getPlayer());
    }

    private void stopDrawing(Player player){
        drawing.remove(player.getName());
        Bukkit.getPluginManager().callEvent(new BowPullEvent(player, false));
    }

}
