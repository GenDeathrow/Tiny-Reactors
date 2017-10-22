package com.arclighttw.tinyreactors.tiles;

import com.arclighttw.tinyreactors.blocks.BlockTiny;
import com.arclighttw.tinyreactors.storage.EnergyStorageRF;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntityReactorEnergyPort extends TileEntityReactorComponent implements IEnergyProvider
{
	EnergyStorageRF energy;
	
	int yield;
	
	public TileEntityReactorEnergyPort()
	{
		energy = new EnergyStorageRF(1000000);
	}
	
	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return energy.getEnergyStored();
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
			int extracted = extractEnergy(facing, 1024, true);
			
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
		
		compound.setInteger("yield", yield);
		energy.writeToNBT(compound);
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		yield = compound.getInteger("yield");
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
