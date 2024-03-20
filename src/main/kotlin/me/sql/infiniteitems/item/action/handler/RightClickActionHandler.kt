package me.sql.infiniteitems.item.action.handler

import me.sql.infiniteitems.InfiniteItems
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.RightClickAction
import me.sql.infiniteitems.item.action.condition.Condition
import me.sql.infiniteitems.item.action.operation.Operation

class RightClickActionHandler(override var operation: Operation, override var condition: Condition) : ActionHandler<RightClickAction> {

    override val type: ActionType = ActionType.RIGHT_CLICK

    override fun handle(action: RightClickAction) {
        if(InfiniteItems.debugging) {
            action.player.sendMessage("action handled")
        }
        operation.execute()
    }
}