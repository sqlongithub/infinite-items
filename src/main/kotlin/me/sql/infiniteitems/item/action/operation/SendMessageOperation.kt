package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.item.action.operation.data.MessageOperationData
import me.sql.infiniteitems.item.action.operation.data.OperationData
import me.sql.infiniteitems.item.action.operation.data.PlayersOperationData
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.withoutItalics
import org.bukkit.Bukkit

class SendMessageOperation(private val message: MessageOperationData, private val players: PlayersOperationData) : Operation {

    constructor() : this(MessageOperationData {
        return@MessageOperationData "Hello, World!".asTextComponent().withoutItalics()
    }, PlayersOperationData(true))

    override val type = OperationType.SEND_MESSAGE
    override val name: String = "Send Message"
    override val description: String
        get() = "Send a message to §a${getFormattedPlayers()}"
    override val data: List<OperationData>
        get() = listOf(message, players)

    private fun getFormattedPlayers(): String {
        val players = players.getPlayers()
        if(players.isNullOrEmpty()) {
            return "no one."
        } else if(this.players.isAll) {
            return "everyone."
        } else {
            var string = ""
            string += players[0].name
            if(players.size > 1) {
                string += " and ${players.size - 1} others"
            }
            return string
        }
    }

    override fun execute() {
        for(player in players.getOnlinePlayers()) {
            player.sendMessage(message.getMessage(player))
        }
    }

}