package xyz.monkeytong.hongbao.aspectj;

/**
 * Created by Administrator on 2017/5/16.
 * 这个检查权限是查询带有@Permissioncheck的方法的权限
 * 在需要检查权限的方法前要加上：
 * @PermissionCheck(permssion = {Manifest.permission.XXX
, Manifest.permission.XXX
, Manifest.permission.XXX
, Manifest.permission.XXX})
 */

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.util.Log;

import xyz.monkeytong.hongbao.aspectj.annotation.PermissionCheck;
/*
@Aspect
public class FindPermission {

    private static final int MY_PERMISSIONS_REQUEST_PERMISSION = 101;


    private static final String POINTCUT_METHOD_PERMISS_CHECK = "execution(@xyz.monkeytong.hongbao.aspectj.annotation.PermissionCheck * *(..)) && @annotation(permissionCheck)";
    //所有方法带有Permission注释的方法


    @Pointcut(POINTCUT_METHOD_PERMISS_CHECK)
    public void methodAnnotated(PermissionCheck permissionCheck) {

    }

    @Around("methodAnnotated(permissionCheck)")
    public Object around(ProceedingJoinPoint joinPoint, PermissionCheck permissionCheck) throws Throwable {
        Object result = null;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        if (permissionCheck.permission() != null && permissionCheck.permission().length != 0) {
            for (int i = 0; i < permissionCheck.permission().length; i++) {
                if (ContextCompat.checkSelfPermission((Context) joinPoint.getTarget(),
                        permissionCheck.permission()[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.e(className, className + ":" + methodName + "没有" + permissionCheck.permission()[i] + "的权限执行");
                }
                else{
                    Log.e(className, className + ":" + methodName + "有" + permissionCheck.permission()[i] + "的权限执行");
                }
            }
        }
        result = joinPoint.proceed();
        return result;
    }


    @After("methodAnnotated(permissionCheck)")
    public void after(JoinPoint joinPoint, PermissionCheck permissionCheck) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        Log.e(className, className + ":" + methodName + " on after");
    }

}*/