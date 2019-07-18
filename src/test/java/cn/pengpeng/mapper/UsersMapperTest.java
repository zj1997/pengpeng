package cn.pengpeng.mapper;

import ch.qos.logback.core.net.SyslogOutputStream;
import cn.pengpeng.PengpengApplicationTests;
import cn.pengpeng.pojo.Users;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author zhaojie
 * @date 2018\12\8 0008 - 15:01
 */
public class UsersMapperTest extends PengpengApplicationTests {

    @Autowired
    private UsersMapper mapper;


    @Test
    public void queryByUsername() {

        Users user = mapper.queryByUsername("zhaopjie");

        System.out.println(user.getPassword());

    }

    @Test
    public void insertUser() {

        Users u = new Users();
        u.setId("1");
        u.setUsername("zhaopjie");
        u.setPassword("123");
        u.setFaceImage("");
        u.setFaceImageBig("");
        u.setNickname("zhaojie");
        u.setQrcode("");
        u.setCid("");

        int i =mapper.insertUser(u);

        System.out.println(i);

    }
}