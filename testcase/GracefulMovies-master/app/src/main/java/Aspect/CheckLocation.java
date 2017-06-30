package Aspect;

/**
 * Created by Administrator on 2017/5/18.
 * 检查所有需要位置权限的方法，包括ACCESS_COARSE_LOCATION和ACCESS_FINE_LOCATION,记录在AccessLocation中
 */

import android.util.Log;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.*;

import java.util.LinkedList;


@Aspect
public class CheckLocation {

    //public LinkedList<PermissionStruct> INTERNET = new LinkedList<>();//保存所有使用INTERNET权限的方法

    private String Tag = "AccessLocation";

    //private boolean flag = true;

    @Pointcut("call(* com.android.server.LocationManagerService..*(..))  && !within(CheckLocation) ")
    public void CheckAPI() {}

    @Before("CheckAPI()")
    public void CheckBefore(JoinPoint thisJoinPoint){
        // System.out.println("access to web");
        Signature Sig = thisJoinPoint.getSignature();
        String className = Sig.getDeclaringType().getSimpleName();//获取类名
        String methodName = Sig.getName(); //获取方法

        SourceLocation srcLoc = thisJoinPoint.getSourceLocation(); //获取源程序位置
        String srcName = srcLoc.getFileName();

        // PermissionStruct tmp = new PermissionStruct(srcName, className, methodName);
        //INTERNET.add(tmp);
        Log.e(Tag,"source location: " + srcName + " classname: " + className + " methodname: " + methodName);
    }

    @After("CheckAPI()")
    public void checkafter() {
        // INTERNET.getLast().PrintResults();
        //Log.e(Tag, "index"+ INTERNET.lastIndexOf(INTERNET.getLast()) );
    }
}
