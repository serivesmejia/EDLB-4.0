package io.github.serivesmejia.EDLB.Arena.Setup;

import io.github.serivesmejia.EDLB.Main;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaSetup
{
  Main main;
  public HashMap<Player, Setup> setups = new HashMap();
  public int sCount = 0;
  
  public ArenaSetup(Main m)
  {
    this.main = m;
  }
  
  public void createSetup(int id, Player p1, String n, BukkitRunnable t1){
    Setup setup1 = new Setup(this.main, p1, id, n, t1);
    this.setups.put(p1, setup1);
    this.sCount += 1;
  }
  
  public HashMap getSetups()
  {
    return this.setups;
  }
  
  public void cancel(Player p)
    throws NullPointerException
  {
    if (this.setups.containsKey(p)) {
        setups.get(p).t1.cancel();
        this.setups.remove(p);
    } else {
      throw new NullPointerException("The player isn't in a arena setup");
    }
  }
}
