package com.arclighttw.tinyreactors.managers;

import com.arclighttw.tinyreactors.client.gui.GuiReactorController;
import com.arclighttw.tinyreactors.container.ContainerReactorController;
import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiManager implements IGuiHandler
{
	public static final int REACTOR_CONTROLLER = 0;
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		
		switch(ID)
		{
		case REACTOR_CONTROLLER:
			if(tile == null || !(tile instanceof TileEntityReactorController))
				break;
			
			return new GuiReactorController(player.inventory, (TileEntityReactorController)tile);
		}
		
		return null;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		
		switch(ID)
		{
		case REACTOR_CONTROLLER:
			if(tile == null || !(tile instanceof TileEntityReactorController))
				break;
			
			return new ContainerReactorController(player.inventory, (TileEntityReactorController)tile);
		}
		
		return null;
	}
}
