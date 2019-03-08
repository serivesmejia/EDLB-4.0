package io.github.serivesmejia.EDLB.Arena;

import static io.github.serivesmejia.EDLB.Arena.GameState.*;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class Arena {
    
public String arenaName;
public int minPlayers;
public int maxPlayers;
public int arenaID;
public Location spawnLocation;
public Location gameLocation;
public Location beastLocation; 
public int currPlayers;
public boolean running;
public int duration;
public int restantTime;
public List<Player> Players;    
public GameState gameS;
public boolean starting;

    public Arena(String name, int arID, int mP, int mxP, int gDur, Location swL, Location gL, Location bLoc) {
        arenaName = name;
        minPlayers = mP;
        maxPlayers = mxP;
        arenaID = arID;
        spawnLocation = swL;
        gameLocation = gL;
        currPlayers = 0;
        beastLocation = bLoc;
        duration = gDur;
        restantTime = 0;
        gameS = WAITING;
        starting = false;
        running = false;
        
    }
    
    public int getMinPlayers(){ return minPlayers; }
    
    public int getMaxPlayers(){ return maxPlayers; }
    
    public int getCurrentPlayers(){ return currPlayers; }
    
    public boolean getIsRunning(){ return running; }
    
    public String getArenaName(){ return arenaName; }
    
    public Location getSpawnLocation(){ return spawnLocation; }
    
    public Location getGameLocation(){ return gameLocation; }
    
    public Location getBeastLocation(){ return beastLocation; }
    
    public int getDuration(){ return duration; }
    
    public boolean getStarting(){ return starting; }
    
    public int getRestantTime(){ return restantTime; }
    
    public void addCurrentPlayers(int add){ currPlayers = currPlayers + add; }
    
    public void removeCurrentPlayers(int remove){ currPlayers = currPlayers - remove; }
    
    public GameState getState(){ return gameS; }
    
    public void setState(GameState state){ gameS = state; }
    
    public List getPlayers(){ return Players; }
    
    public void setSpawnLocation(Location lcn){ spawnLocation = lcn; }
    
    public void setGameLocation(Location lcn){ gameLocation = lcn; }
    
    public void setBeastLocation(Location lcn){ beastLocation = lcn; }
    
    public void setMinPlayers(int minP){ minPlayers = minP; }
    
    public void setMaxPlayers(int maxP){ maxPlayers = maxP; }
    
    public void setArenaName(String name){ arenaName = name; }
    
    public void setDuration(int drtn){ duration = drtn;  }
    
    public void setStarting(Boolean strg){ starting = strg; }
    
    public int getArenaID(){ return arenaID; }
    
    public HashMap getPropertiesToHashMap(){
        HashMap<String, Object> prop = new HashMap();
        
        prop.put("minPlayers", this.getMinPlayers());
        prop.put("arenaDuration", this.getDuration());
        prop.put("maxPlayers", this.getMaxPlayers());
        prop.put("arenaID", this.getArenaID());
        prop.put("arenaName",this.getArenaName());
        
        prop.put("beastLocationX", this.beastLocation.getX());
        prop.put("beastLocationY", this.beastLocation.getY());
        prop.put("beastLocationZ", this.beastLocation.getZ());
        prop.put("beastLocationYW", this.beastLocation.getYaw());
        prop.put("beastLocationPI", this.beastLocation.getPitch());
        prop.put("beastLocationW", this.beastLocation.getWorld().getName());
        
        prop.put("spawnLocationX", this.spawnLocation.getX());
        prop.put("spawnLocationY", this.spawnLocation.getY());
        prop.put("spawnLocationZ", this.spawnLocation.getZ());
        prop.put("spawnLocationYW", this.spawnLocation.getYaw());
        prop.put("spawnLocationPI", this.spawnLocation.getPitch());
        prop.put("spawnLocationW", this.spawnLocation.getWorld().getName());
        
        prop.put("gameLocationX", this.gameLocation.getX());
        prop.put("gameLocationY", this.gameLocation.getY());
        prop.put("gameLocationZ", this.gameLocation.getZ());
        prop.put("gameLocationYW", this.gameLocation.getYaw());
        prop.put("gameLocationPI", this.gameLocation.getPitch());
        prop.put("gameLocationW", this.gameLocation.getWorld().getName());
        
        prop.put("gameState", gameStateToString(this.getState()));
               
        return prop;
   }
    
    public void setPropertiesFromHashMap(HashMap hash){
        
    } 
    
    
        public String gameStateToString(GameState state){
        if(state == BUILDING){
            return "BUILDING";
        }else if(state == SETUPING){
            return "SETUPING";
        }else if(state == WAITING){
            return "WAITING";
        }else if(state == STARTING){
            return "STARTING";
        }else if(state == PLAYING){
            return "PLAYING";
        }else if(state == ENDING){
            return "ENDING";
        }else if(state == CLOSED){
            return "CLOSED";
        }else if(state == DISABLED){
            return "DISABLED";
        }else if(state == BLOCKED){
            return "BLOCKED";
        }
        return null;
    }
    
    public GameState stringToGameState(String state){
        if(state == "BUILDING"){
            return BUILDING;
        }else if(state == "SETUPING"){
            return SETUPING;
        }else if(state == "WAITING"){
            return WAITING;
        }else if(state == "STARTING"){
            return STARTING;
        }else if(state == "PLAYING"){
            return PLAYING;
        }else if(state == "ENDING"){
            return ENDING;
        }else if(state == "CLOSED"){
            return CLOSED;
        }else if(state == "DISABLED"){
            return DISABLED;
        }else if(state == "BLOCKED"){
            return BLOCKED;
        }
        return null;
    }    
    
}