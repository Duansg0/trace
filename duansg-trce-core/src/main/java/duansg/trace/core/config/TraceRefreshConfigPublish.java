package duansg.trace.core.config;

import duansg.trace.core.constants.TraceCustomConstants;
import duansg.trace.core.model.RefreshConfigModel;
import duansg.trace.core.publish.RefreshConfigPublish;
import duansg.trace.core.utils.LoggerFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

public abstract class TraceRefreshConfigPublish extends RefreshConfigPublish {

    /**
     * @desc TraceRefreshConfigPublish's logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(TraceRefreshConfigPublish.class);

    public void publishDao(boolean off) {
        RefreshConfigModel model = new RefreshConfigModel.Builder()
                .buildType(TraceCustomConstants.DAO)
                .buildTraceSwitchDao(off)
                .build(this);
        publish(model);
    }
    public void publishPv(boolean off) {
        RefreshConfigModel model = new RefreshConfigModel.Builder()
                .buildType(TraceCustomConstants.PV)
                .buildTraceSwitchPv(off)
                .build(this);
        publish(model);
    }
    public void publishFeign(boolean off) {
        RefreshConfigModel model = new RefreshConfigModel.Builder()
                .buildType(TraceCustomConstants.FEIGN)
                .buildTraceSwitchFeign(off)
                .build(this);
        publish(model);
    }
    public void publishDubbo(boolean off) {
        RefreshConfigModel model = new RefreshConfigModel.Builder()
                .buildType(TraceCustomConstants.DUBBO)
                .buildTraceSwitchDubbo(off)
                .build(this);
        publish(model);
    }
    public void publishGobal(boolean off) {
        RefreshConfigModel model = new RefreshConfigModel.Builder()
                .buildTraceSwitch(off)
                .build(this);
        publish(model);
    }
    @Override
    public void publish(RefreshConfigModel model) {
        try{
            if (!ObjectUtils.isEmpty(model)){
                applicationEventPublisher.publishEvent(model);
            }
        } catch (Throwable ignore) {
            // 抛出异常不影响业务执行
            LoggerFormatUtil.error(ignore, logger, "{0}", ignore.getMessage());
        }
    }
}
