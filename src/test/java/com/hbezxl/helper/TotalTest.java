package com.hbezxl.helper;

import com.hbezxl.helper.Pojo.User;
import com.hbezxl.helper.Pojo.UserCombo;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

public class TotalTest {

    @Test
    public void CommonTest() {
        int a = CommonHelper.GetRandom();
        String bb = CommonHelper.DateToString(new Date());
        String cc = "2020-04-05 10:10:10";
        Date dd = CommonHelper.StringToDate(cc);
        String ee = CommonHelper.GetGuidStr();
    }

    @Test
    public void CompressTest() {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("C:\\Users\\xl\\Desktop\\a.txt");
        fileList.add("C:\\Users\\xl\\Desktop\\aa6.txt");
        fileList.add("C:\\Users\\xl\\Desktop\\aa7.txt");
        fileList.add("C:\\Users\\xl\\Desktop\\aa\\aa.txt");
        fileList.add("C:\\Users\\xl\\Desktop\\aa\\bb.txt");

        CompressHelper.PackFiles(fileList,"C:\\Users\\xl\\Desktop\\bb.tar","C:\\Users\\xl\\Desktop\\");
        CompressHelper.Compress2GZFile("C:\\Users\\xl\\Desktop\\bb.tar","C:\\Users\\xl\\Desktop\\bb.tar.gz");

        ArrayList<String> fileList1 = new ArrayList<>();
        fileList1.add("C:\\Users\\xl\\Desktop\\a.txt");
        fileList1.add("C:\\Users\\xl\\Desktop\\aa6.txt");

        fileList1.add("C:\\Users\\xl\\Desktop\\aa\\");

        CompressHelper.Compress2ZipFiles(fileList1,"C:\\Users\\xl\\Desktop\\mm.zip");
    }

    @Test
    public void HttpTest()
    {
        HttpHelper.DownloadFileFromUrl("https://www.baidu.com/img/bd_logo1.png","C:\\Users\\xl\\Desktop\\bd_logo1.png");
        String testa =   HttpHelper.Get("http://localhost:8082/HelloController/hello");
        String testb =   HttpHelper.Get("http://localhost:8082/RestController/hello");
        String testc =   HttpHelper.Get("http://localhost:8082/User/json2");

        Map<String, String> curMap = new HashMap<>();
        curMap.put("name","kdfdf哈哈");
        curMap.put("id","1");
        curMap.put("age","15");
        String teste =   HttpHelper.Get("http://localhost:8082/User/user" ,curMap);
        String testd =   HttpHelper.Post("http://localhost:8082/User/user",curMap);

        HttpHelper.DownloadFileFromUrl("https://www.baidu.com/img/bd_logo1.png","C:\\Users\\xl\\Desktop\\bd_logo1.png");
    }

    @Test
    public void ReadWriteTest()
    {
        ArrayList<String> aaList = new ArrayList<>();
        aaList.add("的丰富");
        aaList.add("的丰3富");
        aaList.add("的3丰富");

        TxtHelper.WriteFileByLine("C:\\Users\\xl\\Desktop\\aa6.txt",aaList);
        TxtHelper.WriteFileByLine("C:\\Users\\xl\\Desktop\\aa7.txt",aaList,"GBK");

        ArrayList<String> curList = TxtHelper.ReadTxtFileByLine("c:\\aa4.txt");
        curList = TxtHelper.ReadTxtFileByLine("c:\\aa5.txt","GBK");
    }

    @Test
    public void FtpTest()
    {
        FtpClient curClient = new FtpClient("192.168.11.130",2021,"user","user");
        curClient.UploadFile("path1/哈哈","cc.txt","C:\\Users\\xl\\Desktop\\aa7.txt");
        curClient.DownloadFile("path1/哈哈","cc.txt","C:\\Users\\xl\\Desktop\\aa8.txt");
        curClient.ExistFile("path1/哈哈","cc.txt");
        curClient.DeleteFile("path1/哈哈","cc.txt");
        curClient.ExistFile("path1/哈哈","cc.txt");
    }


    @Test
    public void JsonTest() {

        User user1 = new User(1,"1",new Date(),new float[]{1,1,1});
        User user2 = new User(2,"2",new Date(),new float[]{2,2,2});
        User user3 = new User(3,"3",new Date(),new float[]{3,3,3});
        User user4 = new User(4,"4",new Date(),new float[]{4,4,4});
        User user5 = new User(5,"5",new Date(),new float[]{5,5,5});
        User user6 = new User(6,"6",new Date(),new float[]{6,6,6});


         ArrayList<User> userList = new ArrayList<>();
         HashSet<User> userHash = new HashSet<>();
          HashMap<String,User> userMap = new HashMap<>();

        userList.add(user1);
        userList.add(user2);

        userHash.add(user3);
        userHash.add(user4);

        userMap.put("55",user5);
        userMap.put("66",user6);


        UserCombo curCombo = new UserCombo(userList,userHash,userMap);

        String jsonStr = JsonHelper.ObjectToString(curCombo);
        UserCombo curCombo1  = JsonHelper.StringToObject(jsonStr,UserCombo.class);
    }


    @Test
    public void MySqlTest()
    {
        SqlClient mySqlClient = new SqlClient("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/mybatis?useUnicode=true&characterEncoding=UTF-8&useSSL=false","xl","1010");

        Connection curConnection = null;
        try
        {
              curConnection = mySqlClient.GetConnection();
            mySqlClient.ExecSql(curConnection,"insert into mybatis.user (id,name,pwd) values (1336,'bbb','mmm')");
            Object curObj= mySqlClient.ExecScalar(curConnection,"select * from mybatis.user where id = 1336");

            ResultSet curSet= mySqlClient.ExecResultSet(curConnection,"select * from mybatis.user where id = 1336");

            // 展开结果集数据库
            while(curSet.next()){
                // 通过字段检索
                int id  = curSet.getInt("id");

            }
        }
        catch (Exception Exc)
        {
            TraceHelper.TraceInfo("MySqlTest " + CommonHelper.GetExceptionStackTrace(Exc));
        }
        finally {
            mySqlClient.close(curConnection);
        }
    }

    @Test
    public void SqliteTest()
    {
        SqlClient mySqlClient = new SqlClient("org.sqlite.JDBC","jdbc:sqlite:C:\\Users\\xl\\Desktop\\Sqliteman-1.2.2\\test.db");

        Connection curConnection = null;
        try
        {
            curConnection = mySqlClient.GetConnection();
            mySqlClient.ExecSql(curConnection,"insert into user (id,name,pwd) values (334,'bbb','mmm')");
            Object curObj= mySqlClient.ExecScalar(curConnection,"select * from user where id = 334");

            ResultSet curSet= mySqlClient.ExecResultSet(curConnection,"select * from user where id = 334");

            // 展开结果集数据库
            while(curSet.next()){
                // 通过字段检索
                int id  = curSet.getInt("id");

            }
        }
        catch (Exception Exc)
        {
            TraceHelper.TraceInfo("SqliteTest " + CommonHelper.GetExceptionStackTrace(Exc));
        }
        finally {
            mySqlClient.close(curConnection);
        }


    }



}
