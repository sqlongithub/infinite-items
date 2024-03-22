package me.sql.infiniteitems.event

import me.sql.infiniteitems.item.CustomItemRegistry
import me.sql.infiniteitems.item.action.ItemDropAction
import me.sql.infiniteitems.item.action.RightClickAction
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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

}