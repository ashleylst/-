package xyz.monkeytong.hongbao.aspectj;

import android.util.Log;

import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.*;

import java.util.LinkedList;

/**
 * Created by Administrator on 2017/5/18.
 * 检查所有需要读取手机信息的方法，记录在READPHONESTATE中
 */


@Aspect
public class CheckReadPhoneState {
    public LinkedList<PermissionStruct> READPHONESTATE = new LinkedList<>();//保存所有使用READPHONESTATE权限的方法

    private String Tag = "ReadPhoneState";

    @Pointcut("call(* android.internal.telephony..*(..)) || call(* com.android.server.net..*(..)) && !within(CheckReadPhoneState) ")
    public void CheckAPI() {}

    @Before("CheckAPI()")
    public void CheckBefore(JoinPoint thisJoinPoint){

        Signature Sig = thisJoinPoint.getSignature();
        String className = Sig.getDeclaringType().getSimpleName();//获取类名
        String methodName = Sig.getName(); //获取方法

        SourceLocation srcLoc = thisJoinPoint.getSourceLocation(); //获取源程序位置
        String srcName = srcLoc.getFileName();

        PermissionStruct tmp = new PermissionStruct(srcName, className, methodName);
        READPHONESTATE.add(tmp);
        Log.e(Tag,"source location: " + srcName + " classname: " + className + " methodname: " + methodName);
    }

    @After("CheckAPI()")
    public void checkafter() {
        READPHONESTATE.getLast().PrintResults();
    }
}
