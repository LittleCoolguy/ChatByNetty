package GroupChat_Netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description:
 * @author: XiaoGao
 * @time: 2022/5/3 14:47
 */


public class GroupChatServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    //这样写还要自己遍历Channel
    //public static List<Channel> channels = new ArrayList<Channel>();

    //使用一个hashmap 管理私聊（私聊本案例并未实现，只是提供个思路）
    //public static Map<String, Channel> channels = new HashMap<String,Channel>();

    //定义一个channle 组，管理所有的channel
    //GlobalEventExecutor.INSTANCE) 是全局的事件执行器，是一个单例
    private static ChannelGroup  channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static Map<Long, Channel> channelMap = new HashMap<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


//    //handlerAdded 表示连接建立，一旦连接，第一个被执行
//    //将当前channel 加入到  channelGroup
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        Channel channel = ctx.channel();
//        //将该客户加入聊天的信息推送给其它在线的客户端
//
//        //该方法会将 channelGroup 中所有的channel 遍历，并发送消息，我们不需要自己遍历
//
//        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天" + sdf.format(new java.util.Date()) + " \n");
////        channelGroup.add(channel);
//
//        //私聊如何实现
////         channels.put（"userid100",channel）;
//    }

    //断开连接, 将xx客户离开信息推送给当前在线的客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 离开了\n");
        System.out.println("channelGroup size: " + channelGroup.size());

    }

    //表示channel 处于活动状态, 提示 xx上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        double random = Math.random();
        Long userID = (long)(random * 100);
        channelMap.put(userID, ctx.channel());
        channelGroup.add(ctx.channel()); //todo
        Content content = new Content();
        content.setMsg("[客户端]" + ctx.channel().remoteAddress() + " 加入聊天" + sdf.format(new java.util.Date()) + " \n");
        MessageProtocol messageProtocol = new MessageProtocol(content);
        channelGroup.writeAndFlush(messageProtocol);
        //这个是给服务端看的，客户端上面已经提示xxx加入群聊了
        System.out.println(ctx.channel().remoteAddress() + " 上线了，ID为：" + userID);
        content.setMsg("您的userId为：" + userID);
        messageProtocol = new MessageProtocol(content);
        ctx.channel().writeAndFlush(messageProtocol);
    }

    //表示channel 处于不活动状态, 提示 xx离线了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        System.out.println(ctx.channel().remoteAddress() + " 离线了~");
    }

    //读取数据，转发给在线的每一个客户端
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol buf) throws Exception {

//        //获取到当前channel
//        Channel channel = ctx.channel();
//        //这时我们遍历channelGroup, 根据不同的情况，回送不同的消息
//
//        channelGroup.forEach(ch -> {
//            if(channel != ch) { //不是当前的channel,转发消息
//                ch.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息" + buf.toString() + "\n");
//            }else {//回显自己发送的消息给自己
//                ch.writeAndFlush("[自己]发送了消息" + buf.toString() + "\n");
//            }
//        });
        String s = new String(buf.getContent(), "UTF-8");
        Content content = JSONObject.parseObject(s, Content.class);
        if (content.getUid() != null && content.getUid().equals(0l)) {
            Iterator<Channel> iterator = channelGroup.iterator();
            //是群发
            content.setUid(0l);
            content.setMsg("群聊消息：" + content.getMsg());
            MessageProtocol groupMsg = new MessageProtocol(content);
            while (iterator.hasNext()) {
                Channel next = iterator.next();
                if (next != ctx.channel()) {
                    next.writeAndFlush(groupMsg);
                }
            }
        }
        else {
            Channel channel = channelMap.get(content.getUid());
            content.setUid(12l);
            MessageProtocol messageProtocol = new MessageProtocol(content);
            channel.writeAndFlush(messageProtocol);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭通道
        ctx.close();
    }
}

