package com.arclighttw.tinyreactors.container;

import com.arclighttw.tinyreactors.tiles.TileEntityReactorEnergyPort;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.util.EnumFacing;

public class ContainerReactorEnergyPort extends Container
{
	public final TileEntityReactorEnergyPort energyPort;
	
	int currentInput, currentOutput;
	int energy, capacity;
	
	public ContainerReactorEnergyPort(InventoryPlayer player, TileEntityReactorEnergyPort energyPort)
	{
		this.energyPort = energyPort;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(energyPort.getPos()) <= 64.0;
	}
	
	@Override
	public void detectAndSendChanges()
	{
		for(IContainerListener listener : listeners)
		{
			if(currentInput != energyPort.currentInput)
				listener.sendWindowProperty(this, 0, energyPort.currentInput);
			
			if(currentOutput != energyPort.currentOutput)
				listener.sendWindowProperty(this, 1, energyPort.currentOutput);
			
			if(energy != energyPort.getEnergyStored(EnumFacing.NORTH))
				listener.sendWindowProperty(this, 2, energyPort.getEnergyStored(EnumFacing.NORTH));
			
			if(capacity != energyPort.getMaxEnergyStored(EnumFacing.NORTH))
				listener.sendWindowProperty(this, 3, energyPort.getMaxEnergyStored(EnumFacing.NORTH));
		}
		
		currentInput = energyPort.currentInput;
		currentOutput = energyPort.currentOutput;
		energy = energyPort.getEnergyStored(EnumFacing.NORTH);
		capacity = energyPort.getMaxEnergyStored(EnumFacing.NORTH);
	}
	
	@Override
	public void updateProgressBar(int id, int data)
	{
		switch(id)
		{
		case 0:
			energyPort.currentInput = data;
			break;
		case 1:
			energyPort.currentOutput = data;
			break;
		case 2:
			energyPort.setEnergyStored(data);
			break;
		case 3:
			energyPort.setMaxEnergyStored(data);
			break;
		}
	}
}
