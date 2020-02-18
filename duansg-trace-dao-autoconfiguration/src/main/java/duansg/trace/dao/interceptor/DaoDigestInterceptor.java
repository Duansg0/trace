package duansg.trace.dao.interceptor;

import duansg.trace.core.base.AbstractTraceInterceptor;
import duansg.trace.core.constants.TraceConstants;
import duansg.trace.core.enums.BoolEnum;
import duansg.trace.core.model.DaoDigestModel;
import duansg.trace.core.utils.LoggerFormatUtil;
import duansg.trace.core.utils.TraceUtil;
import lombok.Data;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

/**
 * @author Duansg
 * @desc Dao摘要日志拦截器
 * @date 2019-12-31 19:46:12
 */
@Data
public class DaoDigestInterceptor extends AbstractTraceInterceptor implements MethodInterceptor {
    /**
     * @desc Digestdao's logger.
     */
    private static final Logger digestLogger = LoggerFactory.getLogger(TraceConstants.DAO_DIGEST_LOG);
    /**
     * @desc DaoDigestInterceptor's logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DaoDigestInterceptor.class);

    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        boolean isSuccess = false;
        try {
            Object result = invocation.proceed();
            isSuccess = true;
            return result;
        } finally {
            try {
                /**
                 * AopUtils.getTargetClass(invocation.getThis()).getName();
                 * 因为无法兼容到mybatis-plus,特此如下改造,经测试可以兼容到Mybatis,但是不排除会有异常的情况,请关注！
                 */
                Class<?>[] interfaces = AopUtils.getTargetClass(invocation.getThis()).getInterfaces();
                String url = interfaces[0].getName() + "." + invocation.getMethod().getName();
                long costTime = System.currentTimeMillis() - startTime;
                DaoDigestModel daoDigestModel = new DaoDigestModel(appName, url, BoolEnum.get(isSuccess), costTime);
                logDigest(daoDigestModel, digestLogger, TraceUtil.getPerprotey(TraceConstants.DIGEST_LOG_SWITCH_DAO));
            } catch (Throwable ignore) {
                LoggerFormatUtil.error(ignore, logger, "Dao摘要日志发生了一个异常。请关注!");
            }
        }
    }
}
