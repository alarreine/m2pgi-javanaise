package jvn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class JvnCacheObject {

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
}
