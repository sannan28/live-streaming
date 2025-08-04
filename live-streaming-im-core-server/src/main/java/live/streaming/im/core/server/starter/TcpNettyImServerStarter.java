package live.streaming.im.core.server.starter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import live.streaming.im.core.server.common.ChannelHandlerContextCache;
import live.streaming.im.core.server.common.TcpImMsgDecoder;
import live.streaming.im.core.server.common.TcpImMsgEncoder;
import live.streaming.im.core.server.handler.tcp.TcpImServerCoreHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Slf4j
@Configuration
public class TcpNettyImServerStarter implements InitializingBean {

    // 指定监听的端口
    @Value("${live.im.tcp.port}")
    private int port;

    @Resource
    private TcpImServerCoreHandler tcpImServerCoreHandler;

    @Resource
    private Environment environment;

    public void startApplication() throws InterruptedException {
        // 处理accept事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 处理read&write事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup);

        serverBootstrap.channel(NioServerSocketChannel.class);

        // netty初始化相关的handler
        serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel ch) throws Exception {
                // 为什么要解码器在前 编码器在后 技术上可行 但是会逻辑颠倒 有冗余调用 违反常规认知 遵循 Netty 社区的最佳实践和示例规范
                // 增加编解码器
                // 入站处理器  数据接收 事件从pipeline 头部开始向后传递 TcpImMsgDecoder负责将字节码转化为消息对象
                // 入站的方向 -> 解码器 -> 业务处理器（编码器被跳过）head -> [decoder] -> [encoder]跳过 -> [handler] -> tail
                // 解码必须在业务逻辑之前执行，否则业务层无法直接处理原始字节流。
                ch.pipeline().addLast(new TcpImMsgDecoder());
                // 出站处理器  数据数据发送  TcpImMsgEncoder -> 它将消息对象编码为字节
                // 它不会处理入站事件，所以事件会跳过它，继续向后传递
                // 出站方向：业务处理器 -> 编码器（解码器被跳过） tail -> [handler] -> [encoder] -> [decoder]跳过 -> head
                // 编码必须在业务层之后执行，因为业务层产生的是需要编码的 Java 对象。
                ch.pipeline().addLast(new TcpImMsgEncoder());
                ch.pipeline().addLast(tcpImServerCoreHandler);
            }
        });

        // 基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));

        // 获取im的服务注册ip和暴露端口
        // environment.getProperty("DUBBO_IP_TO_REGISTRY");   environment.getProperty("DUBBO_PORT_TO_REGISTRY")
        String registryIp = "192.168.64.1";
        String registryPort ="6066" ;
        if (StringUtils.isEmpty(registryPort) || StringUtils.isEmpty(registryIp)) {
            throw new IllegalArgumentException("启动参数中的注册端口和注册ip不能为空");
        }
        ChannelHandlerContextCache.setServerIpAddress(registryIp + ":" + registryPort);
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        log.info("服务启动成功，监听端口为{}", port);
        // 这里会阻塞掉主线程，实现服务长期开启的效果
        channelFuture.channel().closeFuture().sync();
    }


    public void afterPropertiesSet() throws Exception {
        Thread nettyServerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startApplication();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        nettyServerThread.setName("live-im-server-tcp");
        nettyServerThread.start();
    }
}
