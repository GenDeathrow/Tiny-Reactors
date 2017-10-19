package com.arclighttw.tinyreactors.tiles;

import com.arclighttw.tinyreactors.init.TRBlocks;
import com.arclighttw.tinyreactors.managers.ReactorManager;

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
	boolean isValid, loaded = true;
	
	int xStart, yStart, zStart;
	int xEnd, yEnd, zEnd;
	
	int availableYield, maximumYield;
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		NBTTagCompound controllerTag = new NBTTagCompound();
		controllerTag.setInteger("xStart", xStart);
		controllerTag.setInteger("yStart", yStart);
		controllerTag.setInteger("zStart", zStart);
		
		controllerTag.setInteger("xEnd", xEnd);
		controllerTag.setInteger("yEnd", yEnd);
		controllerTag.setInteger("zEnd", zEnd);
		
		controllerTag.setInteger("availableYield", availableYield);
		controllerTag.setInteger("maximumYield", maximumYield);
		
		controllerTag.setBoolean("isValid", isValid);
		
		compound.setTag("Controller", controllerTag);

		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		NBTTagCompound controllerTag = compound.getCompoundTag("Controller");
		xStart = controllerTag.getInteger("xStart");
		yStart = controllerTag.getInteger("yStart");
		zStart = controllerTag.getInteger("zStart");
		
		xEnd = controllerTag.getInteger("xEnd");
		yEnd = controllerTag.getInteger("yEnd");
		zEnd = controllerTag.getInteger("zEnd");
		
		availableYield = controllerTag.getInteger("availableYield");
		maximumYield = controllerTag.getInteger("maximumYield");
		
		setState(controllerTag.getBoolean("isValid"));
		
		if(isValid)
			loaded = false;
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 3, getUpdateTag());
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
	public void update()
	{
		if(world == null)
			return;
		
		if(loaded)
			return;
		
		checkValidity();
		loaded = true;
	}
	
	public void checkValidity(boolean returnIfValid)
	{
		if(!(world instanceof WorldServer))
			return;
		
		if(returnIfValid && isValid)
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
				setState(false);
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
				setState(false);
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
				setState(false);
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
			int outputCount = 0;
			
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
							outputCount++;
						}
						
						if(y == yStart || y == yEnd || (x == xStart && z == zStart) || (x == xEnd && z == zEnd) || (x == xStart && z == zEnd) || (x == xEnd && z == zStart))
						{
							if(b == Blocks.AIR || !ReactorManager.isValidStructure(b))
							{
								setState(false);
								return;
							}
						}
						else if(x == xStart || x == xEnd || z == zStart || z == zEnd)
						{
							if(b == Blocks.AIR || !ReactorManager.isValidCasing(b))
							{
								setState(false);
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
								setState(false);
								return;
							}
							
							availableYield += ReactorManager.getReactantRate(b);
						}
					}
				}
			}
			
			if(!hasController || !hasOutput || outputCount > 1)
			{
				isReactor = false;
				availableYield = 0;
				maximumYield = 0;
			}
			
			setState(isReactor);
		});
	}
	
	public boolean isValid()
	{
		return isValid;
	}
	
	@SideOnly(Side.CLIENT)
	public String getReactorScale(String bound)
	{
		if(bound == "x")
			return "    X: " + (xEnd - xStart + 1);
		
		if(bound == "y")
			return "    Y: " + (yStart - yEnd + 1);
		
		if(bound == "z")
			return "    Z: " + (zEnd - zStart + 1);
		
		return "";
	}
	
	@SideOnly(Side.CLIENT)
	public int getReactorOutput()
	{
		return availableYield;
	}
	
	@SideOnly(Side.CLIENT)
	public int getReactorEfficiencyScaled(int scale)
	{
		if(!isValid)
			return 0;
		
		return (int)(scale * ((float)availableYield / (float)maximumYield));
	}
	
	void checkValidity()
	{
		checkValidity(false);
	}
	
	void setState(boolean state)
	{
		boolean oldState = isValid;
		isValid = state;
		
		if(!isValid)
			availableYield = maximumYield = 0;
		
		if(world == null)
			return;
		
		if(world instanceof WorldServer && !isValid)
			ReactorManager.onControllerInvalidated((WorldServer)world, getPos());
		
		if(oldState != isValid || !loaded)
		{
			for(int x = xStart; x <= xEnd; x++)
			{
				for(int z = zStart; z <= zEnd; z++)
				{
					for(int y = yStart; y >= yEnd; y--)
					{
						TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
						
						if(tile == null)
							continue;
						
						if(tile instanceof IReactorComponent)
						{
							IReactorComponent component = (IReactorComponent)tile;
							
							if(component.getController() != null && !component.getController().getPos().equals(getPos()))
								continue;

							component.setController(isValid ? this : null);
						}
						
						if(tile instanceof TileEntityReactorEnergyPort)
							((TileEntityReactorEnergyPort)tile).setOutput(availableYield);
					}
				}
			}
			
			syncServerToClient();
		}
	}
	
	void syncServerToClient()
	{
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		world.scheduleBlockUpdate(pos, getBlockType(), 0, 0);
		markDirty();
	}
}
