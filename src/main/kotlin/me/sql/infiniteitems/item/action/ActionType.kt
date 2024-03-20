package me.sql.infiniteitems.item.action

import me.sql.infiniteitems.item.action.handler.ActionHandler
import me.sql.infiniteitems.item.action.handler.RightClickActionHandler
import org.bukkit.Material
import kotlin.reflect.KClass

enum class ActionType(val actionClass: KClass<out Action>, val handlerClass: KClass<out ActionHandler<Action>>,
                      val material: Material, val whenever: String, val appliesTo: List<Material> = Material.entries) {

    // ATTACK_ENTITY(this::class, Material.WOODEN_SWORD, "the player attacks an entity"),
    // ATTACK_BLOCK(Material.WOODEN_PICKAXE, "the player begins breaking a block"),
    // BLOCK_BREAK(Material.DIAMOND_PICKAXE, "the player breaks a block"),
    RIGHT_CLICK(RightClickAction::class, RightClickActionHandler::class, Material.FISHING_ROD, "the player right-clicks with the item in hand"),

}