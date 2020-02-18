package duansg.trace.dao.config;

import duansg.trace.core.base.AbstractConfiguration;
import duansg.trace.core.constants.TraceConstants;
import duansg.trace.core.constants.TraceCustomConstants;
import duansg.trace.core.model.InterceptorInitInfoModel;
import duansg.trace.core.model.TraceProperties;
import duansg.trace.core.support.InterceptorBuilder;
import duansg.trace.dao.interceptor.DaoDigestInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@Import({TraceDaoContainerConfiguration.class})
@EnableConfigurationProperties({TraceProperties.class})
@ConditionalOnProperty(prefix="spring.boot.trace",name = "traceSwitchDao", havingValue = "true")
public class TraceDaoAutoConfiguration extends AbstractConfiguration {
    /**
     * @desc Register the request interceptor for the Dao layer.
     * @param traceProperties
     * @return
     */
    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisorDao(TraceProperties traceProperties) {
        super.init(traceProperties, TraceCustomConstants.DAO);
        return InterceptorBuilder.build(new DaoDigestInterceptor(), new InterceptorInitInfoModel.Builder().buildAppName(traceProperties.getAppName()).buildExecution(traceProperties.getTraceDaoExecution()).build());
    }
}
