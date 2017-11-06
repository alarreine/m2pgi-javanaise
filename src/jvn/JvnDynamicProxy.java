package jvn;

import irc.Irc;
import jvn.exception.JvnException;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JvnDynamicProxy implements InvocationHandler {

    private JvnObject jo;

    public JvnDynamicProxy(JvnObject jo) {
        this.jo = jo;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if (method.isAnnotationPresent(JvnProxyAction.class)) {
            try {

                JvnProxyAction.JvnActionType lockType = method.getAnnotation(JvnProxyAction.class).jvnProxyActionType();

                switch (lockType) {
                    case READ:
                        jo.jvnLockRead();
                        break;
                    case WRITE:
                        jo.jvnLockWrite();
                        break;

                    case UNLOCK:
                        jo.jvnUnLock();
                        break;

                }

                result = method.invoke(jo.jvnGetObjectState(), args);

            }catch (IllegalArgumentException a){
                throw new IllegalArgumentException();

            }
            catch (Exception e) {
                Irc.logger.severe( e.getMessage());
            }
        }
        return result;
    }

    public static Object getProxyInstance(JvnLocalServer js, String name, Object obj)  {

        JvnObject o = null;
        try {
            o = js.jvnLookupObject(name);


        if (o == null) {
            o = js.jvnCreateObject((Serializable) obj);
            // after creation, I have a write lock on the object
            o.jvnUnLock();
            js.jvnRegisterObject(name, o);
        }
        } catch (JvnException e) {
            e.printStackTrace();
        }
        return Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new JvnDynamicProxy(o));
    }
}
