package com.buuz135.industrial.utils.apihandlers.plant;

import com.buuz135.industrial.api.plant.IPlantRecollectable;
import com.buuz135.industrial.proxy.BlockRegistry;
import com.buuz135.industrial.utils.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreePlantRecollectable implements IPlantRecollectable {

    private final HashMap<BlockPos, TreeCache> treeCache;

    public TreePlantRecollectable() {
        this.treeCache = new HashMap<>();
    }

    @Override
    public boolean canBeHarvested(World world, BlockPos pos, IBlockState blockState) {
        if (treeCache.containsKey(pos)) return true;
        if (BlockUtils.isLog(world, pos)) {
            treeCache.put(pos, new TreeCache(world, pos));
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> doHarvestOperation(World world, BlockPos pos, IBlockState blockState) {
        List<ItemStack> itemStacks = new ArrayList<>();
        if (treeCache.containsKey(pos)) {
            TreeCache cache = treeCache.get(pos);
            for (int i = 0; i < BlockRegistry.cropRecolectorBlock.getTreeOperations(); ++i) {
                if (cache.getWoodCache().isEmpty() && cache.getLeavesCache().isEmpty()) break;
                if (!cache.getLeavesCache().isEmpty()) itemStacks.addAll(cache.chop(cache.getLeavesCache()));
                else itemStacks.addAll(cache.chop(cache.getWoodCache()));
            }
            if (cache.getWoodCache().isEmpty() && cache.getLeavesCache().isEmpty()) treeCache.remove(pos);
        }
        return itemStacks;
    }

    @Override
    public boolean shouldCheckNextPlant(World world, BlockPos pos, IBlockState blockState) {
        return !canBeHarvested(world, pos, blockState);
    }
}