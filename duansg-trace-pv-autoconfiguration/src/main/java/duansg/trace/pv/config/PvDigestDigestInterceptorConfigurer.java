package duansg.trace.pv.config;

import duansg.trace.core.model.TraceProperties;
import duansg.trace.pv.interceptor.PvDigestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Duansg
 * @desc 拦截器注册
 * @date 2020-02-15 23:13:02
 */
@Configuration
public class PvDigestDigestInterceptorConfigurer extends WebMvcConfigurationSupport {

    @Autowired
    private TraceProperties traceProperties;

    /**
     * @desc
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PvDigestInterceptor(traceProperties.getAppName())).addPathPatterns("/**");
    }
}


