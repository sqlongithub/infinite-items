import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import me.sql.infiniteitems.InfiniteItems
import me.sql.infiniteitems.event.ActionListener
import me.sql.infiniteitems.item.CustomItem
import me.sql.infiniteitems.item.CustomItemRegistry
import me.sql.infiniteitems.item.action.RightClickAction
import me.sql.infiniteitems.item.action.condition.NoneCondition
import me.sql.infiniteitems.item.action.handler.RightClickActionHandler
import me.sql.infiniteitems.item.action.operation.SendMessageOperation
import me.sql.infiniteitems.item.action.operation.data.MessageOperationData
import me.sql.infiniteitems.item.action.operation.data.PlayersOperationData
import me.sql.infiniteitems.util.asTextComponent
import me.sql.infiniteitems.util.withoutItalics
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import kotlin.test.*

class CustomItemTest {


    private lateinit var server: ServerMock
    private lateinit var plugin: Any
    @BeforeTest
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(InfiniteItems::class.java)
        server.pluginManager.registerEvents(ActionListener(), InfiniteItems.instance)
    }

    @Test
    fun testCustomItem() {

        val customItem = CustomItem("custom item", "§cSuper§aCool§9Item".asTextComponent(), Material.DIRT, 64)

        assertEquals("custom_item", customItem.identifier)
        assertEquals("§cSuper§aCool§9Item".asTextComponent().color(NamedTextColor.WHITE).withoutItalics(), customItem.name)

        val player = server.addPlayer("jens")
        val itemStack = customItem.getItemStack(player)
        assertNotNull(itemStack)
        assertEquals(Material.DIRT, itemStack.type)
        assertEquals(customItem.name, itemStack.itemMeta.displayName())
        val nms = CraftItemStack.asNMSCopy(itemStack)
        assert(nms.hasTag())
        assertEquals(customItem.identifier, nms.tag!!.getString(CustomItemRegistry.IDENTIFIER_TAG))

    }

     @Test
     fun testActionHandler() {
         val customItem = CustomItem("custom item", "§cSuper§aCool§9Item".asTextComponent(), Material.DIRT, 64)

         val players = PlayersOperationData(true)
         val message = MessageOperationData { _: Player ->
             return@MessageOperationData "test".asTextComponent()
         }
         val operation = SendMessageOperation(message, players)
         val handler = RightClickActionHandler(operation, NoneCondition())

         customItem.actionHandlers.add(handler)

         val player = server.addPlayer("jens")
         customItem.handleAction(RightClickAction(player))
         if(InfiniteItems.debugging)
             assertEquals("action handled", player.nextMessage())
         assertEquals("test", player.nextMessage())
     }

    @AfterTest
    fun tearDown() {
        MockBukkit.unmock()
    }

}