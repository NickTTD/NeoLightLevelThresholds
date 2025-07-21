package com.nickttd.neolightlevels.mixin.early;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.eventhandler.Event.Result;

/**
 * Early mixin for SpawnerAnimals to allow modification of pack spawning logic (12 attempts).
 */
@Mixin(SpawnerAnimals.class)
public abstract class MixinSpawnerAnimals {

    /**
     * Overwrite to *try* to match more closely how newer minecraf versions handle mobspawns
     * 
     * @author NeoLightLevelThresholds
     * @reason Faithful vanilla mob spawning logic for compatibility and debugging.
     */
    @Overwrite
    public int findChunksForSpawning(WorldServer world, boolean spawnHostile, boolean spawnPeaceful,
        boolean spawnAnimals) {
        if (!spawnHostile && !spawnPeaceful) {
            return 0;
        } else {
            HashMap eligibleChunksForSpawning = new HashMap();
            int i;
            int k;

            for (i = 0; i < world.playerEntities.size(); ++i) {
                EntityPlayer entityplayer = (EntityPlayer) world.playerEntities.get(i);
                int j = MathHelper.floor_double(entityplayer.posX / 16.0D);
                k = MathHelper.floor_double(entityplayer.posZ / 16.0D);
                byte b0 = 8;

                for (int l = -b0; l <= b0; ++l) {
                    for (int i1 = -b0; i1 <= b0; ++i1) {
                        boolean flag3 = l == -b0 || l == b0 || i1 == -b0 || i1 == b0;
                        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(l + j, i1 + k);

                        if (!flag3) {
                            eligibleChunksForSpawning.put(chunkcoordintpair, Boolean.valueOf(false));
                        } else if (!eligibleChunksForSpawning.containsKey(chunkcoordintpair)) {
                            eligibleChunksForSpawning.put(chunkcoordintpair, Boolean.valueOf(true));
                        }
                    }
                }
            }

            HashMap<ChunkCoordIntPair, EnumMap<EnumCreatureType, Integer>> localMobCounts = new HashMap<>();
            for (Object obj : eligibleChunksForSpawning.keySet()) {
                ChunkCoordIntPair chunk = (ChunkCoordIntPair) obj;
                EnumMap<EnumCreatureType, Integer> mobCounts = new EnumMap<>(EnumCreatureType.class);
                for (EnumCreatureType type : EnumCreatureType.values()) {
                    mobCounts.put(type, 0);
                }
                localMobCounts.put(chunk, mobCounts);
            }

            List entityList = world.loadedEntityList;
            for (Object entityObj : entityList) {
                if (entityObj instanceof EntityLiving) {
                    EntityLiving entity = (EntityLiving) entityObj;
                    int chunkX = MathHelper.floor_double(entity.posX) >> 4;
                    int chunkZ = MathHelper.floor_double(entity.posZ) >> 4;
                    ChunkCoordIntPair chunk = new ChunkCoordIntPair(chunkX, chunkZ);
                    if (localMobCounts.containsKey(chunk)) {
                        EnumMap<EnumCreatureType, Integer> mobCounts = localMobCounts.get(chunk);
                        for (EnumCreatureType type : EnumCreatureType.values()) {
                            if (type.getCreatureClass()
                                .isInstance(entity)) {
                                mobCounts.put(type, mobCounts.get(type) + 1);
                                break; // Only count for the first matching type
                            }
                        }
                    }
                }
            }

            i = 0;
            ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
            EnumCreatureType[] aenumcreaturetype = EnumCreatureType.values();
            k = aenumcreaturetype.length;

            for (int k3 = 0; k3 < k; ++k3) {
                EnumCreatureType enumcreaturetype = aenumcreaturetype[k3];

                if ((!enumcreaturetype.getPeacefulCreature() || spawnPeaceful)
                    && (enumcreaturetype.getPeacefulCreature() || spawnHostile)
                    && (!enumcreaturetype.getAnimal() || spawnAnimals)
                    && world.countEntities(enumcreaturetype, true)
                        <= enumcreaturetype.getMaxNumberOfCreature() * eligibleChunksForSpawning.size() / 256) {
                    ArrayList<ChunkCoordIntPair> tmp = new ArrayList(eligibleChunksForSpawning.keySet());
                    Collections.shuffle(tmp);
                    Iterator iterator = tmp.iterator();
                    label110: while (iterator.hasNext()) {
                        ChunkCoordIntPair chunkcoordintpair1 = (ChunkCoordIntPair) iterator.next();
                        if (!((Boolean) eligibleChunksForSpawning.get(chunkcoordintpair1)).booleanValue()) {
                            EnumMap<EnumCreatureType, Integer> mobCounts = localMobCounts.get(chunkcoordintpair1);
                            int localCap = Math.max(1, enumcreaturetype.getMaxNumberOfCreature() / 17);
                            if (mobCounts != null && mobCounts.get(enumcreaturetype) >= localCap) {
                                continue;
                            }
                            // Try 3 different random centers per chunk
                            for (int centerAttempt = 0; centerAttempt < 3; ++centerAttempt) {
                                ChunkPosition chunkposition = pickRandomChunkPosition(
                                    world,
                                    chunkcoordintpair1.chunkXPos,
                                    chunkcoordintpair1.chunkZPos);
                                int centerX = chunkposition.chunkPosX;
                                int centerY = chunkposition.chunkPosY;
                                int centerZ = chunkposition.chunkPosZ;

                                if (!world.getBlock(centerX, centerY, centerZ)
                                    .isNormalCube()
                                    && world.getBlock(centerX, centerY, centerZ)
                                        .getMaterial() == enumcreaturetype.getCreatureMaterial()) {
                                    int mobsSpawnedThisPack = 0;
                                    int packSize = 1 + world.rand.nextInt(4); // 1 to 4 inclusive
                                    BiomeGenBase.SpawnListEntry spawnlistentry = null;
                                    IEntityLivingData ientitylivingdata = null;
                                    // For each mob in the pack, try up to 4 different positions
                                    for (int packAttempt = 0; packAttempt < packSize; ++packAttempt) {
                                        boolean spawned = false;
                                        for (int posAttempt = 0; posAttempt < 4 && !spawned; ++posAttempt) {
                                            int tryX = centerX + world.rand.nextInt(6) - world.rand.nextInt(6);
                                            int tryY = centerY + world.rand.nextInt(1) - world.rand.nextInt(1);
                                            int tryZ = centerZ + world.rand.nextInt(6) - world.rand.nextInt(6);
                                            if (canCreatureTypeSpawnAtLocationHelper(
                                                enumcreaturetype,
                                                world,
                                                tryX,
                                                tryY,
                                                tryZ)) {
                                                float f = (float) tryX + 0.5F;
                                                float f1 = (float) tryY;
                                                float f2 = (float) tryZ + 0.5F;
                                                if (world.getClosestPlayer((double) f, (double) f1, (double) f2, 24.0D)
                                                    == null) {
                                                    float f3 = f - (float) chunkcoordinates.posX;
                                                    float f4 = f1 - (float) chunkcoordinates.posY;
                                                    float f5 = f2 - (float) chunkcoordinates.posZ;
                                                    float f6 = f3 * f3 + f4 * f4 + f5 * f5;
                                                    if (f6 >= 576.0F) {
                                                        if (spawnlistentry == null) {
                                                            spawnlistentry = world.spawnRandomCreature(
                                                                enumcreaturetype,
                                                                tryX,
                                                                tryY,
                                                                tryZ);
                                                            if (spawnlistentry == null) {
                                                                break;
                                                            }
                                                        }
                                                        EntityLiving entityliving;
                                                        try {
                                                            entityliving = (EntityLiving) spawnlistentry.entityClass
                                                                .getConstructor(
                                                                    new Class[] { net.minecraft.world.World.class })
                                                                .newInstance(new Object[] { world });
                                                        } catch (Exception exception) {
                                                            exception.printStackTrace();
                                                            return i;
                                                        }
                                                        entityliving.setLocationAndAngles(
                                                            (double) f,
                                                            (double) f1,
                                                            (double) f2,
                                                            world.rand.nextFloat() * 360.0F,
                                                            0.0F);
                                                        Result canSpawn = ForgeEventFactory
                                                            .canEntitySpawn(entityliving, world, f, f1, f2);
                                                        if (canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT
                                                            && entityliving.getCanSpawnHere())) {
                                                            world.spawnEntityInWorld(entityliving);
                                                            if (!ForgeEventFactory
                                                                .doSpecialSpawn(entityliving, world, f, f1, f2)) {
                                                                ientitylivingdata = entityliving
                                                                    .onSpawnWithEgg(ientitylivingdata);
                                                            }
                                                            if (mobCounts != null) {
                                                                mobCounts.put(
                                                                    enumcreaturetype,
                                                                    mobCounts.get(enumcreaturetype) + 1);
                                                            }
                                                            ++mobsSpawnedThisPack;
                                                            ++i;
                                                            spawned = true;
                                                            if (mobsSpawnedThisPack >= ForgeEventFactory
                                                                .getMaxSpawnPackSize(entityliving)) {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return i;
        }
    }

    // Helper method copied from SpawnerAnimals (static in vanilla)
    private static boolean canCreatureTypeSpawnAtLocationHelper(EnumCreatureType type, World world, int x, int y,
        int z) {
        if (type.getCreatureMaterial() == Material.water) {
            return world.getBlock(x, y, z)
                .getMaterial()
                .isLiquid()
                && world.getBlock(x, y - 1, z)
                    .getMaterial()
                    .isLiquid()
                && !world.getBlock(x, y + 1, z)
                    .isNormalCube();
        } else if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
            return false;
        } else {
            Block block = world.getBlock(x, y - 1, z);
            boolean spawnBlock = block.canCreatureSpawn(type, world, x, y - 1, z);
            return spawnBlock && block != Blocks.bedrock
                && !world.getBlock(x, y, z)
                    .isNormalCube()
                && !world.getBlock(x, y, z)
                    .getMaterial()
                    .isLiquid()
                && !world.getBlock(x, y + 1, z)
                    .isNormalCube();
        }
    }

    private static ChunkPosition pickRandomChunkPosition(World world, int chunkX, int chunkZ) {
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        int k = chunkX * 16 + world.rand.nextInt(16);
        int l = chunkZ * 16 + world.rand.nextInt(16);
        int i1 = world.rand.nextInt(chunk == null ? world.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1);
        return new ChunkPosition(k, i1, l);
    }
}
