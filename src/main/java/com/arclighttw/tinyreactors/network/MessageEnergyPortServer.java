package com.arclighttw.tinyreactors.network;

import com.arclighttw.tinyreactors.tiles.TileEntityReactorEnergyPort;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageEnergyPortServer implements IMessage
{
	public BlockPos pos;
	public EnergyPortMode mode;
	public EnergyPortAction action;
	public int amount;
	
	public MessageEnergyPortServer()
	{
	}
	
	public MessageEnergyPortServer(BlockPos pos, EnergyPortMode mode, EnergyPortAction action, int amount)
	{
		this.pos = pos;
		this.mode = mode;
		this.action = action;
		this.amount = amount;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		
		buf.writeInt(mode.ordinal());
		buf.writeInt(action.ordinal());
		
		buf.writeInt(amount);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		
		pos = new BlockPos(x, y, z);
		mode = EnergyPortMode.values()[buf.readInt()];
		action = EnergyPortAction.values()[buf.readInt()];
		
		amount = buf.readInt();
	}
	
	public enum EnergyPortMode
	{
		Input,
		Output
	}
	
	public enum EnergyPortAction
	{
		Increase,
		Decrease
	}
	
	public static class Handler implements IMessageHandler<MessageEnergyPortServer, IMessage>
	{
		@Override
		public IMessage onMessage(MessageEnergyPortServer message, MessageContext ctx)
		{
			WorldServer world = ctx.getServerHandler().player.getServerWorld();
			TileEntity tile = world.getTileEntity(message.pos);
			
			if(tile != null && tile instanceof TileEntityReactorEnergyPort)
			{
				TileEntityReactorEnergyPort energyPort = (TileEntityReactorEnergyPort)tile;
				energyPort.modifySettings(message.mode, message.action, message.amount);
			}
			
			return null;
		}
	}
}
