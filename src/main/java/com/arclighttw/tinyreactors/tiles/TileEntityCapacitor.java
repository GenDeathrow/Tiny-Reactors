package com.arclighttw.tinyreactors.tiles;

import com.arclighttw.tinyreactors.storage.EnergyStorageRF;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
	@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux"),
	@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux")
})
public class TileEntityCapacitor extends TileEntity implements ITickable, IEnergyReceiver, IEnergyProvider
{
	EnergyStorageRF energy;
	
	public TileEntityCapacitor()
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
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
	{
		return energy.receiveEnergy(maxReceive, simulate);
	}
	
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
	{
		return energy.extractEnergy(maxExtract, simulate);
	}
	
	@Override
	public void update()
	{
		if(energy.getEnergyStored() > 0)
		{
			for(EnumFacing facing : EnumFacing.VALUES)
			{
				TileEntity tile = world.getTileEntity(pos.offset(facing));
				
				if(tile != null && tile instanceof IEnergyReceiver)
				{
					int received = ((IEnergyReceiver)tile).receiveEnergy(facing.getOpposite(), energy.getEnergyStored(), false);	
					extractEnergy(facing, received, false);
				}
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
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
}
