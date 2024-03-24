package me.sql.infiniteitems.item.action.operation.data

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.type.HitPlayerAction
import me.sql.infiniteitems.util.add
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class HitPlayersOperationData(isAll: Boolean,
                              isUser: Boolean,
                              var isHit: Boolean,
                              players: ArrayList<OfflinePlayer> = ArrayList()
) : PlayersOperationData(isAll, isUser, players) {

    private var isHitPlayerItem = ItemStack(if(isHit) { Material.LIME_DYE } else { Material.GRAY_DYE })
    private var isHitPlayerMeta = isHitPlayerItem.itemMeta!!
    private var isHitPlayerLore = ArrayList<TextComponent>()

    companion object {
        fun getIsHit(config: Map<String, *>): Boolean {
            if(!config.containsKey("players")) {
                return false
            }
            val playersMap = config["players"]
            if(playersMap !is Map<*, *>) {
                return false
            }

            val isHitPlayer = playersMap["hit_player"] ?: false
            if(isHitPlayer !is Boolean) {
                return false
            }
            return isHitPlayer
        }
    }

    constructor(config: Map<String, *>) : this(getIsAll(config), getIsUser(config), getIsHit(config), getPlayers(config))

    override fun updateGui() {
        super.updateGui()

        if(isAll) isHit = false

        isHitPlayerItem.type = if(isHit) Material.LIME_DYE else Material.GRAY_DYE
        isHitPlayerLore[0] = "§7Current: ${if(isHit) "§aYes" else "§cNo"}".asTextComponent()
        isHitPlayerMeta.lore(isHitPlayerLore)
        isHitPlayerItem.itemMeta = isHitPlayerMeta
    }

    override fun showConfigurationGUI(player: Player, onReturn: Consumer<in OperationData>) {
        super.showConfigurationGUI(player, onReturn)
        togglesPane.gap = 0

        isHitPlayerItem = ItemStack(if(isHit) Material.LIME_DYE else Material.GRAY_DYE)
        isHitPlayerMeta = isHitPlayerItem.itemMeta!!
        isHitPlayerLore = ArrayList()

        isHitPlayerMeta.displayName("§aHit Player".asTextComponent().withoutItalics())

        isHitPlayerLore.add("§7Current: ${if(isHit) "§aYes" else "§cNo"}")
        isHitPlayerLore.add("")
        isHitPlayerLore.add("§eClick to toggle!")
        isHitPlayerMeta.lore(isHitPlayerLore)

        isHitPlayerItem.itemMeta = isHitPlayerMeta

        togglesPane.insertItem(GuiItem(isHitPlayerItem) { click ->
            isHit = !isHit
            isAll = false
            updateGui()
            if(isHit) {
                playersPane.isVisible = false
            } else {
                playersPane.isVisible = true
            }
            gui.update()
        }, 1)

        gui.update()
    }

    override fun getFormattedValue(player: Player?): String {
        return if(isHit) {
            if(isUser) "the attacking player and the hit player"
            else "the hit player"
        } else {
            super.getFormattedValue(player)
        }
    }

    override fun getOnlinePlayers(action: Action): List<Player> = when(isUser) {
        true -> listOf((action as HitPlayerAction).hitPlayer)
        else -> super.getOnlinePlayers(action)
    }

    override fun toMap(): LinkedHashMap<String, Any> {
        val map = super.toMap()
        val newMap = map.clone() as LinkedHashMap<String, Any>
        newMap.clear()
        newMap["hit_player"] = isHit
        newMap.putAll(map)
        return newMap
    }

}