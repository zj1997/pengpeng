package cn.pengpeng.mapper;


import cn.pengpeng.pojo.MyFriends;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MyFriendsMapper{

    String tableName="my_friends";

    String Field=" id,my_user_id,my_friend_user_id";


    @Select({"select ",Field," from ",tableName," " +
            "where my_user_id=#{myUserId} and my_friend_user_id=#{myFriendUserId}"})
    public MyFriends queryMyfriendByEachId(@Param("myUserId") String myUserId
                                          ,@Param("myFriendUserId")String myFriendUserId);


    @Insert({"insert into ",tableName," values(#{id},#{myUserId},#{myFriendUserId})"})
    public int insertMyfriend(MyFriends myFriends);



    @Select({"select ",Field," from ",tableName," " +
            "where my_user_id=#{myUserId}"})
    public List<MyFriends> queryMyfriendByMyUserId(@Param("myUserId") String myUserId);

}