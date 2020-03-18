package com.hbezxl.helper;

import org.apache.log4j.Logger;

public class TraceHelper {

    private static final Logger logger = Logger.getLogger((TraceHelper.class));


    public static void TraceInfo(String I_TraceInfo)
    {
        logger.info(I_TraceInfo);
    }

}
