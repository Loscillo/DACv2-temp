package fr.aumgn.dac2.game;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import fr.aumgn.dac2.arena.Arena;

/**
 * Listener which provides common implementations of listeners
 * for most game mode.
 */
public class GameListener implements Listener {

    private final AbstractGame game;
    private final Arena arena;

    public GameListener(AbstractGame game) {
        this.game = game;
        this.arena = game.getArena();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!game.isPlayerTurn(player)) {
            return;
        }

        if (!(arena.isIn(player.getWorld())
                && arena.getPool().contains(player))) {
            return;
        }

        game.onJumpSuccess(player);
    }

    public void onTeleport(PlayerTeleportEvent event){
    	if(game.isPlayerTurn(event.getPlayer()) && !(event.getCause().equals(TeleportCause.COMMAND)))
    		game.onJumpFail(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != DamageCause.FALL) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (!game.isPlayerTurn(player)) {
            return;
        }

        if (!(arena.isIn(player.getWorld()) && arena.getSurroundingRegion()
                .contains(player))) {
            return;
        }

        game.onJumpFail(player);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRawQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!game.isPlayerTurn(player)) {
            return;
        }

        game.onQuit(player);
    }
}
