package duansg.trace.core.listern;

import duansg.trace.core.constants.TraceConstants;
import duansg.trace.core.constants.TraceCustomConstants;
import duansg.trace.core.model.RefreshConfigModel;
import duansg.trace.core.utils.TraceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.ObjectUtils;

/**
 * @author Duansg
 * @desc 热更新事件监听器。
 * @date 2020-01-08 21:32:43
 */
public class RefreshConfigListern implements ApplicationListener<RefreshConfigModel> {

    private static final Logger logger = LoggerFactory.getLogger(RefreshConfigListern.class);

    @Async
    public void onApplicationEvent(RefreshConfigModel config) {
        if (!ObjectUtils.isEmpty(config)){
            if (StringUtils.isBlank(config.getType())){
                TraceUtil.setPerprotey(TraceConstants.DIGEST_SWITCH,String.valueOf(config.isTraceSwitch()));
            }else {
                switch (config.getType()){
                    case TraceCustomConstants.DAO:
                        TraceUtil.setPerprotey(TraceConstants.DIGEST_LOG_SWITCH_DAO,String.valueOf(config.isTraceSwitchFeign()));
                        break;
                    case TraceCustomConstants.PV:
                        TraceUtil.setPerprotey(TraceConstants.DIGEST_LOG_SWITCH_PV,String.valueOf(config.isTraceSwitchPv()));
                        break;
                    case TraceCustomConstants.FEIGN:
                        TraceUtil.setPerprotey(TraceConstants.DIGEST_LOG_SWITCH_FEIGN,String.valueOf(config.isTraceSwitchFeign()));
                        break;
                    case TraceCustomConstants.DUBBO:
                        TraceUtil.setPerprotey(TraceConstants.DIGEST_LOG_SWITCH_DUBBO,String.valueOf(config.isTraceSwitchDubbo()));
                        break;
                }
            }
        }
    }
}
