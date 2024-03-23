package me.sql.infiniteitems.item.action.handler

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.condition.Condition
import me.sql.infiniteitems.item.action.operation.Operation

class ActionHandler(var type: ActionType, var operation: Operation, var condition: Condition) {

    fun handle(action: Action) {
        operation.execute(action)
    }

}