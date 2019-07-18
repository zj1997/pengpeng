package cn.pengpeng.service;

import cn.pengpeng.netty.ChatMessage;
import cn.pengpeng.pojo.ChatMsg;
import cn.pengpeng.pojo.Users;
import cn.pengpeng.pojo.VO.FriendRequestVO;
import cn.pengpeng.pojo.VO.MyFriendsVO;

import java.util.List;

/**
 * @author zhaojie
 * @date 2018\12\8 0008 - 14:35
 */
public interface UserService {

    //判断用户名是否存在
    public boolean queryUsernameIsExist(Users user);

    //登录操作
    public Users loginForUser(Users user);

    //注册操作
    public Users registerForUser(Users user);
    //更新头像操作
    Users updateUserInfo(Users user);
    //更新昵称
    Users updateNickName(Users user);
    //添加好友的前置校验
    Integer preCondition(String myUserId, String friendUserName);
    //通过用户名查询用户详细信息
    Users queryUserInfoByUserName(String friendUserName);
    //发送好友请求
    void sendFriendRequest(String myUserId, String friendUsername);
    //查询请求加好友列表
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);
    //通过好友请求
    void passFriendRequest(String acceptUserId, String sendUserId);
    //拒绝忽略好友请求
    void deleteFriendRequest(String acceptUserId, String sendUserId);
    //根据userId批量查询其好友
    List<MyFriendsVO> queryMyFriends(String userId);
    //保存聊天内容
    String saveChatMsg(ChatMessage chatMessage);
    //更新消息签收状态
    void updateMsgFlag(List<String> msgIds);


}
