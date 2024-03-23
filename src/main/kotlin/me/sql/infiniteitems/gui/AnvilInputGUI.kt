package me.sql.infiniteitems.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.sql.infiniteitems.InfiniteItems
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer

class AnvilInputGUI(
    title: String, onFinished: Consumer<InventoryClickEvent> = Consumer<InventoryClickEvent> { click ->
    click.isCancelled = true
}) {

    var onFinished: Consumer<InventoryClickEvent> = onFinished
        set(value) {
            field = value
            resultItemPane.setOnClick(value)
        }

    val renameText: TextComponent
        get() {
            return Component.text(anvilGui.renameText)
        }

    private var originalName: TextComponent? = Component.empty()
    private val firstItemPane = StaticPane(1, 1)
    private val secondItemPane = firstItemPane.copy()
    private val resultItemPane = firstItemPane.copy()
    private val firstItem: GuiItem = GuiItem(ItemStack(Material.AIR))
    private val secondItem: GuiItem = GuiItem(ItemStack(Material.ANVIL))
    private val resultItem: GuiItem = GuiItem(ItemStack(Material.AIR))
    private val anvilGui = AnvilGui(title)
    private var updateTask: BukkitTask? = null


    init {
        anvilGui.setOnTopClick { event ->
            event.isCancelled = true
        }
        resultItemPane.setOnClick(onFinished)
        anvilGui.setOnGlobalClick { click ->
            click.isCancelled = true
        }
        anvilGui.setOnNameInputChanged { name ->
            val item = firstItem.item
            val meta = item.itemMeta
            meta.displayName(Component.text(name).withoutItalics().color(NamedTextColor.WHITE))
            item.itemMeta = meta
            firstItem.item = item

            meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', name)).withoutItalics().color(NamedTextColor.WHITE))
            item.itemMeta = meta
            resultItem.item = item

            updateTask?.cancel()
            updateTask = object : BukkitRunnable() {
                override fun run() {
                    anvilGui.update()
                }
            }.runTaskLater(InfiniteItems.instance, 5L)
        }
        anvilGui.firstItemComponent.addPane(firstItemPane)
        anvilGui.secondItemComponent.addPane(secondItemPane)
        anvilGui.resultComponent.addPane(resultItemPane)
    }

    var firstSlot: ItemStack = ItemStack(Material.AIR)
        set(newFirstSlot) {
            originalName = newFirstSlot.itemMeta.displayName() as TextComponent?
            field = newFirstSlot
            firstItem.item = field
            firstItemPane.addItem(firstItem, 0, 0)
            anvilGui.update()
        }
    var secondSlot: ItemStack = ItemStack(Material.ANVIL)
        set(newFirstSlot) {
            field = newFirstSlot
            secondItem.item = field
            secondItemPane.addItem(secondItem, 0, 0)
            anvilGui.update()
        }
    var resultSlot: ItemStack = ItemStack(Material.AIR)
        set(newFirstSlot) {
            field = newFirstSlot
            resultItem.item = field
            resultItemPane.addItem(resultItem, 0, 0)
            anvilGui.update()
        }


    fun show(player: Player) {
        anvilGui.show(player)
    }

}