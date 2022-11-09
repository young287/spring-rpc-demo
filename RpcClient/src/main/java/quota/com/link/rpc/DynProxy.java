package quota.com.link.rpc;

import org.springframework.stereotype.Service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DynProxy implements InvocationHandler {
    private Class<?> serviceInterface;
    private InetSocketAddress addr;

    public DynProxy(Class<?> serviceInterface, InetSocketAddress addr) {
        this.serviceInterface = serviceInterface;
        this.addr = addr;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Socket socket = null;
        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;
        try{
            socket = new Socket();
            socket.connect(addr);
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            //方法所在类名接口名
            outputStream.writeUTF(serviceInterface.getName());
            //方法的名字
            outputStream.writeUTF(method.getName());
            //方法的入参类型
            outputStream.writeObject(method.getParameterTypes());
            //方法入参的值
            outputStream.writeObject(args);

            outputStream.flush();

            inputStream = new ObjectInputStream(socket.getInputStream());
            /*接受服务器的输出*/
            System.out.println(serviceInterface+" remote exec success!");
            return inputStream.readObject();

        }finally {
            if(socket!=null) socket.close();
            if(outputStream!=null) outputStream.close();
            if(inputStream!=null) inputStream.close();

        }
    }

}
