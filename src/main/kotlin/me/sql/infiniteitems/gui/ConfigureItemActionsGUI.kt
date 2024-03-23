package me.sql.infiniteitems.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.Orientable
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.sql.infiniteitems.item.CustomItem
import me.sql.infiniteitems.item.action.condition.NoneCondition
import me.sql.infiniteitems.item.action.handler.ActionHandler
import me.sql.infiniteitems.util.add
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.getBackgroundPane
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class ConfigureItemActionsGUI(private val item: CustomItem, private val onReturn: Consumer<Player>) {

    private fun getReturnItem(): GuiItem {
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!

        meta.displayName("§aBack".asTextComponent().withoutItalics())
        item.itemMeta = meta
        return GuiItem(item) { click ->
            onReturn.accept(click.whoClicked as Player)
        }
    }

    private fun getHandlerItem(handler: ActionHandler): GuiItem {
        val item = ItemStack(Material.COMPARATOR)
        val meta = item.itemMeta!!

        meta.displayName("§aOn §e${handler.type.name}".asTextComponent().withoutItalics())

        val operation = handler.operation.description.substring(0, 1).lowercase() + handler.operation.description.substring(1)
        val lore = ArrayList<TextComponent>()
        if(handler.condition !is NoneCondition) {
            lore.add("§7if")
            lore.add("§7  - §a${handler.condition.description}")
        }
        lore.add("§7then ")
        lore.add("§7  - §a${operation}")
        lore.add("")
        lore.add("§eClick to configure this handler!")
        meta.lore(lore)

        item.itemMeta = meta
        return GuiItem(item) { click ->
            // TODO: configure action
        }
    }

    private fun getAddHandlerItem(): GuiItem {
        val item = ItemStack(Material.PAPER)
        val meta = item.itemMeta!!

        meta.displayName("§aAdd a new Action Handler".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7If <this> do <that>")
        lore.add("")
        lore.add("§eClick to add an Action Handler!")

        meta.lore(lore)
        item.itemMeta = meta
        return GuiItem(item) { click ->
            // TODO: add handler
            AddItemActionHandlerGUI(click.whoClicked as Player, this.item, onReturn = onReturn).show()
        }
    }

    fun show(player: Player) {
        val gui = ChestGui(5, "Configuring Actions")
        gui.setOnTopClick { event ->
            event.isCancelled = true
        }

        gui.addPane(getBackgroundPane(5))

        val handlersPane = OutlinePane(1, 1, 7, 3)
        handlersPane.orientation = Orientable.Orientation.HORIZONTAL
        handlersPane.priority = Pane.Priority.HIGHEST

        val navigationPane = StaticPane(4, 4, 1, 1)
        navigationPane.addItem(getReturnItem(), 0, 0)
        gui.addPane(navigationPane)

        // TODO: show action handlers
        for(handler in item.actionHandlers) {
            handlersPane.addItem(getHandlerItem(handler))
        }
        handlersPane.addItem(getAddHandlerItem())

        gui.addPane(handlersPane)

        gui.show(player)

    }

}