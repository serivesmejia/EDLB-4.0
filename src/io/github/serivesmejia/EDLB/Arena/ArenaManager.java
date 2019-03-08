package io.github.serivesmejia.EDLB.Arena;

import static io.github.serivesmejia.EDLB.Arena.GameState.*;
import io.github.serivesmejia.EDLB.Main;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class ArenaManager{
    
    HashMap<Integer, Arena> arenas;
    HashMap<String, Integer> arenasByName;
    Main main;
    ArenaManager manager = this;
    
    public ArenaManager(Main m){
        main = m;       
        arenas = new HashMap();
        arenasByName = new HashMap();
   }
    
    public boolean createArena(String name, int arID, int mP, int mxP, int gDur, Location swL, Location gL, Location bLoc){              
        
        if(!arenas.containsKey(arID)){
            Arena arena = new Arena(name, arID, mP, mxP, gDur, swL, gL, bLoc);
            arenas.put(arID, arena);
            arenasByName.put(name, arID);
           return true;
        }else{     
           return false; 
        }
        
    }
    
    public boolean addPlayer(Player p, int arenaID){
        if (arenas.containsKey(arenaID)){            
        Arena arena = arenas.get(arenaID);
        if(arena.getPlayers().contains(p)){
        arena.getPlayers().add(p);
        arena.addCurrentPlayers(1);
        
        for(Player pl : arena.Players){
            pl.sendMessage(main.getLang().getString("chatPrefix").replace("&", "§")+ " §r"+ main.getLang().getString("playerJoinArena").replace("&", "§").replace("$PLAYER", p.getName()).replace("$RESTANT", String.valueOf(arena.getCurrentPlayers() - arena.getMinPlayers())));
            
        }
        
        return true;
        }else{
            return false;
        }
    }else{
          return false;  
        }
              
    }
    
    public boolean removePlayer(Player p, int arenaID){
       if (arenas.containsKey(arenaID)){
       Arena arena = arenas.get(arenaID); 
       if(arena.getPlayers().contains(p)){
       arena.getPlayers().remove(p);
       arena.removeCurrentPlayers(1);
       
       for(Player pl : arena.Players){
            pl.sendMessage(main.getLang().getString("chatPrefix").replace("&", "§")+ " §r"+ main.getLang().getString("playerQuitArena").replace("&", "§").replace("$PLAYER", p.getName()).replace("$RESTANT", String.valueOf(arena.getCurrentPlayers() - arena.getMinPlayers())));
            
        }
       
       return true;
        }else{
            return false;
        }
       }else{
           return false;
       }
    }
    
    public Arena getArena(int arenaID){ 
        if (arenas.containsKey(arenaID)){
            return arenas.get(arenaID);
        }else{
            throw new NullPointerException("The arena ID "+ arenaID + "don't exists");
        }        
        }
    
   
    public int getArenaID(String name){
        if(arenasByName.containsKey(name)){
            
           return arenasByName.get(name);
        }else{
            throw new NullPointerException("The arena "+ name + "don't exists");
        }
    }
    
    public GameState getArenaGameState(int arenaID){
        if(arenas.containsKey(arenaID)){
            Arena arena = arenas.get(arenaID);
            return arena.getState();
        }else{
           throw new NullPointerException("The arena ID "+ arenaID + "don't exists"); 
        }
    }
    
    public HashMap getArenas(){ return arenas; }
    
    public HashMap getArenasByName(){ return arenasByName; }
    
    public void startArenaTask(ArenaManager am1){ updateArenas t3 = new updateArenas(am1); t3.runTaskTimerAsynchronously(main, 10, 10); }
    
    public void saveArenas(){
        for(Integer ar : arenas.keySet()){
            Arena arn = this.getArena(ar);
            HashMap prop = arn.getPropertiesToHashMap();
            main.getData().set("arenas." + arn.getArenaName(), prop.keySet());
        }
    }   
    
    public class updateArenas extends BukkitRunnable {

    private final ArenaManager ar;
    
    public updateArenas(ArenaManager instance) {
        ar = instance;
    }
    
    @Override
    public void run() {
        if(!ar.arenas.isEmpty()){
        for(Integer arr : ar.arenas.keySet()){
            Arena arena = ar.getArena(arr);
           
            if(arena.getMinPlayers() >= arena.getCurrentPlayers()){
                
            if(arena.getStarting() == false){
                arena.setStarting(true);
                startArenaCountDown t2 = new startArenaCountDown(ar, arena);
                t2.runTaskTimerAsynchronously(main, 20, 20);
                
                for(Player pl : arena.Players){
                    pl.sendMessage("");
                    
                    
                }
            }
            }
        }
        }
    }    
        
    }
        
    
    class startArenaCountDown extends BukkitRunnable {

    private final ArenaManager am1;
    private final Arena arn;
    
    public startArenaCountDown(ArenaManager instance, Arena arn1) {
        am1 = instance;
        arn = arn1;
    }
    
    @Override
    public void run() {
        
            Arena arena = arn;
           
            if(arena.minPlayers >= arena.currPlayers){
                for(Player pl : arena.Players){
                    pl.sendMessage("");
                    pl.playSound(pl.getLocation(), Sound.NOTE_PLING, 100, 2);
                    
                    
                }
            }else{
                cancel();
                arena.setStarting(false);
                for(Player pl : arena.Players){
                    pl.sendMessage("");
                    
                    
                }
            }
            
        
    }
        
    }
    
    
    public String gameStateToString(GameState state){
        if(null != state)
            switch (state){
            case BUILDING:
                return "BUILDING";
            case SETUPING:
                return "SETUPING";
            case WAITING:
                return "WAITING";
            case STARTING:
                return "STARTING";
            case PLAYING:
                return "PLAYING";
            case ENDING:
                return "ENDING";
            case CLOSED:
                return "CLOSED";
            case DISABLED:
                return "DISABLED";
            case BLOCKED:
                return "BLOCKED";
            default:
                break;
        }
        return null;
    }
    
    public GameState stringToGameState(String state){
        if(null != state)
            switch (state) {
            case "BUILDING":
                return BUILDING;
            case "SETUPING":
                return SETUPING;
            case "WAITING":
                return WAITING;
            case "STARTING":
                return STARTING;
            case "PLAYING":
                return PLAYING;
            case "ENDING":
                return ENDING;
            case "CLOSED":
                return CLOSED;
            case "DISABLED":
                return DISABLED;
            case "BLOCKED":
                return BLOCKED;
            default:
                break;
        }
        return null;
    }    
    
    }
    
  
    

