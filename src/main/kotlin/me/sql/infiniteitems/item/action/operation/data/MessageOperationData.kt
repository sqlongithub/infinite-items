package me.sql.infiniteitems.item.action.operation.data

import me.sql.infiniteitems.gui.AnvilInputGUI
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.withoutItalics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class MessageOperationData(var message: String) : OperationData {
    override val name = "Message"
    override val description = "This is the message that will get sent."
    override val material = Material.OAK_SIGN

    companion object {
        fun getMessage(config: Map<String, *>): String {
            if(!config.containsKey("message")) {
                return ""
            }
            val message = config["message"]
            if(message == null || message !is String) {
                return ""
            }
            return message
        }
    }

    constructor(config: Map<String, *>) : this(getMessage(config))

    override fun showConfigurationGUI(player: Player, onReturn: Consumer<in OperationData>) {
        val gui = AnvilInputGUI("Configuring Message")
        gui.onFinished = Consumer<InventoryClickEvent> { click ->
            message = ChatColor.translateAlternateColorCodes('&', gui.renameText.content())
            onReturn.accept(this)
        }
        val firstItem = ItemStack(Material.PAPER)
        val firstItemMeta = firstItem.itemMeta!!
        firstItemMeta.displayName(message.asTextComponent().withoutItalics())
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
        return "§f" + ChatColor.translateAlternateColorCodes('&', message.replace("%player%", player.name))
    }

    override fun toString(player: Player): String {
        return "message: ${getFormattedValue(player)}"
    }

    override fun toMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map["message"] = this.message
        return map
    }

}