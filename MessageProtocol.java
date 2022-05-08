package GroupChat_Netty;

import com.alibaba.fastjson.JSONObject;

import java.nio.charset.Charset;

/**
 * @description:
 * @author: XiaoGao
 * @time: 2022/5/3 15:02
 */
public class MessageProtocol {

    private int len;
    private byte[] content;

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public MessageProtocol() {

    }

    public MessageProtocol(Content content) {
        String s = JSONObject.toJSONString(content);
        byte[] con = s.getBytes(Charset.forName("UTF-8"));
        int len = con.length;
        this.content = con;
        this.len = len;
    }

}
