package com.arclighttw.tinyreactors.container;

import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerReactorController extends Container
{
	final TileEntityReactorController controller;
	
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
	// TODO: Handle state.
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		return null;
	}
}
