package duansg.trace.core.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Duansg
 * @desc
 * @date 2020-01-15 20:35:22
 */
public class RefreshConfigModel extends ApplicationEvent {

    @Getter
    @Setter
    private String type;
    /**
     * @desc 总开关
     * @desc key:spring.boot.trace.traceSwitch
     */
    @Getter
    @Setter
    private boolean traceSwitch;
    /**
     * @desc Dao开关
     * @desc key:spring.boot.trace.traceSwitch.Dao
     */
    @Getter
    @Setter
    private boolean traceSwitchDao;
    /**
     * @desc Pv开关
     * @desc key:spring.boot.trace.traceSwitch.Pv
     */
    @Getter
    @Setter
    private boolean traceSwitchPv;
    /**
     * @desc feign开关
     * @desc key:spring.boot.trace.traceSwitch.Feign
     */
    @Getter
    @Setter
    private boolean traceSwitchFeign;
    /**
     * @desc dubbo开关
     * @desc key:spring.boot.trace.traceSwitch.Dubbo
     */
    @Getter
    @Setter
    private boolean traceSwitchDubbo;
    /**
     * @param source
     */
    private RefreshConfigModel(Object source) {
        super(source);
    }

    /**
     * @param builder
     * @param source
     */
    public RefreshConfigModel(Builder builder, Object source){
        super(source);
        this.traceSwitch = builder.traceSwitch;
        this.traceSwitchDubbo = builder.traceSwitchDubbo;
        this.traceSwitchFeign = builder.traceSwitchFeign;
        this.traceSwitchDao = builder.traceSwitchDao;
        this.traceSwitchPv = builder.traceSwitchPv;
        this.type = builder.type;
    }

    /**
     * @author Duansg
     * @desc
     * @date
     */
    public static class Builder{
        private String type;
        private boolean traceSwitch = false;
        private boolean traceSwitchFeign = false;
        private boolean traceSwitchDubbo = false;
        private boolean traceSwitchDao = false;
        private boolean traceSwitchPv = false;
        public Builder buildType(String type) {
            this.type = type;
            return this;
        }
        public Builder buildTraceSwitch(boolean traceSwitch) {
            this.traceSwitch = traceSwitch;
            return this;
        }
        public Builder buildTraceSwitchFeign(boolean traceSwitchFeign) {
            this.traceSwitchFeign = traceSwitchFeign;
            return this;
        }
        public Builder buildTraceSwitchDao(boolean traceSwitchDao) {
            this.traceSwitchDao = traceSwitchDao;
            return this;
        }
        public Builder buildTraceSwitchPv(boolean traceSwitchPv) {
            this.traceSwitchPv = traceSwitchPv;
            return this;
        }
        public Builder buildTraceSwitchDubbo(boolean traceSwitchDubbo) {
            this.traceSwitchDubbo = traceSwitchDubbo;
            return this;
        }
        public RefreshConfigModel build(Object source) {
            return new RefreshConfigModel(this,source);
        }
    }

}
