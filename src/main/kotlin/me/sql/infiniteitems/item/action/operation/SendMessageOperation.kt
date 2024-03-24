package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.operation.data.HitPlayersOperationData
import me.sql.infiniteitems.item.action.operation.data.MessageOperationData
import me.sql.infiniteitems.item.action.operation.data.OperationData
import me.sql.infiniteitems.item.action.operation.data.PlayersOperationData
import me.sql.infiniteitems.util.debug

class SendMessageOperation(private val message: MessageOperationData, private val players: PlayersOperationData) : Operation {

    constructor(actionType: ActionType) : this(MessageOperationData("Hello, World!"),
        if(actionType == ActionType.HIT_PLAYER)
            HitPlayersOperationData(false, false, true)
        else
            PlayersOperationData(isAll = false, isUser = true))

    constructor(actionType: ActionType, configurationMap: Map<String, *>)
            : this(MessageOperationData(configurationMap),
        if(actionType == ActionType.HIT_PLAYER)
            HitPlayersOperationData(configurationMap)
        else
            PlayersOperationData(configurationMap))

    override val type = OperationType.SEND_MESSAGE
    override val name: String = "Send Message"
    override val description: String
        get() = "Send a message to §a${players.getFormattedValue(null)}"
    override val data: List<OperationData>
        get() = listOf(message, players)

    override fun execute(action: Action) {
        for(player in players.getOnlinePlayers(action)) {
            player.sendMessage(message.getFormattedValue(player))
        }
    }

    override fun forActionType(actionType: ActionType): SendMessageOperation {
        return if(actionType == ActionType.HIT_PLAYER) {
            SendMessageOperation(message, HitPlayersOperationData(players.isAll, players.isUser, false))
        } else {
            SendMessageOperation(message, PlayersOperationData(players.isAll, players.isUser))
        }
    }

    override fun toMap(): HashMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["type"] = type.name
        map["message"] = message.message
        map["players"] = players.toMap()

        debug(map.toString())

        return map
    }

}