package me.sql.infiniteitems.item

import me.sql.infiniteitems.InfiniteItems
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

class CustomItemRegistry {

    companion object {

        const val ALIAS_TAG = "infiniteitems_custom_item_alias"
        private val registry = mutableMapOf<String, CustomItem>()
        val size
            get() = registry.size
        val registeredItems
            get() = registry.values.toList()
        val registeredAliases
            get() = registry.keys.toList()

        fun register(customItem: CustomItem) {
            if(registry.containsKey(customItem.alias)) {
                Bukkit.getLogger().warning("Custom item already registered: ${customItem.alias}")
                return
            }
            registry[customItem.alias] = customItem
        }

        fun isRegistered(alias: String?): Boolean = registry.containsKey(alias)

        fun get(alias: String?): CustomItem? {
            if(alias == null)
                return null
            val alias = alias.lowercase().replace(' ', '_')
            if (!registry.containsKey(alias.lowercase().replace(' ', '_'))) {
                Bukkit.getLogger().severe("Custom item not found: $alias")
                Bukkit.getLogger().severe("Registered items: ${registry.keys}")
                return null
            }
            if(registry[alias]?.alias  != alias) {
                Bukkit.getLogger().severe("Custom item is registered under a different name: $alias / ${registry[alias]?.alias}")
            }
            return registry[alias]
        }

        fun get(item: ItemStack?): CustomItem? {
            if(item == null)
                return null
            if(item.type == Material.AIR || item.amount == 0)
                return null

            var customItem: CustomItem? = null
            val nbt = CraftItemStack.asNMSCopy(item).tag
            if(nbt != null) {
                if(InfiniteItems.DEBUGGING) {
                    Bukkit.getLogger().info("get(): nbt not null")
                }
                customItem = get(nbt.getString(ALIAS_TAG))
            }
            return customItem
        }
    }

}