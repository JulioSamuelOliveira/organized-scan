# Organized Scan

Aplicação *Spring Boot + Thymeleaf* para gestão de motocicletas, com *autenticação OAuth2 (GitHub)* e *PostgreSQL*.  
Inclui CRUDs de *Motorcycle* e *Portal*.

## 👥 Participantes

- Julio Samuel De Oliveira — RM557453  
- Bruno Da Silva Souza — RM94346 
- Leonardo Da Silva Pereira — RM557598

---

## 🔧 Stack

- *Java*: 17
- *Spring Boot*: 3.5.x  
  - Spring Web, Spring Data JPA, Spring Security (OAuth2 Client), Thymeleaf, Validation
- *Banco*: PostgreSQL (18)  
- *Migrations*: Flyway  
- *Build*: Gradle  
- *UI*: Bootstrap 5 (Thymeleaf)

---

## ✅ Requisitos

- *Java 17+* (recomendado 17 para casar com o projeto)
- *Gradle* (wrapper já incluso: ./gradlew)
- *Docker + Docker Compose* (opcional, para subir PostgreSQL rapidamente)
- *Conta GitHub* para OAuth (criar OAuth App)

---

## 📦 Clonar e configurar

bash
git clone https://github.com/JulioSamuelOliveira/organized-scan.git
cd organized-scan


### Variáveis de ambiente

Defina as variáveis para o OAuth do GitHub:




GITHUB_CLIENT_ID=${GITHUB_CLIENT_ID}
GITHUB_CLIENT_SECRET=${GITHUB_CLIENT_SECRET}



---

## 🐘 Banco de Dados (PostgreSQL)

### Opção A — Docker Compose (recomendado)

Crie/ajuste um compose.yaml no projeto:

yaml
services:
  postgres:
    image: postgres:18
    container_name: organized-scan-postgres
    environment:
      POSTGRES_DB: organizedscan
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d organizedscan"]
      interval: 3s
      timeout: 3s
      retries: 5


Suba o banco:
bash
docker compose up -d


### Opção B — Local sem Docker
Crie o banco manualmente:
sql
CREATE DATABASE organizedscan;


---

## ⚙️ Configurações do Spring

Use *profiles* para separar dev/produção.

src/main/resources/application.properties:
properties
spring.application.name=organized-scan

# OAuth2 (resolve por env var)
spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}

# Método escondido (DELETE/PUT via form)
spring.mvc.hiddenmethod.filter.enabled=true

# Perfil padrão
spring.profiles.active=dev


src/main/resources/application-dev.properties:
properties
# DB DEV
spring.datasource.url=${DB_URL:jdbc:postgresql://127.0.0.1:5432/organizedscan}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASS:postgres}

# JPA/Flyway
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true

# Thymeleaf
spring.thymeleaf.cache=false


---

## 🔐 GitHub OAuth (obrigatório)

1. Acesse *GitHub → Settings → Developer settings → OAuth Apps → New OAuth App*  
2. *Homepage URL*: http://localhost:8080  
3. *Authorization callback URL*: http://localhost:8080/login/oauth2/code/github  
4. Copie Client ID e Client Secret e exporte nas variáveis GITHUB_CLIENT_ID e GITHUB_CLIENT_SECRET

*Após o login, o app redireciona para */motorcycle**.

---

## ▶️ Rodando a aplicação

Com o banco ativo e variáveis configuradas:

bash
./gradlew clean bootRun


Acesse: *http://localhost:8080*

Primeiro acesso pedirá login via *GitHub*.

---

## 🚦 Rotas Principais

- *GET /* → redireciona/links para módulos
- *GET /motorcycle* → lista com filtros (portal, data, tipo, placa)
- *GET /motorcycle/form* → criar moto
- *POST /motorcycle/form* → persistir nova moto
- *GET /motorcycle/{id}/edit* → editar moto
- *POST /motorcycle/{id}* → atualizar moto
- *DELETE /motorcycle/{id}* → remover moto
- *GET /portal* → lista de portais
- *GET /portal/form* → criar portal
- *POST /portal/form* → persistir portal
- *GET /portal/{id}/edit* → editar portal
- *POST /portal/{id}* → atualizar portal
- *DELETE /portal/{id}* → remover portal

> Os formulários utilizam *Thymeleaf* com Bootstrap 5.

---

## 🧪 Build & Testes

bash
./gradlew build


---

## 🔒 Segurança

- *OAuth2 (GitHub)* para login.
- Pós-login, usuário é verificado/registrado na tabela usermottu (email único).