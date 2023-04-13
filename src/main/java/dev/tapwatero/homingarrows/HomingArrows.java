package dev.tapwatero.homingarrows;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.mapping.PatheticMapper;
import org.patheloper.mapping.bukkit.BukkitMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public final class HomingArrows extends JavaPlugin implements Listener {



    @Override
    public void onEnable() {

        PatheticMapper.initialize(this);
        Bukkit.getPluginManager().registerEvents(this, this);

    }


    public Entity fetchTarget(Entity focus, Player player) {
        Optional<Entity> targets = focus.getNearbyEntities(150, 50, 150).stream()
                .filter(entity -> entity instanceof LivingEntity && !(entity instanceof Wolf || entity instanceof Cat || entity instanceof Parrot) && !entity.equals(((Projectile) focus).getShooter()) && !(entity instanceof Projectile))
                .min(Comparator.comparing(entity -> entity.getLocation().subtract(player.getLocation()).toVector().angle(player.getLocation().getDirection())));

        return targets.orElse(null);
    }


    @EventHandler
    public void onShoot(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();

            Entity entity = event.getEntity();
            Entity target = fetchTarget(entity, player);

            ArrayList<PathPosition> path = pathfind(entity, target);
            new HomingArrowRunnable(entity, path, target).runTaskTimer(JavaPlugin.getPlugin(HomingArrows.class), 0L, 0L);
        }

    }

    public static ArrayList<PathPosition> pathfind(Entity projectile, Entity target) {
        ArrayList<PathPosition> positions = new ArrayList<>();

        Pathfinder reusablePathfinder = PatheticMapper.newPathfinder(PathingRuleSet.createAsyncRuleSet()
                .withStrategy(BetterStrategy.class)
                .withAsync(true)
                .withAllowingDiagonal(true)
                .withAllowingFailFast(true)
                .withAllowingFallback(true)
                .withLoadingChunks(true));


        PathPosition startPOS = BukkitMapper.toPathPosition(projectile.getLocation());
        PathPosition targetPOS = BukkitMapper.toPathPosition(target.getBoundingBox().getMax().toLocation(target.getWorld()));

        CompletionStage<PathfinderResult> pathfindingResult = reusablePathfinder.findPath(startPOS, targetPOS); // This is the actual pathfinding.

        pathfindingResult.thenAcceptAsync(result -> {
            try {
                Iterable<PathPosition> positionIterable = result.getPath().getPositions();
                positionIterable.forEach(position -> {
                    positions.add(position.add(0, 0.1, 0));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });




        return positions;
    }

}




