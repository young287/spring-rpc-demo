package quota.com.link.task;

import com.quota.service.TeacherService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import quota.com.link.contains.RpcCacheContains;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class RpcRunnable implements Runnable{

    private Socket socket;
    public RpcRunnable(Socket socket, AbstractApplicationContext applicationContext) {
        this.socket = socket;
    }


    @Override
    public void run() {
          try{
              ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
              ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                  /*方法所在类名接口名*/
                  String serviceName = inputStream.readUTF();
                  /*方法的名字*/
                  String methodName = inputStream.readUTF();
                  /*方法的入参类型*/
                  Class<?>[] paramTypes = (Class<?>[]) inputStream.readObject();
                  /*方法的入参的值*/
                  Object[] args = (Object[]) inputStream.readObject();
                  Class serviceClass = RpcCacheContains.ServiceCache.get(serviceName);
              /*通过反射，执行实际的服务*/
                  Method method = serviceClass.getMethod(methodName, paramTypes);
                  Object result  = method.invoke(serviceClass.newInstance(),args);
                  /*将服务的执行结果通知调用者*/
                  outputStream.writeObject(result);
                  outputStream.flush();

           }catch (Exception e){
              e.printStackTrace();
           }finally {
              try {
                  socket.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
    }
}
