package me.sql.infiniteitems.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import me.sql.infiniteitems.Selectable
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.getBackgroundPane
import me.sql.infiniteitems.util.stringToMultiLineLore
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.TextComponent
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class SelectSelectableGUI(
    private val title: String, override val player: Player, private val values: List<Selectable>,
    private val onSelect: Consumer<in Selectable>, override val onReturn: Consumer<Player>,
    private val prefix: String, private val selected: Selectable?) : NestedGUI {

    override val gui: ChestGui = ChestGui(5, title)
    init {
        gui.setOnTopClick { click ->
            click.isCancelled = true
        }
        gui.addPane(getBackgroundPane(5))

        val valuesPane = OutlinePane(1, 1, 7, 3)
        for(value in values) {
            val item = ItemStack(value.material)
            val meta = item.itemMeta!!

            meta.displayName("§a${value.name}".asTextComponent().withoutItalics())

            val lore = stringToMultiLineLore(value.description, 30, prefix)
            meta.lore(lore)

            if(value == selected) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }

            item.itemMeta = meta
            valuesPane.addItem(GuiItem(item) {
                onSelect.accept(value)
            })
        }
        gui.addPane(valuesPane)

        addReturnItem()
    }

    fun show() {
        gui.show(player)
    }

}