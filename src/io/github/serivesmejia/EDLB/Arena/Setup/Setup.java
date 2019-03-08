package io.github.serivesmejia.EDLB.Arena.Setup;

import io.github.serivesmejia.EDLB.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Setup {
 
    Player p;
    int step = 1;
    Main m;
    int id;
    
    Location sL;
    Location bL;
    Location gL;
    
    int mP;
    int mxP;
    int dur;
    public String name;
    
    public BukkitRunnable t1;
    
    
    
    public Setup(Main main, Player p1, int id1, String arenaN, BukkitRunnable t2){
        p = p1;
        m = main;
        id = id1;
        name = arenaN;
        t1 = t2;
    }
    
    public int getStep(){ return step; }
    
    public void setStep(int i){ step = i; }
    
    public void nextStep(){ step = step + 1; }
    
    public void setMinPlayers(int minP) { mP = minP; }
    
    public void setSpawnLocation(Location loc) { sL = loc; }
    
    public void setBeastLocation(Location loc) { bL = loc; }
    
    public void setGameLocation(Location loc) { gL = loc; }
    
    public int getMinPlayers(){ return mP; }
    
    public void setMaxPlayers(int maxP){ mxP = maxP; }
    
    public int getMaxPlayers(){ return mxP; }
    
    public void setGameDuration(int gd){ dur = gd; }
    
    public int getGameDuration(){ return dur; }
    
    public void end(Player p){ m.getArenaManager().createArena(name, id, mP, mxP, dur, sL, gL, bL); m.getArenaSetup().setups.remove(p); t1.cancel();}
    
}
