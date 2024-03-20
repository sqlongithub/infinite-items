package me.sql.infiniteitems.item

import me.sql.infiniteitems.InfiniteItems
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class CustomItemRegistry {

    companion object {

        val IDENTIFIER_KEY = NamespacedKey(InfiniteItems.instance, "customItemIdentifier")
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
            val meta = item.itemMeta
            if(meta.persistentDataContainer.has(IDENTIFIER_KEY)) {
                customItem = get(meta.persistentDataContainer.get(IDENTIFIER_KEY, PersistentDataType.STRING))
            }
            return customItem
        }
    }

}