package duansg.trace.core.base;

import duansg.trace.core.constants.TraceConstants;
import duansg.trace.core.constants.TraceCustomConstants;
import duansg.trace.core.model.TraceProperties;
import duansg.trace.core.support.AssertSupport;
import duansg.trace.core.utils.TraceUtil;

/**
 * @author Duansg
 * @desc 自动配置基类.
 * @date 2019-12-26 20:42:02
 */
public abstract class AbstractConfiguration {
    /**
     * @param traceProperties
     */
    protected void init(TraceProperties traceProperties,String type) {
        AssertSupport.isNotEmpty(traceProperties,"未能获取到配置信息.");
        TraceUtil.setPerprotey(TraceConstants.DIGEST_SWITCH,String.valueOf(traceProperties.isTraceSwitch()));
        switch (type){
            case TraceCustomConstants.FEIGN:
                TraceUtil.setPerproteyByTrue(traceProperties.isTraceSwitchFeign(),
                        TraceConstants.DIGEST_LOG_SWITCH_FEIGN,String.valueOf(traceProperties.isTraceSwitchFeign()));
                break;
            case TraceCustomConstants.PV:
                TraceUtil.setPerproteyByTrue(traceProperties.isTraceSwitchPv(),
                        TraceConstants.DIGEST_LOG_SWITCH_PV,String.valueOf(traceProperties.isTraceSwitchPv()));
                break;
            case TraceCustomConstants.DAO:
                TraceUtil.setPerproteyByTrue(traceProperties.isTraceSwitchDao(),
                        TraceConstants.DIGEST_LOG_SWITCH_DAO,String.valueOf(traceProperties.isTraceSwitchDao()));
                break;
            case TraceCustomConstants.DUBBO:
                TraceUtil.setPerproteyByTrue(traceProperties.isTraceSwitchDubbo(),
                        TraceConstants.DIGEST_LOG_SWITCH_DUBBO,String.valueOf(traceProperties.isTraceSwitchDubbo()));
                break;
        }
    }
}
