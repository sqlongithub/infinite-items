package me.sql.infiniteitems.item

import me.sql.infiniteitems.InfiniteItems
import me.sql.infiniteitems.item.action.ActionType
import me.sql.infiniteitems.item.action.condition.NoneCondition
import me.sql.infiniteitems.item.action.handler.ActionHandler
import me.sql.infiniteitems.item.action.operation.NoneOperation
import me.sql.infiniteitems.item.action.operation.Operation
import me.sql.infiniteitems.item.action.operation.OperationType
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.debug
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.io.File

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

        private val itemsFile = File(InfiniteItems.instance.dataFolder, "items.yml")
        private val itemsConfig = YamlConfiguration.loadConfiguration(itemsFile)

        fun loadFromConfigAsync() {
            registry.clear()
            Bukkit.getAsyncScheduler().runNow(InfiniteItems.instance) {
                debug("Reading items.yml...")
                if(!itemsFile.exists()) {
                    debug("items.yml doesn't exist")
                    saveToConfigAsync()
                    return@runNow
                }
                itemsConfig.load(itemsFile)
                for(item in itemsConfig.getKeys(false)) {
                    if(item.isNullOrBlank()) continue
                    debug("$item:")
                    val itemSection = itemsConfig.getConfigurationSection(item)
                    if(itemSection == null) {
                        Bukkit.getLogger().severe("Error reading config: item $item is improperly defined in items.yml (not a configuration section)")
                        return@runNow
                    }

                    val name = itemSection.getString("name")
                    if(name == null) {
                        Bukkit.getLogger().severe("Error reading config: item $item doesn't have a name specified")
                        return@runNow
                    }

                    val materialName = itemSection.getString("material")
                    if(materialName == null) {
                        Bukkit.getLogger().severe("Error reading config: item $item doesn't have a material specified")
                        return@runNow
                    }

                    val material = Material.getMaterial(materialName)
                    if(material == null) {
                        Bukkit.getLogger().severe("Error reading config: item $item doesn't have a valid material")
                        return@runNow
                    }

                    debug("  - name: ${name}")
                    debug("  - material: $material")
                    debug("  - actions:")

                    val actionsList = itemSection.getMapList("actions")
                    val actionHandlers = ArrayList<ActionHandler>()
                    if(actionsList.isEmpty()) {
                        debug("    - no actions specified")
                    } else {
                        for((index, action) in actionsList.withIndex()) {
                            val actionTypeString = action["type"]
                            if(actionTypeString !is String || actionTypeString.isBlank()) {
                                Bukkit.getLogger().severe("Error reading config: action type $actionTypeString is not valid for item $item")
                                return@runNow
                            }

                            val actionType: ActionType
                            try {
                                actionType = ActionType.valueOf(actionTypeString)
                            } catch(e: IllegalArgumentException) {
                                Bukkit.getLogger().severe("Error reading config: action type $actionTypeString is not valid for item $item")
                                return@runNow
                            }
                            debug("    - type: ${actionType.name}")

                            val operationMap = action["operation"]
                            var operation: Operation = NoneOperation()
                            if(operationMap == null || operationMap !is Map<*, *>) {
                                debug("    - no operation specified")
                                debug("    - operationSection: $operationMap")
                            } else {
                                debug("    - operation:")
                                val operationTypeString = operationMap["type"]
                                var operationType = OperationType.NONE
                                if(operationTypeString == null || operationTypeString !is String) {
                                    debug("      - no type specified")
                                } else {
                                    try {
                                        operationType = OperationType.valueOf(operationTypeString)
                                        debug("      - type: ${operationType.name}")
                                    } catch(e: IllegalArgumentException) {
                                        Bukkit.getLogger().warning("Error reading config: operation type $operationTypeString is not valid for item $item")
                                    }
                                }

                                try {
                                    operation = operationType.parentClass.java.getDeclaredConstructor(ActionType::class.java, Map::class.java)
                                        .newInstance(actionType, operationMap)
                                } catch(e: Exception) {
                                    Bukkit.getLogger().severe("Error reading config: operation type ${operationType.name} could not be read.")
                                    Bukkit.getLogger().severe("This is likely not your mistake, and most probably a bug in the plugin.")
                                    Bukkit.getLogger().severe("Report this with this info:")
                                    e.printStackTrace()
                                }
                            }

                            actionHandlers.add(ActionHandler(actionType, operation, NoneCondition()))
                        }
                    }

                    val customItem = CustomItem(
                        item,
                        ChatColor.translateAlternateColorCodes('&', name).asTextComponent(),
                        material,
                        64,
                        actionHandlers)
                    register(customItem)
                }
            }
        }

        fun saveToConfigAsync() {
            Bukkit.getAsyncScheduler().runNow(InfiniteItems.instance) {
                saveToConfigSync()
            }
        }

        fun saveToConfigSync() {
            for(item in registeredItems) {
                itemsConfig.set("${item.alias}.name", item.name.content())
                itemsConfig.set("${item.alias}.material", item.material.name)
                val actionsList = ArrayList<Map<String, Any>>()
                for(actionHandler in item.actionHandlers) {
                    val map = HashMap<String, Any>()

                    val operation = actionHandler.operation.toMap()

                    map["type"] = actionHandler.type.name
                    map["operation"] = operation
                    actionsList.add(map)
                }
                itemsConfig.set("${item.alias}.actions", actionsList)
            }
            itemsConfig.save(itemsFile)
        }

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