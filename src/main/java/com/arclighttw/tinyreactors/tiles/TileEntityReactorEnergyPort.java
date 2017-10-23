package com.arclighttw.tinyreactors.tiles;

import com.arclighttw.tinyreactors.blocks.BlockTiny;
import com.arclighttw.tinyreactors.network.MessageEnergyPortServer.EnergyPortAction;
import com.arclighttw.tinyreactors.network.MessageEnergyPortServer.EnergyPortMode;
import com.arclighttw.tinyreactors.storage.EnergyStorageRF;
import com.arclighttw.tinyreactors.util.Util;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityReactorEnergyPort extends TileEntityReactorComponent implements IEnergyProvider
{
	public int maxInput, maxOutput;
	public int currentInput, currentOutput;
	
	private boolean loaded;
	
	EnergyStorageRF energy;
	
	public TileEntityReactorEnergyPort()
	{
	}
	
	public TileEntityReactorEnergyPort(int maxTransfer)
	{
		maxInput = maxOutput = maxTransfer;
		currentInput = currentOutput = maxTransfer;
		
		energy = new EnergyStorageRF(1000000, currentInput, currentOutput);
	}
	
	@SideOnly(Side.CLIENT)
	public int getEnergyStoredScaled(int scale)
	{
		return (int)(scale * ((float)energy.getEnergyStored() / (float)energy.getMaxEnergyStored()));
	}
	
	public void modifySettings(EnergyPortMode mode, EnergyPortAction action, int amount)
	{
		switch(mode)
		{
		case Input:
			switch(action)
			{
			case Increase:
				currentInput += amount;
				
				if(currentInput > maxInput)
					currentInput = maxInput;
				
				break;
			case Decrease:
				currentInput -= amount;
				
				if(currentInput < 1)
					currentInput = 1;
				
				break;
			}
			
			break;
		case Output:
			switch(action)
			{
			case Increase:
				currentOutput += amount;
				
				if(currentOutput > maxOutput)
					currentOutput = maxOutput;
				
				break;
			case Decrease:
				currentOutput -= amount;
				
				if(currentOutput < 1)
					currentOutput = 1;
				
				break;
			}
			
			break;
		}
		
		energy.setMaxReceive(currentInput);
		energy.setMaxExtract(currentOutput);
		Util.syncServerToClient(world, pos, this);
	}
	
	public void setEnergyStored(int energy)
	{
		this.energy.setEnergyStored(energy);
	}
	
	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return energy.getEnergyStored();
	}
	
	public void setMaxEnergyStored(int capacity)
	{
		energy.setCapacity(capacity);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return true;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
	{
		return energy.extractEnergy(maxExtract, simulate);
	}
	
	@Override
	public void update()
	{
		if(world != null && !loaded)
		{
			Util.syncServerToClient(world, pos, this);
			loaded = true;
		}
		
		if(energy.getEnergyStored() <= 0)
			return;
		
		IBlockState block = world.getBlockState(pos);
		EnumFacing facing = null;
		
		try
		{
			facing = block.getValue(BlockTiny.FACING);
		}
		catch(IllegalArgumentException e)
		{
		}
		
		if(facing == null)
			return;
		
		TileEntity tile = world.getTileEntity(pos.offset(facing));
		
		if(tile != null && tile instanceof IEnergyReceiver)
		{
			int extracted = extractEnergy(facing, currentOutput, true);
			
			if(extracted > 0)
			{
				int received = ((IEnergyReceiver)tile).receiveEnergy(facing.getOpposite(), extracted, false);
				extractEnergy(facing, received, false);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		compound.setInteger("maxInput", maxInput);
		compound.setInteger("maxOutput", maxOutput);
		
		compound.setInteger("currentInput", currentInput);
		compound.setInteger("currentOutput", currentOutput);
		
		energy.writeToNBT(compound);
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		maxInput = compound.getInteger("maxInput");
		maxOutput = compound.getInteger("maxOutput");
		
		currentInput = compound.getInteger("currentInput");
		currentOutput = compound.getInteger("currentOutput");
		
		energy = new EnergyStorageRF(1000000, currentInput, currentOutput);
		energy.readFromNBT(compound);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY)
			return (T)energy;
		
		return super.getCapability(capability, facing);
	}
	
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return energy.receiveEnergy(maxReceive, simulate);
	}
}
