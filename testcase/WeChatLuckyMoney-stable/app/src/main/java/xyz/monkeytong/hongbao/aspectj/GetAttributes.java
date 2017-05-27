package xyz.monkeytong.hongbao.aspectj;

import android.util.Log;

import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.*;

import java.util.LinkedList;
/**
 * Created by Administrator on 2017/5/21.
 */

/*
@Aspect
public class GetAttributes {

    @Pointcut("get(* xyz.monkeytong.hongbao.utils.ConnectivityUtil.*)")
    public void fieldAccess() {}

    @AfterReturning(pointcut = "fieldAccess()", returning = "field")
    public void afterFieldAccess(Object field, JoinPoint thisJoinPoint) {
        Log.e("access",thisJoinPoint.toLongString());
        Log.e("access","  " + thisJoinPoint.getSignature().getName());
        Log.e("access","  " + field);
    }
}*/
