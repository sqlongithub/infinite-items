package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.InfiniteItems
import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.operation.data.HitPlayersOperationData
import me.sql.infiniteitems.item.action.operation.data.OperationData
import me.sql.infiniteitems.item.action.operation.data.PlayersOperationData
import org.bukkit.Bukkit

class BanPlayerOperation(val players: PlayersOperationData) : Operation {
    override val type = OperationType.BAN_PLAYER
    override val name = "Ban Player"
    override val description
        get() = "Permanently ban §a${players.getFormattedValue(null)}"
    override val data: List<OperationData>
        get() = listOf(players)

    constructor(actionType: ActionType) : this(
        if(actionType == ActionType.HIT_PLAYER)
            HitPlayersOperationData(false, false, true)
        else
            PlayersOperationData(isAll = false, isUser = false))

    constructor(actionType: ActionType, config: Map<String, *>) : this(PlayersOperationData(config))

    override fun execute(action: Action) {
        if(InfiniteItems.DEBUGGING) {
            Bukkit.getLogger().info("ban player operation executed")
        }
        for(player in players.getPlayers(action)) {
            player.banPlayer("§cwomp womp")
        }
    }

    override fun forActionType(actionType: ActionType): Operation {
        return if(actionType == ActionType.HIT_PLAYER) {
            BanPlayerOperation(HitPlayersOperationData(players.isAll, players.isUser, false, ArrayList(players.getPlayers(null))))
        } else {
            BanPlayerOperation(players)
        }
    }

    override fun toMap(): HashMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["type"] = type.name
        map["players"] = players.toMap()

        return map
    }


}
