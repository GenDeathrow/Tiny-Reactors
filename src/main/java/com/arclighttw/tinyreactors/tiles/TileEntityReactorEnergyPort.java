package com.arclighttw.tinyreactors.tiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.arclighttw.tinyreactors.blocks.BlockTiny;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityReactorEnergyPort extends TileEntityReactorPart implements IEnergyStorage, ITickable
{
	int energy;
	int capacity;
	int maxExtract;
	
	int yield;
	
	public TileEntityReactorEnergyPort()
	{
		capacity = 1024000;
		maxExtract = 1024;
	}
	
	@Override
	public void update()
	{
		if(controller == null || !controller.isValid())
		{
			receiveEnergyInternal(yield / -4);
			return;
		}
		
		if(getEnergyStored() < getMaxEnergyStored())
			receiveEnergyInternal(yield);
		
		EnumFacing facing = world.getBlockState(pos).getValue(BlockTiny.FACING);
		TileEntity tile = world.getTileEntity(getPos().offset(facing));
		
		if(tile == null || tile instanceof TileEntityReactorController || tile instanceof IReactorComponent)
			return;
		
		if(attemptForgeEnergy(tile))
			return;
		
		attemptRFEnergy(facing.getOpposite(), tile);
	}
	
	@Override
	public int extractEnergy(int extract, boolean simulate)
	{
		int amount = extract <= energy ? extract : energy;
		
		if(!simulate)
			energy -= amount;
		
		if(energy < 0)
			energy = 0;
		
		return amount;
	}
	
	@Override
	public int receiveEnergy(int receive, boolean simulate)
	{
		return 0;
	}
	
	@Override
	public int getEnergyStored()
	{
		return energy;
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return capacity;
	}
	
	@Override
	public boolean canExtract()
	{
		return true;
	}
	
	@Override
	public boolean canReceive()
	{
		return false;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		compound.setInteger("energy", energy);
		compound.setInteger("yield", yield);
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		energy = compound.getInteger("energy");
		yield = compound.getInteger("yield");
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if(capability != null && capability.getName().equals("net.minecraftforge.energy.IEnergyStorage"))
			return true;
		
		return super.hasCapability(capability, facing);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(hasCapability(capability, facing))
			return (T)this;
		
		return super.getCapability(capability, facing);
	}
	
	public void receiveEnergyInternal(int receive)
	{
		energy += receive;
		
		if(energy > capacity)
			energy = capacity;
	}
	
	public void setOutput(int yield)
	{
		this.yield = yield;
	}
	
	boolean attemptForgeEnergy(TileEntity tile)
	{
		try
		{
			Method receiveEnergy = tile.getClass().getMethod("receiveEnergy", new Class[] { int.class, boolean.class });
			
			if(receiveEnergy == null)
				return false;
			
			receiveEnergy.invoke(tile, extractEnergy(yield, false));
			return true;
		}
		catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
		}
		catch(NoSuchMethodException | SecurityException e)
		{
		}
		
		return false;
	}
	
	boolean attemptRFEnergy(EnumFacing sourceFacing, TileEntity tile)
	{
		try
		{
			Method receiveEnergy = tile.getClass().getMethod("receiveEnergy", new Class[] { EnumFacing.class, int.class, boolean.class });
			
			if(receiveEnergy == null)
				return false;
			
			receiveEnergy.invoke(sourceFacing, tile, extractEnergy(yield, false));
			return true;
		}
		catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
		}
		catch(NoSuchMethodException | SecurityException e)
		{
		}
		
		return false;
	}
}
