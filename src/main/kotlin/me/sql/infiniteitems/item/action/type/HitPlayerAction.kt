package me.sql.infiniteitems.item.action.type

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import org.bukkit.entity.Player

class HitPlayerAction(override var player: Player, val hitPlayer: Player) : Action {
    override val type = ActionType.HIT_PLAYER
}