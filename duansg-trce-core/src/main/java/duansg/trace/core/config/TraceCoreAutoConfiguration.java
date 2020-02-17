package duansg.trace.core.config;

import duansg.trace.core.listern.RefreshConfigListern;
import duansg.trace.core.model.RefreshConfigModel;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

/**
 * @author Duansg
 * @desc
 * @date
 */
public class TraceCoreAutoConfiguration {
    /**
     * @desv Refresh config listern .
     * @return
     */
    @Bean
    public ApplicationListener<RefreshConfigModel> refreshConfigListern(){
        return new RefreshConfigListern();
    }
}
