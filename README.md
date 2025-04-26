# Spring Boot Back Vectora backend

Solución de microservicios orquestada en un docker-compose donde cada microservicio de contextos de dominios (Cuentas y Transacciones) tienen una estructura de desarrollo con el patron Hexagonal Puerto Adaptador. Los dos microservicios mencionados anteriormente poseen sus pruebas unitarias y de integración.

** Antes de iniciar la ejecución asegurarse de tener instalados en su maquina docker, docker-compose, openjava-jdk 17 y maven (mvn).

## Exec whit Docker-Compose in Shell Console (Git Bash o Linux) this command for Run

- cd bank_vectora/
- sh ./start.sh

** Esto creara los contenedores y ejecutara la migracion de las estructuras de las tablas de los microservicios y la poblara con unos datos iniciales.

** En caso de que la migraciones no se ejecuten en el archivo de arranque ejecutarla manualmente con estos comandos en la misma raiz de la solución:

- mvn flyway:migrate -f ./mcs_account-service/pom.xml
- mvn flyway:migrate -f ./mcs_transaction-service/pom.xml

Luego de esto levantar o relevantar los servicios de contenedores de microservicios dependientes:

- docker-compose up -d account-service transaction-service


## Servidor de Descurimiento Eureka:

- Navegar: http://localhost:8080/


## Servidor de Cola de Mensajeria (Usuario: guest,  Password: guest):

- Navegar: http://localhost:15672/ 


## POST Login for get Token (Anonimo)

- http://localhost:8000/account-service/token/generate

Body Request:

{
    "username": "test-user",
    "password": "1234abcd"
}

** Este Token tiene una duración de 24 horas y debe ser aplicado en la cabecera de autorización de tipo Bearer Token en todas las peticiones.

## POST Create Account (Set Authorization Header Bearer Token: {Token_Obtained}):

- http://localhost:8000/account-service/accounts

Body:
    {
        "nombre": "Julio Mendez",
        "saldoInicial": 1114
    }

## GET One Acount (Set Authorization Header Bearer Token: {Token_Obtained}): 

- http://localhost:8000/account-service/accounts/1

## POST Create Transaction (Set Authorization Header Bearer Token: {Token_Obtained}):

- http://localhost:8000/transaction-service/transactions

Body:
    {
        "fromAccount": 2,
        "toAccount": 9,
        "monto": 1248
    }

** Valida que la cuenta destino "toAccount" exista en el microservicio de cuentas y si se procesa la transacción le suma el saldo transferido a la cuenta destino.

## GET Record Transactions (Set Authorization Header Bearer Token: {Token_Obtained}): 

- http://localhost:8000/transaction-service/transactions/2



## Exec whit Kubernete in Shell Console (Git Bash o Linux) this command for Run. (Se debe disponer de un contexto local de Cluster como Minikube, Kubernete-Docker/Desktop o Cluster Gestionado, ademas de tener instalado el cliente kubectl). En caso de usar Minikube levantar el cluster de esta forma para garantizar mejor rendimiento: minikube start --memory=4842 --cpus=4

- cd bank_vectora/
- sh ./apply_kubectl.sh

** Esto creara los pods de los microservicios, bases de datos y rabittmq con las replicas declaradas en su manifiestos y lo asignara un servicio a cada grupo de pods del contexto de dominio correspondiente y le asignara sus IPs privadas de cluster y externas publicas dependiendo el tipo de cluster si es gestionado. Si se usan cluster locales el proceso le creación de pods tarda un poco mas, esperar como 5 min.

-  kubectl get pods    
(Consultar todos los pods del deployment con sus status)

-  kubectl get services    
(Consultar todos los servicios con su status IP y Puertos privados y publicos)

Si se usa el coxtexto de Minikube o Kubernete-Docker/Desktop se puede obtener la IP publica navegable de los servicios expuestos de decubrimiento y gateway de la siguiente forma:

# Minikube

 ** Servidor de Descubrimiento Eureka:

    - minikube service eureka-server-service

 ** Servicio de Entrada Gateway (URL Base para las Peticiones en Postmant)

    - minikube service api-gateway-service

# Kubernete-Docker/Desktop

 ** Servidor de Descubrimiento Eureka:

    -  kubectl port-forward service/eureka-server-service 8080:80 
       (Luego Navegar con esta IP: http://127.0.0.1/:8080)

 ** Servicio de Entrada Gateway (URL Base para las Peticiones en Postmant)

    - kubectl port-forward service/api-gateway-service 9000:90
      (Luego Navegar con esta IP: http://127.0.0.1/:9000)
 
 -- NOTA: Si se usa Minikube el rendimiento de los deployment y replicas va depender de los recursos de computos tanto de memoria y volumen compartido del cluster y dado que este solo tiene un solo nodo. Para mejor rendimiento y rapide de despliegue y expocición dentro del cluster se garantiza en un cluster gestionado como AKS de Microsoft Azure o EKS de AWS.



## En la carpeta docs/ se encuentra una collection.postman donde estan todas las peticiones para probar los microservicios. Tambien el diagrama de la arquiteatura en modelo C2.