package io.github.serivesmejia.EDLB.Task;

import io.github.serivesmejia.EDLB.Arena.ArenaManager;
import io.github.serivesmejia.EDLB.Main;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateGameSignsTask extends BukkitRunnable {

    private final Main plugin;
    private final ArenaManager am;
    
    public UpdateGameSignsTask(Main instance, ArenaManager ams) {
        plugin = instance;
        am = ams;
    }
    
    @Override
    public void run() {
        
    }
        
    }
