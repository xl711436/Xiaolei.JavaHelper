package com.hbezxl.helper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TxtHelper {


    //了解随机存取文件的方式


    //编码 UTF-8  GBK
    public static String ReadFileContent(String I_FileName,String I_Charset) {
        String R_Result = "";
        try {
            R_Result = new String(Files.readAllBytes(Paths.get(I_FileName)),I_Charset);
        } catch (IOException e) {
            TraceHelper.TraceInfo("ReadFileContent " + CommonHelper.GetExceptionStackTrace(e));
        }
        return R_Result;
    }

    public static String ReadFileContent(String I_FileName ) {
        return ReadFileContent(I_FileName,"UTF-8");
    }


    public static ArrayList<String> ReadTxtFileByLine(String I_FilePath,String I_Charset) {
        ArrayList<String> R_Result = new ArrayList<>();

        try (InputStreamReader inputStreamReader = new InputStreamReader( new FileInputStream(I_FilePath),I_Charset);
             BufferedReader br = new BufferedReader(inputStreamReader)
        ) {
            String line = null;
            while ((line = br.readLine()) != null) {
                R_Result.add(line);
            }
        } catch (IOException e) {
            TraceHelper.TraceInfo("ReadTxtFileByLine " + CommonHelper.GetExceptionStackTrace(e));
        }
        return R_Result;
    }

    public static ArrayList<String> ReadTxtFileByLine(String I_FilePath) {
        return ReadTxtFileByLine(I_FilePath,"UTF-8");
    }

    public static void WriteFileByLine(String I_FileName, ArrayList<String> I_FileContentList,String I_Charset) {
        try {
            File writeName = new File(I_FileName);
            writeName.createNewFile();
            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(writeName),I_Charset);
                 BufferedWriter out = new BufferedWriter(outputStreamWriter)
            ) {
                for (String curLine : I_FileContentList) {
                    out.write(curLine + System.getProperty("line.separator"));
                }
                out.flush();
            }
        } catch (IOException e) {
            TraceHelper.TraceInfo("WriteFileByLine " + CommonHelper.GetExceptionStackTrace(e));
        }
    }


    public static void WriteFileByLine(String I_FileName, ArrayList<String> I_FileContentList) {
        WriteFileByLine(I_FileName,I_FileContentList,"UTF-8");
    }

}
