package tororo1066.beaconprotect

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BeaconProtect : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        server.pluginManager.registerEvents(BeaconListener,this)
        getCommand("beaconprotect")?.setExecutor(BeaconCommands)
        getCommand("beaconprotect")?.tabCompleter = BeaconCommands
        plugin = this
        pluginEnable = config.getBoolean("defaultMode",false)
    }

    companion object{
        val protectLocations = HashMap<UUID,ArrayList<ProtectLoc>>()
        lateinit var plugin : BeaconProtect
        var pluginEnable = true
        const val prefix = "§f[§b§lBeacon§a§lProtect§f]§r"
    }
}

class ProtectLoc() {
    private val levelOneLoc = ArrayList<Triple<Int,Int,Int>>()
    private val levelTwoLoc = ArrayList<Triple<Int,Int,Int>>()
    private val levelThreeLoc = ArrayList<Triple<Int,Int,Int>>()
    private val levelFourLoc = ArrayList<Triple<Int,Int,Int>>()
    var allowPlayers = ArrayList<UUID>()
    var beaconLoc = Triple(0,0,0)
    private var success = true

    constructor(world : World, beaconLoc : Triple<Int,Int,Int>) : this() {
        this.beaconLoc = beaconLoc
        var x = beaconLoc.first-1
        var y = beaconLoc.second-1
        var z = beaconLoc.third-1
        for (loc in 1..9){
            val type = Location(world,x.toDouble(),y.toDouble(),z.toDouble()).block.type
            if (!isBeaconBlock(type)){
                success = false
                return
            }
            levelOneLoc.add(Triple(x,y,z))
            z += 1
            if (loc % 3 == 0){
                x += 1
                z -= 3
            }

        }

        x = beaconLoc.first-2
        y = beaconLoc.second-2
        z = beaconLoc.third-2
        for (loc in 1..25){
            val type = Location(world,x.toDouble(),y.toDouble(),z.toDouble()).block.type
            if (!isBeaconBlock(type)){
                levelTwoLoc.clear()
                return
            }
            levelTwoLoc.add(Triple(x,y,z))
            z += 1
            if (loc % 5 == 0){
                x += 1
                z -= 5
            }

        }


        x = beaconLoc.first-3
        y = beaconLoc.second-3
        z = beaconLoc.third-3
        for (loc in 1..49){
            val type = Location(world,x.toDouble(),y.toDouble(),z.toDouble()).block.type
            if (!isBeaconBlock(type)){
                levelThreeLoc.clear()
                return
            }
            levelThreeLoc.add(Triple(x,y,z))
            z += 1
            if (loc % 7 == 0){
                x += 1
                z -= 7
            }

        }


        x = beaconLoc.first-4
        y = beaconLoc.second-4
        z = beaconLoc.third-4
        for (loc in 1..81){
            val type = Location(world,x.toDouble(),y.toDouble(),z.toDouble()).block.type
            if (!isBeaconBlock(type)){
                levelFourLoc.clear()
                return
            }
            levelFourLoc.add(Triple(x,y,z))
            z += 1
            if (loc % 9 == 0){
                x += 1
                z -= 9
            }

        }
    }

    fun isProtected(loc : Location): Boolean {
        val triple = Triple(loc.blockX,loc.blockY,loc.blockZ)
        return levelOneLoc.contains(triple) || levelTwoLoc.contains(triple) || levelThreeLoc.contains(triple) || levelFourLoc.contains(triple) || beaconLoc == triple
    }

    fun isAllowPlayer(uuid: UUID): Boolean {
        return allowPlayers.contains(uuid)
    }

    private fun isBeaconBlock(type : Material): Boolean {
        return type == Material.DIAMOND_BLOCK || type == Material.EMERALD_BLOCK || type == Material.IRON_BLOCK || type == Material.GOLD_BLOCK || type == Material.NETHERITE_BLOCK
    }

    fun isSuccess(): Boolean {
        return success
    }

}