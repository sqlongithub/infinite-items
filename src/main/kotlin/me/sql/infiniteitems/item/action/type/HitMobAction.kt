package me.sql.infiniteitems.item.action.type

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

class HitMobAction(override var player: Player, val mob: Monster) : Action {
    override val type = ActionType.HIT_MOB
}