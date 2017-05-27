package Aspect;

/**
 * Created by Administrator on 2017/5/18.
 * 检查所有需要获取联系人权限的方法，记录在ReadContact中
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
        import java.lang.String;


@Aspect
public class CheckReadContact {
    private String Tag = "ReadContact";

    @Pointcut("call(* android.net.Uri..*(..)) || call (* com.android.mms.ui..*(..)) ||call (* com.android.contacts..*(..)) || call (* com.android.mms.data.Contact..*(..)) && !within(CheckReadContact) ")
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
            if(parameterValues[i].toString().indexOf("contact") != -1) {
                Log.e(Tag, "ArgName: " + parameterNames[i]);
                Log.e(Tag, "ArgValue: " + parameterValues[i]);
            }
        }


        SourceLocation srcLoc = thisJoinPoint.getSourceLocation(); //获取源程序位置
        String srcName = srcLoc.getFileName();

        
        Log.e(Tag,"source location: " + srcName + " classname: " + className + " methodname: " + methodName);
    }
}

