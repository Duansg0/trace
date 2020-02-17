package duansg.trace.pv.config;

import duansg.trace.core.constants.TraceConstants;
import duansg.trace.core.model.TraceProperties;
import duansg.trace.core.utils.LoggerFormatUtil;
import duansg.trace.core.utils.TraceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.util.ObjectUtils;

/**
 * @author Duansg
 * @desc
 * @date
 */
public class TracePvInitializingBean implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(TracePvContainerConfiguration.class);

    private TraceProperties traceProperties;

    /**
     * @desc
     * @param traceProperties
     */
    public TracePvInitializingBean(TraceProperties traceProperties){
        this.traceProperties = traceProperties;
    }

    public void afterPropertiesSet() throws Exception {

    }
}
