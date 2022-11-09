package quota.com.link.rpc;

import org.springframework.stereotype.Service;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

@Service
public class RpcProxyService {
    /*远程服务的代理对象，参数为客户端要调用的的服务*/
    public <T> T getRemoteProxyObject( Class<T> serviceInterface){
        /*获得远程服务的一个网络地址*/
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1",9997);
        /*拿到一个代理对象，由这个代理对象通过网络进行实际的服务调用*/
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                new DynProxy(serviceInterface,addr));
    }

}
