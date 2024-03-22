package me.sql.infiniteitems.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.sql.infiniteitems.item.CustomItem
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.condition.NoneCondition
import me.sql.infiniteitems.item.action.handler.ActionHandler
import me.sql.infiniteitems.item.action.operation.NoneOperation
import me.sql.infiniteitems.util.add
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.getBackgroundPane
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class AddItemActionHandlerGUI(private val player: Player,
                              private val item: CustomItem,
                              var actionHandler: ActionHandler = ActionHandler(ActionType.RIGHT_CLICK,
                                  NoneOperation(),
                                  NoneCondition()
                              )) {


    private fun getReturnItem(): GuiItem {
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!

        meta.displayName("§aBack".asTextComponent().withoutItalics())
        item.itemMeta = meta
        return GuiItem(item) { click ->
            ConfigureItemActionsGUI(this.item).show(click.whoClicked as Player)
        }
    }

    private fun getSelectActionItem(): GuiItem {
        val item = ItemStack(actionHandler.type.material)
        val meta = item.itemMeta!!
        meta.displayName("§aAction Type".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7Current: §a" + actionHandler.type.name)
        lore.add("")
        lore.add("§7This is the action that will")
        lore.add("§7cause this handler to be executed")
        lore.add("")
        lore.add("§eClick to change!")
        meta.lore(lore)

        item.itemMeta = meta

        return GuiItem(item) { click ->
            SelectSelectableGUI("Selecting Action Type", player, ActionType.entries, { type ->
                this.actionHandler.type = type as ActionType
                this.show()
            }, { player ->
                AddItemActionHandlerGUI(player, this.item).show()
            }, "Execute when ", this.actionHandler.type).show()
        }
    }

    private fun getActionInfoItem(): GuiItem {
        val item = ItemStack(Material.WRITABLE_BOOK)
        val meta = item.itemMeta!!
        meta.displayName("§9Info".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7When a player does something while")
        lore.add("§7holding this item, that is called an §aAction§7.")
        lore.add("")
        lore.add("§7By configuring what §aAction§7 activates this")
        lore.add("§7handler, the configured §aOperation§7 will only")
        lore.add("§7be executed when that §aAction§7 happens.")
        lore.add("")
        lore.add("§6Example: §aRIGHT_CLICK")
        lore.add("§8The handler will only execute when the player")
        lore.add("§8right-clicks with the item in hand.")

        meta.lore(lore)
        item.itemMeta = meta

        return GuiItem(item)
    }

    private fun getChangeConditionItem(): GuiItem {
        val item = ItemStack(Material.COMPARATOR)
        val meta = item.itemMeta!!

        meta.displayName("§aCondition".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7Current: §a${actionHandler.condition.description}")
        if(actionHandler.condition is NoneCondition) {
            lore.add("§8This handler will always be executed.")
        }
        lore.add("")
        lore.add("§7This is the condition that has to be met")
        lore.add("§7for the handler to be executed")
        lore.add("")
        if(actionHandler.condition is NoneCondition) {
            lore.add("§eClick to add a Condition!")
        } else {
            lore.add("§eClick to change!")
        }

        meta.lore(lore)
        item.itemMeta = meta
        return GuiItem(item) {

        }
    }

    private fun getChangeConditionInfoItem(): GuiItem {
        val item = ItemStack(Material.WRITABLE_BOOK)
        val meta = item.itemMeta!!

        meta.displayName("§9Info".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7When the selected Action happens, the selected")
        lore.add("§7condition has to be met for this handler")
        lore.add("§7to be executed.")
        lore.add("")
        lore.add("§6Example: §7Player's §aHealth§7 is greater than §a10")
        lore.add("§8In this case the handler will only be executed if")
        lore.add("§8the player holding the item has more than 10 health")
        lore.add("§8(5 hearts)")

        meta.lore(lore)

        item.itemMeta = meta
        return GuiItem(item) {

        }
    }

    private fun getChangeOperationItem(): GuiItem {
        val item = ItemStack(Material.PISTON)
        val meta = item.itemMeta!!

        meta.displayName("§aOperation".asTextComponent().withoutItalics())

        val lore  = ArrayList<TextComponent>()
        lore.add("§7Current: §a${actionHandler.operation.description}")
        for(operationData in actionHandler.operation.data) {
            lore.add("  §8 - ${operationData.toString(player)}")
        }
        lore.add("")
        lore.add("§7This is what will happen when the selected §aAction§7")
        lore.add("§7happens, and the selected §aCondition§7 is met.")
        lore.add("")
        lore.add("§eClick to change!")

        meta.lore(lore)

        item.itemMeta = meta
        return GuiItem(item) {
            ConfigureOperationGUI(this.item, this.actionHandler).show(player)
        }
    }

    private fun getChangeOperationInfoItem(): GuiItem {
        val item = ItemStack(Material.WRITABLE_BOOK)
        val meta = item.itemMeta!!

        meta.displayName("§9Info".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7An §aOperation§7 is what happens when the selected §aAction")
        lore.add("§7is fired, and the selected §aCondition§7 is met. An §aOperation§7")
        lore.add("§7can be configured to do anything from run a command")
        lore.add("§7to launch a player to space, or spawn an Ender Dragon.")
        lore.add("")
        lore.add("§6Example: §aSend a message to imSQL with the text [...]")

        meta.lore(lore)

        item.itemMeta = meta
        return GuiItem(item) {

        }
    }

    private fun getHandlerPreviewItem(): GuiItem {
        val item = ItemStack(Material.PAPER)
        val meta = item.itemMeta!!

        meta.displayName("§aHandler Preview".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7When")
        lore.add("  §7 - §a${actionHandler.type.description}")
        if(actionHandler.condition !is NoneCondition) {
            lore.add("§7and")
            lore.add("  §7 - §a${actionHandler.condition.description}")
        }
        lore.add("§7then")
        lore.add("  §7 - §a${actionHandler.operation.description.replaceFirstChar { it.lowercase(Locale.getDefault()) }}")

        meta.lore(lore)

        item.itemMeta = meta
        return GuiItem(item) {

        }
    }

    private fun getCreateHandlerItem(): GuiItem {
        val item = ItemStack(Material.RED_CONCRETE)
        val meta = item.itemMeta!!

        val lore = ArrayList<TextComponent>()
        if(actionHandler.operation !is NoneOperation) {
            item.type = Material.GREEN_CONCRETE
            meta.displayName("§aAdd Handler".asTextComponent().withoutItalics())
            lore.add("§eClick to add this handler to the item!")
        } else {
            meta.displayName("§cNo Operation".asTextComponent().withoutItalics())
            lore.add("§7You must configure an §aOperation§7 first.")
        }
        meta.lore(lore)

        item.itemMeta = meta
        return GuiItem(item) {
            if(actionHandler.operation !is NoneOperation) {
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 0.7f, 1.5f)
                this.item.actionHandlers.add(actionHandler)
                ConfigureItemActionsGUI(this.item).show(player)
            } else {
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 0.7f, 0.8f)
            }
        }
    }

    fun show() {
        val gui = ChestGui(5, "Adding Action Handler")
        gui.setOnTopClick { event ->
            event.isCancelled = true
        }

        gui.addPane(getBackgroundPane(5))

        val handlerConfigurationPane = OutlinePane(1, 1, 7, 1)
        handlerConfigurationPane.gap = 1
        handlerConfigurationPane.addItem(getSelectActionItem())
        handlerConfigurationPane.addItem(getChangeConditionItem())
        handlerConfigurationPane.addItem(getChangeOperationItem())
        handlerConfigurationPane.addItem(getHandlerPreviewItem())
        gui.addPane(handlerConfigurationPane)

        val handlerConfigurationInfoPane = OutlinePane(1, 2, 7, 1)
        handlerConfigurationInfoPane.gap = 1
        handlerConfigurationInfoPane.addItem(getActionInfoItem())
        handlerConfigurationInfoPane.addItem(getChangeConditionInfoItem())
        handlerConfigurationInfoPane.addItem(getChangeOperationInfoItem())
        handlerConfigurationInfoPane.addItem(getCreateHandlerItem())
        gui.addPane(handlerConfigurationInfoPane)

        val navigationPane = StaticPane(4, 4, 1, 1)
        navigationPane.addItem(getReturnItem(), 0, 0)
        gui.addPane(navigationPane)

        gui.show(player)
    }


}