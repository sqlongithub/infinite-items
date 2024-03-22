package me.sql.infiniteitems.item.action

import me.sql.infiniteitems.Selectable
import me.sql.infiniteitems.item.action.type.*
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
    ITEM_DROP(ItemDropAction::class, Material.DROPPER, "the player drops the item"),
    JUMP(JumpAction::class, Material.SLIME_BLOCK, "the player jumps"),
    HIT_ENTITY(HitEntityAction::class, Material.DIAMOND_SWORD, "the player hits an entity"),
    HIT_PLAYER(HitPlayerAction::class, Material.GOLDEN_SWORD, "the player hits another player"),
    HIT_MOB(HitMobAction::class, Material.IRON_SWORD, "the player hits a mob"),
    HIT_ANIMAL(HitAnimalAction::class, Material.STONE_SWORD, "the player hits an animal")


}