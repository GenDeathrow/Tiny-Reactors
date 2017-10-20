package com.arclighttw.tinyreactors.network;

import com.arclighttw.tinyreactors.tiles.TileEntityReactorController;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageReactorStateServer implements IMessage
{
	public BlockPos pos;
	public boolean currentState;
	
	public MessageReactorStateServer()
	{
	}
	
	public MessageReactorStateServer(BlockPos pos, boolean currentState)
	{
		this.pos = pos;
		this.currentState = currentState;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		
		buf.writeBoolean(currentState);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		
		pos = new BlockPos(x, y, z);
		currentState = buf.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<MessageReactorStateServer, IMessage>
	{
		@Override
		public IMessage onMessage(MessageReactorStateServer message, MessageContext ctx)
		{
			WorldServer world = ctx.getServerHandler().player.getServerWorld();
			TileEntity tile = world.getTileEntity(message.pos);
			
			if(tile != null && tile instanceof TileEntityReactorController)
			{
				TileEntityReactorController controller = (TileEntityReactorController)tile;
				controller.setActive(!message.currentState);
			}
			
			return null;
		}
	}
}
