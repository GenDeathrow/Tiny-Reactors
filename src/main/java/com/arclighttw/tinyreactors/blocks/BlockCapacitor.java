package com.arclighttw.tinyreactors.blocks;

import com.arclighttw.tinyreactors.tiles.TileEntityCapacitor;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockCapacitor extends BlockTiny implements ITileEntityProvider
{
	public BlockCapacitor()
	{
		super(Material.IRON);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityCapacitor();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		boolean shift = super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
		
		if(shift)
			return shift; 
		
		if(!world.isRemote)
		{
			TileEntity tile = world.getTileEntity(pos);
			
			if(tile == null || !(tile instanceof TileEntityCapacitor))
				return true;
			
			EnumFacing blockFacing = state.getValue(BlockTiny.FACING);
			
			TileEntityCapacitor energyPort = (TileEntityCapacitor)tile;
			player.sendMessage(new TextComponentString(String.format("Stored: %d RF; Capacity: %d RF", energyPort.getEnergyStored(blockFacing), energyPort.getMaxEnergyStored(blockFacing))));
		}
		
		return true;
	}
}
