package me.sql.infiniteitems.gui

import com.destroystokyo.paper.profile.ProfileProperty
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.sql.infiniteitems.item.CustomItem
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.handler.ActionHandler
import me.sql.infiniteitems.item.action.operation.Operation
import me.sql.infiniteitems.item.action.operation.OperationType
import me.sql.infiniteitems.item.action.operation.data.OperationData
import me.sql.infiniteitems.item.action.operation.data.PlayersOperationData
import me.sql.infiniteitems.util.add
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.getBackgroundPane
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KClass

class ConfigureOperationGUI(val item: CustomItem, private val actionHandler: ActionHandler, private val onReturn: Consumer<Player>) {

    private val actionHandlerDraft = actionHandler

    private fun getChangeOperationTypeItem(player: Player): GuiItem {
        val item = ItemStack(Material.PISTON)
        val meta = item.itemMeta!!

        meta.displayName("§aOperation Type".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7Current: §a${actionHandler.operation.name}")
        lore.add("")
        lore.add("§7This is what the Operation does")
        lore.add("")
        lore.add("§eClick to change!")

        meta.lore(lore)
        item.itemMeta = meta

        return GuiItem(item) {
            SelectSelectableGUI("Selecting Operation Type", player, OperationType.entries, { type ->
                if ((type as OperationType).parentClass::class.java.interfaces.contains(Operation::class.java)) {
                    Bukkit.getLogger().severe("operation type parent class is not operation: " + type.name)
                    return@SelectSelectableGUI
                }
                this.actionHandlerDraft.operation =
                    (type.parentClass as KClass<Operation>).java.getDeclaredConstructor(ActionType::class.java).newInstance(actionHandler.type)
                ConfigureOperationGUI(this.item, this.actionHandlerDraft, onReturn).show(player)
            }, { player ->
                ConfigureOperationGUI(this.item, this.actionHandlerDraft, onReturn).show(player)
            }, "", this.actionHandlerDraft.operation.type).show()
        }
    }

    private fun getChangeOperationDataItem(data: OperationData, player: Player): GuiItem {
        val item = ItemStack(data.material)
        val meta = item.itemMeta!!

        meta.displayName("§a${data.name}".asTextComponent().withoutItalics())

        if(data is PlayersOperationData) {
            if(data.getPlayers(null).isNotEmpty())
                (meta as SkullMeta).owningPlayer = data.getPlayers(null)[0]
            else
                if(data.isAll)
                    (meta as SkullMeta).owningPlayer = player
                else {
                    val skinProfile = Bukkit.createProfile(UUID.fromString("25c9b55f-98b0-41d8-a035-85ef9e638832"), "skin45827d54")
                    skinProfile.setProperty(ProfileProperty("textures", "eyJ0aW1lc3RhbXAiOjE1NzEzMTYzMzY1MjgsInByb2ZpbGVJZCI6IjVkZTZlMTg0YWY4ZDQ5OGFiYmRlMDU1ZTUwNjUzMzE2IiwicHJvZmlsZU5hbWUiOiJBc3Nhc2luSmlhbmVyMjUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzlhNmUwYzE2ZGYwMTMzNDE4OGVhNjliNzVjN2M4Y2IxOGVmODZmMjZhMTVjYTk2YTJkYTI1MWVhZGQ5NDU1NTkifX19", "gE3WVJmkfEaPy/GF7Z0hStNwWV9NBz5v06iMkDRV94X8cQ4dFuPiKKbtiYjCbSaQWG26DuXbvxQWFk/hyBZ/Z3bxvMjiEIauMQj9jEZvvnW6u+rue7hWnHlc3fN/RiTs0ax4XjK4rEAnxIGBB9uqJ4r1EWxInMw80OujROR8YQmMQ9Kp7vBrCD7qbmIcekhxhi3Vj81Z+jplQhezzDsXl9VxewuOjERVVnsef0O0RXSSGHGRxY/euBubrWb7yZQ+JoRJqpqWrIqWtvQ2Bx7A/M4VcF0wLo8NRJdgu7W8r3nTds/g9WRyw03ah7REt/SGXpGWi0RiNn3hmYJMWwQ67Jw/7grsYIPBAPHm0ZGplSYFXtQa8pP6VKrwKCQTGRqTKiPdTzW/+EeVPfOOFe1zUJkPidBkAGKFR3TZMfG9gRai28bUrY9fHXrxfR31qgIVPLMCruJtkXRDk4FB1LHO2HycOvHWLsY1+sD3g2ATcakx8dbVXhMul9SWqUgl2GTbeMQ17G8RuelNOjvIZ6z37QZXF3PdCQ/AVOMhj96pWxfGtSFN+Mqm5Ei2HqHc5Cd1K7+fWnkwmYESioPI/TjXj/Y5dB2CIzeTNGikkWf7K3idBpTlBMqrogA6tyKjHe19Euuxv1w3kUenKBB0qg1uVc94XgOExMv/S5oBuliR0Ew="))
                    (meta as SkullMeta).ownerProfile = skinProfile
                }
        }

        val lore = ArrayList<TextComponent>()
        lore.add("§7Current: §a${data.getFormattedValue(player)}")
        lore.add("§7${data.description}")
        lore.add("")
        lore.add("§eClick to change!")
        meta.lore(lore)

        item.itemMeta = meta
        return GuiItem(item) {
            data.showConfigurationGUI(player) {
                ConfigureOperationGUI(this.item, actionHandlerDraft, onReturn).show(player)
            }
        }
    }

    private fun getAddOperationItem(): GuiItem {
        val item = ItemStack(Material.GREEN_CONCRETE)
        val meta = item.itemMeta!!

        meta.displayName("§aSave Operation".asTextComponent().withoutItalics())

        val lore = ArrayList<TextComponent>()
        lore.add("§7${actionHandlerDraft.operation.description}")
        lore.add("")
        lore.add("§eClick to save!")

        meta.lore(lore)

        item.itemMeta = meta
        return GuiItem(item) { click ->
            AddItemActionHandlerGUI(click.whoClicked as Player, this.item, this.actionHandlerDraft, onReturn).show()
        }
    }

    fun show(player: Player) {
        val gui = ChestGui(4, "Configuring Operation")
        gui.setOnTopClick { click ->
            click.isCancelled = true
        }

        val operationTypePane = StaticPane(1, 1, 1, 1)
        operationTypePane.addItem(getChangeOperationTypeItem(player), 0, 0)
        gui.addPane(operationTypePane)

        val operationDataPane = OutlinePane(3, 1, 4, 1)
        operationDataPane.gap = 1
        for(operationData in this.actionHandler.operation.data) {
            operationDataPane.addItem(getChangeOperationDataItem(operationData, player))
        }
        gui.addPane(operationDataPane)

        val addOperationPane = StaticPane(7, 1, 1, 1)
        addOperationPane.addItem(getAddOperationItem(), 0, 0)
        gui.addPane(addOperationPane)

        val navigationPane = StaticPane(4, 3, 1, 1)
        val item = ItemStack(Material.ARROW)
        val meta = item.itemMeta!!
        meta.displayName("§aBack".asTextComponent().withoutItalics())
        item.itemMeta = meta
        navigationPane.addItem(GuiItem(item) {
            AddItemActionHandlerGUI(player, this.item, this.actionHandler, onReturn).show()
        }, 0, 0)
        gui.addPane(navigationPane)

        gui.addPane(getBackgroundPane(4))
        gui.show(player)
    }

}