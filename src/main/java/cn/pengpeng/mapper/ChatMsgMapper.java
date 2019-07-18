package cn.pengpeng.mapper;

import cn.pengpeng.pojo.ChatMsg;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ChatMsgMapper{

    String tableName="chat_msg";

    String Field=" id,send_user_id,accept_user_id,msg,sign_flag,create_time";

    @Insert({"insert into ",tableName," values(#{id},#{sendUserId},#{acceptUserId},#{msg},#{signFlag},#{createTime})"})
    public int insertChatMsg(ChatMsg chatMsg);

    @Update({"update ",tableName," set sign_flag=#{signFlag} where id=#{id}"})
    public int updateChatMsg(@Param("signFlag") Integer signFlag,@Param("id") String id);

}