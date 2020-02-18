package duansg.trace.feign.interceptor;

import duansg.trace.core.base.AbstractTraceInterceptor;
import duansg.trace.core.constants.TraceConstants;
import duansg.trace.core.model.FeignDigestModel;
import duansg.trace.core.utils.LoggerFormatUtil;
import duansg.trace.core.utils.TraceUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Duansg
 * @desc Feign摘要日志配置
 * @date 2020-01-07 22:12:03
 */

@Data
public class FeignDigestInterceptor extends AbstractTraceInterceptor implements RequestInterceptor {
    /**
     * @desc Digestpv's logger.
     */
    private static final Logger digestLogger = LoggerFactory.getLogger(TraceConstants.FEIGN_DIGEST_LOG);
    /**
     * @desc FeignDigestInterceptor's logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(FeignDigestInterceptor.class);

    /**
     * @desc Constructor
     * @param appName
     */
    public FeignDigestInterceptor(String appName) {
        super.appName = appName;
    }

    /**
     * @desc
     * @param requestTemplate
     */
    public void apply(RequestTemplate requestTemplate) {
        try{
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (TraceUtil.getPerprotey(TraceConstants.DIGEST_LOG_SWITCH_FEIGN) && !ObjectUtils.isEmpty(attributes)){
                HttpServletRequest request = attributes.getRequest();
                //请求地址
                String requestUrl = !ObjectUtils.isEmpty(request)?TraceConstants.EMPTY_DIGEST_VALUE:request.getRequestURI();
                //获取上下文中的traceId,上下文中没有traceId,初始化统一上下文
                String traceId = TraceUtil.getTraceIdOrInitTraceContext();
                //获取调用地址
                String serviceUrl = StringUtils.isBlank(requestTemplate.url())?TraceConstants.EMPTY_DIGEST_VALUE:requestTemplate.url();
                //解析是否携带了压测参数
                String extendField = TraceUtil.getContextExtendParam(TraceConstants.LOAD_TEST_KEY);
                if (StringUtils.isNotBlank(extendField)){
                    requestTemplate.header(TraceConstants.LOAD_TEST_KEY,extendField);
                }
                requestTemplate.header(TraceConstants.TRACE_ID_KEY, traceId);
                FeignDigestModel feignDigestModel = new FeignDigestModel(requestUrl, serviceUrl, TraceConstants.FEIGN_FRAM_NAME, appName);
                logDigest(feignDigestModel, digestLogger,TraceUtil.getPerprotey(TraceConstants.DIGEST_LOG_SWITCH_FEIGN));
            }
        }catch (Throwable ignore) {
            LoggerFormatUtil.error(ignore, logger, "Feign摘要日志发生了一个异常。请关注!");
        }
    }
}
