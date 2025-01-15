# Transparency Results Service
## Transparency Results - REST Services

[![Supported JVM Versions](https://img.shields.io/badge/JVM-21-brightgreen.svg?style=for-the-badge&logo=Java)](https://openjdk.java.net/install/)

Transparency Results Service è parte della suite di servizi per la verifica delle informazioni sulla
Trasparenza dei siti web delle Pubbliche amministrazioni italiane.
 
## Transparency Results Service

Transparency Results Service è il componente che si occupa di gestire i risultati delle verifiche 
sulla corrispondenza dei siti degli enti pubblici italiani in relazione al decreto legge 33/2013 
sulla transparenza.

Transparency Results Service fornisce alcuni servizi REST utilizzabili in produzione per:

 - inserire, aggiornare e cancellare all'interno del servizio le informazioni di una verifica 
   effettuata su un sito web di una PA
 - visualizzare i dati di una verifica su un sito web
 - mostrare la lista delle verifiche effettuate
 - esportare in CSV i risultati delle validazioni presenti

I servizi REST sono documentati tramite OpenAPI consultabile all'indirizzo /swagger-ui/index.html.
L'OpenAPI del servizio di devel è disponibile all'indirizzo https://dica33.ba.cnr.it/result-service/swagger-ui/index.html.

# <img src="https://www.docker.com/wp-content/uploads/2021/10/Moby-logo-sm.png" width=80> Startup

#### _Per avviare una istanza del result-service con postgres locale_

Il result-service può essere facilmente installato via docker compose su server Linux utilizzando il file 
docker-compose.yml presente in questo repository.

Accertati di aver installato docker e il plugin di docker `compose` dove vuoi installare il result-service e in seguito
esegui il comando successivo per un setup di esempio.

```
curl -fsSL https://raw.githubusercontent.com/cnr-anac/result-service/main/first-setup.sh -o first-setup.sh && sh first-setup.sh
```

Collegarsi a http://localhost:8080/swagger-ui/index.html per visualizzare la documentazione degli endpoint REST presenti nel servizio. 

## Backups

Il servizio mantiene le informazioni relative alla configurazione nel db postgres, quindi è opportuno fare il backup
del database a scadenza regolare. Nel repository è presente un file di esempio [backups.sh](https://github.com/cnr-anac/result-service/blob/main/backups.sh) per effettuare i backup.

All'interno dello script backups.sh è necessario impostare il corretto path dove si trova il docker-compose.yml del progetto, tramite la
variabile `SERVICE_DIR`.

## 👏 Come Contribuire 

E' possibile contribuire a questo progetto utilizzando le modalità standard della comunità opensource 
(issue + pull request) e siamo grati alla comunità per ogni contribuito a correggere bug e miglioramenti.

## 📄 Licenza

Transparency Results Service è concesso in licenza GNU AFFERO GENERAL PUBLIC LICENSE, come si trova 
nel file [LICENSE][l].

[l]: https://github.com/cnr-anac/result-service/blob/master/LICENSE
