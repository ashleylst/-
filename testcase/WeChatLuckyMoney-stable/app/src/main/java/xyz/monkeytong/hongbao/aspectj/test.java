package xyz.monkeytong.hongbao.aspectj;

/**
 * Created by Administrator on 2017/5/15.
 */

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import xyz.monkeytong.hongbao.activities.MainActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
/*
@Aspect
public class test {

    @Pointcut("call(* android.net..*(..)) || call (* android.webkit.WebView..*(..)) || call (* java.net.HttpURLConnection..*(..)) && !within(test) ")
    public void CheckAPI() {}

    @Before("CheckAPI()")
    public void CheckBefore(){
        System.out.println("access to web");
    }

}*/
