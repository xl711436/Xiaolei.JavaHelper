package com.hbezxl.helper;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.List;
import java.util.zip.*;

public class CompressHelper {

    private static int BUFFER = 100 * 1000;

    public static void PackFiles(List<String> I_SourceFileList, String I_TargetPath, String I_SourceRootPath)
    {
        File target = new File(I_TargetPath);
        try(FileOutputStream  out = new FileOutputStream(target);
            TarArchiveOutputStream os = new TarArchiveOutputStream(out))
        {
            os.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

            for(String curFilePath :    I_SourceFileList)
            {
                File curFile = new File (curFilePath);
                if(curFile.exists())
                {
                    try(InputStream inputStream = new FileInputStream(curFile)) {
                        os.putArchiveEntry(new TarArchiveEntry(curFile,curFile.getPath().substring(I_SourceRootPath.length())));
                        IOUtils.copy(inputStream,os);
                        os.closeArchiveEntry();
                    }
                    catch (Exception InnerExc)
                    {
                        TraceHelper.TraceInfo("PackFiles InnerExc" + CommonHelper.GetExceptionStackTrace(InnerExc));
                    }
                }
            }
        }
        catch (Exception Exc)
        {
            TraceHelper.TraceInfo("PackFiles" +  CommonHelper.GetExceptionStackTrace(Exc));
        }
    }


    public static void Compress2GZFile(String I_SourcePath, String I_TargetPath)
    {
        File sourcFile = new File(I_SourcePath);
        File targetFile = new File (I_TargetPath);

        try(FileInputStream in = new FileInputStream(sourcFile);
            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(targetFile)) )
        {
            byte[] array = new byte[BUFFER];
            int number;
            while((number = in.read(array,0,array.length)) != -1)
            {
                out.write(array,0,number);
            }
        }
        catch (Exception Exc)
        {
            TraceHelper.TraceInfo("Compress2GZFile" + CommonHelper.GetExceptionStackTrace(Exc));
        }
    }

    public static void Compress2ZipFiles(List<String> I_SourceFileList, String I_TargetPath)
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(I_TargetPath);
             CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
             ZipOutputStream out = new ZipOutputStream(cos);){

            String basedir = "";
            for(String curFilePath :I_SourceFileList)
            {
                File curFile = new File(curFilePath);
                Compress(curFile, out, basedir);
            }

        } catch (Exception Exc) {
            TraceHelper.TraceInfo("Compress2ZipFiles" + CommonHelper.GetExceptionStackTrace(Exc));
        }
    }

    private static void Compress(File I_File, ZipOutputStream I_OutStream, String I_Basedir) {
        /* 判断是目录还是文件 */
        if (I_File.isDirectory()) {
            CompressDirectory(I_File, I_OutStream, I_Basedir);
        } else {
            CompressFile(I_File, I_OutStream, I_Basedir);
        }
    }

    /** 压缩一个目录 */
    private static void CompressDirectory(File I_Dir, ZipOutputStream I_OutStream, String I_Basedir) {
        if (!I_Dir.exists())
            return;
        File[] files = I_Dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            Compress(files[i], I_OutStream, I_Basedir + I_Dir.getName() + "/");
        }
    }

    /** 压缩一个文件 */
    private static void CompressFile(File I_File, ZipOutputStream I_OutStream, String I_Basedir) {
        if (!I_File.exists()) {
            return;
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(I_File))){
            ZipEntry entry = new ZipEntry(I_Basedir + I_File.getName());
            I_OutStream.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                I_OutStream.write(data, 0, count);
            }
        } catch (Exception Exc) {
            TraceHelper.TraceInfo("CompressFile" + CommonHelper.GetExceptionStackTrace(Exc));
        }
    }

}
