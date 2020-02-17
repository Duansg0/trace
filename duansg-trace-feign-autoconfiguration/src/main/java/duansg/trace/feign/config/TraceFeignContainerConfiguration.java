package duansg.trace.feign.config;

import duansg.trace.core.annotation.TraceCustom;
import duansg.trace.core.utils.DynamicPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
public class TraceFeignContainerConfiguration implements ApplicationContextAware, SmartInitializingSingleton {

    private static final Logger logger = LoggerFactory.getLogger(TraceFeignContainerConfiguration.class);

    private ConfigurableApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DynamicPropertyUtil.setApplicationContext(applicationContext);
        this.applicationContext = ((ConfigurableApplicationContext)applicationContext);
    }

    public void afterSingletonsInstantiated() {
        //TODO 自定义拦截器配置
        try{
            Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(TraceCustom.class);
        }catch (Exception e){

        }
    }
}
