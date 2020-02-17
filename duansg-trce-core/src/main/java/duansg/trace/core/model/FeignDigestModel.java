package duansg.trace.core.model;

import duansg.trace.core.constants.TraceConstants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import java.text.MessageFormat;

/**
 * @author Duansg
 * @desc Feign摘要日志模型
 * @date 2020-02-16 01:17:28
 */
@Data
public class FeignDigestModel extends DigestModel{

    /**
     * @desc 本请求地址
     */
    private String requestUrl;

    /**
     * @desc 服务地址
     */
    private String servicetUrl;

    /**
     * @desc 框架名称
     */
    private String framName;

    /**
     * @desc 应用名称
     */
    private String appName;

    /**
     * @desc 空构造函数
     * @desc Creates a new instance of PvDigestModel.
     */
    public FeignDigestModel() {

    }

    /**
     * @desc 构造函数
     * @desc Creates a new instance of PvDigestModel.
     * @param requestUrl
     * @param servicetUrl
     * @param framName
     * @param appName
     */
    public FeignDigestModel(String requestUrl, String servicetUrl, String framName, String appName) {
        this.requestUrl = requestUrl;
        this.servicetUrl = servicetUrl;
        this.framName = framName;
        this.appName = appName;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        //(appName,requestUrl,servicetUrl,framName)
        return MessageFormat.format("({0},{1},{2},{3})",
                StringUtils.defaultIfBlank(appName, TraceConstants.EMPTY_DIGEST_VALUE),
                StringUtils.defaultIfBlank(requestUrl, TraceConstants.EMPTY_DIGEST_VALUE),
                StringUtils.defaultIfBlank(servicetUrl, TraceConstants.EMPTY_DIGEST_VALUE),
                StringUtils.defaultIfBlank(framName, TraceConstants.EMPTY_DIGEST_VALUE));
    }
}
