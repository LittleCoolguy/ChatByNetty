package GroupChat_Netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @description:
 * @author: XiaoGao
 * @time: 2022/5/3 14:48
 */
public class GroupChatClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    //从服务器拿到的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
//        System.out.println(msg.trim());
        String s = new String(msg.getContent(), "UTF-8");
        Content content = JSONObject.parseObject(s, Content.class);
        String uid = content.getUid() == null ? "server" : content.getUid().toString();
        System.out.println(uid + " : " + content.getMsg());
    }
}