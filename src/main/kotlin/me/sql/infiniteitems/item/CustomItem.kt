package me.sql.infiniteitems.item

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.handler.ActionHandler
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class CustomItem(
    var identifier: String = "custom_item-" + (CustomItemRegistry.size + 1),
    actualName: TextComponent = Component.text("&aCustom Item"),
    var material: Material = Material.STICK,
    var maxStackSize: Int = material.maxStackSize,
    actionHandlers: ArrayList<ActionHandler<Action>> = ArrayList()
) {

    init {
        identifier = identifier.lowercase().replace(" ", "_")
    }

    private var actualName: TextComponent = actualName.withoutItalics().color(NamedTextColor.WHITE)
    var name: TextComponent
        get() = ChatColor.translateAlternateColorCodes('&', actualName.content()).asTextComponent().withoutItalics().color(NamedTextColor.WHITE)
        set(value) {
            actualName = value.withoutItalics().color(NamedTextColor.WHITE)
        }

    var actionHandlers: ArrayList<ActionHandler<Action>> = actionHandlers
        private set

    fun getItemStack(player: Player): ItemStack {
        val itemStack = ItemStack(material)
        val meta = itemStack.itemMeta!!
        meta.displayName(name)

        meta.persistentDataContainer.set(CustomItemRegistry.IDENTIFIER_KEY, PersistentDataType.STRING, identifier)
        itemStack.itemMeta = meta



        return itemStack
    }

    fun create() {
        CustomItemRegistry.register(this)
    }

    fun handleAction(action: Action) {
        for(handler in actionHandlers) {
            if(handler.type == action.type) {
                handler.handle(action)
            }
        }
    }

}