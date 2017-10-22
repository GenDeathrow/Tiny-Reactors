package com.arclighttw.tinyreactors.tiles;

import java.util.List;
import java.util.function.Consumer;

import com.arclighttw.tinyreactors.config.TRConfig;
import com.arclighttw.tinyreactors.init.TRBlocks;
import com.arclighttw.tinyreactors.main.TinyReactors;
import com.arclighttw.tinyreactors.managers.ReactorManager;
import com.arclighttw.tinyreactors.network.MessageReactorStateClient;
import com.arclighttw.tinyreactors.storage.EnergyStorageRF;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityReactorController extends TileEntity implements ITickable
{
	boolean isValid, isActive;
	boolean hasLoaded;
	
	int xStart, yStart, zStart;
	int xEnd, yEnd, zEnd;
	
	int availableYield, maximumYield;
	
	List<TileEntityReactorEnergyPort> energyPorts;
	EnergyStorageRF energy;
	
	public TileEntityReactorController()
	{
		energyPorts = Lists.newArrayList();
		energy = new EnergyStorageRF(1000000);
		
		checkStructure();
	}
	
	@Override
	public void update()
	{
		if(hasLoaded)
		{
			checkStructure();
			hasLoaded = true;
			return;
		}
		
		if(isActive() && energyPorts.size() > 0)
		{
			int maximumPerPort = availableYield / energyPorts.size();
			int remaining = availableYield;
			
			for(TileEntityReactorEnergyPort energyPort : energyPorts)
			{
				int expended;
				
				if(maximumPerPort == 0)
					expended = energyPort.receiveEnergy(remaining, false);
				else	
					expended = energyPort.receiveEnergy(Math.min(maximumPerPort, 1024), false);
				
				remaining -= expended;
			}
			
			if(remaining > 0)
				modifyEnergy(remaining);
		}
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(getPos(), 3, getUpdateTag());
	}
	
	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		compound.setBoolean("isValid", isValid);
		compound.setBoolean("isActive", isActive);
		
		compound.setInteger("xStart", xStart);
		compound.setInteger("yStart", yStart);
		compound.setInteger("zStart", zStart);
		
		compound.setInteger("xEnd", xEnd);
		compound.setInteger("yEnd", yEnd);
		compound.setInteger("zEnd", zEnd);
		
		compound.setInteger("availableYield", availableYield);
		compound.setInteger("maximumYield", maximumYield);

		energy.writeToNBT(compound);
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		isValid = compound.getBoolean("isValid");
		isActive = compound.getBoolean("isActive");
		
		xStart = compound.getInteger("xStart");
		yStart = compound.getInteger("yStart");
		zStart = compound.getInteger("zStart");
		
		xEnd = compound.getInteger("xEnd");
		yEnd = compound.getInteger("yEnd");
		zEnd = compound.getInteger("zEnd");
		
		availableYield = compound.getInteger("availableYield");
		maximumYield = compound.getInteger("maximumYield");
		
		energy.readFromNBT(compound);
	}
	
	@SideOnly(Side.CLIENT)
	public int getEnergyStoredScaled(int scale)
	{
		return (int)(scale * ((float)energy.getEnergyStored() / (float)energy.getMaxEnergyStored()));
	}
	
	public int getEnergyStored()
	{
		return energy.getEnergyStored();
	}
	
	public void setEnergyStored(int energy)
	{
		this.energy.setEnergyStored(energy);
	}
	
	public int getMaxEnergyStored()
	{
		return energy.getMaxEnergyStored();
	}
	
	public void setMaxEnergyStored(int capacity)
	{
		energy.setCapacity(capacity);
	}
	
	public void modifyEnergy(int energy)
	{
		if(this.energy.getEnergyStored() + energy < this.energy.getMaxEnergyStored())
		{
			this.energy.modifyEnergyStored(energy);
			return;
		}
		
		if(TRConfig.REACTOR_MELTDOWN && !world.isRemote)
		{
			switch(TRConfig.REACTOR_MELTDOWN_TYPE)
			{
			case CONTROLLER_ONLY:
				world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 250F, true);
				world.setBlockToAir(pos);
				break;
			case REACTANTS_ONLY:
				world.createExplosion(null, xStart + (xEnd - xStart), yStart + (yEnd - yStart), zStart + (zEnd - zStart), 250F, true);
				
				loopReactor((pos) -> {
					if(world.getTileEntity(pos) == null)
						world.setBlockToAir(pos);
				});
				
				world.setBlockToAir(pos);
				break;
			case CASING_ONLY:
				world.createExplosion(null, xStart + (xEnd - xStart), yStart + (yEnd - yStart), zStart + (zEnd - zStart), 250F, true);
				
				loopReactor((pos) -> {
					if(ReactorManager.isValidCasing(world.getBlockState(pos).getBlock()) && world.getBlockState(pos).getBlock() != TRBlocks.REACTOR_CONTROLLER)
						world.setBlockToAir(pos);
				});
				
				world.setBlockToAir(pos);
				
				break;
			case ENTIRE_REACTOR:
				world.createExplosion(null, xStart + (xEnd - xStart), yStart + (yEnd - yStart), zStart + (zEnd - zStart), 250F, true);
				
				loopReactor((pos) -> {
					if(world.getBlockState(pos) != TRBlocks.REACTOR_CONTROLLER)
						world.setBlockToAir(pos);
				});
				
				world.setBlockToAir(pos);
				break;
			}
		}
	}
	
	public boolean isStructureValid()
	{
		return isValid;
	}
	
	public boolean isActive()
	{
		return isValid && isActive;
	}
	
	public int getReactorTotalYield()
	{
		return isActive ? availableYield : 0;
	}
	
	public int[] getReactorBounds()
	{
		if(isValid)
			return new int[] { xEnd - xStart + 1, yStart - yEnd + 1, zEnd - zStart + 1 };
		
		return new int[] { -1, -1, -1 };
	}
	
	public void checkStructure()
	{
		if(!(world instanceof WorldServer))
			return;
		
		((WorldServer)world).addScheduledTask(() -> {
			BlockPos pos = getPos();
			Block b = null;

			while(true)
			{
				pos = pos.up();
				b = world.getBlockState(pos).getBlock();
				
				if(!ReactorManager.isValidCasing(b))
					break;
				
				yStart = pos.getY();
			}
			
			pos = getPos();
			
			while(true)
			{
				pos = pos.down();
				b = world.getBlockState(pos).getBlock();
				
				if(!ReactorManager.isValidCasing(b))
					break;
				
				yEnd = pos.getY();
			}
			
			if(yStart - yEnd < 2)
			{
				setValid(false);
				return;
			}
			
			pos = getPos();
			xStart = xEnd = pos.getX();
			zStart = zEnd = pos.getZ();
			
			EnumFacing f = null;
			
			for(EnumFacing facing : EnumFacing.HORIZONTALS)
			{
				b = world.getBlockState(pos.offset(facing)).getBlock();
				
				if(!ReactorManager.isValidCasing(b))
					continue;
				
				f = facing;
				break;
			}
			
			if(f == null)
			{
				setValid(false);
				return;
			}
			
			int x1 = xStart, z1 = zStart;
			pos = new BlockPos(xStart, yStart, zStart);
			
			while(true)
			{
				pos = pos.offset(f);
				b = world.getBlockState(pos).getBlock();
				
				if(!ReactorManager.isValidCasing(b))
					break;
				
				x1 = pos.getX();
				z1 = pos.getZ();
			}
			
			int x2 = xStart, z2 = zStart;
			f = f.getOpposite();
			
			while(true)
			{
				pos = pos.offset(f);
				b = world.getBlockState(pos).getBlock();
				
				if(!ReactorManager.isValidCasing(b))
					break;
				
				x2 = pos.getX();
				z2 = pos.getZ();
			}
			
			boolean dirChanged = false;
			pos = new BlockPos(x1, yStart, z1);
			
			for(EnumFacing facing : EnumFacing.HORIZONTALS)
			{
				b = world.getBlockState(pos.offset(facing)).getBlock();
				
				if(!ReactorManager.isValidCasing(b))
					continue;
				
				if(facing == f || facing == f.getOpposite())
					continue;
				
				f = facing;
				dirChanged = true;
				break;
			}
			
			if(!dirChanged)
			{
				setValid(false);
				return;
			}
			
			int x3 = xStart, z3 = zStart;
			
			while(true)
			{
				pos = pos.offset(f);
				b = world.getBlockState(pos).getBlock();
				
				if(!ReactorManager.isValidCasing(b))
					break;
				
				x3 = pos.getX();
				z3 = pos.getZ();
			}
			
			xStart = Math.min(Math.min(xStart, x1), Math.min(x2, x3));
			xEnd = Math.max(Math.max(xStart, x1), Math.max(x2, x3));
			
			zStart = Math.min(Math.min(zStart, z1), Math.min(z2, z3));
			zEnd = Math.max(Math.max(zStart, z1), Math.max(z2, z3));
			
			boolean isReactor = true;
			boolean hasController = false;
			boolean hasOutput = false;
			
			availableYield = 0;
			maximumYield = 0;
		
			energyPorts = Lists.newArrayList();
			
			for(int x = xStart; x <= xEnd; x++)
			{
				for(int z = zStart; z <= zEnd; z++)
				{
					for(int y = yStart; y >= yEnd; y--)
					{
						pos = new BlockPos(x, y, z);
						b = world.getBlockState(pos).getBlock();
						
						if(b == TRBlocks.REACTOR_CONTROLLER)
							hasController = true;
						
						if(b == TRBlocks.REACTOR_ENERGY_PORT)
						{
							hasOutput = true;
							energyPorts.add((TileEntityReactorEnergyPort)world.getTileEntity(pos));
						}
						
						TileEntity tile = world.getTileEntity(pos);
						
						if(tile instanceof IReactorComponent)
							((IReactorComponent)tile).setController(this);
						
						if(y == yStart || y == yEnd || (x == xStart && z == zStart) || (x == xEnd && z == zEnd) || (x == xStart && z == zEnd) || (x == xEnd && z == zStart))
						{
							if(b == Blocks.AIR || !ReactorManager.isValidStructure(b))
							{
								setValid(false);
								return;
							}
						}
						else if(x == xStart || x == xEnd || z == zStart || z == zEnd)
						{
							if(b == Blocks.AIR || !ReactorManager.isValidCasing(b))
							{
								setValid(false);
								return;
							}
						}
						else
						{
							maximumYield += ReactorManager.getMaximumReactantRate();
							
							if(b == Blocks.AIR)
								continue;
							
							if(!ReactorManager.isReactant(b))
							{
								setValid(false);
								return;
							}
							
							availableYield += ReactorManager.getReactantRate(b);
						}
					}
				}
			}
			
			if(!hasController || !hasOutput || energyPorts.size() > 1)
			{
				isReactor = false;
				availableYield = 0;
				maximumYield = 0;
			}
			
			setValid(isReactor);
		});
	}
	
	public void setValid(boolean valid)
	{
		isValid = valid;
		
		if(!isValid)
		{
			setActive(false);
			return;
		}
		
		syncServerToClient();
	}
	
	public void setActive(boolean active)
	{
		isActive = active;
		syncServerToClient();
	}
	
	void syncServerToClient()
	{
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		world.scheduleBlockUpdate(pos, getBlockType(), 0, 0);
		markDirty();
		
		if(!world.isRemote)
			TinyReactors.NETWORK.sendToAll(new MessageReactorStateClient());
	}
	
	void loopReactor(Consumer<BlockPos> action)
	{
		if(action == null)
			return;
		
		BlockPos pos = null;
		
		for(int x = xStart; x <= xEnd; x++)
		{
			for(int z = zStart; z <= zEnd; z++)
			{
				for(int y = yStart; y >= yEnd; y--)
				{
					pos = new BlockPos(x, y, z);
					action.accept(pos);
				}
			}
		}
	}
}
