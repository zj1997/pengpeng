package cn.pengpeng.netty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cn.pengpeng.SpringUtil;
import cn.pengpeng.enumBean.MsgActionEnum;
import cn.pengpeng.pojo.ChatMsg;
import cn.pengpeng.service.UserService;
import cn.pengpeng.service.serviceImpl.UserServiceImp;
import cn.pengpeng.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import javax.xml.crypto.Data;

/**
 * 
 * @Description: 处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	// 用于记录和管理所有客户端的channle
	private static ChannelGroup users =
			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) 
			throws Exception {

           String content = msg.text();  //客户端传来的消息

           Channel currentChannel = ctx.channel();

           //1.获取客户端发来的信息
           DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
           
           //根据客户端发来消息的类型不同进行不同的逻辑出路
           Integer action = dataContent.getAction();

           if(action == MsgActionEnum.CONNECT.type){
           //当websocket第一次open的时候  初始化channel 并且将channel 和 userId联系在一起
               String senderId = dataContent.getChatMessage().getSenderId();

               UserChannelRel.set(senderId,currentChannel);

           }else if(action == MsgActionEnum.CHAT.type){
            //聊天类型 将聊天记录保存在数据库中  同时标记签收状态为【未签收】

               ChatMessage chatMessage = dataContent.getChatMessage();

               String senderId = chatMessage.getSenderId();
               String message = chatMessage.getMsg();
               String reciverId = chatMessage.getReciverId();

               UserService userService = (UserService)SpringUtil.getBean("userServiceImp");

               String msgId = userService.saveChatMsg(chatMessage);

               chatMessage.setMsgId(msgId);

               DataContent dataContentMsg = new DataContent();

               Channel reciverChannel = UserChannelRel.get(reciverId);

               if(reciverChannel == null){
                   //处于离线状态
               }else{
                 //若reciverChannel不为空 查看channelGroup中是否存在channel
                   Channel channel = users.find(reciverChannel.id());

                   if(channel!=null){
                       //在线状态
                       channel.writeAndFlush(
                               new TextWebSocketFrame(
                                       JsonUtils.objectToJson(dataContentMsg)));
                   }else {
                       //离线状态
                   }

               }


           }else if(action == MsgActionEnum.SIGNED.type){
               //签收状态
               String msgIdStr = dataContent.getExtand();
               String[] msgIds = msgIdStr.split(".");

               List<String> MsgIdlist = new ArrayList<>();

               for(String msgId: msgIds){
                   if(StringUtils.isNotBlank(msgId)) {
                       MsgIdlist.add(msgId);
                   }
               }

               UserService userService = (UserService)SpringUtil.getBean("userServiceImp");

               if(MsgIdlist!=null && !MsgIdlist.isEmpty() && MsgIdlist.size() >0){

                   userService.updateMsgFlag(MsgIdlist);

               }

           }else if(action == MsgActionEnum.KEEPALIVE.type){
                //心跳机制
           }

    }

	/**
	 * 当客户端连接服务端之后（打开连接）
	 * 获取客户端的channle，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
		users.remove(ctx.channel());
	}


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	    //发生异常之后，需要抛出异常信息  关闭channel 并且移除channelGroup
	    cause.printStackTrace();

        ctx.channel().close();

        users.remove(ctx.channel());
    }
}
