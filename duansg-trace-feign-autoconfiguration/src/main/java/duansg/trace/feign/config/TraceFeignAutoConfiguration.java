package duansg.trace.feign.config;

import duansg.trace.core.base.AbstractConfiguration;
import duansg.trace.core.constants.TraceCustomConstants;
import duansg.trace.core.model.TraceProperties;
import duansg.trace.feign.interceptor.FeignDigestInterceptor;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({TraceFeignContainerConfiguration.class})
@EnableConfigurationProperties({TraceProperties.class})
public class TraceFeignAutoConfiguration extends AbstractConfiguration {

    @Bean
    @ConditionalOnProperty(prefix="spring.boot.trace",name = "traceSwitchFeign", havingValue = "true")
    public RequestInterceptor feignDigestInterceptor(TraceProperties traceProperties){
        super.init(traceProperties, TraceCustomConstants.FEIGN);
        return new FeignDigestInterceptor(traceProperties.getAppName());
    }
}
