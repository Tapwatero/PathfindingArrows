package dev.tapwatero.homingarrows;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.mapping.PatheticMapper;
import org.patheloper.mapping.bukkit.BukkitMapper;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HomingArrowRunnable extends BukkitRunnable {

    private final Entity projectile;
    private final Entity target;
    private ArrayList<PathPosition> positions;
    private int pointer;

    public HomingArrowRunnable(Entity projectile, ArrayList<PathPosition> positions, Entity target) {
        this.projectile = projectile;
        this.positions = positions;
        this.target = target;
        this.pointer = 0;
    }



    public void updatePath() throws ExecutionException, InterruptedException, TimeoutException {

        positions = HomingArrows.pathfind(projectile, target);
        pointer = 0;
    }


    @Override
    public void run() {
        float interpolation = 0.175F;



        if  ((pointer >= 3 && pointer == positions.size()) || target == null ||  projectile.isOnGround() || 	projectile.isDead()) {
            this.cancel();
            return;
        }

        if (positions.isEmpty()) {
            return;
        }

       if (BukkitMapper.toLocation(positions.get(positions.size()-1)).distance(target.getLocation()) >= 10) {
            try {
                updatePath();
                return;
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }


        Vector target_vector;
        Location block_target = BukkitMapper.toLocation(positions.get(pointer));


        if ((((LivingEntity) target).hasLineOfSight(projectile) && !target.isOnGround()) || (target.getLocation().distanceSquared(projectile.getLocation()) <= 15)) {

            target_vector = target.getLocation().toVector().subtract(projectile.getLocation().toVector()).normalize();
            target_vector = target_vector.clone().multiply(1- Math.max(interpolation, 0.99)).add(target_vector.multiply(1)).multiply(1.05);
        } else {
            target_vector = block_target.toVector().add(new Vector(0, 0.5, 0)).subtract(projectile.getLocation().toVector()).normalize();
        }


        projectile.setVelocity(target_vector);



        if (projectile.getLocation().distanceSquared(block_target) <= 4) {
            pointer += 1;
        }
    }


}