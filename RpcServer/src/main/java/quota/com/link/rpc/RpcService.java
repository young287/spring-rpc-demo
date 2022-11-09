package quota.com.link.rpc;

import com.quota.service.TeacherService;
import com.quota.service.UserService;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import quota.com.link.contains.RpcCacheContains;
import quota.com.link.service.impl.TeacherServiceImpl;
import quota.com.link.service.impl.UserServiceImpl;
import quota.com.link.task.RpcRunnable;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

@Service
public class RpcService  {

    @Nullable
    private MetadataReaderFactory metadataReaderFactory;
    @Nullable
    private ResourcePatternResolver resourcePatternResolver;
    @Nullable
    private Environment environment;

    private String resourcePattern = "**/*.class";

    @Autowired
    ApplicationContext applicationContext;
    @PostConstruct
    public void rpcServiceStart(){
        int port=9997;
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(port));
            AbstractApplicationContext abstractApplicationContext=(AbstractApplicationContext)applicationContext;
            String basePackage="com.quota.service";
            Resource[] resources = getResource(basePackage);
            if(resources.length>0){
                for (Resource resource : resources) {
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    Class<?> aClass = Class.forName(metadataReader.getAnnotationMetadata().getClassName());
                    Object bean = applicationContext.getBean(aClass);
                    RpcCacheContains.ServiceCache.put(aClass.getName(),bean.getClass());
                }
            }
            while (true){
                new Thread(new RpcRunnable(serverSocket.accept(), abstractApplicationContext)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Resource[] getResource(String basePackage){
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + '/' + this.resourcePattern;
        Resource[] resources = new Resource[0];
        try {
            resources = getResourcePatternResolver().getResources(packageSearchPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resources;
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    /**
     * Return the MetadataReaderFactory used by this component provider.
     */
    public final MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    public final Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }
}
