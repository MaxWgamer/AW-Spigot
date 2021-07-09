package net.minecraft.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.Waitable;
import org.spigotmc.AsyncCatcher;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public final class MCUtil {
    private static final Executor asyncExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Paper Async Task Handler Thread - %1$d").build());

    private MCUtil() {}

    public static <T> T ensureMain(String reason, Supplier<T> run) {
        if (AsyncCatcher.enabled && Thread.currentThread() != MinecraftServer.getServer().primaryThread) {
            new IllegalStateException( "Asynchronous " + reason + "! Blocking thread until it returns ").printStackTrace();
            Waitable<T> wait = new Waitable<T>() {
                @Override
                protected T evaluate() {
                    return run.get();
                }
            };
            MinecraftServer.getServer().processQueue.add(wait);
            try {
                return wait.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
        return run.get();
    }

    public static double distance(Entity e1, Entity e2) {
        return Math.sqrt(distanceSq(e1, e2));
    }

    public static double distance(BlockPosition e1, BlockPosition e2) {
        return Math.sqrt(distanceSq(e1, e2));
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(distanceSq(x1, y1, z1, x2, y2, z2));
    }

    public static double distanceSq(Entity e1, Entity e2) {
        return distanceSq(e1.locX,e1.locY,e1.locZ, e2.locX,e2.locY,e2.locZ);
    }

    public static double distanceSq(BlockPosition pos1, BlockPosition pos2) {
        return distanceSq(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    public static double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
    }

    public static Location toLocation(World world, double x, double y, double z) {
        return new Location(world.getWorld(), x, y, z);
    }

    public static Location toLocation(World world, BlockPosition pos) {
        return new Location(world.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    public static Location toLocation(Entity entity) {
        return new Location(entity.getWorld().getWorld(), entity.locX, entity.locY, entity.locZ);
    }

    public static BlockPosition toBlockPosition(Location loc) {
        return new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static boolean isEdgeOfChunk(BlockPosition pos) {
        final int modX = pos.getX() & 15;
        final int modZ = pos.getZ() & 15;
        return (modX == 0 || modX == 15 || modZ == 0 || modZ == 15);
    }

    @Nullable
    public static Chunk getLoadedChunkWithoutMarkingActive(World world, int x, int z) {
        return ((ChunkProviderServer) world.chunkProvider).chunks.get(ChunkCoordIntPair.a(x, z));
    }

    @Nullable
    public static Chunk getLoadedChunkWithoutMarkingActive(IChunkProvider provider, int x, int z) {
        return ((ChunkProviderServer)provider).chunks.get(ChunkCoordIntPair.a(x, z));
    }

    public static void scheduleAsyncTask(Runnable run) {
        asyncExecutor.execute(run);
    }
    
}