package com.arclighttw.tinyreactors.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntityReactorComponent extends TileEntitySyncable implements IReactorComponent, ITickable
{
	TileEntityReactorController controller;
	
	private boolean loaded;
	private int x, z, y = -1;
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		if(controller != null)
		{
			NBTTagCompound controllerTag = new NBTTagCompound();
			controllerTag.setInteger("x", controller.getPos().getX());
			controllerTag.setInteger("y", controller.getPos().getY());
			controllerTag.setInteger("z", controller.getPos().getZ());
			
			compound.setTag("Controller", controllerTag);
		}
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		if(!compound.hasKey("Controller"))
			return;
		
		NBTTagCompound controllerTag = compound.getCompoundTag("Controller");
		x = controllerTag.getInteger("x");
		y = controllerTag.getInteger("y");
		z = controllerTag.getInteger("z");
	}
	
	@Override
	public void update()
	{
		if(world == null)
			return;
		
		if(y == -1 || loaded)
			return;
		
		loaded = true;
		
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

		if(tile != null && tile instanceof TileEntityReactorController)
			setController((TileEntityReactorController)tile);
	}
	
	@Override
	public TileEntityReactorController getController()
	{
		return controller;
	}
	
	@Override
	public void setController(TileEntityReactorController controller)
	{
		this.controller = controller;
	}
	
	@Override
	public void invalidateController()
	{
		if(controller == null)
			return;
		
		controller.setValid(false);
	}
}
