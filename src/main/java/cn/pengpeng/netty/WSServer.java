package cn.pengpeng.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * @author zhaojie
 * @date 2018\12\4 0004 - 16:54
 */
@Component
public class WSServer {

    private static class SingletonInstance{
        static final WSServer instance = new WSServer();
    }

    public static WSServer getInstance(){
        return SingletonInstance.instance;
    }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;

    public WSServer(){

        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup,workGroup).
                       channel(NioServerSocketChannel.class).
                       childHandler(new WSServerInitialzer());
    }


    public void start(){

        this.channelFuture = serverBootstrap.bind(8099);

        System.out.println("netty server 启动完毕");

    }

}
