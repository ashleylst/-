package xyz.monkeytong.hongbao.aspectj;

import android.util.Log;

/**
 * Created by Administrator on 2017/5/18.
 */
public class PermissionStruct {

    public String PermSrcLoc;
    public String PermClassName;
    public String PermMethodName;

    PermissionStruct(String a, String b, String c) {
        PermSrcLoc = a;
        PermClassName = b;
        PermMethodName= c;
    }

    public void PrintResults(){
        Log.e("InternetAccess"," add source " + PermSrcLoc +" class "+  PermClassName +" method "+ PermMethodName );
        return;
    }
}
