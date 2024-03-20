package me.sql.infiniteitems.item.action.operation.data

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PlayersOperationData(private var isAll: Boolean, private var players: List<Player> = ArrayList()) : OperationData {

    override fun toString(player: Player): String {
        if(isAll) {
            return "to: §aeveryone"
        } else {
            if(players.isEmpty()) {
                return "to: §ano one"
            }
            return "to: §a${players[0].name} and ${players.size - 1} others"
        }
    }

    fun getPlayers(): List<Player> = when(isAll) {
        true -> Bukkit.getOnlinePlayers().toList()
        else -> players
    }

}