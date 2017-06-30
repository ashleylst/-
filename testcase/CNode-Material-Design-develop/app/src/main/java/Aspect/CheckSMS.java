package Aspect;
/**
 * Created by Administrator on 2017/5/18.
 * 检查所有需要SMS权限的方法，包括READ_SMS,WRITE_SMS,RECEIVE_SMS，记录在SMSMessage中
 */
 
import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import java.util.Arrays;


@Aspect
public class CheckSMS {
    private String Tag = "SMSMessage";


    @Pointcut("call (* android.provider.Telephony..*(..)) || call (* com.android.mms.transaction..*(..)) && !within(CheckReadSMS) ")
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
            
            Log.e(Tag, "ArgName: " + parameterNames[i]);
            Log.e(Tag, "ArgValue: " + parameterValues[i]);
            
        }

        SourceLocation srcLoc = thisJoinPoint.getSourceLocation(); //获取源程序位置
        String srcName = srcLoc.getFileName();

        
        Log.e(Tag,"source location: " + srcName + " classname: " + className + " methodname: " + methodName);
    }
}
