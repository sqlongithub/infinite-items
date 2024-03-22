package me.sql.infiniteitems.item.action

import org.bukkit.entity.Player

class RightClickAction(override var player: Player) : Action {

    override val type = ActionType.RIGHT_CLICK


}