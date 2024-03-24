package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.operation.data.OperationData

interface Operation {

    val type: OperationType
    val name: String
    val description: String
    val data: List<OperationData>
    fun execute(action: Action)
    fun forActionType(actionType: ActionType): Operation
    fun toMap(): HashMap<String, Any>

}