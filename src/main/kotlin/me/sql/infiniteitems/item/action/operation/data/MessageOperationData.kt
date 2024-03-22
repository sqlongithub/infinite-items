package me.sql.infiniteitems.item.action.operation.data

import me.sql.infiniteitems.gui.AnvilInputGUI
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class MessageOperationData(var getMessage: (Player) -> TextComponent) : OperationData {
    override val name = "Message"
    override val description = "This is the message that will get sent."
    override val material = Material.OAK_SIGN

    override fun showConfigurationGUI(player: Player, onReturn: Consumer<in OperationData>) {
        val gui = AnvilInputGUI("Configuring Message")
        gui.onFinished = Consumer<InventoryClickEvent> { click ->
            getMessage = { _ ->
                ChatColor.translateAlternateColorCodes('&', gui.renameText.content()).asTextComponent().withoutItalics()
            }
            onReturn.accept(this)
        }
        val firstItem = ItemStack(Material.PAPER)
        val firstItemMeta = firstItem.itemMeta!!
        firstItemMeta.displayName(getMessage.invoke(player))
        firstItem.itemMeta = firstItemMeta
        gui.firstSlot = firstItem

        val resultItem = firstItem.clone()
        gui.resultSlot = resultItem

        gui.show(player)
    }

    override fun getFormattedValue(player: Player?): String {
        if(player == null) {
            Bukkit.getLogger().warning("MessageOperationData#getFormattedValue called with player = null")
            return ""
        }
        return "§f" + getMessage.invoke(player).color(NamedTextColor.WHITE).content()
    }

    override fun toString(player: Player): String {
        return "message: ${getFormattedValue(player)}"
    }

}