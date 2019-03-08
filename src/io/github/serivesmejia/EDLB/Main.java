package io.github.serivesmejia.EDLB;

import io.github.serivesmejia.EDLB.Arena.ArenaManager;
import io.github.serivesmejia.EDLB.Arena.Setup.ArenaSetup;
import io.github.serivesmejia.EDLB.Arena.Setup.Setup;
import io.github.serivesmejia.EDLB.Exception.DataSaveException;
import io.github.serivesmejia.EDLB.Exception.LangSaveException;
import io.github.serivesmejia.EDLB.Task.UpdateGameSignsTask;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main
  extends JavaPlugin
  implements Listener
{
  public static Main plugin;
  public static final Logger logger = Logger.getLogger("Minecraft");
  File config = new File(getDataFolder() + File.separator + "config.yml");
  public static ArrayList players = new ArrayList();
  public static Location spwl = null;
  private Boolean canLogin = Boolean.valueOf(true);
  private boolean locationSet = false;
  ArenaManager am = new ArenaManager(this);
  ArenaSetup as = new ArenaSetup(this);
  int c = 0;
  
  @Override
  public void onLoad()
  {
    logger.info("[EDLB] Checking files...");
    int count = 3;
    Boolean thing = Boolean.valueOf(false);
    if (!this.config.exists())
    {
      logger.info("[EDLB] File config.yml don't exists!");
      count -= 1;
    }
    else
    {
      logger.info("[EDLB] File config.yml exists.");
    }
    if (!this.data.exists())
    {
      logger.info("[EDLB] File data.yml don't exists!");
      count -= 1;
    }
    else
    {
      logger.info("[EDLB] File data.yml exists.");
    }
    if (!this.lang.exists())
    {
      logger.info("[EDLB] File lang.yml don't exists!");
      count -= 1;
    }
    else
    {
      logger.info("[EDLB] File lang.yml exists.");
    }
    if (count <= 0)
    {
      count = 0;
      logger.info("[EDLB] There are 0/3 files. The files will be created soon...");
      thing = Boolean.valueOf(true);
    }
    if (count == 3)
    {
      logger.info("[EDLB] There are 3/3 files. All seems to be ok.");
      thing = Boolean.valueOf(true);
    }
    if ((count <= 2) && (count != 0) && (!thing.booleanValue()) && (count != 3))
    {
      this.canLogin = Boolean.valueOf(true);
      logger.info("[EDLB] There are " + String.valueOf(count) + "/3 files. Creating files soon...");
    }
  }
  
  @Override
  public void onEnable()
  {
    plugin = this;
    
    getServer().getPluginManager().registerEvents(this, this);
    
    int count = 0;
    if (!this.config.exists())
    {
      logger.info("[EDLB] File config.yml don't exists!. Creating one for you...");
      saveResource("config.yml", false);
      count += 1;
    }
    if (!this.data.exists())
    {
      logger.info("[EDLB] File data.yml don't exists!. Creating one for you...");
      saveResource("data.yml", false);
      count += 1;
    }
    else
    {
      logger.info("[EDLB] Loading data from data.yml file...");
      players.addAll(getData().getList("players"));
      
      this.c = getData().getInt("c");
      if (Boolean.valueOf(getData().get("slb").toString()).booleanValue() == true)
      {
        this.locationSet = true;
        World w = Bukkit.getWorld(getData().getString("spawnW"));
        
        spwl = new Location(w, Double.valueOf(getData().get("spawnlX").toString()).doubleValue(), Double.valueOf(getData().get("spawnlY").toString()).doubleValue(), Double.valueOf(getData().get("spawnlZ").toString()).doubleValue(), Float.valueOf(getData().get("spawnlPI").toString()).floatValue(), Float.valueOf(getData().get("spawnlYW").toString()).floatValue());
      }
    }
    if (!this.lang.exists())
    {
      count += 1;
      logger.info("[EDLB] File lang.yml don't exists!. Creating one for you...");
      saveResource("lang.yml", false);
    }
    UpdateGameSignsTask t1 = new UpdateGameSignsTask(this, this.am);
    
    logger.info("[EDLB] Starting main tasks...");
    t1.runTaskTimerAsynchronously(plugin, 20L, 20L);
    this.am.startArenaTask(this.am);
    if (count >= 1)
    {
      reloadConfig();
      reloadData();
      reloadLang();
    }
  }
  
  @Override
  public void onDisable()
  {
    getData().set("players", players.listIterator());
    getData().set("c", Integer.valueOf(this.c));
    getData().set("slb", Boolean.valueOf(this.locationSet));
    if (!this.am.getArenas().isEmpty())
    {
      logger.info("[EDLB] Saving all arenas...");
      this.am.saveArenas();
    }
    else
    {
      logger.info("[EDLB] There are not any arenas to save.");
    }
    try
    {
      saveData();
    }
    catch (InvalidConfigurationException|DataSaveException ex)
    {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public ArenaSetup getArenaSetup(){ return as; }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent ev)
  {
    if (!this.canLogin.booleanValue())
    {
      ev.getPlayer().kickPlayer("§7[§eEDLB§7]\n\n§2Please wait, reloading server...");
    }
    else
    {
      pTask t3 = new pTask(this, ev.getPlayer());
      
      t3.runTaskTimerAsynchronously(this, 20L, 20L);
      
      ev.setJoinMessage("");
      if (getLang().get("joinMessage") != "") {
        Bukkit.broadcastMessage(getLang().get("joinMessage").toString().replace("$PLAYER", ev.getPlayer().getName()).replace("&", "§"));
      }
      if (spwl == null) {
        ev.getPlayer().sendMessage(String.valueOf(getLang().get("chatPrefix")).replace("&", "§") + " " + getLang().getString("spawnNotSet").replace("&", "§"));
      } else {
        ev.getPlayer().teleport(spwl);
      }
      if (!players.contains(ev.getPlayer().getName())) {
        players.add(ev.getPlayer().getName());
      }
    }
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent ev)
  {
    ev.setQuitMessage("");
    if (getLang().get("quitMessage") != "") {
      Bukkit.broadcastMessage(getLang().get("quitMessage").toString().replace("$PLAYER", ev.getPlayer().getName()).replace("&", "§"));
    }
  }
  
  private File data = new File(getDataFolder(), "data.yml");
  private FileConfiguration A = YamlConfiguration.loadConfiguration(this.data);
  
  public void reloadData()
  {
    if (this.data == null) {
      this.data = new File(getDataFolder(), "data.yml");
    }
    this.A = YamlConfiguration.loadConfiguration(this.data);
    
    InputStream defStream = getResource("data.yml");
    if (defStream != null)
    {
      YamlConfiguration defData = YamlConfiguration.loadConfiguration(defStream);
      this.A.setDefaults(defData);
    }
  }
  
  public FileConfiguration getData()
  {
    if (this.A == null) {
      reloadData();
    }
    return this.A;
  }
  
  public void saveData()
    throws InvalidConfigurationException, DataSaveException
  {
    if ((this.A == null) || (this.data == null)) {
      return;
    }
    try
    {
      getData().save(this.data);
    }
    catch (IOException ex)
    {
      try
      {
        DataSaveException();
      }
      catch (DataSaveException ex1)
      {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex1);
      }
      logger.log(Level.SEVERE, "Could not save data to data.yml, " + ex);
    }
  }
  
  private void DataSaveException()
    throws DataSaveException
  {
    throw new DataSaveException("Can't save data.yml file");
  }
  
  private File lang = new File(getDataFolder(), "lang.yml");
  private FileConfiguration B = YamlConfiguration.loadConfiguration(this.lang);
  
  public void reloadLang()
  {
    if (this.lang == null) {
      this.lang = new File(getDataFolder(), "lang.yml");
    }
    this.B = YamlConfiguration.loadConfiguration(this.data);
    
    InputStream defStream = getResource("lang.yml");
    File cfg = new File(defStream.toString());
    if (defStream != null)
    {
      YamlConfiguration defData = YamlConfiguration.loadConfiguration(defStream);
      this.B.setDefaults(defData);
    }
  }
  
  public FileConfiguration getLang()
  {
    if (this.B == null) {
      reloadLang();
    }
    return this.B;
  }
  
  public void saveLang()
    throws InvalidConfigurationException, LangSaveException
  {
    if ((this.B == null) || (this.lang == null)) {
      return;
    }
    try
    {
      getLang().save(this.lang);
    }
    catch (IOException ex)
    {
      LangSaveException();
      logger.log(Level.SEVERE, "Could not save data to lang.yml, " + ex);
    }
  }
  
  private void LangSaveException()
    throws LangSaveException
  {
    throw new LangSaveException("Can't save lang.yml file");
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((sender instanceof Player))
    {
      Player p = (Player)sender;
      String prefix = getLang().getString("chatPrefix").replace("&", "§") + "§r ";
      if ((cmd.getName().equalsIgnoreCase("edlb")) || (cmd.getName().equalsIgnoreCase("rftb")))
      {
        if (args.length <= 0)
        {
          sender.sendMessage("§e-------------------------------");
          sender.sendMessage("");
          sender.sendMessage("§9 EDLB §ePlugin by serivesmejia");
          sender.sendMessage("§2 For command help use /edlb help");
          sender.sendMessage("");
          sender.sendMessage("§e-------------------------------");
          return true;
        }
        if (args[0].equalsIgnoreCase("help"))
        {
          if (p.hasPermission("edlb.cmd.help"))
          {
            sender.sendMessage("");
            sender.sendMessage("§7/edlb help §e(Show this help page)");
            sender.sendMessage("§7/edlb spawn §e(Spawn commands)");
            sender.sendMessage("§7/edlb arena §e(Arena commands)");
            sender.sendMessage("§7/ultragrapple grapple §e(Turn on/off grapple for you)");
            sender.sendMessage("§7/ultragrapple reload §e(Reloads the plugin)");
            sender.sendMessage("");
            return true;
          }
          p.sendMessage(prefix + getLang().getString("noPermission").replace("&", "§"));
          return true;
        }
        double x;
        if (args[0].equalsIgnoreCase("spawn"))
        {
          if (args.length == 1)
          {
            if (p.hasPermission("edlb.cmd.spawn"))
            {
              sender.sendMessage("");
              sender.sendMessage("§7/edlb spawn set <x> <y> <z> <pitch> <yaw> §e(Set spawn location)");
              sender.sendMessage("§7/edlb spawn tp §e(Teleport to the spawn)");
              sender.sendMessage("");
              return true;
            }
            p.sendMessage(prefix + getLang().getString("noPermission"));
            return true;
          }
          if (p.hasPermission("edlb.cmd.spawn"))
          {
            if (args[1].equalsIgnoreCase("set"))
            {
              if (p.hasPermission("edlb.cmd.spawn.set"))
              {
                if (args.length == 7)
                {
                  if ((!isNumeric(args[2])) || (!isNumeric(args[3])) || (!isNumeric(args[4])) || (!isNumeric(args[5])) || (!isNumeric(args[6])))
                  {
                    p.sendMessage(prefix + getLang().getString("notaNumber").replace("&", "§"));
                    return true;
                  }
                  double x1 = Integer.valueOf(args[2]);
                  
                  double y = Integer.valueOf(args[3]);
                  
                  double z = Integer.valueOf(args[4]);
                  
                  float pitch = Integer.valueOf(args[5]);
                  
                  float yaw = Integer.valueOf(args[6]);
                  
                  spwl = new Location(p.getWorld(), x1, y, z, yaw, pitch);
                  p.sendMessage(prefix + getLang().getString("spawnSet").replace("&", "§"));
                  
                  getData().set("spawnlX", x1);
                  getData().set("spawnlY", y);
                  getData().set("spawnlZ", z);
                  getData().set("spawnlYW", yaw);
                  getData().set("spawnlPI", pitch);
                  getData().set("spawnW", p.getWorld().getName());
                  this.locationSet = true;
                  return true;
                }
                if (args.length > 7)
                {
                  sender.sendMessage("");
                  sender.sendMessage("§7/edlb spawn set <x> <y> <z> <pitch> <yaw> §e(Set spawn location)");
                  sender.sendMessage("§7/edlb spawn tp §e(Teleport to the spawn)");
                  sender.sendMessage("");
                  return true;
                }
                if (args.length == 2)
                {
                  x = p.getLocation().getX();
                  
                  double y = p.getLocation().getY();
                  
                  double z = p.getLocation().getZ();
                  
                  float pitch = p.getLocation().getPitch();
                  
                  float yaw = p.getLocation().getYaw();
                  
                  spwl = new Location(p.getWorld(), x, y, z, yaw, pitch);
                  getData().set("spawnlX", x);
                  getData().set("spawnlY", Double.valueOf(y));
                  getData().set("spawnlZ", Double.valueOf(z));
                  getData().set("spawnlYW", Float.valueOf(yaw));
                  getData().set("spawnlPI", Float.valueOf(pitch));
                  getData().set("spawnW", p.getWorld().getName().toString());
                  p.sendMessage(prefix + getLang().getString("spawnSet").replace("&", "§"));
                  this.locationSet = true;
                  
                  return true;
                }
              }
              else
              {
                p.sendMessage(prefix + getLang().getString("noPermission").replace("&", "§"));
                return true;
              }
            }
            else if (args[1].equalsIgnoreCase("tp")) {
              if (p.hasPermission("edlb.cmd.spawn.tp"))
              {
                if (spwl == null)
                {
                  p.sendMessage(String.valueOf(getLang().get("chatPrefix")).replace("&", "§") + " " + getLang().getString("spawnNotSet").replace("&", "§"));
                }
                else
                {
                  p.sendMessage(prefix + getLang().getString("tpSpawn").replace("&", "§"));
                  p.teleport(spwl);
                  return true;
                }
              }
              else
              {
                p.sendMessage(prefix + getLang().getString("noPermission").replace("&", "§"));
                return true;
              }
            }
          }
          else
          {
            p.sendMessage(prefix + getLang().getString("noPermission").replace("&", "§"));
            return true;
          }
        }
        else if (args[0].equalsIgnoreCase("arena"))
        {
          if (p.hasPermission("edlb.cmd.arena"))
          {
            if (args.length == 1)
            {
              sender.sendMessage(" ");
              sender.sendMessage("§7/edlb arena §e(Shows this help page)");
              sender.sendMessage("§7/edlb arena create <name> §e(Creates a new arena)");
              sender.sendMessage("§7/edlb arena set <argument> <value> §e(Execute this command for more info)");
              sender.sendMessage("§7/edlb arena remove <name> §e(Delete a arena)");
              sender.sendMessage(" ");
              return true;
            }
            if (args.length == 2)
            {
              if (args[1].equalsIgnoreCase("create"))
              {
                sender.sendMessage(prefix + "§7Usage: /edlb arena create <name>");
                return true;
              }
            }
            else if (args.length > 2)
            {
              if ((args.length == 3) && 
                (args[1].equalsIgnoreCase("create")))
              {
                if (!this.as.getSetups().containsKey(p))
                {
                  sender.sendMessage(prefix + getLang().getString("startingSetup").replace("&", "§"));
                  
                  TimeOut t5 = new TimeOut(this.as, (Player)sender, this);
                  
                  t5.runTaskLater(plugin, 900L);
                  
                  this.as.createSetup(this.as.sCount, p, args[2], t5);    
                  for (Player p1 : Bukkit.getOnlinePlayers()) {
                    p1.hidePlayer(p);
                  }
                  p.setGameMode(GameMode.CREATIVE);
                  
                  sender.sendMessage(" ");
                  p.sendMessage(getLang().getString("Step1").replace("&", "§"));
                  p.playSound(p.getLocation(), Sound.LEVEL_UP, 100.0F, 1.0F);
                  return true;
                }
                p.sendMessage(prefix + getLang().getString("arenaSetupAlredy").replace("&", "§"));
                return true;
              }
              return true;
            }
          }
          else
          {
            p.sendMessage(prefix + getLang().getString("noPermission").replace("&", "§"));
            return true;
          }
        }
      }
    }
    else if ((cmd.getName().equalsIgnoreCase("edlb")) || (cmd.getName().equalsIgnoreCase("rftb")))
    {
      String prefix = getLang().getString("chatPrefix").replace("&", "§") + "§r ";
      sender.sendMessage(prefix + " " + getLang().getString("onlyPlayerExecutable").replace("&", "§"));
      return true;
    }
    return false;
  }
  
  public static boolean isNumeric(String str)
  {
    NumberFormat formatter = NumberFormat.getInstance();
    ParsePosition pos = new ParsePosition(0);
    formatter.parse(str, pos);
    return str.length() == pos.getIndex();
  }
  
  class pTask
    extends BukkitRunnable
  {
    private final Main plugin;
    private final Player p;
    
    public pTask(Main instance, Player pl)
    {
      this.plugin = instance;
      this.p = pl;
    }
    
    @Override
    public void run()
    {
      if (!Main.this.canLogin.booleanValue()) {
        this.p.kickPlayer("§7[§eEDLB§7] \n §2Please wait, reloading server...");
      }
      if (!this.p.isOnline()) {
        cancel();
      }
    }
  }
  
  public static class TimeOut
    extends BukkitRunnable
  {
    ArenaSetup as1;
    Player p1;
    Main m1;
    
    public TimeOut(ArenaSetup as, Player p, Main m)
    {
      this.as1 = as;
      this.p1 = p;
      this.m1 = m;
    }
    
    @Override
    public void run()
    {
      if (!this.as1.getSetups().containsKey(this.p1))
      {
        cancel();
      }
      else
      {
        this.p1.sendMessage(this.m1.getLang().getString("chatPrefix").replace("&", "§") + "§r " + this.m1.getLang().getString("arenaSetupTOut").replace("&", "§"));
        this.p1.playSound(this.p1.getLocation(), Sound.WITHER_DEATH, 100.0F, 2.0F);
        this.as1.cancel(this.p1);
        cancel();
      }
    }
  }
  
  @EventHandler
  public void onRespawn(PlayerRespawnEvent ev)
  {
    if (spwl != null)
    {
      ev.setRespawnLocation(spwl);
      ev.getPlayer().teleport(spwl);
    }
    else
    {
      ev.getPlayer().sendMessage(String.valueOf(getLang().get("chatPrefix")).replace("&", "§") + " " + getLang().getString("spawnNotSet").replace("&", "§"));
    }
  }
  
  @EventHandler
  public void onChat(AsyncPlayerChatEvent ev)
  {
    Player p = ev.getPlayer();
    String prefix = getLang().getString("chatPrefix").replace("&", "§") + "§r ";
    if (this.as.getSetups().containsKey(p))
    {
      ev.setCancelled(true);
      
      Setup setup = (Setup)this.as.getSetups().get(p);
      try
      {
        if (setup.getStep() == 1)
        {
          if (isNumeric(ev.getMessage()))
          {
            int it = Integer.parseInt(ev.getMessage());
            if (it >= 3)
            {
              if (it <= 10)
              {
                p.sendMessage(getLang().getString("Step2").replace("&", "§"));
                setup.setMinPlayers(it);
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 100.0F, 1.0F);
                setup.nextStep();
              }
              else
              {
                p.sendMessage(prefix + getLang().getString("shouldBeBetween").replace("&", "§").replace("$1", "3").replace("$2", "10"));
                p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
              }
            }
            else
            {
              p.sendMessage(prefix + getLang().getString("shouldBeBetween").replace("&", "§").replace("$1", "3").replace("$2", "10"));
              p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
            }
          }
          else if (!ev.getMessage().equalsIgnoreCase("cancel"))
          {
            p.sendMessage(prefix + getLang().getString("notaNumber").replace("&", "§"));
          }
        }
        else if (setup.getStep() == 2)
        {
          if (isNumeric(ev.getMessage()))
          {
            int it = Integer.parseInt(ev.getMessage());
            if (it >= setup.getMinPlayers())
            {
              if (it <= 15)
              {
                p.sendMessage(getLang().getString("Step3").replace("&", "§"));
                
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 100.0F, 1.0F);
                setup.setMaxPlayers(it);
                setup.nextStep();
              }
              else
              {
                p.sendMessage(prefix + getLang().getString("shouldBeBetween").replace("&", "§").replace("$1", String.valueOf(setup.getMinPlayers())).replace("$2", "15"));
                p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
              }
            }
            else
            {
              p.sendMessage(prefix + getLang().getString("shouldBeBetween").replace("&", "§").replace("$1", String.valueOf(setup.getMinPlayers())).replace("$2", "15"));
              p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
            }
          }
          else if (!ev.getMessage().equalsIgnoreCase("cancel"))
          {
            p.sendMessage(prefix + getLang().getString("notaNumber").replace("&", "§"));
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
          }
        }
        else if (setup.getStep() == 3)
        {
          if (isNumeric(ev.getMessage()))
          {
            int it = Integer.parseInt(ev.getMessage());
            if (it >= 2)
            {
              if (it <= 5)
              {
                p.sendMessage(getLang().getString("Step4").replace("&", "§"));
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 100.0F, 1.0F);
                setup.setGameDuration(it);
                setup.nextStep();
              }
              else
              {
                p.sendMessage(prefix + getLang().getString("shouldBeBetween").replace("&", "§").replace("$1", "2").replace("$2", "5"));
                p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
              }
            }
            else
            {
              p.sendMessage(prefix + getLang().getString("shouldBeBetween").replace("&", "§").replace("$1", "2").replace("$2", "5"));
              p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
            }
          }
          else if (!ev.getMessage().equalsIgnoreCase("cancel"))
          {
            p.sendMessage(prefix + getLang().getString("notaNumber").replace("&", "§"));
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
          }
        }
        else if (setup.getStep() == 4)
        {
          if ((ev.getMessage().equalsIgnoreCase("set 1")) && 
            (!ev.getMessage().equalsIgnoreCase("cancel")))
          {
            Location location = p.getLocation();
            setup.setSpawnLocation(location);
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 100.0F, 1.0F);
            p.sendMessage(getLang().getString("Step5").replace("&", "§"));
            setup.nextStep();
          }
        }
        else if (setup.getStep() == 5)
        {
          if ((ev.getMessage().equalsIgnoreCase("set 2")) && 
            (!ev.getMessage().equalsIgnoreCase("cancel")))
          {
            Location location = p.getLocation();
            setup.setBeastLocation(location);
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 100.0F, 1.0F);
            p.sendMessage(getLang().getString("Step6").replace("&", "§"));
            setup.nextStep();
          }
        }
        else if ((setup.getStep() == 6) && 
          (ev.getMessage().equalsIgnoreCase("set 3")) && 
          (!ev.getMessage().equalsIgnoreCase("cancel")))
        {
          Location location = p.getLocation();
          setup.setGameLocation(location);
          p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 100.0F, 2.0F);
          p.sendMessage(prefix + getLang().getString("arenaSetupEnd").replace("&", "§"));
          p.sendMessage(prefix + getLang().getString("arenaSetupEnd2").replace("&", "§").replace("$name", setup.name));
          setup.end(p);
        }
        if (ev.getMessage().equalsIgnoreCase("cancel"))
        {
          this.as.cancel(p);
          p.sendMessage(prefix + getLang().getString("arenaSetupCancelled").replace("&", "§"));
          p.playSound(p.getLocation(), Sound.VILLAGER_DEATH, 100.0F, 1.0F);
          for (Player p1 : Bukkit.getOnlinePlayers()) {
            p1.showPlayer(p);
          }
        }
      }
      catch (NumberFormatException ex)
      {
        Location location;
        p.sendMessage(prefix + getLang().getString("notAInt").replace("&", "§"));
        p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
      }
    }
  }
  
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent ev)
  {
    Player p = ev.getPlayer();
    if (this.as.setups.containsKey(p))
    {
      ev.setCancelled(true);
      p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
  }
  
  @EventHandler
  public void onBlockDestroy(BlockBreakEvent ev)
  {
    Player p = ev.getPlayer();
    if (this.as.setups.containsKey(p))
    {
      ev.setCancelled(true);
      p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
    else if(!(ev.getBlock() instanceof Sign)) {}
  }
  
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent ev)
  {
    if (((ev.getEntity() instanceof Player)) && 
      (this.as.setups.containsKey((Player)ev.getEntity()))) {
      ev.setCancelled(true);
    }
    if (((ev.getDamager() instanceof Player)) && 
      (this.as.setups.containsKey((Player)ev.getDamager())))
    {
      ev.setCancelled(true);
      Player p3 = (Player)ev.getDamager();
      p3.playSound(ev.getDamager().getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
  }
  
  @EventHandler
  public void onEntityDamageByBlock(EntityDamageByBlockEvent ev)
  {
    if (((ev.getEntity() instanceof Player)) && 
      (this.as.setups.containsKey((Player)ev.getEntity()))) {
      ev.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onEntityDamage(EntityDamageEvent ev)
  {
    if ((ev.getEntity() instanceof Player))
    {
      Player p1 = (Player)ev.getEntity();
      if (this.as.getSetups().containsKey(p1)) {
        ev.setCancelled(true);
      }
    }
  }
  
  @EventHandler
  public void onGameModeChange(PlayerGameModeChangeEvent ev)
  {
    Player p1 = ev.getPlayer();
    if (this.as.getSetups().containsKey(p1))
    {
      ev.setCancelled(true);
      p1.setGameMode(GameMode.CREATIVE);
      p1.playSound(p1.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
  }
  
  @EventHandler
  public void onBucketFill(PlayerBucketFillEvent ev)
  {
    Player p1 = ev.getPlayer();
    if (this.as.getSetups().containsKey(p1))
    {
      ev.setCancelled(true);
      p1.playSound(p1.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
  }
   
  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent ev)
  {
    Player p1 = ev.getPlayer();
    if (this.as.getSetups().containsKey(p1))
    {
      ev.setCancelled(true);
      p1.playSound(p1.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
  }
  
  @EventHandler
  public void onBedEnter(PlayerBedEnterEvent ev)
  {
    Player p1 = ev.getPlayer();
    if (this.as.getSetups().containsKey(p1))
    {
      ev.setCancelled(true);
      p1.playSound(p1.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
  }
  
  @EventHandler
  public void onDropItem(PlayerDropItemEvent ev)
  {
    Player p1 = ev.getPlayer();
    if (this.as.getSetups().containsKey(p1))
    {
      ev.setCancelled(true);
      p1.playSound(p1.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent ev)
  {
    Player p1 = ev.getPlayer();
    if (this.as.getSetups().containsKey(p1))
    {
      ev.setCancelled(true);
      p1.playSound(p1.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
  }
  
  @EventHandler
  public void onPortal(PlayerPortalEvent ev)
  {
    Player p1 = ev.getPlayer();
    if (this.as.getSetups().containsKey(p1))
    {
      ev.setCancelled(true);
      p1.playSound(p1.getLocation(), Sound.ENDERDRAGON_GROWL, 100.0F, 2.0F);
    }
  }
  
  @EventHandler
  public void onPickupItem(PlayerPickupItemEvent ev)
  {
    Player p1 = ev.getPlayer();
    if (this.as.getSetups().containsKey(p1)) {
      ev.setCancelled(true);
    }
  }
  
  public ArenaManager getArenaManager()
  {
    return this.am;
  }
}
