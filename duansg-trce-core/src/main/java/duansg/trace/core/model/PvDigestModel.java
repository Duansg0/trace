package duansg.trace.core.model;

import duansg.trace.core.constants.TraceConstants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import java.text.MessageFormat;

/**
 * @author Duansg
 * @desc
 * @date 2020-01-15 20:35:22
 */
@Data
public class PvDigestModel extends DigestModel {

    /**
     * @desc url
     */
    private String url;

    /**
     * @desc 框架名称，SpringMvc、Struts2
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
    public PvDigestModel() {

    }

    /**
     * @desc 构造函数
     * @desc Creates a new instance of PvDigestModel.
     * @param url
     * @param costTime
     * @param framName
     * @param resultCode
     * @param appName
     */
    public PvDigestModel(String url, long costTime, String framName, String resultCode, String appName) {
        super(resultCode, costTime);
        this.url = url;
        this.framName = framName;
        this.appName = appName;
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        //(appName,url,costTime,framName,resultCode)
        return MessageFormat.format("({0},{1},{2},{3},{4})",
                StringUtils.defaultIfBlank(appName, TraceConstants.EMPTY_DIGEST_VALUE),
                StringUtils.defaultIfBlank(url, TraceConstants.EMPTY_DIGEST_VALUE), String.valueOf(costTime),
                StringUtils.defaultIfBlank(framName, TraceConstants.EMPTY_DIGEST_VALUE),
                StringUtils.defaultIfBlank(resultCode, TraceConstants.EMPTY_DIGEST_VALUE));
    }
}
