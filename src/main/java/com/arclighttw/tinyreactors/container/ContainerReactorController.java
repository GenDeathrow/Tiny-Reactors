package com.arclighttw.tinyreactors.container;

import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerReactorController extends Container
{
	public final TileEntityReactorController controller;
	
	boolean isValid, isActive;
	
	public ContainerReactorController(InventoryPlayer player, TileEntityReactorController controller)
	{
		this.controller = controller;
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player, x, 8 + x * 18, 139));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player, 9 + x + y * 9, 8 + x * 18, 81 + y * 18));
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(controller.getPos()) <= 64.0;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public void detectAndSendChanges()
	{
		for(IContainerListener listener : listeners)
		{
			if(isValid != controller.isStructureValid())
				listener.sendWindowProperty(this, 0, controller.isStructureValid() ? 1 : 0);
			
			if(isActive != controller.isActive())
				listener.sendWindowProperty(this, 1, controller.isActive() ? 1 : 0);
		}
		
		isValid = controller.isStructureValid();
		isActive = controller.isActive();
	}
	
	@Override
	public void updateProgressBar(int id, int data)
	{
		switch(id)
		{
		case 0:
			controller.setValid(data == 1 ? true : false);
			break;
		case 1:
			controller.setActive(data == 1 ? true : false);
			break;
		}
	}
}
