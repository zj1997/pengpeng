package cn.pengpeng.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaojie
 * @date 2018\12\16 0016 - 17:22
 */
public class UserChannelRel {

    private static Map<String ,Channel> map = new HashMap<>();

    public static void set(String userId,Channel channel){
        map.put(userId,channel);
    }

    public static Channel get(String userId){
        return map.get(userId);
    }

}
