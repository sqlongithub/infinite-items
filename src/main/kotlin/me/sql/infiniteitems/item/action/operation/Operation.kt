package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.item.action.operation.data.OperationData
import org.bukkit.entity.Player

interface Operation {

    val type: OperationType
    val name: String
    val description: String
    val data: List<OperationData>
    fun execute(player: Player)

}