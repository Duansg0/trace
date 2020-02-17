package duansg.trace.core.annotation;

import java.lang.annotation.*;

/**
 * @author Duansg
 * @desc 自定义拦截器.
 * @see //duansg.trace.autoconfiguration.config.TraceContainerConfiguration
 * @date 2019-12-27 23:57:12
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TraceCustom {

}
