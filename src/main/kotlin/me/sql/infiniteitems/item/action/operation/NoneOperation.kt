package me.sql.infiniteitems.item.action.operation

import me.sql.infiniteitems.item.action.operation.data.OperationData

class NoneOperation : Operation {

    override val name = "None"
    override val description = "do nothing"
    override val data: List<OperationData> = emptyList()

    override fun execute() {
        return
    }


}