package me.sql.infiniteitems.event

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import me.sql.infiniteitems.InfiniteItems
import me.sql.infiniteitems.item.CustomItemRegistry
import me.sql.infiniteitems.item.action.type.*
import org.bukkit.Bukkit
import org.bukkit.entity.Animals
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ActionListener : Listener {

    private fun handleRightClick(item: ItemStack?, player: Player) {
        val customItem = CustomItemRegistry.get(item) ?: return
        val action = RightClickAction(player)
        customItem.handleAction(action)
    }

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {
        handleRightClick(event.item, event.player)
    }

    @EventHandler
    fun onRightClickAtEntity(event: PlayerInteractEntityEvent) {
        handleRightClick(event.player.inventory.getItem(event.hand), event.player)
    }

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        val customItem = CustomItemRegistry.get(event.itemDrop.itemStack) ?: return
        val action = ItemDropAction(event.player)
        customItem.handleAction(action)
    }

    @EventHandler
    fun onPlayerHit(event: EntityDamageByEntityEvent) {
        if(event.damager !is Player) return
        val customItem = CustomItemRegistry.get((event.damager as Player).inventory.itemInMainHand)
        if(customItem == null) {
            if(InfiniteItems.DEBUGGING) {
                Bukkit.getLogger().info("invalid custom item")
            }
            return
        }
        val hitEntityAction = HitEntityAction(event.damager as Player, event.entity)
        customItem.handleAction(hitEntityAction)

        val specificAction = when(event.entity) {
            is Monster -> HitMobAction(event.damager as Player, event.entity as Monster)
            is Animals -> HitAnimalAction(event.damager as Player, event.entity as Animals)
            is Player -> HitPlayerAction(event.damager as Player, event.entity as Player)
            else -> return
        }
        if(InfiniteItems.DEBUGGING) {
            Bukkit.getLogger().info("player hit entity ")
            Bukkit.getLogger().info("is monster: " + (event.entity is Monster))
            Bukkit.getLogger().info("is animal: " + (event.entity is Animals))
            Bukkit.getLogger().info("is player: " + (event.entity is Player))
        }
        customItem.handleAction(specificAction)
    }

    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        val customItem = CustomItemRegistry.get(event.player.inventory.itemInMainHand) ?: return
        val action = JumpAction(event.player)
        customItem.handleAction(action)
    }

}