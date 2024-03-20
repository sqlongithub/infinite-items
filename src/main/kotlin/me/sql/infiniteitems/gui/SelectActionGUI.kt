package me.sql.infiniteitems.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.sql.infiniteitems.item.CustomItem
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.getBackgroundPane
import me.sql.infiniteitems.util.stringToMultiLineLore
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class SelectActionGUI(private val item: CustomItem, private val currentlySelected: ActionType, private val onSelect: Consumer<ActionType>) {

    fun getReturnItem(): GuiItem {
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!

        meta.displayName("§aBack".asTextComponent().withoutItalics())
        item.itemMeta = meta
        return GuiItem(item) { click ->
            AddItemActionHandlerGUI(click.whoClicked as Player, this.item).show()
        }
    }

    fun show(player: Player) {
        val gui = ChestGui(5, "Selecting Action")
        gui.setOnTopClick { click ->
            click.isCancelled = true
        }

        gui.addPane(getBackgroundPane(5))

        val navigationPane = StaticPane(4, 4, 1, 1)
        navigationPane.addItem(getReturnItem(), 0, 0)
        gui.addPane(navigationPane)

        val actionsPane = OutlinePane(1, 1, 7, 3)
        for(type in ActionType.entries) {
            val item = ItemStack(type.material)
            val meta = item.itemMeta!!
            meta.displayName(type.name.asTextComponent().withoutItalics().color(NamedTextColor.GREEN))

            meta.lore(stringToMultiLineLore(type.whenever, 25, "Execute when "))
            if(type == currentlySelected) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
            item.itemMeta = meta

            actionsPane.addItem(GuiItem(item) { click ->
                onSelect.accept(type)
            })
            player.sendMessage("action")
        }
        gui.addPane(actionsPane)

        gui.show(player)
    }

}