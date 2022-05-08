package GroupChat_Netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @description:
 * @author: XiaoGao
 * @time: 2022/5/3 15:06
 */
public class MessageDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //将二进制字节码->数据包
        int len = byteBuf.readInt();
        byte[] con = new byte[len];
        byteBuf.readBytes(con);
        MessageProtocol mp = new MessageProtocol();
        mp.setLen(len);
        mp.setContent(con);
        list.add(mp);
    }
}
