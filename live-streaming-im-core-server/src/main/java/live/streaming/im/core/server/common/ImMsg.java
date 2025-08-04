package live.streaming.im.core.server.common;

import live.streaming.im.interfaces.constants.ImConstants;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ImMsg implements Serializable {


    private static final long serialVersionUID = 3058791918027069769L;

    private short magic;

    private int code;

    private int len;

    private byte[] body;

    public static ImMsg build(int code, String data) {
        ImMsg imMsg = new ImMsg();
        imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
        imMsg.setCode(code);
        imMsg.setBody(data.getBytes());
        imMsg.setLen(imMsg.getBody().length);
        return imMsg;
    }
}
