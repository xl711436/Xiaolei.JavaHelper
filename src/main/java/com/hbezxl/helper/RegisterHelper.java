package com.hbezxl.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

/**
 * Windows下读取注册表的工具类
 *
 * 使用这些方法的示例如下：
 *
 * 下面的方法从给定路径检索键的值：
 *
 * String hex = WinRegistry.GetValueFormKey(WinRegistry.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\WindowsUpdate\\Auto Update", "AUOptions");
 *
 * 此方法检索指定路径的所有数据(以键和值的形式)：
 *
 * Map<String, String> map = WinRegistry.GetKeyValuesFormPath(WinRegistry.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\WSMAN");
 *
 * 此方法从给定路径递归检索键的值：
 *
 * String val = WinRegistry.valueForKeyPath(WinRegistry.HKEY_LOCAL_MACHINE, "System", "TypeID");
 *
 * 并且这个方法递归地从给定路径中检索一个键的所有值：
 *
 * List<String> list = WinRegistry.valuesForKeyPath( WinRegistry.HKEY_LOCAL_MACHINE, //HKEY "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall", //path "DisplayName" //Key );
 *
 * 在上面的代码中，我检索了Windows系统中所有已安装的软件名称。
 *
 * 注意：请参阅这些方法的文档。
 *
 * 这个检索给定路径的所有子键：
 *
 * List<String> list3 = WinRegistry.GetSubPathsFormPath(WinRegistry.HKEY_CURRENT_USER, "Software");
 *
 * 注意：在这个过程中，我只修改了阅读目的的方法，而不是像createKey、删除Key等写作目的的方法。它们仍然和我收到的一样。
 *
 * https://cloud.tencent.com/developer/ask/43600
 */
@SuppressWarnings("all")
public class RegisterHelper {

    private static final int REG_SUCCESS = 0;
    private static final int REG_NOTFOUND = 2;
    private static final int KEY_READ = 0x20019;
    private static final int REG_ACCESSDENIED = 5;
    private static final int KEY_ALL_ACCESS = 0xf003f;
    public static final int HKEY_CLASSES_ROOT = 0x80000000;
    public static final int HKEY_CURRENT_USER = 0x80000001;
    public static final int HKEY_LOCAL_MACHINE = 0x80000002;
    private static final String CLASSES_ROOT = "HKEY_CLASSES_ROOT";
    private static final String CURRENT_USER = "HKEY_CURRENT_USER";
    private static final String LOCAL_MACHINE = "HKEY_LOCAL_MACHINE";
    private static Preferences userRoot = Preferences.userRoot();
    private static Preferences systemRoot = Preferences.systemRoot();
    private static Class<? extends Preferences> userClass = userRoot.getClass();
    private static Method regOpenKey = null;
    private static Method regCloseKey = null;
    private static Method regQueryValueEx = null;
    private static Method regEnumValue = null;
    private static Method regQueryInfoKey = null;
    private static Method regEnumKeyEx = null;
    private static Method regCreateKeyEx = null;
    private static Method regSetValueEx = null;
    private static Method regDeleteKey = null;
    private static Method regDeleteValue = null;

    static {
        try {
            regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey", new Class[] {int.class, byte[].class, int.class});
            regOpenKey.setAccessible(true);
            regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey", new Class[] {int.class});
            regCloseKey.setAccessible(true);
            regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx", new Class[] {int.class, byte[].class});
            regQueryValueEx.setAccessible(true);
            regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue", new Class[] {int.class, int.class, int.class});
            regEnumValue.setAccessible(true);
            regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1", new Class[] {int.class});
            regQueryInfoKey.setAccessible(true);
            regEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx", new Class[] {int.class, int.class, int.class});
            regEnumKeyEx.setAccessible(true);
            regCreateKeyEx = userClass.getDeclaredMethod("WindowsRegCreateKeyEx", new Class[] {int.class, byte[].class});
            regCreateKeyEx.setAccessible(true);
            regSetValueEx = userClass.getDeclaredMethod("WindowsRegSetValueEx", new Class[] {int.class, byte[].class, byte[].class});
            regSetValueEx.setAccessible(true);
            regDeleteValue = userClass.getDeclaredMethod("WindowsRegDeleteValue", new Class[] {int.class, byte[].class});
            regDeleteValue.setAccessible(true);
            regDeleteKey = userClass.getDeclaredMethod("WindowsRegDeleteKey", new Class[] {int.class, byte[].class});
            regDeleteKey.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }




    //得到某个key的value
    public static String GetValueFormKey(int I_Root, String I_Path, String I_Key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        if (I_Root == HKEY_LOCAL_MACHINE)
            return GetValueFormKey(systemRoot, I_Root, I_Path, I_Key);
        else if (I_Root == HKEY_CURRENT_USER)
            return GetValueFormKey(userRoot, I_Root, I_Path, I_Key);
        else
            return GetValueFormKey(null, I_Root, I_Path, I_Key);
    }

    //设置某个key的value
    public static void SetValueToKey(int I_Root, String I_Path, String I_Key, String I_Value)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (I_Root == HKEY_LOCAL_MACHINE)
            SetValueToKey(systemRoot, I_Root, I_Path, I_Key, I_Value);
        else if (I_Root == HKEY_CURRENT_USER)
            SetValueToKey(userRoot, I_Root, I_Path, I_Key, I_Value);
        else
            throw new IllegalArgumentException("hkey=" + I_Root);
    }

    //删除某个key
    public static void DeleteKey(int I_Root, String I_Path, String I_Key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int rc = -1;
        if (I_Root == HKEY_LOCAL_MACHINE)
            rc = DeleteKey(systemRoot, I_Root, I_Path, I_Key);
        else if (I_Root == HKEY_CURRENT_USER)
            rc = DeleteKey(userRoot, I_Root, I_Path, I_Key);
        if (rc != REG_SUCCESS)
            throw new IllegalArgumentException("rc=" + rc + "  key=" + I_Path + "  value=" + I_Key);
    }


    // 得到某个path 下的 key和对应的value的 map
    public static Map<String, String> GetKeyValuesFormPath(int I_Root, String I_Path)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        if (I_Root == HKEY_LOCAL_MACHINE)
            return GetKeyValuesFormPath(systemRoot, I_Root, I_Path);
        else if (I_Root == HKEY_CURRENT_USER)
            return GetKeyValuesFormPath(userRoot, I_Root, I_Path);
        else
            return GetKeyValuesFormPath(null, I_Root, I_Path);
    }

    // 得到某个path 下的子path
    public static List<String> GetSubPathsFormPath(int I_Root, String I_Path)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (I_Root == HKEY_LOCAL_MACHINE)
            return GetSubPathsFormPath(systemRoot, I_Root, I_Path);
        else if (I_Root == HKEY_CURRENT_USER)
            return GetSubPathsFormPath(userRoot, I_Root, I_Path);
        else
            return GetSubPathsFormPath(null, I_Root, I_Path);
    }



    //建立path，可以直接建立多层子目录
    public static void CreatePath(int I_Root, String I_Path)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int [] ret;
        if (I_Root == HKEY_LOCAL_MACHINE) {
            ret = CreatePath(systemRoot, I_Root, I_Path);
            regCloseKey.invoke(systemRoot, new Object[] { new Integer(ret[0]) });
        } else if (I_Root == HKEY_CURRENT_USER) {
            ret = CreatePath(userRoot, I_Root, I_Path);
            regCloseKey.invoke(userRoot, new Object[] { new Integer(ret[0]) });
        } else
            throw new IllegalArgumentException("hkey=" + I_Root);
        if (ret[1] != REG_SUCCESS)
            throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + I_Path);
    }


    //删除path,当有子path时，会抛异常
    public static void DeletePath(int I_Root, String I_Path)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int rc = -1;
        if (I_Root == HKEY_LOCAL_MACHINE)
            rc = DeletePath(systemRoot, I_Root, I_Path);
        else if (I_Root == HKEY_CURRENT_USER)
            rc = DeletePath(userRoot, I_Root, I_Path);
        if (rc != REG_SUCCESS)
            throw new IllegalArgumentException("rc=" + rc + "  key=" + I_Path);
    }


    //根据value来查找某个path下的所有 keyvalue值
    public static Map<String,String> SearchKeyValueMapFromPathByValue(int I_Root, String I_Path, String I_Key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        Map<String,String> map = new HashMap<>();
        if (I_Root == HKEY_LOCAL_MACHINE)
            return KeyValuesForValuePath(systemRoot, I_Root, I_Path, I_Key, map);
        else if (I_Root == HKEY_CURRENT_USER)
            return KeyValuesForValuePath(userRoot, I_Root, I_Path, I_Key, map);
        else
            return KeyValuesForValuePath(null, I_Root, I_Path, I_Key, map);
    }

    //根据key来查找某个path下的所有 keyvalue值
    public static Map<String,String> SearchKeyValueMapFromPathByKey(int I_Root, String I_Path, String I_Key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        Map<String,String> map = new HashMap<>();
        if (I_Root == HKEY_LOCAL_MACHINE)
            return KeyValuesForKeyPath(systemRoot, I_Root, I_Path, I_Key, map);
        else if (I_Root == HKEY_CURRENT_USER)
            return KeyValuesForKeyPath(userRoot, I_Root, I_Path, I_Key, map);
        else
            return KeyValuesForKeyPath(null, I_Root, I_Path, I_Key, map);
    }


    public static List<String> SearchKeyListFromPathByKey(int I_Root, String I_Path, String I_Key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        List<String> list = new ArrayList<String>();
        if (I_Root == HKEY_LOCAL_MACHINE)
            return valuesForKeyPath(systemRoot, I_Root, I_Path, I_Key, list);
        else if (I_Root == HKEY_CURRENT_USER)
            return valuesForKeyPath(userRoot, I_Root, I_Path, I_Key, list);
        else
            return valuesForKeyPath(null, I_Root, I_Path, I_Key, list);
    }

    public static List<String> SearchPathListFromPathByPathName(int I_Root, String I_Path, String I_PathName)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        List<String> list = new ArrayList<String>();
        if (I_Root == HKEY_LOCAL_MACHINE)
            return valuesForPath(systemRoot, I_Root, I_Path, I_PathName, list);
        else if (I_Root == HKEY_CURRENT_USER)
            return valuesForPath(userRoot, I_Root, I_Path, I_PathName, list);
        else
            return valuesForPath(null, I_Root, I_Path, I_PathName, list);
    }




    // =====================

    private static int DeleteKey(Preferences root, int hkey, String key, String value)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {new Integer(hkey), toCstr(key), new Integer(KEY_ALL_ACCESS)});
        if (handles[1] != REG_SUCCESS)
            return handles[1];                                  // can be REG_NOTFOUND, REG_ACCESSDENIED
        int rc =((Integer) regDeleteValue.invoke(root, new Object[] {new Integer(handles[0]), toCstr(value)})).intValue();
        regCloseKey.invoke(root, new Object[] { new Integer(handles[0])});
        return rc;
    }

    private static int DeletePath(Preferences root, int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int rc =((Integer) regDeleteKey.invoke(root, new Object[] {new Integer(hkey), toCstr(key)})).intValue();
        return rc;                                                  // can REG_NOTFOUND, REG_ACCESSDENIED, REG_SUCCESS
    }

    private static String GetValueFormKey(Preferences root, int hkey, String path, String key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {new Integer(hkey), toCstr(path), new Integer(KEY_READ)});
        if (handles[1] != REG_SUCCESS)
            throw new IllegalArgumentException("The system can not find the specified path: '"+getParentKey(hkey)+"\\"+path+"'");
        byte[] valb = (byte[]) regQueryValueEx.invoke(root, new Object[] {new Integer(handles[0]), toCstr(key)});
        regCloseKey.invoke(root, new Object[] {new Integer(handles[0])});
        return (valb != null ? parseValue(valb) : queryValueForKey(hkey, path, key));
    }

    private static String queryValueForKey(int hkey, String path, String key) throws IOException {
        return queryValuesForPath(hkey, path).get(key);
    }

    private static Map<String,String> GetKeyValuesFormPath(Preferences root, int hkey, String path)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        HashMap<String, String> results = new HashMap<String,String>();
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {new Integer(hkey), toCstr(path), new Integer(KEY_READ)});
        if (handles[1] != REG_SUCCESS)
            throw new IllegalArgumentException("The system can not find the specified path: '"+getParentKey(hkey)+"\\"+path+"'");
        int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] {new Integer(handles[0])});
        int count = info[2];                            // Fixed: info[0] was being used here
        int maxlen = info[4];                           // while info[3] was being used here, causing wrong results
        for(int index=0; index<count; index++) {
            byte[] valb = (byte[]) regEnumValue.invoke(root, new Object[] {new Integer(handles[0]), new Integer(index), new Integer(maxlen + 1)});
            String vald = parseValue(valb);
            if(valb == null || vald.isEmpty())
                return queryValuesForPath(hkey, path);
            results.put(vald, GetValueFormKey(root, hkey, path, vald));
        }
        regCloseKey.invoke(root, new Object[] {new Integer(handles[0])});
        return results;
    }






    private static Map<String,String> KeyValuesForKeyPath(Preferences root, int hkey, String path, String key, Map<String,String> map)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {

        try {
            if (!isDirectory(root, hkey, path)) {
                takeKeyValueInMapForKey(hkey, path, key, map);
            } else {
                List<String> subKeys = GetSubPathsFormPath(root, hkey, path);
                for (String subkey : subKeys) {
                    String newPath = path + "\\" + subkey;
                    if (isDirectory(root, hkey, newPath)) {
                        KeyValuesForKeyPath(root, hkey, newPath, key, map);
                    }
                    takeKeyValueInMapForKey(hkey, newPath, key, map);
                }
            }
        } catch (Exception Exc) {

        }
        return map;
    }


    private static Map<String,String> KeyValuesForValuePath(Preferences root, int hkey, String path, String key, Map<String,String> map)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {

        try {
            if (!isDirectory(root, hkey, path)) {
                takeKeyValueInMapForValue(hkey, path, key, map);
            } else {
                List<String> subKeys = GetSubPathsFormPath(root, hkey, path);
                for (String subkey : subKeys) {
                    String newPath = path + "\\" + subkey;
                    if (isDirectory(root, hkey, newPath)) {
                        KeyValuesForValuePath(root, hkey, newPath, key, map);
                    }
                    takeKeyValueInMapForValue(hkey, newPath, key, map);
                }
            }
        } catch (Exception Exc) {

        }
        return map;
    }





    private static List<String> valuesForKeyPath(Preferences root, int hkey, String path, String key, List<String> list)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        try
        {
            if(!isDirectory(root, hkey, path)) {
                takeValueInListForKey(hkey, path, key, list);
            } else {
                List<String> subKeys = GetSubPathsFormPath(root, hkey, path);
                for(String subkey: subKeys) {
                    String newPath = path+"\\"+subkey;
                    if(isDirectory(root, hkey, newPath))
                        valuesForKeyPath(root, hkey, newPath, key, list);
                    takeValueInListForKey(hkey, newPath, key, list);
                }
            }
        }
        catch (Exception Exc)
        {

        }
        return list;
    }

    private static List<String> valuesForPath(Preferences root, int hkey, String path, String key, List<String> list)  {

        try
        {
            List<String> subKeys = GetSubPathsFormPath(root, hkey, path);
            for(String subkey: subKeys) {
                String newPath = path+"\\"+subkey;
                if(isDirectory(root, hkey, newPath)) {
                    valuesForPath(root, hkey, newPath, key, list);
                }
                if(subkey.equals(key))
                {
                    list.add(newPath);
                }

            }
        }
        catch (Exception Exc)
        {

        }

        return list;
    }

    /**
     * Takes value for key in list
     * @param hkey
     * @param path
     * @param key
     * @param list
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IOException
     */
    private static void takeValueInListForKey(int hkey, String path, String key, List<String> list)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        String value = GetValueFormKey(hkey, path, key);
        if(value != null)
            list.add(value);
    }

    private static void takeKeyValueInMapForKey(int hkey, String path, String key, Map<String,String> map)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
        String value = GetValueFormKey(hkey, path, key);
        if(value != null)
        {
            map.put(path + "\\" + key ,value);
        }
    }

    private static void takeKeyValueInMapForValue(int hkey, String path, String value, Map<String,String> map)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {

       Map<String,String> curMap =   GetKeyValuesFormPath(hkey, path);
        for(String key : curMap.keySet()){
           if(curMap.get(key).equals(value))
           {
               map.put(path + "\\" + key ,value);
           }
        }
    }




    /**
     * Checks if the path has more subkeys or not
     * @param root
     * @param hkey
     * @param path
     * @return true if path has subkeys otherwise false
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static boolean isDirectory(Preferences root, int hkey, String path)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return !GetSubPathsFormPath(root, hkey, path).isEmpty();
    }

    private static List<String> GetSubPathsFormPath(Preferences root, int hkey, String path)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<String> results = new ArrayList<String>();
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {new Integer(hkey), toCstr(path), new Integer(KEY_READ)});
        if (handles[1] != REG_SUCCESS)
            throw new IllegalArgumentException("The system can not find the specified path: '"+getParentKey(hkey)+"\\"+path+"'");
        int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] {new Integer(handles[0])});
        int count  = info[0]; // Fix: info[2] was being used here with wrong results. Suggested by davenpcj, confirmed by Petrucio
        int maxlen = info[3]; // value length max
        for(int index=0; index<count; index++) {
            byte[] valb = (byte[]) regEnumKeyEx.invoke(root, new Object[] {new Integer(handles[0]), new Integer(index), new Integer(maxlen + 1)});
            results.add(parseValue(valb));
        }
        regCloseKey.invoke(root, new Object[] {new Integer(handles[0])});
        return results;
    }

    private static int [] CreatePath(Preferences root, int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return (int[]) regCreateKeyEx.invoke(root, new Object[] {new Integer(hkey), toCstr(key)});
    }

    private static void SetValueToKey(Preferences root, int hkey, String key, String valueName, String value)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {new Integer(hkey), toCstr(key), new Integer(KEY_ALL_ACCESS)});
        regSetValueEx.invoke(root, new Object[] {new Integer(handles[0]), toCstr(valueName), toCstr(value)});
        regCloseKey.invoke(root, new Object[] {new Integer(handles[0])});
    }

    /**
     * Makes cmd query for the given hkey and path then executes the query
     * @param hkey
     * @param path
     * @return the map containing all results in form of key(s) and value(s) obtained by executing query
     * @throws IOException
     */
    private static Map<String, String> queryValuesForPath(int hkey, String path) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        Map<String, String> map = new HashMap<String, String>();
        Process process = Runtime.getRuntime().exec("reg query \""+getParentKey(hkey)+"\\" + path + "\"");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while((line = reader.readLine()) != null) {
            if(!line.contains("REG_"))
                continue;
            StringTokenizer tokenizer = new StringTokenizer(line, " \t");
            while(tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if(token.startsWith("REG_"))
                    builder.append("\t ");
                else
                    builder.append(token).append(" ");
            }
            String[] arr = builder.toString().split("\t");
            map.put(arr[0].trim(), arr[1].trim());
            builder.setLength(0);
        }
        return map;
    }

    /**
     * Determines the string equivalent of hkey
     * @param hkey
     * @return string equivalent of hkey
     */
    private static String getParentKey(int hkey) {
        if(hkey == HKEY_CLASSES_ROOT)
            return CLASSES_ROOT;
        else if(hkey == HKEY_CURRENT_USER)
            return CURRENT_USER;
        else if(hkey == HKEY_LOCAL_MACHINE)
            return LOCAL_MACHINE;
        return null;
    }

    /**
     *Intern method which adds the trailing \0 for the handle with java.dll
     * @param str String
     * @return byte[]
     */
    private static byte[] toCstr(String str) {
        if(str == null)
            str = "";
        return (str += "\0").getBytes();
    }

    /**
     * Method removes the trailing \0 which is returned from the java.dll (just if the last sign is a \0)
     * @param buf the byte[] buffer which every read method returns
     * @return String a parsed string without the trailing \0
     */
    private static String parseValue(byte buf[]) {
        if(buf == null)
            return null;
        String ret = new String(buf);
        if(ret.charAt(ret.length()-1) == '\0')
            return ret.substring(0, ret.length()-1);
        return ret;
    }
}
