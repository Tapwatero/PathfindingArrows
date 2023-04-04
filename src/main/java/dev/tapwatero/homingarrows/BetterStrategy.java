//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.tapwatero.homingarrows;

import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathBlockType;
import org.patheloper.api.wrapper.PathEnvironment;
import org.patheloper.api.wrapper.PathPosition;

public class BetterStrategy implements PathfinderStrategy {

    public boolean passable(PathBlock pathBlock) {
        return pathBlock.getPathBlockType() == PathBlockType.LIQUID || pathBlock.isAir();
    }

    public boolean isValid(@NonNull PathPosition position, @NonNull SnapshotManager snapshotManager) {
        return passable(snapshotManager.getBlock(position)) &&
                passable(snapshotManager.getBlock(position.add(0, 0, 1))) &&
                passable(snapshotManager.getBlock(position.add(0, 0, -1))) &&
                passable(snapshotManager.getBlock(position.add(1, 0, 0))) &&
                passable(snapshotManager.getBlock(position.add(-1, 0, 0))) &&
                passable(snapshotManager.getBlock(position.add(1, 0, 1))) &&
                passable(snapshotManager.getBlock(position.add(-1, 0, 1))) &&
                passable(snapshotManager.getBlock(position.add(1, 0, -1))) &&
                passable(snapshotManager.getBlock(position.add(-1, 0, -1))) &&
                passable(snapshotManager.getBlock(position.add(0, 1, 0)));
    }
}
