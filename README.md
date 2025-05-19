# Result Service
## Result - REST Services

[![Supported JVM Versions](https://img.shields.io/badge/JVM-17-brightgreen.svg?style=for-the-badge&logo=Java)](https://openjdk.java.net/install/)

Result Service è parte della suite di servizi per la verifica delle informazioni sulla
Trasparenza dei siti web delle Pubbliche amministrazioni italiane.
 
## Result Service

Result Service è il componente che si occupa di gestire i risultati delle verifiche 
sulla corrispondenza dei siti degli enti pubblici italiani in relazione al decreto legge 33/2013 
sulla transparenza.

Result Service fornisce alcuni servizi REST utilizzabili in produzione per:

 - inserire, aggiornare e cancellare all'interno del servizio le informazioni di una verifica 
   effettuata su un sito web di una PA
 - visualizzare i dati di una verifica su un sito web
 - mostrare la lista delle verifiche effettuate
 - esportare in CSV i risultati delle validazioni presenti

Il Result Service si occupa anche di cancellare dal Minio i sorgenti HTML e gli Screenshot associati
ai risultati di validazione ogni qual volta i risultati di validazione vengono cancellati.
Per questo motivo è necessario fornire al servizio, tramite la propria configurazione, anche le
variabili d'accesso al `Minio`.

I servizi REST sono documentati tramite OpenAPI consultabile all'indirizzo /swagger-ui/index.html.
L'OpenAPI del servizio di devel è disponibile all'indirizzo https://dica33.ba.cnr.it/result-service/swagger-ui/index.html.

### Sicurezza

Gli endpoint REST di questo servizio sono protetti tramite autenticazione OAuth con Bearer Token.
E' necessario configurare l'idp da utilizzare per validare i token OAuth tramite le due proprietà
mostrare nell'esempio seguente:

```
    - spring.security.oauth2.resourceserver.jwt.issuer-uri=https://dica33.ba.cnr.it/keycloak/realms/trasparenzai
    - spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://dica33.ba.cnr.it/keycloak/realms/trasparenzai/protocol/openid-connect/certs
```

Per l'accesso in HTTP GET all'API è sufficiente essere autenticati, per gli endpoint accessibili
con PUT/POST/DELETE è necessario oltre che essere autenticati che il token OAuth contenga un 
role ADMIN o SUPERUSER.

# <img src="https://www.docker.com/wp-content/uploads/2021/10/Moby-logo-sm.png" width=80> Startup

#### _Per avviare una istanza del result-service con postgres locale_

Il result-service può essere facilmente installato via docker compose su server Linux utilizzando il file 
docker-compose.yml presente in questo repository.

Accertati di aver installato docker e il plugin di docker `compose` dove vuoi installare il result-service e in seguito
esegui il comando successivo per un setup di esempio.

```
curl -fsSL https://raw.githubusercontent.com/trasparenzai/result-service/main/first-setup.sh -o first-setup.sh && sh first-setup.sh
```

Configurare nel file `.env` l'url e le credenziali per l'accesso al Minio, tramite le variabili d'ambiente
`MINIO_URL`, `MINIO_ACCESS_KEY`, `MINIO_ACCESS_PASSWORD`.

Collegarsi a http://localhost:8080/swagger-ui/index.html per visualizzare la documentazione degli endpoint REST presenti nel servizio. 

## Backups

Il servizio mantiene le informazioni relative alla configurazione nel db postgres, quindi è opportuno fare il backup
del database a scadenza regolare. Nel repository è presente un file di esempio [backups.sh](https://github.com/trasparenzai/result-service/blob/main/backups.sh) per effettuare i backup.

All'interno dello script backups.sh è necessario impostare il corretto path dove si trova il docker-compose.yml del progetto, tramite la
variabile `SERVICE_DIR`.

## 👏 Come Contribuire 

E' possibile contribuire a questo progetto utilizzando le modalità standard della comunità opensource 
(issue + pull request) e siamo grati alla comunità per ogni contribuito a correggere bug e miglioramenti.

## 📄 Licenza

Transparency Results Service è concesso in licenza GNU AFFERO GENERAL PUBLIC LICENSE, come si trova 
nel file [LICENSE][l].

[l]: https://github.com/trasparenzai/result-service/blob/master/LICENSE
