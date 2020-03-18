package com.hbezxl.helper;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

public class FtpClient {
    //ftp服务器地址
    private String serverIP = "192.168.1.249";
    //ftp服务器端口号默认为21
    private int port = 21 ;
    //ftp登录账号
    private String username = "root";
    //ftp登录密码
    private String password = "123";

    private FTPClient ftpClient = null;

    public FtpClient(String I_ServerIP, int I_ServerPort, String I_UserName, String I_Password) {
        this.serverIP = I_ServerIP;
        this.port = I_ServerPort;
        this.username = I_UserName;
        this.password = I_Password;
    }

    /**
     * 初始化ftp服务器
     */
    public void InitFtpClient() {

        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
           TraceHelper.TraceInfo("connecting...ftp服务器:"+this.serverIP +":"+this.port);
            ftpClient.connect(serverIP, port);
            ftpClient.login(username, password);
            int replyCode = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(replyCode)){
                TraceHelper.TraceInfo("connect failed...ftp服务器:"+this.serverIP +":"+this.port);
            }
            TraceHelper.TraceInfo("connect successfu...ftp服务器:"+this.serverIP +":"+this.port);
        }catch (Exception Exc) {
            TraceHelper.TraceInfo("InitFtpClient" + CommonHelper.GetExceptionStackTrace(Exc));
        }
    }

    /**
     * 上传文件
     * @param I_Pathname ftp服务保存地址
     * @param I_FileName 上传到ftp的文件名
     *  @param I_SourceFilePath 待上传文件的名称（绝对地址） *
     * @return
     */
    public boolean UploadFile(String I_Pathname, String I_FileName, String I_SourceFilePath){
        boolean R_Result = false;

        try(InputStream inputStream = new FileInputStream(new File(I_SourceFilePath))){
            TraceHelper.TraceInfo("开始上传文件" + I_Pathname + "  " + I_FileName);

            InitFtpClient();
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            ftpClient.makeDirectory(I_Pathname);
            ftpClient.changeWorkingDirectory(I_Pathname);
            ftpClient.storeFile(I_FileName, inputStream);
            ftpClient.logout();
            R_Result = true;
            TraceHelper.TraceInfo("上传文件成功");
        }catch (Exception Exc) {
            TraceHelper.TraceInfo("UploadFile " +  CommonHelper.GetExceptionStackTrace(Exc));
        }finally{
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException Exc){
                    TraceHelper.TraceInfo("UploadFile1 " +  CommonHelper.GetExceptionStackTrace(Exc));
                }
            }
        }
        return R_Result;
    }


    //判断ftp服务器文件是否存在
    public boolean ExistFile(String I_Path,String I_FileName)   {
        boolean R_Result = false;

        try
        {
            TraceHelper.TraceInfo("开始删除文件");
            InitFtpClient();
            ftpClient.changeWorkingDirectory(I_Path);
            FTPFile[] ftpFileArr = ftpClient.listFiles(I_FileName);
            if (ftpFileArr.length > 0) {
                R_Result = true;
            }
        }catch (Exception Exc) {
            TraceHelper.TraceInfo("删除文件失败");
            TraceHelper.TraceInfo("ExistFile " + CommonHelper.GetExceptionStackTrace(Exc));
        } finally {
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException Exc){
                    TraceHelper.TraceInfo("ExistFile1 " + CommonHelper.GetExceptionStackTrace(Exc));
                }
            }
        }

        return R_Result;
    }


    /** * 下载文件 *
     * @param I_Pathname FTP服务器文件目录 *
     * @param I_FileName 文件名称 *
     * @param I_TargetFilePath 下载后的文件路径 *
     * @return */
    public  boolean DownloadFile(String I_Pathname, String I_FileName, String I_TargetFilePath) {
        boolean R_Result = false;

        try (OutputStream os = new FileOutputStream(new File(I_TargetFilePath))) {
            TraceHelper.TraceInfo("开始下载文件" + I_Pathname + " " + I_FileName + I_TargetFilePath);
            InitFtpClient();

            ftpClient.changeWorkingDirectory(I_Pathname);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (I_FileName.equalsIgnoreCase(file.getName())) {
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            ftpClient.logout();
            R_Result = true;
            TraceHelper.TraceInfo("下载文件成功");
        } catch (Exception Exc) {
            TraceHelper.TraceInfo("下载文件失败");
            TraceHelper.TraceInfo("DownloadFile1 " + CommonHelper.GetExceptionStackTrace(Exc));
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException Exc) {
                    TraceHelper.TraceInfo("DownloadFile " + CommonHelper.GetExceptionStackTrace(Exc));
                }
            }

        }
        return R_Result;
    }

    /** * 删除文件 *
     * @param I_Path FTP服务器保存目录 *
     * @param I_FileName 要删除的文件名称 *
     * @return */
    public boolean DeleteFile(String I_Path, String I_FileName){
        boolean R_Result = false;
        try {
            TraceHelper.TraceInfo("开始删除文件");
            InitFtpClient();
            //切换FTP目录
            ftpClient.changeWorkingDirectory(I_Path);
            ftpClient.dele(I_FileName);
            ftpClient.logout();
            R_Result = true;
            TraceHelper.TraceInfo("删除文件成功");
        } catch (Exception Exc) {
            TraceHelper.TraceInfo("删除文件失败");
            TraceHelper.TraceInfo("DeleteFile1 " +CommonHelper.GetExceptionStackTrace(Exc));
        } finally {
            if(ftpClient.isConnected()){
                try{
                    ftpClient.disconnect();
                }catch(IOException Exc){
                    TraceHelper.TraceInfo("DeleteFile " + CommonHelper.GetExceptionStackTrace(Exc));
                }
            }
        }
        return R_Result;
    }



}