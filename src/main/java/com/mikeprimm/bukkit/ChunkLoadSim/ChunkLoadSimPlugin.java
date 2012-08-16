
package com.mikeprimm.bukkit.ChunkLoadSim;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author MikePrimm
 */
public class ChunkLoadSimPlugin extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");

	/* On disable, stop doing our function */
    public void onDisable() {
    	/* Since our registered listeners are disabled by default, we don't need to do anything */
    }

    private int playercount;
    private int chunkradius;
    private int maxradius;
    
    private static class PlayerZone {
        int x, z;
        World w;
    }
    
    private PlayerZone[] players;
    
    public void onEnable() {
        log.info("Loading ChunkLoadSim");
        
        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);   /* Load defaults, if needed */
        this.saveConfig();  /* Save updates, if needed */

        playercount = cfg.getInt("numplayers", 10);
        chunkradius = cfg.getInt("chunkradius", 10);
        maxradius = cfg.getInt("maxradius", 1000);
        
        System.out.println("playercount=" + playercount);
        System.out.println("chunkradius=" + chunkradius);
        System.out.println("maxradius=" + maxradius);
        players = new PlayerZone[playercount];
        
        List<World> worlds = getServer().getWorlds();
        Random rnd = new Random();
        
        for (int i = 0; i < playercount; i++) {
            players[i] = new PlayerZone();
            PlayerZone p = players[i];
            World w = worlds.get(0/*rnd.nextInt(worlds.size())*/);
            p.w = w;
            Location spawn = p.w.getSpawnLocation();
            p.x = (spawn.getBlockX() + (rnd.nextInt(2*maxradius) - maxradius)) >> 4;
            p.z = (spawn.getBlockZ() + (rnd.nextInt(2*maxradius) - maxradius)) >> 4;
            for(int j = 0; j <= chunkradius; j++) {
                for(int k = 0; k <= chunkradius; k++) {
                    w.loadChunk(p.x + j, p.z + k);
                    w.loadChunk(p.x + j, p.z - k);
                    w.loadChunk(p.x - j, p.z + k);
                    w.loadChunk(p.x - j, p.z - k);
                }
            }
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }
}
