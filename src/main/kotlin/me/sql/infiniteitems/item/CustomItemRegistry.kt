package me.sql.infiniteitems.item

import me.sql.infiniteitems.InfiniteItems
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class CustomItemRegistry {

    companion object {

        val IDENTIFIER_TAG = "infiniteitems_custom_item_identifier"
        private val registry = mutableMapOf<String, CustomItem>()
        val size
            get() = registry.size
        val registeredItems
            get() = registry.values.toList()
        val registeredIdentifiers
            get() = registry.keys.toList()

        fun register(customItem: CustomItem) {
            if(registry.containsKey(customItem.identifier)) {
                Bukkit.getLogger().warning("Custom item already registered: ${customItem.identifier}")
                return
            }
            registry[customItem.identifier] = customItem
        }

        fun isRegistered(identifier: String?): Boolean = registry.containsKey(identifier)

        fun get(identifier: String?): CustomItem? {
            if(identifier == null)
                return null
            val identifier = identifier.lowercase().replace(' ', '_')
            if (!registry.containsKey(identifier.lowercase().replace(' ', '_'))) {
                Bukkit.getLogger().severe("Custom item not found: $identifier")
                Bukkit.getLogger().severe("Registered items: ${registry.keys}")
                return null
            }
            if(registry[identifier]?.identifier  != identifier) {
                Bukkit.getLogger().severe("Custom item is registered with different identifier: $identifier / ${registry[identifier]?.identifier}")
            }
            return registry[identifier]
        }

        fun get(item: ItemStack?): CustomItem? {
            if(item == null)
                return null
            if(item.type == Material.AIR || item.amount == 0)
                return null

            var customItem: CustomItem? = null
            val nbt = CraftItemStack.asNMSCopy(item).tag
            if(nbt != null) {
                customItem = get(nbt.getString(IDENTIFIER_TAG))
            }
            return customItem
        }
    }

}