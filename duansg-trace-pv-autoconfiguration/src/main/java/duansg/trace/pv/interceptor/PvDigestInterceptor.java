package duansg.trace.pv.interceptor;

import duansg.trace.core.base.AbstractTraceInterceptor;
import duansg.trace.core.constants.TraceConstants;
import duansg.trace.core.enums.BoolEnum;
import duansg.trace.core.model.PvDigestModel;
import duansg.trace.core.utils.LoggerFormatUtil;
import duansg.trace.core.utils.TraceInitUtil;
import duansg.trace.core.utils.TraceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Duansg
 * @desc Pv摘要日志拦截器
 * @date 2020-01-02 19:13:22
 */
public class PvDigestInterceptor extends AbstractTraceInterceptor implements HandlerInterceptor {

    /**
     * @desc Digestpv's logger.
     */
    private static final Logger digestLogger = LoggerFactory.getLogger(TraceConstants.PV_DIGEST_LOG);

    /**
     * @desc PvDigestInterceptor's logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(PvDigestInterceptor.class);
    /**
     * @desc 起始时间
     */
    protected final static ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    /**
     * @desc
     * @param appName
     */
    public PvDigestInterceptor(String appName) {
        super.appName = appName;
    }

    /**
     * @desc 前置处理
     * @desc 1:记录起始时间,2:初始化统一上下文
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //记录起始时间
        startTime.set(System.currentTimeMillis());
        //校验是否由feign过来的请求,如果不是初始化统一上下文
        String traceId = request.getHeader(TraceConstants.TRACE_ID_KEY);
        if (StringUtils.isNotBlank(traceId)){
            TraceInitUtil.generateTraceContext(traceId);
        }else {
            TraceInitUtil.initTraceContext();
        }
        //解析压测参数
        resolveLoadTest(request);
        return true;
    }

    /**
     * @desc do nothing
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //do nothing
    }

    /**
     * @desc 后置处理
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try{
            long costTime = -1L;
            boolean isSuccess = (ObjectUtils.isEmpty(ex)) ? true : false;
            //避免空指针
            if (!ObjectUtils.isEmpty(startTime.get())) {
                costTime = System.currentTimeMillis() - startTime.get();
            }
            PvDigestModel pvDigestModel = new PvDigestModel(request.getRequestURI(), costTime,TraceConstants.MVC_FRAM_NAME, BoolEnum.get(isSuccess), appName);
            logDigest(pvDigestModel, digestLogger);
        } catch (Throwable ignore) {
            // 抛出异常不影响业务执行
            LoggerFormatUtil.error(ignore, logger, "PV摘要日志发生了一个异常。请关注!,uri={0}.", request.getRequestURI());
        } finally {
            //统一上下文后置操作,清理统一上下文
            TraceUtil.clearTraceContext();
            //清理起始时间
            startTime.remove();
        }
    }

    /**
     * @desc Analyze the pressure parameters
     */
    public void resolveLoadTest(HttpServletRequest request) {
        if (!ObjectUtils.isEmpty(request) && StringUtils.isNotBlank(request.getHeader(TraceConstants.HTTP_LOAD_TEST_KEY))) {
            String loadTest = request.getHeader(TraceConstants.HTTP_LOAD_TEST_KEY);
            LoggerFormatUtil.debug(logger, "loadTest={0}", loadTest);
            TraceUtil.putContextExtendParam(TraceConstants.LOAD_TEST_KEY, loadTest);
        }
    }

}
