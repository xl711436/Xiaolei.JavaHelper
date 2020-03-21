package com.hbezxl.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ProcessHelper {

    public static ArrayList<String> GetPidListByName(String I_ProcessName)
    {
        ArrayList<String> R_Result = new ArrayList<String>();
        String os = System.getProperty("os.name");

        if(os !=null && os.toLowerCase().contains("linux"))
        {
            R_Result = GetLinuxPidListByName(I_ProcessName);
        }
        else
        {
            R_Result = GetWindowsPidListByName(I_ProcessName);
        }

        return R_Result;
    }


    public static void KillProcessByPid(String I_Pid ) {
        String os = System.getProperty("os.name");

        if (os != null && os.toLowerCase().contains("linux")) {
            KillLinuxProcess(I_Pid);
        } else {
            KillWindowsProcess(I_Pid);
        }
    }

    private static ArrayList<String> GetLinuxPidListByName(String I_ProcessName)
    {
        ArrayList<String> R_Result= new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("ps -ef").getInputStream()))){

            String line = null;
            while((line = reader.readLine()) != null)
            {
                if(line.equals(""))
                {
                    continue;
                }
                if(line.contains(I_ProcessName))
                {
                    String[] strs = line.split("\\s+");
                    if(strs.length >1 ) {
                        R_Result.add((strs[1]));
                    }
                }

            }
        } catch (IOException Exc) {
            TraceHelper.TraceInfo("GetWindowsPidListByName " + CommonHelper.GetExceptionStackTrace(Exc));
        }

        return R_Result;
    }

    private static ArrayList<String> GetWindowsPidListByName(String I_ProcessName)
    {
        ArrayList<String> R_Result= new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("tasklist").getInputStream()))){

            String line = null;
            while((line = reader.readLine()) != null)
            {
                if(line.equals(""))
                {
                    continue;
                }
                if(line.contains(I_ProcessName))
                {
                    String[] strs = line.split("\\s+");
                    if(strs.length >0 && (strs[0].toLowerCase().equals(I_ProcessName.toLowerCase())  || strs[0].toLowerCase().equals(I_ProcessName.toLowerCase() +".exe" )  ))
                    {
                        R_Result.add((strs[1]));
                    }
                }

            }
        } catch (IOException Exc) {
            TraceHelper.TraceInfo("GetWindowsPidListByName " + CommonHelper.GetExceptionStackTrace(Exc));
        }

        return R_Result;
    }


    private static void KillLinuxProcess(String I_Pid)
    {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("kill -9 " +I_Pid).getInputStream()))){

            String line = null;
            while((line = reader.readLine()) != null)
            {
                TraceHelper.TraceInfo("KillLinuxProcess out  " + line);
            }
        } catch (IOException Exc) {
            TraceHelper.TraceInfo("KillLinuxProcess " + CommonHelper.GetExceptionStackTrace(Exc));
        }

    }


    private static void KillWindowsProcess(String I_Pid)
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("TASKKILL /F /PID " +I_Pid).getInputStream()))){

            String line = null;
            while((line = reader.readLine()) != null)
            {
                TraceHelper.TraceInfo("KillWindowsProcess out  " + line);
            }
        } catch (IOException Exc) {
            TraceHelper.TraceInfo("KillWindowsProcess " + CommonHelper.GetExceptionStackTrace(Exc));
        }

    }


}
