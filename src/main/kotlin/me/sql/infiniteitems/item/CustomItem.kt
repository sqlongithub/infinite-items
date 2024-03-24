package me.sql.infiniteitems.item

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.handler.ActionHandler
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.nbt.CompoundTag
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CustomItem(
    var alias: String = "custom_item-" + (CustomItemRegistry.size + 1),
    actualName: TextComponent = Component.text("&aCustom Item"),
    var material: Material = Material.STICK,
    var maxStackSize: Int = material.maxStackSize,
    actionHandlers: ArrayList<ActionHandler> = ArrayList()
) {

    init {
        alias = alias.lowercase().replace(" ", "_")
    }

    private var actualName: TextComponent = actualName.withoutItalics().color(NamedTextColor.WHITE)
    var name: TextComponent
        get() = ChatColor.translateAlternateColorCodes('&', actualName.content()).asTextComponent().withoutItalics().color(NamedTextColor.WHITE)
        set(value) {
            actualName = value.withoutItalics().color(NamedTextColor.WHITE)
        }

    var actionHandlers: ArrayList<ActionHandler> = actionHandlers
        private set

    fun getItemStack(player: Player): ItemStack {
        var itemStack = ItemStack(material)

        val nmsItem = CraftItemStack.asNMSCopy(itemStack)
        val tag = CompoundTag()
        tag.putString(CustomItemRegistry.ALIAS_TAG, alias)
        nmsItem.tag = tag

        itemStack = nmsItem.asBukkitMirror()
        val meta = itemStack.itemMeta!!
        meta.displayName(name)
        itemStack.itemMeta = meta

        return itemStack
    }

    fun create() {
        CustomItemRegistry.register(this)
        CustomItemRegistry.saveToConfigAsync()
    }

    fun handleAction(action: Action) {
        for(handler in actionHandlers) {
            if(handler.type == action.type) {
                handler.handle(action)
            }
        }
    }

}