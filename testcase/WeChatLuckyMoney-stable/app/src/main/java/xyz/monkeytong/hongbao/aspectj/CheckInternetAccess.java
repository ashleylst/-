package xyz.monkeytong.hongbao.aspectj;

import android.os.Debug;
import android.util.Log;

import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.*;

import java.util.LinkedList;

import xyz.monkeytong.hongbao.aspectj.PermissionStruct;

/**
 * Created by Administrator on 2017/5/18.
 * 检查所有需要网络权限的方法，记录在INTERNET中
 */

@Aspect
public class CheckInternetAccess {

    public LinkedList<PermissionStruct> INTERNET = new LinkedList<>();//保存所有使用INTERNET权限的方法

    private String Tag = "InternetAccess";

    private static String buildStackTraceString(final StackTraceElement[] elements) {
        StringBuilder sb = new StringBuilder();
        if (elements != null && elements.length > 0) {
            for (StackTraceElement element : elements) {
                sb.append(element.toString());
            }
        }
        return sb.toString();
    }

    //private boolean flag = true;

    @Pointcut("call(* android.net..*(..)) || call (* android.webkit.WebView..*(..)) || call (* java.net.HttpURLConnection..*(..)) && !within(CheckInternetAccess) ")
    public void CheckAPI() {}

    @Before("CheckAPI()")
    public void CheckBefore(JoinPoint thisJoinPoint){
       // System.out.println("access to web");
        Signature Sig = thisJoinPoint.getSignature();
        String className = Sig.getDeclaringType().getSimpleName();//获取类名
        String methodName = Sig.getName(); //获取方法

        SourceLocation srcLoc = thisJoinPoint.getSourceLocation(); //获取源程序位置
        String srcName = srcLoc.getFileName();

        PermissionStruct tmp = new PermissionStruct(srcName, className, methodName);
        INTERNET.add(tmp);
        Log.e(Tag,"source location: " + srcName + " classname: " + className + " methodname: " + methodName);
    }

    @After("CheckAPI()")
    public void checkafter() {
         INTERNET.getLast().PrintResults();
         //Log.e(Tag, "index"+ INTERNET.lastIndexOf(INTERNET.getLast()) );
    }
}
