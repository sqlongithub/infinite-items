package me.sql.infiniteitems.item.action.type

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import org.bukkit.entity.Player

class ItemDropAction(override var player: Player) : Action {
    override val type = ActionType.ITEM_DROP
}