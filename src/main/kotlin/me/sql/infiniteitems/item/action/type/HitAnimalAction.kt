package me.sql.infiniteitems.item.action.type

import me.sql.infiniteitems.item.action.Action
import me.sql.infiniteitems.item.action.ActionType
import org.bukkit.entity.Animals
import org.bukkit.entity.Player


class HitAnimalAction(override var player: Player, val animal: Animals) : Action {
    override val type = ActionType.HIT_ANIMAL
}