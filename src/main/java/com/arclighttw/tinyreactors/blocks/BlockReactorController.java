package com.arclighttw.tinyreactors.blocks;

import com.arclighttw.tinyreactors.main.TinyReactors;
import com.arclighttw.tinyreactors.managers.GuiManager;
import com.arclighttw.tinyreactors.managers.ReactorManager;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReactorController extends BlockReactorComponent
{
	public BlockReactorController()
	{
		super(Material.IRON);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityReactorController();
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		ReactorManager.addReactor(pos);
		super.onBlockAdded(world, pos, state);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		ReactorManager.removeReactor(world, pos);
		super.breakBlock(world, pos, state);
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
			
			if(tile != null && tile instanceof TileEntityReactorController)
			{
				((TileEntityReactorController)tile).checkStructure();
				player.openGui(TinyReactors.instance, GuiManager.REACTOR_CONTROLLER, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		
		return true;
	}
}
