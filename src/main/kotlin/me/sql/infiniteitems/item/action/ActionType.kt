package me.sql.infiniteitems.item.action

import me.sql.infiniteitems.Selectable
import org.bukkit.Material
import kotlin.reflect.KClass

enum class ActionType(
    override val parentClass: KClass<*>,
    override val material: Material,
    override val description: String,
    val appliesTo: List<Material> = Material.entries) : Selectable {

    // ATTACK_ENTITY(this::class, Material.WOODEN_SWORD, "the player attacks an entity"),
    // ATTACK_BLOCK(Material.WOODEN_PICKAXE, "the player begins breaking a block"),
    // BLOCK_BREAK(Material.DIAMOND_PICKAXE, "the player breaks a block"),
    RIGHT_CLICK(RightClickAction::class, Material.FISHING_ROD, "the player right-clicks with the item in hand"),
    ITEM_DROP(ItemDropAction::class, Material.DROPPER, "the player drops the item")
}