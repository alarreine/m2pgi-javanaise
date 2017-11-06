# JAVANAISE_V2
## Ce qui a été fait
### Implementation du  Dynamic Proxy, classes:
* Annotation: `JvnProxyAction.java`
* DynamicProxy: `JvnDynamicProxy.java`
* InterfaceProxy: `ISentence.java`

### Extension 1 : Gestion de la saturation d’un cache client: 
Le client utilise [LinkedHashMap](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html) pour gérer le 
nombre d'objects dans le cache. 
On s'ensert du méthod [removeEldestEntry](https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html#removeEldestEntry-java.util.Map.Entry-) 
pour effacer le premier object ajouté dans le cache.

### Extension 3 : Traitement des pannes du coordinateur (branche feat/panne_client)
On a implmementé un `JvnCoordManager` qui gère les requêtes. Quand il détecte une ConnectException il se met en 
mode offline, puis il utilise le cache local. Dans la prochaine requête, il va essayer de se reconnecter. 

## Complement
* ConcurrentHashMap
* Nouveau layout fenêtre graphique
* Client multithread pour tester en mode Burst. `IrcBurst` 
* Integration Continue avec Travis

## Ce qui marche
* DynamicProxy

## Ce qui ne marche pas
Il y a un erreur de Unmarshal lorsqu'après une reprise sur panne le client synchronise son objet avec le coordinateur.
  
# AUTHOR
Gerardo LARREINEGABE
Michael ZODEHOUGAN
