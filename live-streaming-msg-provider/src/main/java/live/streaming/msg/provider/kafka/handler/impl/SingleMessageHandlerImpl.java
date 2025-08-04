package live.streaming.msg.provider.kafka.handler.impl;

import com.alibaba.fastjson.JSON;
import live.streaming.im.interfaces.dto.ImMsgBody;
import live.streaming.im.interfaces.enums.AppIdEnum;
import live.streaming.im.router.interfaces.contants.ImMsgBizCodeEnum;
import live.streaming.im.router.interfaces.rpc.ImRouterRpc;
import live.streaming.living.inerfaces.dto.LivingRoomReqDTO;
import live.streaming.living.inerfaces.rpc.ILivingRoomRpc;
import live.streaming.msg.dto.MessageDTO;
import live.streaming.msg.provider.kafka.handler.MessageHandler;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SingleMessageHandlerImpl implements MessageHandler {

    @DubboReference(check = false)
    private ImRouterRpc routerRpc;

    @DubboReference(check = false)
    private ILivingRoomRpc livingRoomRpc;

  /*  public void onMsgReceive(ImMsgBody imMsgBody) {
        int bizCode = imMsgBody.getBizCode();
        // 直播间的聊天消息
        if (bizCode == ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode()) {
            MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
            //还不是直播间业务，暂时不做过多的处理

            ImMsgBody respMsgBody = new ImMsgBody();
            //这里的userId设置的是objectId，因为是发送给对方客户端
            respMsgBody.setUserId(messageDTO.getObjectId());
            respMsgBody.setAppId(AppIdEnum.LIVE_BIZ.getCode());
            respMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("senderId", messageDTO.getUserId());
            jsonObject.put("content", messageDTO.getContent());
            respMsgBody.setData(jsonObject.toJSONString());
            //将消息推送给router进行转发给im服务器
            routerRpc.sendMsg(respMsgBody);
        }
    }*/

    public void onMsgReceive(ImMsgBody imMsgBody) {
        System.out.println("SingleMessageHandlerImpl  来了 " + imMsgBody.getBizCode());

        int bizCode = imMsgBody.getBizCode();
        // 直播间的聊天消息
        if (bizCode == ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode()) {
            // 一个人发送，n个人接收
            //根据roomId去调用rpc方法查询直播间在线userId
            MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
            Integer roomId = messageDTO.getRoomId();
            LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
            reqDTO.setRoomId(roomId);
            reqDTO.setAppId(imMsgBody.getAppId());
            // 自己不用发
            List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(reqDTO).stream().filter(x -> !x.equals(imMsgBody.getUserId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(userIdList)) {
                System.out.println("[SingleMessageHandlerImpl] 要转发的userIdList为空");
                return;
            }

            List<ImMsgBody> respMsgBodies = new ArrayList<>();
            userIdList.forEach(userId -> {
                ImMsgBody respMsgBody = new ImMsgBody();
                respMsgBody.setAppId(AppIdEnum.LIVE_BIZ.getCode());
                respMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
                respMsgBody.setData(JSON.toJSONString(messageDTO));
                // 设置发送目标对象的id
                respMsgBody.setUserId(userId);
                respMsgBodies.add(respMsgBody);
            });
            // 将消息推送给router进行转发给im服务器
            routerRpc.batchSendMsg(respMsgBodies);
        }
    }
}
