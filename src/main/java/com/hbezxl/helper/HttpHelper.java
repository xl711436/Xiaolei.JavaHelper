package com.hbezxl.helper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URL;
import java.util.*;


public class HttpHelper {


    public static String Post(String I_ReqUrl)  {
        return Post(I_ReqUrl,null);
    }

    public static String Post(String I_ReqUrl, Map<String, String> I_ReqParams)
    {
        return Post(I_ReqUrl,I_ReqParams,null);
    }

    public static String Post(String I_ReqUrl, Map<String, String> I_ReqParams,Map<String, String> I_Header) {
        String R_Result = "";
        if (I_ReqParams == null) {
            I_ReqParams = new HashMap<>();
        }

        HttpPost post = new HttpPost(I_ReqUrl);

        if(I_Header != null)
        {
            for (String key : I_Header.keySet()) {
                post.addHeader(key,I_Header.get(key));
            }
        }

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (String key : I_ReqParams.keySet()) {
                params.add(new BasicNameValuePair(key, I_ReqParams.get(key)));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, "UTF-8");
            /* 设置参数 */
            post.setEntity(urlEncodedFormEntity);
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            R_Result = EntityUtils.toString(entity, "UTF-8");

        } catch (Exception Exc) {
            TraceHelper.TraceInfo("Post " + CommonHelper.GetExceptionStackTrace(Exc));
        } finally {
            post.releaseConnection();
        }
        return R_Result;
    }


    public static String Get(String I_ReqUrl)  {
        return Get(I_ReqUrl,null);
    }

    public static String Get(String I_ReqUrl, Map<String, String> I_ReqParams)
    {
        return Get(I_ReqUrl,I_ReqParams,null);
    }


    public static String Get(String url, Map<String, String> I_ReqParams,Map<String, String> I_Header)   {
        String R_Result = "";

        if(I_ReqParams != null && I_ReqParams.size()>0)
        {
            String curPara =  GetStringData(I_ReqParams);
            url = url + "?" +  curPara;
        }

        HttpGet httpGet = new HttpGet(url);

        if(I_Header != null)
        {
            for (String key : I_Header.keySet()) {
                httpGet.addHeader(key,I_Header.get(key));
            }
        }

        try(CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse Response = client.execute(httpGet))
        {
            HttpEntity entity = Response.getEntity();
            // 通过EntityUtils 来将我们的数据转换成字符串
            R_Result  = EntityUtils.toString(entity, "UTF-8");

        }catch (Exception Exc) {
            TraceHelper.TraceInfo("Get " + CommonHelper.GetExceptionStackTrace(Exc));
        }finally {
            httpGet.releaseConnection();
        }
        return R_Result;
    }



    //将map转换为字符串，拼接为url数组
    public static String GetStringData(Map<String, String> params) {
        StringBuffer content = new StringBuffer();

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key =   keys.get(i);
            String value = params.get(key);
            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            } else {
                content.append((i == 0 ? "" : "&") + key + "=");
            }
        }
        return content.toString();


    }


    public static void DownloadFileFromUrl(String I_Url,String I_LocalPath)
    {

        try(BufferedInputStream inputStream = new BufferedInputStream(new URL(I_Url).openStream());
            FileOutputStream fileOS = new FileOutputStream(I_LocalPath))
        {

            byte data[] = new byte[1024];
            int byteContent;
            while((byteContent = inputStream.read(data,0,1024)) != -1)
            {
                fileOS.write(data,0,byteContent);
            }
        }  catch (Exception Exc) {
            TraceHelper.TraceInfo("DownloadFileFromUrl " + CommonHelper.GetExceptionStackTrace(Exc));
        }

    }

}