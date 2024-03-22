package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.item.action.operation.data.OperationData
import org.bukkit.entity.Player

class NoneOperation : Operation {

    override val type = OperationType.NONE
    override val name = "None"
    override val description = "do nothing"
    override val data: List<OperationData> = emptyList()

    override fun execute(player: Player) {
        return
    }


}