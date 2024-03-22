package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.item.action.operation.data.MessageOperationData
import me.sql.infiniteitems.item.action.operation.data.OperationData
import me.sql.infiniteitems.item.action.operation.data.PlayersOperationData
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.withoutItalics
import org.bukkit.entity.Player

class SendMessageOperation(private val message: MessageOperationData, private val players: PlayersOperationData) : Operation {

    constructor() : this(MessageOperationData {
        return@MessageOperationData "Hello, World!".asTextComponent().withoutItalics()
    }, PlayersOperationData(isAll = false, isUser = true))

    override val type = OperationType.SEND_MESSAGE
    override val name: String = "Send Message"
    override val description: String
        get() = "Send a message to §a${players.getFormattedValue(null)}"
    override val data: List<OperationData>
        get() = listOf(message, players)

    override fun execute(player: Player) {
        for(player in players.getOnlinePlayers(player)) {
            player.sendMessage(message.getMessage(player))
        }
    }

}