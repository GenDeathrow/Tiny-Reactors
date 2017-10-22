package com.arclighttw.tinyreactors.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageRF implements IEnergyStorage
{
	protected int energy;
	protected int capacity;
	
	protected int maxReceive;
	protected int maxExtract;
	
	public EnergyStorageRF(int capacity)
	{
		this(capacity, capacity, capacity);
	}
	
	public EnergyStorageRF(int capacity, int maxTransfer)
	{
		this(capacity, maxTransfer, maxTransfer);
	}
	
	public EnergyStorageRF(int capacity, int maxReceive, int maxExtract)
	{
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		int received = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
		
		if(!simulate)
			energy += received;
		
		return received;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		int extracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
		
		if(!simulate)
			energy -= extracted;
		
		return extracted;
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
		return true;
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		if(energy < 0)
			energy = 0;
		
		nbt.setInteger("Energy", energy);
		return nbt;
	}
	
	public EnergyStorageRF readFromNBT(NBTTagCompound nbt)
	{
		this.energy = nbt.getInteger("Energy");
		
		if(energy > capacity)
			energy = capacity;
		
		return this;
	}
	
	public EnergyStorageRF setCapacity(int capacity)
	{
		this.capacity = capacity;
		
		if(energy > capacity)
			energy = capacity;
		
		return this;
	}
	
	public EnergyStorageRF setMaxTransfer(int maxTransfer)
	{
		setMaxReceive(maxTransfer);
		setMaxExtract(maxTransfer);
		return this;
	}
	
	public EnergyStorageRF setMaxReceive(int maxReceive)
	{
		this.maxReceive = maxReceive;
		return this;
	}
	
	public EnergyStorageRF setMaxExtract(int maxExtract)
	{
		this.maxExtract = maxExtract;
		return this;
	}
	
	public int getMaxReceive()
	{
		return maxReceive;
	}
	
	public int getMaxExtract()
	{
		return maxExtract;
	}
	
	public void setEnergyStored(int energy)
	{
		if(energy > capacity)
			energy = capacity;
		else if(energy < 0)
			energy = 0;
		
		this.energy = energy;
	}
	
	public boolean modifyEnergyStored(int energy)
	{
		int toadd = Math.min(energy, capacity - this.energy);
		setEnergyStored(this.energy + toadd);
		
		return toadd == energy;
	}
}
