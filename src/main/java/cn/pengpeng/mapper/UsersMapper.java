package cn.pengpeng.mapper;


import cn.pengpeng.pojo.Users;
import org.apache.ibatis.annotations.*;

public interface UsersMapper {

    String tableName="users";

    String Field=" id,username,password,face_image,face_image_big,nickname,qrcode,cid";


    //通过用户名查询用户
    @Select({"select ",Field," from ",tableName," where username= #{username}"})
    public Users queryByUsername(@Param("username") String username);

    //注册操作
    @Insert({"insert into ",tableName,"(",Field,") " +
            "values(#{id},#{username},#{password},#{faceImage},#{faceImageBig},#{nickname},#{qrcode},#{cid})"})
    public int insertUser(Users user);

    //通过id查询用户
    @Select({"select ",Field," from ",tableName," where id= #{id}"})
    public Users queryByUserId(@Param("id") String id);

    //更新用户操作头像
    @Update({"update ",tableName," set face_image=#{faceImage},face_image_big=#{faceImageBig} where id=#{id}"})
    public int updateUser(Users user);

    //更新用户操作用户名
    @Update({"update ",tableName," set nickname=#{nickname} where id=#{id}"})
    public int updateNickName(Users user);


}