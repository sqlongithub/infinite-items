package me.sql.infiniteitems.item.action.operation.data

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import me.sql.infiniteitems.InfiniteItems
import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.util.add
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.getBackgroundPane
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.w3c.dom.Text
import java.util.function.Consumer

open class PlayersOperationData(
    var isAll: Boolean, var isUser: Boolean,
    private var players: ArrayList<OfflinePlayer> = ArrayList()
) : OperationData {

    override val name = "Players"
    override val description = "These are the players the operation applies to."
    override val material = Material.PLAYER_HEAD

    protected var togglesPane = OutlinePane(3, 0, 3, 1)
    protected var gui = ChestGui(6, "Selecting Players")

    private var isAllItem = ItemStack(if(isAll) { Material.LIME_DYE } else { Material.GRAY_DYE })
    private var isAllMeta = isAllItem.itemMeta!!
    private var isAllLore = ArrayList<TextComponent>()

    private var thePlayerItem = ItemStack(if(isUser) { Material.LIME_DYE } else { Material.GRAY_DYE })
    private var thePlayerMeta = thePlayerItem.itemMeta!!
    private var thePlayerLore = ArrayList<TextComponent>()

    protected var playersPane = OutlinePane(1, 1, 7, 4)

    init {
        if(isAll) isUser = false
    }

    protected open fun updateGui() {
        isAllItem.type = if(isAll) { Material.LIME_DYE } else { Material.GRAY_DYE }
        // should this be currently instead of current?
        isAllLore[0] = ("§7Current: " + if(isAll) { "§aYes" } else { "§cNo" }).asTextComponent()
        isAllMeta.lore(isAllLore)
        isAllItem.itemMeta = isAllMeta

        thePlayerItem.type = if(isUser) { Material.LIME_DYE } else { Material.GRAY_DYE }
        thePlayerLore[0] = ("§7Current: " + if(isUser) { "§aYes" } else { "§cNo" }).asTextComponent()
        thePlayerMeta.lore(thePlayerLore)
        thePlayerItem.itemMeta = thePlayerMeta

    }

    // TODO: this code needs cleaning
    override fun showConfigurationGUI(player: Player, onReturn: Consumer<in OperationData>) {
        gui = ChestGui(6, "Selecting Players")
        togglesPane = OutlinePane(3, 0, 3, 1)
        // this is all so bad ugghhghghgh
        isAllItem = ItemStack(if(isAll) { Material.LIME_DYE } else { Material.GRAY_DYE })
        isAllMeta = isAllItem.itemMeta!!
        isAllLore = ArrayList<TextComponent>()

        thePlayerItem = ItemStack(if(isUser) { Material.LIME_DYE } else { Material.GRAY_DYE })
        thePlayerMeta = thePlayerItem.itemMeta!!
        thePlayerLore = ArrayList<TextComponent>()

        gui.addPane(getBackgroundPane(6))
        gui.setOnTopClick { click ->
            click.isCancelled = true
        }

        togglesPane.gap = 1

        isAllMeta.displayName("§aEveryone".asTextComponent().withoutItalics())
        isAllLore.add("§7Current: " + if(isAll) { "§aEveryone" } else { "§cNo" })
        isAllLore.add("")
        isAllLore.add("§eClick to toggle!")
        isAllMeta.lore(isAllLore)
        isAllItem.itemMeta = isAllMeta

        playersPane = OutlinePane(1, 1, 7, 4)
        playersPane.priority = Pane.Priority.HIGH
        if(isAll || isUser) playersPane.isVisible = false
        gui.addPane(playersPane)
        // TODO: minimize code duplication or dont if u are theprimeagen idk there is a yellow line in my ide that's annoying
        togglesPane.addItem(GuiItem(isAllItem) {
            isAll = !isAll
            isUser = false
            updateGui()
            if(isAll || isUser) {
                playersPane.isVisible = false
            } else {
                playersPane.isVisible = true
            }

            gui.update()
        })

        thePlayerMeta.displayName("§aThe player".asTextComponent().withoutItalics())
        thePlayerLore.add("§7Current: " + if(isUser) { "§aYes" } else { "§cNo" })
        thePlayerLore.add("§7Apply to the player who is holding the item")
        thePlayerLore.add("")
        thePlayerLore.add("§eClick to toggle!")
        thePlayerMeta.lore(thePlayerLore)
        thePlayerItem.itemMeta = thePlayerMeta

        togglesPane.addItem(GuiItem(thePlayerItem) {
            isUser = !isUser
            isAll = false
            updateGui()

            playersPane.isVisible = !(isAll || isUser)
            gui.update()
        })

        gui.addPane(togglesPane)

        for(listPlayer in players) {
            val playerItem = ItemStack(Material.PLAYER_HEAD)
            val meta = playerItem.itemMeta!! as SkullMeta
            meta.owningPlayer = listPlayer
            playerItem.itemMeta = meta

            meta.displayName("§a${listPlayer.name}".asTextComponent().withoutItalics().color(NamedTextColor.WHITE))
            val skullLore = ArrayList<TextComponent>()
            skullLore.add("")
            skullLore.add("§eClick to remove this player from the list!")
            meta.lore(skullLore)

            playerItem.itemMeta = meta
            val guiItem = GuiItem(playerItem)
            guiItem.setAction {
                players.remove(listPlayer)
                playersPane.removeItem(guiItem)
                gui.update()
            }
            playersPane.addItem(guiItem)
        }

        val addPlayerItem = ItemStack(Material.PAPER)
        val addPlayerMeta = addPlayerItem.itemMeta!!
        addPlayerMeta.displayName("§aAdd Player".asTextComponent().withoutItalics())

        val addPlayerLore = ArrayList<TextComponent>()
        addPlayerLore.add("")
        addPlayerLore.add("§eClick to add a player!")
        addPlayerMeta.lore(addPlayerLore)
        addPlayerItem.itemMeta = addPlayerMeta
        playersPane.addItem(GuiItem(addPlayerItem) { click ->
            val anvilGui = AnvilGui("Enter player name")
            val playerPane = StaticPane(0, 0, 1, 1)

            val playerSkull = ItemStack(Material.PLAYER_HEAD)
            val skullMeta = playerSkull.itemMeta!! as SkullMeta
            skullMeta.owningPlayer = Bukkit.getOfflinePlayer("Notch")
            skullMeta.displayName("Notch".asTextComponent().withoutItalics().color(NamedTextColor.WHITE))
            playerSkull.itemMeta = skullMeta
            playerPane.addItem(GuiItem(playerSkull) {
                if(anvilGui.renameText.isNotEmpty()) {
                    players.add(Bukkit.getOfflinePlayer(anvilGui.renameText))
                }
                showConfigurationGUI(player, onReturn)
            }, 0, 0)

            anvilGui.firstItemComponent.addPane(playerPane)
            anvilGui.resultComponent.addPane(playerPane)


            var newSkullOwner: String = "Notch"
            var updateTask: BukkitTask? = null
            anvilGui.setOnNameInputChanged { name ->
                newSkullOwner = name
                skullMeta.displayName(name.asTextComponent().withoutItalics().color(NamedTextColor.WHITE))
                playerSkull.itemMeta = skullMeta
                updateTask?.cancel()
                updateTask = object : BukkitRunnable() {
                    override fun run() {
                        if(newSkullOwner.isEmpty()) return
                        skullMeta.owningPlayer = Bukkit.getOfflinePlayer(newSkullOwner)
                        playerSkull.itemMeta = skullMeta
                        Bukkit.getScheduler().runTask(InfiniteItems.instance) { _ ->
                            anvilGui.update()
                        }
                    }
                }.runTaskLaterAsynchronously(InfiniteItems.instance, 13L)
            }

            anvilGui.show(click.whoClicked)
        })

        val navigationPane = StaticPane(4, gui.rows - 1, 1, 1)
        val returnItem = ItemStack(Material.ARROW)
        val returnMeta = returnItem.itemMeta
        returnMeta.displayName("§aBack".asTextComponent().withoutItalics())
        returnItem.itemMeta = returnMeta
        navigationPane.addItem(GuiItem(returnItem) { click ->
            onReturn.accept(this)
        }, 0, 0)
        gui.addPane(navigationPane)

        gui.show(player)
    }

    override fun getFormattedValue(player: Player?): String {
        if(isAll) {
            return "everyone"
        } else if(isUser) {
            return "the player"
        } else {
            if(players.isEmpty()) {
                return "no one"
            }
            return "${players[0].name}" + if(players.size > 1) { " and ${players.size - 1} others" } else { "" }
        }
    }

    override fun toString(player: Player): String {
        return ("to: §a${getFormattedValue(player)}")
    }

    open fun getOnlinePlayers(action: Action): List<Player> = when(isAll) {
        true -> Bukkit.getOnlinePlayers().toList()
        else -> when(isUser) {
            true -> listOf(action.player)
            else -> players.filterIsInstance<Player>()
        }
    }

    fun getPlayers(action: Action?): List<OfflinePlayer> {
        val allPlayers = ArrayList(players)
        if(action != null)
            allPlayers.addAll(getOnlinePlayers(action))
        return allPlayers.toList()
    }

}