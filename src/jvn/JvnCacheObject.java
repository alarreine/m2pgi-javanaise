package jvn;

import jvn.exception.JvnException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class JvnCacheObject implements Serializable{

    private JvnObject object;
    private List<JvnRemoteServer> listClient;
    private Serializable latesContent;
    private JvnState state;

    /**
     * Constructor
     * @param object
     * @param state
     * @param serverClient
     */
    public JvnCacheObject(JvnObject object, JvnState state, JvnRemoteServer serverClient) {
        this.object = object;
        this.state=state;
        getListClient().add(serverClient);

    }

    /**
     * Constructor
     * @param object
     * @param serverClient
     */
    public JvnCacheObject(JvnObject object, JvnRemoteServer serverClient){
        this.object = object;
        getListClient().add(serverClient);
    }

    public List<JvnRemoteServer> getListClient(){
        if(listClient==null){
            listClient = new ArrayList<>();
        }
        return listClient;
    }

    /**
     * Get the last updated content of the object
     * @return
     */
    public Serializable getLatesContent() {
        return latesContent;
    }

    /**
     * Set the last content of the object
     * @param latesContent
     */
    public void setLatesContent(Serializable latesContent) {
        this.latesContent = latesContent;
    }

    public JvnState getState() {
        return state;
    }

    public void setState(JvnState state) {
        this.state = state;
    }

    public JvnObject getObject() {
        return object;
    }

    public void setObject(JvnObject object) {
        this.object = object;
    }

    public void putLastContentAsCurrent() throws JvnException {
        setLatesContent(getObject().jvnInvalidateWriter());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JvnCacheObject that = (JvnCacheObject) o;

        if (object != null ? !object.equals(that.object) : that.object != null) return false;
        if (listClient != null ? !listClient.equals(that.listClient) : that.listClient != null) return false;
        if (latesContent != null ? !latesContent.equals(that.latesContent) : that.latesContent != null) return false;
        return state == that.state;
    }

    @Override
    public int hashCode() {
        int result = object != null ? object.hashCode() : 0;
        result = 31 * result + (listClient != null ? listClient.hashCode() : 0);
        result = 31 * result + (latesContent != null ? latesContent.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JvnCacheObject{" +
                "object=" + object +
                ", listClient=" + listClient +
                ", latesContent=" + latesContent +
                ", state=" + state +
                '}';
    }
}
