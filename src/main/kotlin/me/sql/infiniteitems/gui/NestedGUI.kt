package me.sql.infiniteitems.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.withoutItalics
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

interface NestedGUI {

    val gui: ChestGui
    val player: Player
    val onReturn: Consumer<Player>

    fun addReturnItem() {
        val navigationPane = StaticPane(4, gui.rows - 1, 1, 1)

        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!
        meta.displayName("§aReturn".asTextComponent().withoutItalics())
        item.itemMeta = meta

        navigationPane.addItem(GuiItem(item) {
            onReturn.accept(player)
        }, 1, 1)

        gui.addPane(navigationPane)
    }

}