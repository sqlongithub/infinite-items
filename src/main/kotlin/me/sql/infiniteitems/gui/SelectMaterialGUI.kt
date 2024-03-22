package me.sql.infiniteitems.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.getBackgroundPane
import me.sql.infiniteitems.util.toInt
import me.sql.infiniteitems.util.withoutItalics
import net.minecraft.world.item.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.math.ceil

class SelectMaterialGUI(val player: Player, private val onSelect: Consumer<Material>) {

    private val gui: ChestGui = ChestGui(6, "Select an item")
    private val rows = 6
    private val paginatedPane = PaginatedPane(0, 0, 9, rows - 1)

    init {
        gui.setOnTopClick { click ->
            click.isCancelled = true
        }
        gui.addPane(getBackgroundPane(rows))

        val itemsToDisplay: Deque<GuiItem> = Arrays.stream(Material.entries.toTypedArray())
            .filter { material ->
                !material.isEmpty && !material.isLegacy && CraftMagicNumbers.getItem(material) != null
                        && if(material.isBlock) {
                    // nms magic to see if block is disabled. added in 1.19.3, but not yet in paper api
                    // https://github.com/PaperMC/Paper/issues/8950 one day this can be pretty
                    CraftMagicNumbers.getBlock(material).isEnabled((player.world as CraftWorld).handle.enabledFeatures())
                } else { if(material.isItem) {
                    CraftMagicNumbers.getItem(material).isEnabled((player.world as CraftWorld).handle.enabledFeatures())
                } else { false }}}
            .sorted { o1, o2 ->  o1.isBlock.toInt() - o2.isBlock.toInt()}
            .map { getMaterialItem(it) }
            .collect(Collectors.toCollection { return@toCollection LinkedList() })

        val itemsPerPage = (9 * (rows - 1))
        val numPages: Int = ceil(itemsToDisplay.size.toDouble() / itemsPerPage.toDouble()).toInt()
        Bukkit.getLogger().info("items: ${itemsToDisplay.size}, per page: ${itemsPerPage}, numpages: ${numPages}")
        for(i in 0 until numPages) {
            Bukkit.getLogger().info("page $i")
            val pagePane = OutlinePane(0, 0, 9, rows - 1)
            for(j in 0 until itemsPerPage) {
                if(itemsToDisplay.isEmpty()) break
                val item = itemsToDisplay.removeFirst()
                pagePane.addItem(item)
            }
            paginatedPane.addPane(i, pagePane)
        }
        paginatedPane.priority = Pane.Priority.HIGH
        gui.addPane(paginatedPane)

        val pageNavigationPane = OutlinePane(0, rows - 1, 9, 1)
        pageNavigationPane.gap = 7
        pageNavigationPane.addItem(getPreviousPageItem())
        pageNavigationPane.addItem(getNextPageItem())
        gui.addPane(pageNavigationPane)
    }

    private fun getMaterialItem(material: Material): GuiItem {
        val item = ItemStack(material)
        return GuiItem(item) {
            onSelect.accept(material)
        }
    }

    private fun getPreviousPageItem(): GuiItem {
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta
        meta.displayName("§aPrevious Page".asTextComponent().withoutItalics())
        item.itemMeta = meta
        return GuiItem(item) {
            if(paginatedPane.page == 0) {
                paginatedPane.page = paginatedPane.pages - 1
            } else {
                paginatedPane.page -= 1
            }
            gui.update()
        }
    }

    private fun getNextPageItem(): GuiItem {
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta
        meta.displayName("§aNext Page".asTextComponent().withoutItalics())
        item.itemMeta = meta
        return GuiItem(item) {
            if(paginatedPane.page == paginatedPane.pages - 1) {
                paginatedPane.page = 0
            } else {
                paginatedPane.page += 1
            }
            gui.update()
        }
    }


    fun show() {
        gui.show(player)
    }

}