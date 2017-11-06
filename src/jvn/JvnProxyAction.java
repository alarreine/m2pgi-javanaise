package jvn;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JvnProxyAction
{
    static enum JvnActionType
    {
        READ,
        WRITE,
        UNLOCK
    }
    JvnActionType jvnProxyActionType();
}