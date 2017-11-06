package jvn.inter;

import jvn.JvnProxyAction;
import jvn.JvnProxyAction.JvnActionType;

public interface ISentence {
    @JvnProxyAction(jvnProxyActionType = JvnActionType.WRITE)
    public void write(String text);

    @JvnProxyAction(jvnProxyActionType = JvnActionType.READ)
    public String read();

    @JvnProxyAction(jvnProxyActionType = JvnActionType.UNLOCK)
    public void unlock();

}

