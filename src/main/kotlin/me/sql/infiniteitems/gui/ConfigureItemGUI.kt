package me.sql.infiniteitems.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.sql.infiniteitems.InfiniteItems
import me.sql.infiniteitems.item.CustomItem
import me.sql.infiniteitems.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer
import kotlin.time.measureTime

class ConfigureItemGUI(
    private val title: String,
    private val customItem: CustomItem,
    val player: Player,
) {

    private fun getChangeItemNameButton(): GuiItem {
        val itemStack = ItemStack(Material.OAK_SIGN)
        val itemMeta = itemStack.itemMeta!!

        itemMeta.displayName(customItem.name.withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add(Component.text("§7This is the name of your item")
            .withoutItalics())
        lore.add(Component.empty())
        lore.add(Component.text("§eClick to change!").withoutItalics())
        itemMeta.lore(lore)

        itemStack.itemMeta = itemMeta

        val guiItem = GuiItem(itemStack) { event ->
            val anvilGUI = AnvilInputGUI("Enter a new name")
            anvilGUI.onFinished = Consumer<InventoryClickEvent> { click ->
                click.clickedInventory?.close()
                customItem.name = anvilGUI.renameText
                show()
            }
            anvilGUI.firstSlot = getItemPreview().item
            anvilGUI.resultSlot = getItemPreview().item
            anvilGUI.show(player)
        }

        return guiItem
    }

    private fun getChangeItemMaterialButton(): GuiItem {
        val itemStack = ItemStack(customItem.material)
        val itemMeta = itemStack.itemMeta!!

        itemMeta.displayName(Component.text("§aMaterial").withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add(Component.text("§7Current: §a" + customItem.material.formattedName).withoutItalics())
        lore.add(Component.empty())
        lore.add(Component.text("§eClick to change!").withoutItalics())

        itemMeta.lore(lore)
        itemStack.itemMeta = itemMeta
        val guiItem = GuiItem(itemStack) { click ->
            SelectMaterialGUI(player) { material ->
                this.customItem.material = material
                show()
            }.show()
        }

        return guiItem
    }

    private fun getChangeItemMaxStackSizeButton(): GuiItem {
        val itemStack = ItemStack(Material.ENDER_PEARL)
        val itemMeta = itemStack.itemMeta!!

        itemMeta.displayName(Component.text("§aMax stack size").withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add(Component.text("§7Current: §a" + customItem.maxStackSize).withoutItalics())
        lore.add(Component.empty())
        lore.add(Component.text("§eClick to change!").withoutItalics())
        itemMeta.lore(lore)

        itemStack.itemMeta = itemMeta
        itemStack.amount = customItem.maxStackSize
        return GuiItem(itemStack) { event ->

        }
    }

    private fun getChangeItemActionsButton(): GuiItem {
        val itemStack = ItemStack(Material.COMPARATOR)
        val itemMeta = itemStack.itemMeta!!

        itemMeta.displayName("§aItem Actions".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7Configure what happens when a player")
        lore.add("§7interacts with this item.")
        lore.add("")
        lore.add("§eClick to configure!")

        itemMeta.lore(lore)
        itemStack.itemMeta = itemMeta

        return GuiItem(itemStack) { click ->
            ConfigureItemActionsGUI(customItem) { player ->
                ConfigureItemGUI(title, customItem, player).show()
            }.show(player)
        }
    }

    private fun getCancelButton(): GuiItem {
        val itemStack = ItemStack(Material.RED_CONCRETE)
        val itemMeta = itemStack.itemMeta!!

        itemMeta.displayName(Component.text("§cCancel").withoutItalics())

        itemStack.itemMeta = itemMeta
        return GuiItem(itemStack) { event ->
            event.clickedInventory!!.close()
        }
    }

    private fun getItemPreview(): GuiItem {
        val itemStack = customItem.getItemStack(player)
        val itemMeta = itemStack.itemMeta!!

        val previewLore = ArrayList<TextComponent>()
        previewLore.add(Component.empty())
        previewLore.add(Component.text("§8Preview").withoutItalics())
        if(itemMeta.lore() == null) {
            itemMeta.lore(previewLore)
        } else {
            itemMeta.lore()!!.addAll(previewLore)
        }

        itemStack.itemMeta = itemMeta
        return GuiItem(itemStack)
    }

    private fun getCreateButton(): GuiItem {
        val itemStack = ItemStack(Material.GREEN_CONCRETE)
        val itemMeta = itemStack.itemMeta

        itemMeta.displayName(Component.text("§aCreate").withoutItalics())

        itemStack.itemMeta = itemMeta
        return GuiItem(itemStack) {
            customItem.create()
            player.inventory.addItem(customItem.getItemStack(player))
            player.closeInventory()
            player.sendMessage("§aSuccessfully created custom item §r${customItem.alias}§a!")
        }
    }

    fun show() {
        val showGui = {
            val gui = ChestGui(5, title)
            gui.setOnTopClick { event ->
                event.isCancelled = true
            }

            gui.addPane(getBackgroundPane(5))

            val itemPropertiesPane = OutlinePane(1, 1, 2, 3)
            itemPropertiesPane.addItem(getChangeItemNameButton())
            itemPropertiesPane.addItem(getChangeItemMaterialButton())
            itemPropertiesPane.addItem(getChangeItemMaxStackSizeButton())
            gui.addPane(itemPropertiesPane)

            val itemActionsPane = StaticPane(4, 1, 1, 1)
            itemActionsPane.addItem(getChangeItemActionsButton(), 0, 0)
            gui.addPane(itemActionsPane)

            val finishActionsPane = StaticPane(3, 3, 3, 1)
            finishActionsPane.addItem(getCancelButton(), 0, 0)
            finishActionsPane.addItem(getItemPreview(), 1, 0)
            finishActionsPane.addItem(getCreateButton(), 2, 0)
            gui.addPane(finishActionsPane)
            gui.show(player)
        }
        if(InfiniteItems.debugging) {
            player.sendMessage(Component.text((measureTime(showGui).inWholeMicroseconds * 1000f).toString() + " ms"))
        } else {
            showGui()
        }
    }

}