package com.arclighttw.tinyreactors.blocks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.arclighttw.tinyreactors.tiles.IReactorComponent;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorPart;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReactorComponent extends BlockTiny implements ITileEntityProvider
{
	public BlockReactorComponent(Material material)
	{
		super(material);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile != null)
		{
			if(tile instanceof TileEntityReactorController)
			{
				try
				{
					Method setState = TileEntityReactorController.class.getDeclaredMethod("setState", Boolean.class);
					setState.setAccessible(true);
					setState.invoke(tile, false);
					setState.setAccessible(false);
				}
				catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
				{
				}
			}
			
			if(tile instanceof IReactorComponent)
				((IReactorComponent)tile).invalidateController();
		}
		
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityReactorPart();
	}
	
}
