package cn.pengpeng.service.serviceImpl;

import cn.pengpeng.enumBean.MsgSignFlagEnum;
import cn.pengpeng.enumBean.SearchFriendsStatusEnum;
import cn.pengpeng.mapper.ChatMsgMapper;
import cn.pengpeng.mapper.FriendsRequestMapper;
import cn.pengpeng.mapper.MyFriendsMapper;
import cn.pengpeng.mapper.UsersMapper;

import cn.pengpeng.netty.ChatMessage;
import cn.pengpeng.pojo.ChatMsg;
import cn.pengpeng.pojo.FriendsRequest;
import cn.pengpeng.pojo.MyFriends;
import cn.pengpeng.pojo.Users;
import cn.pengpeng.pojo.VO.FriendRequestVO;
import cn.pengpeng.pojo.VO.MyFriendsVO;
import cn.pengpeng.service.UserService;
import cn.pengpeng.utils.FileService;
import cn.pengpeng.utils.MD5Utils;
import cn.pengpeng.utils.QRCodeUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author zhaojie
 * @date 2018\12\8 0008 - 14:41
 */
@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;
    
    @Autowired
    private ChatMsgMapper chatMsgMapper;
    
    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;


    public boolean queryUsernameIsExist(Users user) {

        Users result = usersMapper.queryByUsername(user.getUsername());

        return result == null ? false:true;
    }



    public Users loginForUser(Users user) {

        Users result = null;

        try {

            result = usersMapper.queryByUsername(user.getUsername());

            if(MD5Utils.getMD5Str(user.getPassword()).equals(result.getPassword())){
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Transactional
    public Users registerForUser(Users user) {

        int isSuccess = 0;

        try {

            //生成唯一的id
            String userId = sid.nextShort();

            //生成二维码

            String qrcodePath="D:\\" + user.getId() + "qrcode.png";

            qrCodeUtils.createQRCode(qrcodePath,"pengpeng:"+user.getUsername());

            String qrcodeUrl = FileService.upload(new File(qrcodePath), UUID.randomUUID().toString());


            user.setQrcode(qrcodeUrl);
            user.setNickname(user.getUsername());
            user.setId(userId);
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));

            isSuccess = usersMapper.insertUser(user);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isSuccess!=0? user:null;
    }

    @Override
    public Users updateUserInfo(Users user) {

        Users usersResult = null;

        int isSuccess = usersMapper.updateUser(user);

        if(isSuccess!=0){
            usersResult = usersMapper.queryByUserId(user.getId());
        }

        return usersResult;
    }

    @Override
    public Users updateNickName(Users user) {

        Users usersResult = null;

        int isSuccess = usersMapper.updateNickName(user);

        if(isSuccess!=0){
            usersResult = usersMapper.queryByUserId(user.getId());
        }

        return usersResult;
    }


    public Integer preCondition(String myUserId, String friendUserName) {

        Users users = usersMapper.queryByUsername(friendUserName);
        //用户不存在
        if(users == null){
            return SearchFriendsStatusEnum.USER_NOT_EXIST.getStatus();
        }
        //不能添加自己
        if(users.getId().equals(myUserId)){
            return SearchFriendsStatusEnum.NOT_YOURSELF.getStatus();
        }
        //用户已经是你的好友

        MyFriends myFriends = myFriendsMapper.queryMyfriendByEachId(myUserId, users.getId());

        if(myFriends!=null){
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.getStatus();
        }

        return SearchFriendsStatusEnum.SUCCESS.getStatus();
    }

    @Override
    public Users queryUserInfoByUserName(String friendUserName) {
        return usersMapper.queryByUsername(friendUserName);
    }

    @Override
    public void sendFriendRequest(String myUserId, String friendUsername) {

        //查询要添加朋友的信息
        Users friendUser = usersMapper.queryByUsername(friendUsername);

        FriendsRequest friendsRequest = friendsRequestMapper.queryRequest(myUserId, friendUser.getId());

        if(friendsRequest == null){
            FriendsRequest request = new FriendsRequest();
            String requestId = sid.nextShort();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friendUser.getId());
            request.setRequestDateTime(new Date());

            friendsRequestMapper.insertFriendRequest(request);
        }

    }


    /**
     * 查询请求加好友列表
     * @param acceptUserId
     * @return
     */
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId){

        List<FriendsRequest> friendsRequests = friendsRequestMapper.queryRequestByAcceptId(acceptUserId);

        List<FriendRequestVO> friendRequestVOS = new ArrayList<>();

        for(FriendsRequest friendsRequest : friendsRequests){

            Users users = usersMapper.queryByUserId(friendsRequest.getSendUserId());

            FriendRequestVO friendRequestVO = new FriendRequestVO();

            friendRequestVO.setSendUserId(users.getId());
            friendRequestVO.setSendUsername(users.getUsername());

            String[] strFaceImg = users.getFaceImage().split("m/");

            friendRequestVO.setSendFaceImage(strFaceImg[strFaceImg.length-1]);
            friendRequestVO.setSendNickname(users.getNickname());

            friendRequestVOS.add(friendRequestVO);
        }

        return friendRequestVOS;
    }

    @Transactional
    public void passFriendRequest(String acceptUserId, String sendUserId) {

        SaveFriend(acceptUserId,sendUserId);
        SaveFriend(sendUserId,acceptUserId);
        deleteFriendRequest(acceptUserId,sendUserId);
    }

    @Override
    public void deleteFriendRequest(String acceptUserId, String sendUserId) {
        friendsRequestMapper.deleteFriendRequest(sendUserId, acceptUserId);
    }

    @Override
    public List<MyFriendsVO> queryMyFriends(String userId) {

        List<MyFriends> myFriendsList = myFriendsMapper.queryMyfriendByMyUserId(userId);

        List<MyFriendsVO> myFriendsVOList = new ArrayList<>();

        for(MyFriends myFriends : myFriendsList){

            Users MyFriendsUser = usersMapper.queryByUserId(myFriends.getMyFriendUserId());

            MyFriendsVO myFriendsVO = new MyFriendsVO();

            myFriendsVO.setFriendUserId(MyFriendsUser.getId());
            myFriendsVO.setFriendUsername(MyFriendsUser.getUsername());

            String[] faceImg = MyFriendsUser.getFaceImage().split("m/");

            myFriendsVO.setFriendFaceImage(faceImg[faceImg.length-1]);
            myFriendsVO.setFriendNickname(MyFriendsUser.getNickname());

            myFriendsVOList.add(myFriendsVO);
        }


        return myFriendsVOList;
    }

    @Transactional
    public String saveChatMsg(ChatMessage chatMessage) {

        ChatMsg chatMsg = new ChatMsg();

        String chatMsgId = sid.nextShort();

        chatMsg.setId(chatMsgId);
        chatMsg.setSendUserId(chatMessage.getSenderId());
        chatMsg.setAcceptUserId(chatMessage.getReciverId());
        chatMsg.setSignFlag(MsgSignFlagEnum.unsign.type);
        chatMsg.setCreateTime(new Date());
        chatMsg.setMsg(chatMessage.getMsg());

        int isSuccess = chatMsgMapper.insertChatMsg(chatMsg);

        if(0 != isSuccess){
            return chatMsgId;
        }

        return null;
    }

    /**
     * 批量更新消息状态
     * @param msgIds
     */
    @Transactional
    public void updateMsgFlag(List<String> msgIds) {

       for(String msgId : msgIds){

           chatMsgMapper.updateChatMsg(MsgSignFlagEnum.signed.type,msgId);

       }

    }

    public void SaveFriend(String acceptUserId, String sendUserId){

        MyFriends myFriends = new MyFriends();

        myFriends.setId(sid.nextShort());
        myFriends.setMyUserId(acceptUserId);
        myFriends.setMyFriendUserId(sendUserId);

        myFriendsMapper.insertMyfriend(myFriends);

    }

}