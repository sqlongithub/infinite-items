package me.sql.infiniteitems.item.action.handler

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.condition.Condition
import me.sql.infiniteitems.item.action.operation.Operation

interface ActionHandler<out T : Action> {

    val type: ActionType
    var condition: Condition
    var operation: Operation
    fun handle(action: @UnsafeVariance T)

}