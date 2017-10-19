package com.arclighttw.tinyreactors.network;

import com.arclighttw.tinyreactors.managers.ReactorManager;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageReactorController implements IMessage
{
	public String playerName;
	public int x, y, z;
	
	public MessageReactorController()
	{
	}
	
	public MessageReactorController(EntityPlayer player, BlockPos pos)
	{
		playerName = player.getName();
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, playerName);
		
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		playerName = ByteBufUtils.readUTF8String(buf);
		
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}
	
	public static class Handler implements IMessageHandler<MessageReactorController, IMessage>
	{
		@Override
		public IMessage onMessage(MessageReactorController message, MessageContext ctx)
		{
			ReactorManager.onControllerOpened(message);
			return null;
		}
	}
}
