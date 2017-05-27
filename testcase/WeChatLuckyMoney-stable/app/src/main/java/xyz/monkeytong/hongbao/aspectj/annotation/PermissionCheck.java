package xyz.monkeytong.hongbao.aspectj.annotation;

/**
 * Created by Administrator on 2017/5/16.
 * 权限的Annotation
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionCheck {
    public String[] permission();
}

