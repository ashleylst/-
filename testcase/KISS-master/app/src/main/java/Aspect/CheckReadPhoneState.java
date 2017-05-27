package Aspect;

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

import java.util.Arrays;

/**
 * Created by ke on 2017/5/22 0022.
 */
@Aspect
public class CheckReadPhoneState {

    private String Tag = "ReadPhoneState";

    //private boolean flag = true;

    @Pointcut("call (* android.telephony..*(..)) || call (* android.content.Intent..*(..)) || call(* android.content.Context.getSystemService(..))&& !within(CheckReadPhoneState) ")
    public void CheckAPI() {}

    @Before("CheckAPI()")
    public void CheckBefore(JoinPoint thisJoinPoint){
        // System.out.println("access to web");
        Signature Sig = thisJoinPoint.getSignature();
        String className = Sig.getDeclaringType().getSimpleName();//获取类名
        String methodName = Sig.getName(); //获取方法

        StackTraceElement[] cause = Thread.currentThread().getStackTrace();
        Log.e(Tag, Arrays.toString(cause));

        MethodSignature mSig = (MethodSignature)thisJoinPoint.getSignature();
        String[] parameterNames = mSig.getParameterNames();
        Object[] parameterValues = thisJoinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++ ) {
            //if(parameterValues[i].toString().indexOf("contact") != -1) {
                Log.e(Tag, "ArgName: " + parameterNames[i]);
                Log.e(Tag, "ArgValue: " + parameterValues[i]);
            //}
        }

        SourceLocation srcLoc = thisJoinPoint.getSourceLocation(); //获取源程序位置
        String srcName = srcLoc.getFileName();

        // PermissionStruct tmp = new PermissionStruct(srcName, className, methodName);
        // INTERNET.add(tmp);
        Log.e(Tag,"source location: " + srcName + " classname: " + className + " methodname: " + methodName);
    }
}
