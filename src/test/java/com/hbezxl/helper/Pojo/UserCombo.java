package com.hbezxl.helper.Pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UserCombo {
    private ArrayList<User> userList;
    private HashSet<User>  userHash;
    private HashMap<String,User> UseMap;

    public UserCombo() {
    }

    public UserCombo(ArrayList<User> userList, HashSet<User> userHash, HashMap<String, User> useMap) {
        this.userList = userList;
        this.userHash = userHash;
        UseMap = useMap;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public HashSet<User> getUserHash() {
        return userHash;
    }

    public void setUserHash(HashSet<User> userHash) {
        this.userHash = userHash;
    }

    public HashMap<String, User> getUseMap() {
        return UseMap;
    }

    public void setUseMap(HashMap<String, User> useMap) {
        UseMap = useMap;
    }


}
