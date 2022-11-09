package quota.com.link.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import quota.com.link.rpc.RpcProxyService;

import java.lang.reflect.Proxy;

@Configuration
public class BeanConfig implements BeanDefinitionRegistryPostProcessor {

    private String resourcePattern = "**/*.class";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }
    public  String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    @Nullable
    private MetadataReaderFactory metadataReaderFactory;
    @Nullable
    private ResourcePatternResolver resourcePatternResolver;
    @Nullable
    private Environment environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory defaultListableBeanFactory=(DefaultListableBeanFactory)beanFactory;
        String basePackage="com.quota.service";
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + '/' + this.resourcePattern;
        try {
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                Class<?> aClass = Class.forName(metadataReader.getAnnotationMetadata().getClassName());
                if(aClass.isInterface()){
                    RpcProxyService proxyService = beanFactory.getBean(RpcProxyService.class);
                    Object remoteProxyObject = proxyService.getRemoteProxyObject(aClass);
                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(remoteProxyObject.getClass());
                    beanDefinitionBuilder.addConstructorArgValue(Proxy.getInvocationHandler(remoteProxyObject));
                    AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
                    beanDefinition.setBeanClass(remoteProxyObject.getClass());
                    String name = aClass.getName();
                    String[] split = name.split("\\.");
                    String s = split[split.length - 1];
                    String decapitalize = capitalize(s);
                    defaultListableBeanFactory.registerBeanDefinition(decapitalize,beanDefinitionBuilder.getRawBeanDefinition());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
