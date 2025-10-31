# Organized Scan

Aplicação **Spring Boot + Thymeleaf** para gestão de motocicletas, com **autenticação OAuth2 (GitHub)** e **PostgreSQL**.  
Inclui CRUDs de **Motorcycle** e **Portal**.

## 👥 Participantes

- **Julio Samuel De Oliveira — RM557453**  
- **Bruno Da Silva Souza — RM94346**  
- **Leonardo Da Silva Pereira — RM557598**

---

## 🔧 Stack

- **Java**: 17
- **Spring Boot**: 3.5.x  
  - Spring Web, Spring Data JPA, Spring Security (OAuth2 Client), Thymeleaf, Validation
- **Banco**: PostgreSQL (18)  
- **Migrations**: Flyway  
- **Build**: Gradle  
- **UI**: Bootstrap 5 (Thymeleaf)

---

## ✅ Requisitos

- **Java 17+** (recomendado 17 para casar com o projeto)
- **Gradle** (wrapper já incluso: `./gradlew`)
- **Docker + Docker Compose** (opcional, para subir PostgreSQL rapidamente)
- **Conta GitHub** para OAuth (criar OAuth App)

---

## 📦 Clonar e configurar

```bash
git clone https://github.com/JulioSamuelOliveira/organized-scan.git
cd organized-scan
```

### Variáveis de ambiente

Defina as variáveis para o OAuth do GitHub (e DB, se necessário):

**Linux/macOS (bash/zsh):**
```bash
export GITHUB_CLIENT_ID=Ov23liTzKRI5A47nomHv
export GITHUB_CLIENT_SECRET=fe8972c251403be3599a5f66c870ba7cfb145512

# Opcional, se quiser sobrepor
export DB_URL=jdbc:postgresql://127.0.0.1:5432/organizedscan
export DB_USER=postgres
export DB_PASS=postgres
```

**Windows (PowerShell):**
```powershell
setx GITHUB_CLIENT_ID "Ov23liTzKRI5A47nomHv"
setx GITHUB_CLIENT_SECRET "<fe8972c251403be3599a5f66c870ba7cfb145512"
setx DB_URL "jdbc:postgresql://127.0.0.1:5432/organizedscan"
setx DB_USER "postgres"
setx DB_PASS "postgres"
```

---

## 🐘 Banco de Dados (PostgreSQL)

### Opção A — Docker Compose (recomendado)

Crie/ajuste um `compose.yaml` no projeto:

```yaml
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
```

Suba o banco:
```bash
docker compose up -d
```

### Opção B — Local sem Docker
Crie o banco manualmente:
```sql
CREATE DATABASE organizedscan;
```

---

## ⚙️ Configurações do Spring

Use **profiles** para separar dev/produção.

`src/main/resources/application.properties`:
```properties
spring.application.name=organized-scan

# OAuth2 (resolve por env var)
spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}

# Método escondido (DELETE/PUT via form)
spring.mvc.hiddenmethod.filter.enabled=true

# Perfil padrão
spring.profiles.active=dev
```

`src/main/resources/application-dev.properties`:
```properties
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
```

> Se preferir rodar **sem** profiles, mova as propriedades de `application-dev.properties` para `application.properties`.

---

## 🗃️ Migrations (Flyway)

Crie as migrações em `src/main/resources/db/migration/`.

### `V1__schema.sql`
```sql
-- PORTAL
create table if not exists portal (
  id    bigserial primary key,
  type  varchar(40)  not null,
  name  varchar(255) not null,
  constraint ck_portal_type_enum
    check (type in ('QUICK_MAINTENANCE','SLOW_MAINTENANCE','POLICE_REPORT','RECOVERED_MOTORCYCLE'))
);
create index if not exists idx_portal_type on portal(type);

-- USERMOTTU
create table if not exists usermottu (
  id    bigserial primary key,
  name  varchar(255) not null,
  email varchar(255) not null unique
);

-- MOTORCYCLE
create table if not exists motorcycle (
  id                    bigserial primary key,
  type                  varchar(30)  not null,
  license_plate         varchar(20)  not null,
  chassi                varchar(50)  not null,
  rfid                  varchar(100) not null,
  portal                bigint       not null references portal(id) on delete restrict,
  problem_description   varchar(500),
  entry_date            date         not null,
  availability_forecast date         not null,
  constraint ck_moto_type_enum
    check (type in ('MOTTU_SPORT_110I','MOTTU_E','MOTTU_POP'))
);

create index if not exists idx_moto_type on motorcycle(type);
create index if not exists idx_moto_plate on motorcycle(license_plate);
create index if not exists idx_moto_portal on motorcycle(portal);
```

### `V2__seed.sql` (opcional — dados iniciais)
```sql
insert into portal (name, type) values
('Box Rápido Centro', 'QUICK_MAINTENANCE'),
('Oficina Lenta Zona Sul', 'SLOW_MAINTENANCE'),
('Delegacia 15º DP', 'POLICE_REPORT'),
('Pátio Recuperadas', 'RECOVERED_MOTORCYCLE');

-- usuário será criado no primeiro login via OAuth, mas pode semear:
insert into usermottu (name, email) values
('Usuário Demo', 'demo@example.com');
```

> Se você já criou tabelas manualmente, garanta que batem com esse schema.

---

## 🔐 GitHub OAuth (obrigatório)

1. Acesse **GitHub → Settings → Developer settings → OAuth Apps → New OAuth App**  
2. **Homepage URL**: `http://localhost:8080`  
3. **Authorization callback URL**: `http://localhost:8080/login/oauth2/code/github`  
4. Copie `Client ID` e `Client Secret` e exporte nas variáveis `GITHUB_CLIENT_ID` e `GITHUB_CLIENT_SECRET` (ver acima).

**Após o login**, o app redireciona para **`/motorcycle`**.

---

## ▶️ Rodando a aplicação

Com o banco ativo e variáveis configuradas:

```bash
./gradlew clean bootRun
```

Acesse: **http://localhost:8080**

Primeiro acesso pedirá login via **GitHub**.

---

## 🚦 Rotas Principais

- **GET `/`** → redireciona/links para módulos
- **GET `/motorcycle`** → lista com filtros (portal, data, tipo, placa)
- **GET `/motorcycle/form`** → criar moto
- **POST `/motorcycle/form`** → persistir nova moto
- **GET `/motorcycle/{id}/edit`** → editar moto
- **POST `/motorcycle/{id}`** → atualizar moto
- **DELETE `/motorcycle/{id}`** → remover moto
- **GET `/portal`** → lista de portais
- **GET `/portal/form`** → criar portal
- **POST `/portal/form`** → persistir portal
- **GET `/portal/{id}/edit`** → editar portal
- **POST `/portal/{id}`** → atualizar portal
- **DELETE `/portal/{id}`** → remover portal

> Os formulários utilizam **Thymeleaf** com Bootstrap 5.

---

## 🧪 Build & Testes

```bash
./gradlew build
```

---

## 🛠️ Solução de Problemas Comuns

### 1) **Redireciona sempre para autorizar no GitHub**
- Verifique `GITHUB_CLIENT_ID` e `GITHUB_CLIENT_SECRET`.
- Confirme a **Authorization callback URL**: `http://localhost:8080/login/oauth2/code/github`.

### 2) **`ERROR: relation "usermottu" does not exist`**
- Falta criar a tabela `usermottu`.  
  → Rode as migrações (`V1__schema.sql`) com Flyway  
  → Ou crie manualmente conforme script acima.  
- Certifique-se de que a app usa o **mesmo banco/schema**.

### 3) **`TemplateInputException` (Thymeleaf)**
- Geralmente HTML inválido (ex. `<option />` self-closing).  
- Expressões vazias `th:text=""` causam erro — remova ou use `th:if`.
- Templates esperados:
  - `templates/index.html`
  - `templates/motorcycle.html`
  - `templates/form-motorcycle.html`
  - `templates/portal.html`
  - `templates/form-portal.html`
  - `templates/error.html`

### 4) **Combo de Portais vazio no formulário**
- Verifique se há dados em `portal`:
  ```sql
  select count(*) from portal;
  select id, name, type from portal order by id;
  ```
- Garanta no controller:
  ```java
  @ModelAttribute("portais") public List<Portal> portais() { return portalRepository.findAll(); }
  ```
- No HTML, o select deve usar:
  ```html
  <select th:field="*{portal.id}">
    <option value="">Selecione</option>
    <option th:each="p : ${portais}" th:value="${p.id}" th:text="${p.name}"></option>
  </select>
  ```

### 5) **Erro de parâmetros: “Ensure that the compiler uses the '-parameters' flag.”**
- Adicione no `build.gradle` (JavaCompile):
  ```gradle
  tasks.withType(JavaCompile).configureEach {
      options.compilerArgs += ['-parameters']
  }
  ```
- Ou garanta que os `@RequestParam(name="...")` estejam com `name` explícito.

---

## 🔒 Segurança

- **OAuth2 (GitHub)** para login.
- Pós-login, usuário é verificado/registrado na tabela `usermottu` (email único).

---

## 🧹 Convenções / Organização

- **Camadas**: `controller` → `service` → `repository` → `entity`
- **Mensagens i18n**: via `MessageHelper` + `messages.properties`
- **Validações**: Bean Validation (Jakarta)

---

## 📄 Licença

Uso acadêmico/educacional.
