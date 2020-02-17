package duansg.trace.core.utils;

import duansg.trace.core.model.TraceContext;
import duansg.trace.core.support.TraceContextSupport;

/**
 * @author Duansg
 * @desc TraceUtil.
 * @date 2020-01-08 19:22:11
 */
public class TraceInitUtil {
    /**
     * @desc init traceContext
     */
    public static void initTraceContext() {
        TraceContextSupport.initTraceContext();
    }

    /**
     * @desc generate traceContext
     * @return TraceContext
     */
    public static TraceContext generateTraceContext(){
        return TraceContextSupport.generateTraceContext();
    }

    /**
     * @desc generate traceContext by traceId.
     * @param traceId
     * @return
     */
    public static void generateTraceContext(String traceId){
        TraceContextSupport.setTraceContext(new TraceContext(traceId));
    }

}
