# JAVANAISE
Projet d'une Cache Distribuée
Version [V1.0](https://github.com/alarreine/m2pgi-javanaise/releases/tag/1.0)
Version [V2.0](https://github.com/alarreine/m2pgi-javanaise/releases/tag/v2.0)

[Rapport V1](https://alarreine.github.io/m2pgi-javanaise/Javanaise_v1)
[Rapport V2](https://alarreine.github.io/m2pgi-javanaise/Javanaise_v2)

## Compiler le projet
```sh
mkdir build
javac -d build/ $(find ./src -name "*.java")
```

## Comment lancer le projet
Il faut suivre dans cet ordre
### Lancement du Serveur
```sh
cd build
java jvn.coordinator.JvnCoordImpl &
```

### Lancement du client IRC
```sh
cd build
java irc.Irc &
```
  
# AUTHOR
Gerardo LARREINEGABE
Michael ZODEHOUGAN
