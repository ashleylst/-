package Aspect;

/**
 * Created by Administrator on 2017/5/18.
 * 检查所有需要获取手机状态权限的方法，记录在ReadPhoneState中
 */


import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.io.IOException;
import java.util.Arrays;

@Aspect
public class CheckReadPhoneState {

    private String Tag = "ReadPhoneState";


    @Pointcut("call (* android.telephony..*(..)) || call (* android.content.Intent..*(..)) || call(* android.content.Context.getSystemService(..))&& !within(CheckReadPhoneState) ")
    public void CheckAPI() {}

    @Before("CheckAPI()")
    public void CheckBefore(JoinPoint thisJoinPoint){

        Signature Sig = thisJoinPoint.getSignature();
        String className = Sig.getDeclaringType().getSimpleName();//获取类名
        String methodName = Sig.getName(); //获取方法

        StackTraceElement[] cause = Thread.currentThread().getStackTrace();
        Log.e(Tag, Arrays.toString(cause));

        MethodSignature mSig = (MethodSignature)thisJoinPoint.getSignature();
        String[] parameterNames = mSig.getParameterNames();
        Object[] parameterValues = thisJoinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++ ) {

            Log.e(Tag, "ArgName: " + parameterNames[i]);
            Log.e(Tag, "ArgValue: " + parameterValues[i]);

        }

        SourceLocation srcLoc = thisJoinPoint.getSourceLocation(); //获取源程序位置
        String srcName = srcLoc.getFileName();

        Log.e(Tag,"source location: " + srcName + " classname: " + className + " methodname: " + methodName);


    }
}

