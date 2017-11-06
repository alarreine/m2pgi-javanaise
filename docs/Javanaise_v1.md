# JAVANAISE_V1
## Ce qui a été fait
* Implementation de la classe `JvnCoordImpl` 
* Implementation de la classe `JvnInterceptorImpl`
* Implementation de la classe `JvnServerImpl`
* Implementation de la classe `JvnState`: Pour gérer les états

## Complement
* ConcurrentHashMap
* Nouveau layout fenêtre graphique
* Client multithread pour tester en mode Burst. `IrcBurst` 
* Integration Continue avec Travis

## Ce qui marche
* LockRead
* LockWrite
* Les méthods d'invalidation

## Ce qui ne marche pas

## Obs
* Logique d'invalidation. Lorqu'on a un client A qui a un object en RC et si le client B fait une modification sur l'object. Suit le client A fait un read, Il ne reçoit pas la MAJ de l'object modifié pour Client B  

# AUTHOR
Gerardo LARREINEGABE
Mickael ZODEHOUGAN