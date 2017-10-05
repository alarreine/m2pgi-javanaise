package jvn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JvnCacheObject {

    private JvnObject object;
    private HashMap<JvnState, List<JvnRemoteServer>> stateClient;
    private Serializable latesContent = null;


    public JvnCacheObject(JvnObject object, JvnState initialState, JvnRemoteServer initialServer) {
        this.object = object;
        this.stateClient = new HashMap<>();
        List<JvnRemoteServer> listClient = new ArrayList<>();
        listClient.add(initialServer);
        this.stateClient.put(initialState, listClient);
    }

    /**
     * Return object
     * @return Object instance
     */
    public JvnObject getObject() {
        return object;
    }

    /**
     * Set Object
     * @param object
     */
    public void setObject(JvnObject object) {
        this.object = object;
    }

    /**
     * Get List of Client that are using the object with an state
     * @param state
     * @return List object
     */
    public List<JvnRemoteServer> getRemoteServerByState(JvnState state) {
        return stateClient.get(state);
    }

    /**
     * Add Referece of the remote client
     * @param state State of the object in the client side
     * @param client reference of the client
     */
    public void setRemoteClientByState(JvnState state, JvnRemoteServer client) {
        if (stateClient.containsKey(state)) {
            stateClient.get(state).add(client);
        } else {
            List<JvnRemoteServer> listClient = new ArrayList<>();
            listClient.add(client);
            stateClient.put(state, listClient);
        }
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
