package xyz.monkeytong.hongbao.aspectj;

/**
 * Created by Administrator on 2017/5/15.
 */

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;


import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
/*
@Aspect
public class CheckPermission {

    //所有需要检查的权限的定义
    private  String[] permission = {"android.permission.READ_SMS"
            , "android.permission.INTERNET"
            , "android.permission.READ_CONTACTS"
            ,"android.permission.ACCESS_FINE_LOCATION"
            ,"android.permission.ACCESS_COARSE_LOCATION"
            ,"android.permission.READ_HISTORY_BOOKMARKS"
            ,"android.permission.READ_PHONE_STATE"
            ,"android.permission.READ_SOCIAL_STREAM"
            ,"android.permission.READ_PROFILE"
            ,"android.permission.READ_EXTERNAL_STORAGE"
            ,"android.permission.READ_CALL_LOG"
            ,"android.permission.READ_CALENDAR"
            ,"android.permission.PROCESS_OUTGOING_CALLS"
            ,"android.permission.WRITE_SMS"
            ,"android.permission.SEND_SMS"
            ,"android.permission.RECEIVE_SMS"
            ,"android.permission.INSTALL_SHORTCUT"
            ,"android.permission.UNINSTALL_SHORTCUT"
            ,"android.permission.INSTALL_PACKAGES"
            ,"android.permission.RECORD_AUDIO"
    };


    //对所有xyz.monkeytong.hongbao.activities包和service包下的方法做检查
   // 如果检查utils/fragments包下的方法会有程序崩溃的情况，因此只能使用手动加注释的方法检查，需使用FindPermission.java下的Aspect来检查

    @Pointcut("execution(* xyz.monkeytong.hongbao.activities.MainActivity.*(..)) ")
    public void Check() {}

    //在调用方法前检查
    @Before("Check()")
    public void log(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();//获取当前处理中的方法
        String className = methodSignature.getDeclaringType().getSimpleName();//获取类名
        String methodName = methodSignature.getName();//获取方法名
        //对需要检查的权限进行依次遍历并打印log
        for(String i : permission)
        {
            if (ContextCompat.checkSelfPermission((Context) joinPoint.getTarget(), i)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e(className, className + ":" + methodName + "没有" + i + "的权限执行");
            }
            else{
                Log.e(className, className + ":" + methodName + "有" + i + "的权限执行");
            }
        }
    }
}*/
