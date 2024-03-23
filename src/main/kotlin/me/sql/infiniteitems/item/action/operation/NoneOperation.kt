package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.operation.data.OperationData

class NoneOperation : Operation {

    override val type = OperationType.NONE
    override val name = "None"
    override val description = "do nothing"
    override val data: List<OperationData> = emptyList()

    override fun execute(action: Action) {
        return
    }

    override fun forActionType(actionType: ActionType): Operation {
        return this
    }


}