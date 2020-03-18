package com.hbezxl.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class CommonHelper {

    //得到一个异常的详细调用信息
    public static String GetExceptionStackTrace(Exception I_Exception) {
        StringBuffer sb = new StringBuffer();
        if (I_Exception != null) {
            for (StackTraceElement element : I_Exception.getStackTrace()) {
                sb.append("\r\n").append(element);
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    public static String GetGuidStr()
    {
        UUID uuid = UUID.randomUUID();
        return  uuid.toString();
    }

    //返回1-10000间的随机数
    public static int GetRandom()
    {
        Random random = new Random();
        return random.nextInt(10000);
    }

    public static Date StringToDate(String I_DateString)
    {
        Date R_Result= new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            R_Result=formatter.parse(I_DateString);
        } catch (ParseException e) {
            TraceHelper.TraceInfo("StringToDate" + CommonHelper.GetExceptionStackTrace(e));
        }
        return R_Result;
    }

    public static String DateToString(Date I_Date)
    {
        String R_Result;
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        R_Result=formatter.format(I_Date);

        return R_Result;
    }

}
