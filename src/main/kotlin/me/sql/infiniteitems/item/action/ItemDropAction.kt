package me.sql.infiniteitems.item.action

import org.bukkit.entity.Player

class ItemDropAction(override var player: Player) : Action {
    override val type = ActionType.ITEM_DROP
}