package cn.pengpeng.controller;


import cn.pengpeng.enumBean.OperatorFriendRequestTypeEnum;
import cn.pengpeng.enumBean.SearchFriendsStatusEnum;
import cn.pengpeng.mapper.UsersMapper;
import cn.pengpeng.pojo.BO.UsersBO;
import cn.pengpeng.pojo.MyFriends;
import cn.pengpeng.pojo.Users;
import cn.pengpeng.pojo.VO.FriendRequestVO;
import cn.pengpeng.pojo.VO.MyFriendsVO;
import cn.pengpeng.pojo.VO.UsersVO;
import cn.pengpeng.service.UserService;
import cn.pengpeng.utils.FileService;
import cn.pengpeng.utils.FileUtils;
import cn.pengpeng.utils.JSONResult;
import com.fasterxml.jackson.annotation.JsonRawValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * @author zhaojie
 * @date 2018\12\8 0008 - 14:23
 */
@RequestMapping("/u")
@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/registerOrLogin")
    public JSONResult RegisterOrLogin(@RequestBody Users user){

        Users userResult = null;

        //判断用户名和密码不能为空
       if(StringUtils.isBlank(user.getUsername())
               ||StringUtils.isBlank(user.getPassword())){
           return JSONResult.errorMsg("用户名或密码不能为空");
       }

       //判断用户是否存在
        boolean isExist = userService.queryUsernameIsExist(user);

       if(isExist){
           //登录
           userResult = userService.loginForUser(user);

           if(userResult==null){
               return JSONResult.errorMsg("密码错误，请重新输入！");
           }

       }else {
           //注册
           user.setFaceImage("");
           user.setFaceImageBig("");

           userResult = userService.registerForUser(user);

       }

        return JSONResult.ok(resolveImg(userResult));

    }


    @PostMapping("/uploadFaceBase64")
    public JSONResult uploadFace(@RequestBody UsersBO usersBO) throws Exception {


        // 获取前端传过来的base64字符串, 然后转换为文件对象再上传
        String base64Data = usersBO.getFaceData();

        String userFacePath = "D:\\" + usersBO.getUserId() + "userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);

        File faceFile =new File(userFacePath);

        //TODO对上传的图片进行裁剪，文件太大影响用户体验度


        String uploadPath = FileService.upload(faceFile, UUID.randomUUID().toString());

        // 更细用户头像
        Users user = new Users();
        user.setId(usersBO.getUserId());
        user.setFaceImage(uploadPath);
        user.setFaceImageBig(uploadPath);

        Users result = userService.updateUserInfo(user);

        return JSONResult.ok(resolveImg(result));

    }


    @PostMapping("/setNickname")
    public JSONResult updateNickName(@RequestBody UsersBO usersBO){

        Users user = new Users();

        user.setId(usersBO.getUserId());
        user.setNickname(usersBO.getNickname());

        Users result = userService.updateNickName(user);

        return JSONResult.ok(resolveImg(result));
    }


    /**
     * 图片路径处理
     * @param users
     * @return
     */

    public UsersVO resolveImg(Users users){

        //vo用于返回给客户端的结果集
        UsersVO usersVO = new UsersVO();

        BeanUtils.copyProperties(users,usersVO);

        String[] strQrcode = users.getQrcode().split("m/");
        usersVO.setQrcode(strQrcode[strQrcode.length-1]);

        if(!users.getFaceImage().equals("")){
            //对图片路径进行设置，仅返回图片的名称
            String[] strFaceImg = users.getFaceImage().split("m/");
            usersVO.setFaceImage(strFaceImg[strFaceImg.length-1]);
            usersVO.setFaceImageBig(strFaceImg[strFaceImg.length-1]);
        }

        return usersVO;
    }

    @PostMapping("/addFriendRequest")
    public JSONResult addFriendRequest(@RequestParam("myUserId") String myUserId,
                                 @RequestParam("friendUsername") String friendUsername){

        //非空校验
        if(StringUtils.isBlank(myUserId)||StringUtils.isBlank(friendUsername)){
            return JSONResult.errorMsg("");
        }

        /**
         前置条件：
                 1.添加用户为空 返回[用户不存在]
                 2.添加的用户未自己  返回【不能添加自己】
                 3.添加用户已经是你的好友 返回【该用户已经是你的好友】
         */
        Integer status = userService.preCondition(myUserId,friendUsername);

        if(status.equals(SearchFriendsStatusEnum.SUCCESS.getStatus())){
               userService.sendFriendRequest(myUserId,friendUsername);
        }else {
            return JSONResult.errorMsg(SearchFriendsStatusEnum.getMsgByKey(status));
        }
        return JSONResult.ok();
    }

    @PostMapping("/search")
    public JSONResult searchUser(@RequestParam("myUserId") String myUserId,
                                 @RequestParam("friendUsername") String friendUsername){

        //非空校验
        if(StringUtils.isBlank(myUserId)||StringUtils.isBlank(friendUsername)){
            return JSONResult.errorMsg("");
        }

        /**
         前置条件：
         1.添加用户为空 返回[用户不存在]
         2.添加的用户未自己  返回【不能添加自己】
         3.添加用户已经是你的好友 返回【该用户已经是你的好友】
         */
        Integer status = userService.preCondition(myUserId,friendUsername);

        Users friendUser = null;

        if(status.equals(SearchFriendsStatusEnum.SUCCESS.getStatus())){
            friendUser = userService.queryUserInfoByUserName(friendUsername);
        }else {
            return JSONResult.errorMsg(SearchFriendsStatusEnum.getMsgByKey(status));
        }
        return JSONResult.ok(resolveImg(friendUser));
    }

    //用户接收到的朋友的申请
    @PostMapping("/queryFriendRequests")
    public JSONResult queryFriendRequestList(String userId){

        //非空校验
        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("");
        }

        List<FriendRequestVO> requestList = userService.queryFriendRequestList(userId);

        return JSONResult.ok(requestList);
    }


    @PostMapping("/operFriendRequest")
    public JSONResult operFriendRequest(String acceptUserId ,String sendUserId,
                                                             Integer operType){
        //非空校验
        if(StringUtils.isBlank(acceptUserId)||StringUtils.isBlank(sendUserId)
                                                         ||operType == null){
                 return JSONResult.errorMsg("");
        }
        //没有匹配的操作类型
        if(StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))){
              return JSONResult.errorMsg("");
        }

        if(OperatorFriendRequestTypeEnum.PASS.type.equals(operType)){
            //同意添加好友
            userService.passFriendRequest(acceptUserId,sendUserId);

        }else if(OperatorFriendRequestTypeEnum.IGNORE.type.equals(operType)){
            //忽略拒绝添加好友添加
            userService.deleteFriendRequest(acceptUserId,sendUserId);

        }

        //刷新朋友列表
        List<MyFriendsVO> myFriendsList = userService.queryMyFriends(acceptUserId);

         return JSONResult.ok(myFriendsList);
    }

    @PostMapping("/myFriends")
    public JSONResult queryMyFriend(@RequestParam("userId") String userId){

        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("");
        }

        List<MyFriendsVO> myFriendsList = userService.queryMyFriends(userId);

        return JSONResult.ok(myFriendsList);

    }

}