package cn.pengpeng.mapper;

import cn.pengpeng.pojo.FriendsRequest;
import org.apache.ibatis.annotations.*;
import org.hibernate.validator.constraints.EAN;

import java.util.List;

public interface FriendsRequestMapper {

    String tableName="friends_request";

    String Field=" id,send_user_id,accept_user_id,request_date_time";


    @Select({"select ",Field," from ",tableName," where send_user_id=#{sendUserId} and accept_user_id=#{acceptUserId}"})
    public FriendsRequest queryRequest(@Param("sendUserId") String sendUserId,
                                       @Param("acceptUserId") String acceptUserId);

    @Insert({"insert into ",tableName," values(#{id},#{sendUserId},#{acceptUserId},#{requestDateTime})"})
    public int insertFriendRequest(FriendsRequest friendsRequest);

    @Select({"select ",Field," from ",tableName," where accept_user_id=#{acceptUserId}"})
    public List<FriendsRequest> queryRequestByAcceptId(@Param("acceptUserId") String acceptUserId);

    @Delete({"delete from",tableName," where send_user_id=#{sendUserId} and accept_user_id=#{acceptUserId}"})
    public int deleteFriendRequest(@Param("sendUserId") String sendUserId,
                                   @Param("acceptUserId") String acceptUserId);


}