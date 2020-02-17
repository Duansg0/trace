package duansg.trace.pv.config;

import duansg.trace.core.base.AbstractConfiguration;
import duansg.trace.core.constants.TraceCustomConstants;
import duansg.trace.core.model.TraceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({TracePvContainerConfiguration.class})
@EnableConfigurationProperties({TraceProperties.class})
public class TracePvAutoConfiguration extends AbstractConfiguration {

    @Bean
    @ConditionalOnProperty(prefix="spring.boot.trace",name = "traceSwitchPv", havingValue = "true")
    public TracePvInitializingBean tracePvInitializingBean(TraceProperties traceProperties){
        super.init(traceProperties, TraceCustomConstants.PV);
        return new TracePvInitializingBean(traceProperties);
    }
}
