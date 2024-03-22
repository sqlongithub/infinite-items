package me.sql.infiniteitems.item.action.type

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class HitEntityAction(override var player: Player, val entity: Entity) : Action {
    override val type = ActionType.HIT_ENTITY
}