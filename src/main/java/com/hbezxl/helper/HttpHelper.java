package com.hbezxl.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


public class HttpHelper {


    //get   string
    //post  string  jsonstring
    //post  string map
    //下载文件

    //common  string  type  map



    public static String Post(String I_ReqUrl) throws Exception {
        return Post(I_ReqUrl,null);
    }

    public static String Post(String I_ReqUrl, Map<String, String> I_ReqParams) {
        String R_Result = "";
        if (I_ReqParams == null) {
            I_ReqParams = new HashMap<>();
        }

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(I_ReqUrl);
        try {
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

        return Execute(I_ReqUrl, null, "GET");
    }

    public static String Get(String I_ReqUrl, Map<String, String> I_ReqParams)  {
        return Execute(I_ReqUrl, I_ReqParams, "GET");
    }

    public static String Execute(String I_ReqUrl, Map<String, String> I_ReqParams, String I_Method) {
        return Execute(I_ReqUrl, I_ReqParams, I_Method, "application/x-www-form-urlencoded");
    }

    public static String Execute(String I_ReqUrl, Map<String, String> I_ReqParams, String I_Method, String I_ContentType)
          {
        String response = "";
        String invokeUrl = I_ReqUrl;
              HttpURLConnection conn = null;
        try {
            URL serverUrl = new URL(invokeUrl);
              conn = (HttpURLConnection) serverUrl.openConnection();

            conn.setReadTimeout(1000 * 30);
            conn.setRequestMethod(I_Method);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            if (I_ReqParams != null && I_ReqParams.size() > 0) {
                conn.setRequestProperty("Cookie", I_ReqParams.get("Cookie") + "");
                I_ReqParams.remove("Cookie");
            }
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("Content-Type", I_ContentType);
            if (I_ReqParams != null && I_ReqParams.size() > 0) {
                String content = GetStringData(I_ReqParams);
                byte[] bypes = content.toString().getBytes("UTF-8");
                conn.getOutputStream().write(bypes);
            }
            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            response = buffer.toString();
        }
        catch (Exception Exc) {
            TraceHelper.TraceInfo("Execute " + CommonHelper.GetExceptionStackTrace(Exc));
        }
        finally {
            if(conn != null)
            {
                conn.disconnect();
            }
        }
        return response;
    }


    //将map转换为字符串，拼接为url数组
    public static String GetStringData(Map<String, String> params) {
        StringBuffer content = new StringBuffer();

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
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