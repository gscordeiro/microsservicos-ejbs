# Microsserviços com EJBs — Casa do Código

Projeto de estudo e referência de **Java EE / Jakarta EE** demonstrando EJBs, JPA, JAX-RS, JSF, transações, processamento assíncrono e comunicação entre módulos. Construído com **Java 21**, **Jakarta EE 11** e **WildFly 40**, é um build Maven multi-módulo (reactor) com um POM pai agregando três módulos de aplicação e um módulo de suporte a testes.

---

## Visão Geral da Arquitetura

```
┌────────────────────────────────────────────────────┐
│              Projeto Raiz (POM)                    │
│                                                    │
│   ┌─────────────────┐   ┌──────────────────────┐   │
│   │    javacred     │   │  javacred-corretora  │   │
│   │  (WAR · EJB)    │   │   (WAR · EJB Lite)   │   │
│   │  WildFly 40     │   │   WildFly 40         │   │
│   │  Porta :8080    │   │   Porta :8081        │   │
│   └────────▲────────┘   └──────────────────────┘   │
│            │ Remote EJB / REST                     │
│   ┌────────┴────────┐                              │
│   │javacred-desktop │                              │
│   │   (JAR · CLI)   │                              │
│   └─────────────────┘                              │
└────────────────────────────────────────────────────┘
```

| Módulo                                         | Tipo | Descrição resumida |
|------------------------------------------------|------|--------------------|
| [javacred](#javacred)                          | WAR | Sistema de crédito/empréstimos com JSF, EJBs completos, REST, SSE e JPA |
| [javacred-corretora](#javacred-corretora)      | WAR | API REST de corretora com cotações, ordens e HTTP caching (EJB Lite) |
| [javacred-desktop](#javacred-desktop)          | JAR | Cliente desktop que consome EJBs remotos e endpoints REST do javacred |
| javacred-test-support | JAR | Infra de testes compartilhada — `WildFlyContainer` (Testcontainers) que sobe o WildFly e faz deploy dos WARs nos testes de integração |

### Stack Tecnológico

| Camada | Tecnologia |
|--------|-----------|
| Servidor | WildFly 40.0.0.Final em **todos** os ambientes — runtime via Bootable JAR (`wildfly-jar-maven-plugin`) e testes na imagem Docker `quay.io/wildfly/wildfly:40.0.0.Final-jdk21` (Testcontainers). Versão única definida em `version.wildfly` no POM pai |
| Plataforma | Jakarta EE 11 · Java 21 |
| EJB | Stateless, Stateful, Singleton, @Asynchronous, Timer |
| Persistência | JPA (Jakarta Persistence 3.2) + Hibernate ORM 7 · H2 (file-based) |
| REST | JAX-RS 4.0 + RESTEasy 7.0.2 · AsyncResponse · SSE · ETags |
| Web UI | Jakarta Faces 4.1 (Mojarra) + PrimeFaces 15 |
| Serialização | Jackson 2.21 |
| Testes | JUnit 5 · Arquillian · Mockito · Testcontainers (WildFly em Docker) |

---

## Pré-requisitos

- **Java 21+**
- **Maven** não é necessário — o projeto inclui o **Maven Wrapper** (`mvnw`/`mvnw.cmd`) na raiz, que baixa o Maven automaticamente. Todos os comandos rodam **a partir da raiz** do projeto.
- **Docker** apenas para os testes de integração (Testcontainers sobe o WildFly automaticamente). Não é preciso instalar o WildFly localmente.

> Windows: troque `./mvnw` por `mvnw.cmd` em todos os comandos.

---

## Módulos

- [javacred — Sistema de Crédito](#javacred)
- [javacred-corretora — API de Corretora](#javacred-corretora)
- [javacred-desktop — Cliente Desktop / Testes de Integração](#javacred-desktop)

### Rodando todos os testes

```bash
./mvnw verify
```

Roda os testes de unidade (`test`) e de integração (`verify`) de todos os módulos. Os testes de integração rodam na fase `verify` porque dependem do empacotamento do WAR para fazer o deploy no container WildFly.

---

# javacred

> [↑ Voltar à visão geral](#visão-geral-da-arquitetura) · [javacred-corretora →](#javacred-corretora) · [javacred-desktop →](#javacred-desktop)

Sistema de gerenciamento de crédito e empréstimos. Demonstra o uso completo de EJBs (Stateless, Stateful, Singleton), JPA com relacionamentos, JAX-RS com resposta assíncrona e SSE, JSF com PrimeFaces e múltiplas estratégias de processamento paralelo.

## Como executar

```bash
./mvnw -pl javacred wildfly-jar:dev-watch
# disponível em http://localhost:8080/javacred
```

`dev-watch` sobe o WildFly em modo *bootable JAR*, faz o deploy do WAR e fica observando alterações no código-fonte para redeploy automático.

**Testes** (unidade + integração, este último sobe o WildFly em Docker via Testcontainers):

```bash
./mvnw -pl javacred verify
```

## Interfaces disponíveis para testar

Com a aplicação no ar, as mesmas funcionalidades (EJB + JPA por baixo) podem ser exercitadas por qualquer uma destas interfaces:

| Interface | Onde | Melhor para |
|-----------|------|-------------|
| **Swagger UI** | `http://localhost:8080/javacred/swagger-ui.html` | Explorar e executar a API clicando em *Try it out* — sem instalar nada |
| **Arquivo `.http`** | `javacred/src/main/http/javacred.http` | Rodar dentro da IDE (HTTP Client do IntelliJ / extensão *REST Client* do VS Code); roda de cima para baixo capturando os ids automaticamente |
| **curl** | terminal | Exemplos copiáveis e scripts |
| **Telas JSF** | `http://localhost:8080/javacred` | Interface web original (em remoção) |

> Base REST: `http://localhost:8080/javacred/rest`. O documento OpenAPI fica em `http://localhost:8080/openapi`.
> No Windows, use `curl.exe` no PowerShell (lá `curl` é alias de `Invoke-WebRequest`).
> **Regra de negócio:** todo contrato exige um **índice de correção**. Comece pela funcionalidade 1 para descobrir o `id` do índice e reutilize-o nos cadastros.

## Funcionalidades e como testar

### 1. Listar índices de correção
Descobre o `id` do índice exigido nos cadastros de cliente e empréstimo.
- **Swagger UI:** grupo *Índices* → `GET /rest/indice` → *Try it out* → *Execute*
- **curl:** `curl http://localhost:8080/javacred/rest/indice`
- **.http:** requisição *"Listar índices"* (captura `indiceId`)
- **JSF:** aparece como combo "Índice de correção" nas telas de cliente e empréstimo

### 2. Cadastrar cliente com contratos (cascade JPA `Cliente → Contratos`)
- **Swagger UI:** grupo *Clientes* → `POST /rest/cliente`
- **curl:**
  ```bash
  curl -X POST http://localhost:8080/javacred/rest/cliente \
    -H "Content-Type: application/json" \
    -d '{"nome":"Maria","preferencial":true,
         "contratos":[{"descricao":"A","indiceCorrecao":{"id":1}}]}'
  ```
- **.http:** requisição *"Cadastrar cliente já com contratos"* (captura `clienteId`)
- **JSF:** `cliente.jsf` — preencha nome, adicione contratos e escolha o índice de cada um
- *Validação:* contrato sem índice → **HTTP 422**

### 3. Listar clientes
- **Swagger UI:** grupo *Clientes* → `GET /rest/cliente/todos`
- **curl:** `curl http://localhost:8080/javacred/rest/cliente/todos`
- **.http:** requisição *"Listar todos os clientes"*
- **JSF:** `clientes.jsf`

### 4. Registrar empréstimo (EJB + análise de crédito + rollback)
- **Swagger UI:** grupo *Empréstimos* → `POST /rest/emprestimo`
- **curl:**
  ```bash
  curl -X POST http://localhost:8080/javacred/rest/emprestimo \
    -H "Content-Type: application/json" \
    -d '{"descricao":"Financiamento","valor":60000.0,
         "cliente":{"id":1},"indiceCorrecao":{"id":1}}'
  ```
- **.http:** requisição *"Registrar empréstimo para cliente PREFERENCIAL"* (captura `contratoId`)
- **JSF:** `emprestimo.jsf` — combos de cliente e índice obrigatórios
- *Validação:* sem índice → **HTTP 422**

### 5. Listar empréstimos / contratos
- **Swagger UI:** grupo *Empréstimos* → `GET /rest/emprestimo/contratos`
- **curl:** `curl http://localhost:8080/javacred/rest/emprestimo/contratos`
- **.http:** requisição *"Listar contratos/empréstimos"*
- **JSF:** `emprestimos.jsf`

### 6. Quitar empréstimo (transação `REQUIRES_NEW` + registro de quitação)
- **Swagger UI:** grupo *Empréstimos* → `POST /rest/emprestimo/{id}/quitar`
- **curl:** `curl -X POST http://localhost:8080/javacred/rest/emprestimo/1/quitar`
- **.http:** requisição *"Quitar o empréstimo recém-registrado"*
- **JSF:** `emprestimos.jsf` — botão *Quitar* na linha do contrato
- *Validação:* id inexistente → **HTTP 404**

### 7. Correção de saldo — benchmark de concorrência
Compara estratégias de processamento paralelo (síncrono × paralelo × `ManagedExecutorService`).
- **Setup (gerar massa):**
  - **Swagger UI:** grupo *Correção de saldo* → `POST /rest/correcao/gerar-contratos`
  - **curl:** `curl -X POST http://localhost:8080/javacred/rest/correcao/gerar-contratos`
- **Executar cada estratégia** (devolve `Benchmark` com duração e saldo geral):
  - **Swagger UI:** `POST /rest/correcao/sync` · `/paralelo` · `/mes`
  - **curl:** `curl -X POST http://localhost:8080/javacred/rest/correcao/sync` (idem `paralelo`, `mes`)
  - **.http:** requisições *"Correção SÍNCRONA / PARALELA / via MES"*
- **JSF:** `ajustaContratos.jsf` — botões com medição de desempenho na tela
- *Total de contratos:* `GET /rest/correcao/total`
- **Versão assíncrona (SSE + `CompletableFuture`)** — Jakarta Concurrency 3.1: empurra o resultado quando o processamento termina, sem polling:
  - **curl (streaming):** `curl -N http://localhost:8080/javacred/rest/correcao/async-stream`
  - **.http:** requisição *"Correção ASSÍNCRONA via SSE"* (melhor visto com `curl -N`)
  - **Swagger UI:** lista o endpoint, mas não renderiza o stream

### 8. Cotação de moedas — REST assíncrono (`@Suspended`/`AsyncResponse`)
- **Swagger UI:** grupo *Cotações* → `GET /rest/cotacao/dolar/{quantidade}`
- **curl:** `curl http://localhost:8080/javacred/rest/cotacao/dolar/10`
- **JSF:** não há tela (somente REST)

### 9. Acompanhamento de cotações — Server-Sent Events (SSE)
- **curl (streaming):** `curl -N http://localhost:8080/javacred/rest/acompanhar-cotacao/dolar`
  (no Windows: `curl.exe -N ...`)
- **Swagger UI:** lista o endpoint, mas não renderiza o stream — use `curl -N`
- **JSF:** não há tela (somente REST)

## Configuração do Módulo

| Item | Valor |
|------|-------|
| Packaging | WAR |
| Java | 21 |
| Jakarta EE | 11.0.0 |
| Servidor | WildFly 40.0.0.Final (Bootable JAR; testes na imagem `…:40.0.0.Final-jdk21`) |
| Context Root | `/javacred` |
| Banco de dados | H2 file · `~/temp/javacred_db` (modo `AUTO_SERVER`) |
| JNDI datasource | `java:/datasources/JavacredDS` |

> O caminho `~/temp/javacred_db` vale ao rodar o app (`dev-watch`), onde `~` é o seu home. Nos testes de integração o WildFly roda em container com `user.home=/tmp`, então o H2 fica em `/tmp/temp/...` **dentro do container** — efêmero, descartado ao fim do teste.

**Layers Galleon provisionados:** `jaxrs`, `ejb`, `jpa`, `jsf`, `microprofile-openapi`, `h2-driver`

## Estrutura de Diretórios

```
javacred/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/br/com/casadocodigo/javacred/
    │   │   ├── StartupConfig.java
    │   │   ├── entidades/
    │   │   ├── ejbs/
    │   │   ├── control/
    │   │   ├── rest/
    │   │   │   └── sse/
    │   │   ├── servlet/
    │   │   └── exceptions/
    │   ├── http/
    │   │   └── javacred.http        # requisições REST (IntelliJ/VS Code HTTP Client)
    │   ├── resources/META-INF/
    │   │   └── persistence.xml
    │   └── webapp/
    │       ├── WEB-INF/
    │       │   ├── web.xml
    │       │   ├── beans.xml
    │       │   ├── faces-config.xml
    │       │   └── javacred-ds.xml
    │       ├── index.html
    │       ├── swagger-ui.html      # Swagger UI (webjar) apontando para /openapi
    │       ├── cliente.xhtml
    │       ├── clientes.xhtml
    │       ├── emprestimo.xhtml
    │       ├── emprestimos.xhtml
    │       └── ajustaContratos.xhtml
    └── test/
        └── java/br/com/casadocodigo/javacred/
```

## Pacotes e Classes

### `entidades/` — Modelo de Domínio (JPA)

| Classe | Tipo | Descrição |
|--------|------|-----------|
| `Cliente` | `@Entity` | Cliente com lista de contratos (`@OneToMany`) |
| `Contrato` | `@Entity` | Contrato de empréstimo vinculado a cliente e índice |
| `Indice` | `@Entity` | Índice de correção monetária (`@OneToMany` IndiceValor) |
| `IndiceValor` | `@Entity` | Valor do índice por mês (`YearMonth`) |
| `RegistroEmprestimo` | `@Entity` | Log de transações (tipo: `CONTRATACAO` / `QUITACAO`) |
| `PropostaQuitacao` | DTO | Proposta de quitação antecipada (não persistida) |
| `FormaPagamento` | `enum` | `VISTA` (30%), `ATE_6_VEZES` (20%), `MAIS_6_VEZES` (10%) |
| `OffsetDateTimeConverter` | `@Converter` | Serialização JPA de `OffsetDateTime` |
| `YearMonthConverter` | `@Converter` | Serialização JPA de `YearMonth` ↔ `Date` |

### `ejbs/` — Session Beans

| Classe | Tipo EJB | Responsabilidade |
|--------|----------|-----------------|
| `ClienteBean` | `@Stateful @RequestScoped` | CRUD de clientes com `PersistenceContextType.EXTENDED` |
| `ContratoBean` | `@Stateless @Path /contrato` | Gestão de contratos + endpoints REST |
| `EmprestimoBean` | `@Stateless` | Orquestração de empréstimos (criação e quitação) |
| `NegociacaoBean` | `@Stateful @StatefulTimeout(30s)` | Sessão de negociação com estado (proposta de desconto) |
| `ServicoAnaliseFinanceira` | `@Stateless` | Análise financeira e aprovação de crédito |
| `AjustadorContratoBean` | `@Stateless @Asynchronous` | Ajuste paralelo de contratos por índice |
| `AjustadorContratoCallable` | `Callable<Double>` | Tarefa para `ManagedExecutorService` |
| `CalculadoraFinanciamento` | `@Stateless @Path /financiamento` | Simulação de financiamento + REST |
| `CalculadoraFinanciamentoRemota` | `@Remote` interface | Contrato para chamada remota via EJB client |
| `CalculadoraSimplesBean` | `@Stateless` | Cálculos simples de percentual |

**Destaque — Estratégias de Processamento Paralelo em `ContratoBean`:**

O módulo demonstra cinco abordagens diferentes para processar contratos em paralelo:

```
corrigeContratos()          → sequencial / síncrono
corrigeContratosAsync()     → @Asynchronous com Future<Double>
corrigeContratosPar()       → injeção de AjustadorContratoBean
corrigeContratosAsyncPar()  → combinação de async + paralelo
corrigeContratosViaMES1/2() → ManagedExecutorService com Callable
```

### `control/` — Controladores JSF

| Classe | Escopo | Responsabilidade |
|--------|--------|-----------------|
| `ClienteController` | `@Named @ViewScoped` | UI de gerenciamento de clientes |
| `EmprestimoController` | `@Named @RequestScoped` | UI de criação de empréstimos |
| `AtualizadorSaldoController` | `@Named @ViewScoped` | UI de correção de contratos com benchmarking |
| `GeradorContratos` | `@Stateless @Path /gera-contratos` | Geração de dados de teste via REST |
| `ClienteConverter` | `@FacesConverter` | Converte `Cliente` para/de `String` em componentes JSF |
| `Benchmark` | utilitário | Medição de tempo de execução das estratégias |

### `rest/` — Endpoints JAX-RS

| Classe | Path | Descrição |
|--------|------|-----------|
| `JavacredApplication` | `@ApplicationPath /rest` | Bootstrap JAX-RS |
| `CotacaoMoedasBean` | `/cotacao` | Cotações de moedas com `AsyncResponse` e `CompletionStage` |
| `GerenciadorCotacoesBean` | SSE broadcast | `@Singleton` com `@Schedule` publicando cotações a cada 5 s |
| `JacksonProducer` | `@Provider` | `ObjectMapper` customizado para serialização JSON |
| `Cotacao` | DTO | Quantidade, valor e par de moedas |

**Padrões REST implementados:**
- `AsyncResponse` com `ManagedExecutorService` para cotações assíncronas
- Server-Sent Events (SSE) para stream de cotações em tempo real
- API Rx (`rx().get()`) com `CompletionStage` para chamadas reativas encadeadas

### `servlet/` — Servlets Demonstrativos

| Classe | Propósito |
|--------|-----------|
| `CalculadoraFinanciamentoServlet` | Demonstra lookup JNDI de EJB local |
| `NegociacaoBeanServlet` | Demonstra EJB Stateful em sessão HTTP |

### `exceptions/` — Hierarquia de Exceções

| Classe | Tipo | Comportamento Transacional |
|--------|------|---------------------------|
| `JavacredException` | `RuntimeException` | — |
| `JavacredApplicationException` | `@ApplicationException` | rollback controlado |
| `NegociacaoException` | `@ApplicationException(rollback=true)` | sempre faz rollback |

### `StartupConfig` — Inicialização

`@Singleton @Startup` que inicializa dados de índices no banco ao subir a aplicação.

## Páginas JSF

| Arquivo | Conteúdo |
|---------|----------|
| `cliente.xhtml` | Formulário de cliente com contratos aninhados e AJAX |
| `clientes.xhtml` | Listagem de clientes |
| `emprestimo.xhtml` | Formulário de criação de empréstimo |
| `emprestimos.xhtml` | Datatable de empréstimos com ação de quitação (PrimeFaces) |
| `ajustaContratos.xhtml` | UI de correção em lote com medição de desempenho das estratégias |

## Transações

- `@TransactionAttribute(REQUIRES_NEW)` no método `quitar()`
- `@TransactionTimeout` nos métodos de correção
- Rollback programático via `EJBContext.setRollbackOnly()`
- Exceções com `@ApplicationException` controlam o comportamento transacional

---

# javacred-corretora

> [↑ Voltar à visão geral](#visão-geral-da-arquitetura) · [← javacred](#javacred) · [javacred-desktop →](#javacred-desktop)

API REST de uma corretora de valores. Demonstra EJB Lite, versionamento de API, estratégias de HTTP caching com `CacheControl`, ETags e `Last-Modified`, além de log de consultas em banco de dados.

## Como executar

```bash
./mvnw -pl javacred-corretora wildfly-jar:dev-watch
# disponível em http://localhost:8081
```

Roda em paralelo com o `javacred` sem conflito: a corretora usa `port-offset=1` (HTTP em :8081), enquanto o `javacred` ocupa :8080.

**Testes**:

```bash
./mvnw -pl javacred-corretora verify
```

## Configuração do Módulo

| Item | Valor |
|------|-------|
| Packaging | WAR |
| Java | 21 |
| Jakarta EE | 11.0.0 (EJB Lite) |
| Servidor | WildFly 40.0.0.Final (Bootable JAR; testes na imagem `…:40.0.0.Final-jdk21`) |
| Banco de dados | H2 file · `~/temp/javacred_corretora_db` (modo `AUTO_SERVER`) |
| JNDI datasource | `java:/datasources/JavacredCorretoraDS` |

**Layers Galleon provisionados:** `jaxrs`, `ejb-lite`, `jpa`, `jsf`, `h2-driver`

## Estrutura de Diretórios

```
javacred-corretora/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/br/com/casadocodigo/javacred/corretora/
    │   │   ├── rest/
    │   │   ├── entity/
    │   │   └── cliente/
    │   ├── resources/META-INF/
    │   │   └── persistence.xml
    │   └── webapp/WEB-INF/
    │       └── javacred-corretora-ds.xml
    └── test/
        └── java/
```

## Pacotes e Classes

### `entity/` — Modelo de Domínio

| Classe | Tipo | Descrição |
|--------|------|-----------|
| `Ordem` | `@Entity` | Ordem de compra/venda com `NamedQueries` e timestamp de modificação (`@PreUpdate`) |
| `LogConsulta` | `@Entity` | Registro de cada consulta de cotação (IP, código, timestamp) |

### `cliente/` — DTOs de Entrada

| Classe | Descrição |
|--------|-----------|
| `Consulta` | DTO de requisição: código do ativo + quantidade |

### `rest/` — Endpoints JAX-RS

#### `RestApplication`
`@ApplicationPath("/")` — ponto de entrada JAX-RS.

#### `HelloWorldEndpoint` — `GET /hello`
Endpoint de sanidade (`ping`).

#### `CotacaoBean` — `/cotacao`

| Método HTTP | Path | Retorno | Observação |
|-------------|------|---------|------------|
| `GET` | `/cotacao/{codigo}/v0` | `Double` | Valor bruto da cotação |
| `GET` | `/cotacao/{codigo}` | `LogConsulta` | Cotação com log de consulta persistido |

Cada requisição gera um `LogConsulta` com IP do cliente e timestamp.

#### `CotacaoBeanV2` — `/cotacao/v2`

| Método HTTP | Path | Descrição |
|-------------|------|-----------|
| `GET` | `/cotacao/v2/{codigo}` | Cotação com `CacheControl(maxAge=15 min)` |
| `POST` | `/cotacao/v2` (form) | Cotação via form-encoded |
| `POST` | `/cotacao/v2` (JSON) | Cotação via JSON |

Demonstra múltiplos `@Consumes` e `CacheControl` em respostas REST.

#### `OrdemBean` — `/ordem`

| Método HTTP | Path | Estratégia de Cache | Comportamento |
|-------------|------|---------------------|--------------|
| `GET` | `/ordem/{id}` | `Last-Modified` + `If-Modified-Since` | `304` se não alterada, `204` se inexistente, `CacheControl(10s)` |
| `POST` | `/ordem` | — | Persiste ordem simples |
| `POST` | `/ordem/v2` | `ETag` + `If-None-Match` | `304`/`412` para falhas de precondição |

**Estratégias de HTTP Caching demonstradas:**

```
CacheControl com maxAge       → evita requisições desnecessárias
Last-Modified / If-Modified-Since  → 304 Not Modified para ordens inalteradas
ETag / If-None-Match          → validação de integridade em POSTs condicionais
```

---

# javacred-desktop

> [↑ Voltar à visão geral](#visão-geral-da-arquitetura) · [← javacred-corretora](#javacred-corretora)

Aplicação standalone (JAR com `main()`) que atua como cliente do módulo `javacred`. Demonstra invocação de EJBs remotos via JNDI, consumo de APIs REST (síncrono, assíncrono e reativo) e recepção de eventos SSE.

## Como executar

A `main` faz lookup remoto de EJB, então o `javacred` precisa estar no ar antes — suba-o com `./mvnw -pl javacred wildfly-jar:dev-watch` em outro terminal.

```bash
# uma vez, para publicar o jar de classes do javacred em ~/.m2
./mvnw install -pl javacred -am -DskipTests

./mvnw -pl javacred-desktop exec:java \
  -Dexec.mainClass="br.com.casadocodigo.javacred.desktop.FinanciamentoMain"
```

**Testes** de integração REST (sobem o WildFly via Testcontainers — não precisa do `javacred` rodando):

```bash
./mvnw -pl javacred-desktop -am verify
```

`-am` (also-make) garante que o `javacred.war` seja empacotado antes, já que os testes fazem deploy dele no container.

## Configuração do Módulo

| Item | Valor |
|------|-------|
| Packaging | JAR |
| Java | 21 |
| Depende de | `javacred` (classifier `classes`) |
| Servidor alvo | `http://localhost:8080/javacred` |

**Dependências principais:** JAX-RS Client · Jackson · WildFly EJB Client BOM · JUnit 5

## Estrutura de Diretórios

```
javacred-desktop/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/br/com/casadocodigo/javacred/
    │   │   └── desktop/
    │   │       └── FinanciamentoMain.java
    │   └── resources/
    │       ├── jboss-ejb-client.properties
    │       └── jndi.properties
    └── test/
        └── java/br/com/casadocodigo/javacred/
            └── restclient/
                ├── JavacredTestBase.java
                ├── ContratoBeanTest.java
                ├── SimuladorFinanciamentoTest.java
                ├── CotacaoMoedasBeanTest.java
                ├── CotacaoMoedasSSETest.java
                └── bean/
                    ├── MeuContrato.java
                    └── MeuCliente.java
```

## Classes e Testes

### `desktop/FinanciamentoMain` — Ponto de Entrada

Demonstra lookup JNDI de um EJB remoto e invocação via interface `@Remote`:

```
JNDI: ejb:/javacred/CalculadoraFinanciamento!<CalculadoraFinanciamentoRemota>
```

Configurado por `jboss-ejb-client.properties` (host `localhost`, porta `8080`) e `jndi.properties` (factory WildFly).

### `restclient/` — Testes de Integração REST

Todos herdam de `JavacredTestBase`, que registra um `WildFlyContainer` (Testcontainers), faz deploy do `javacred.war` e inicializa o `Client` JAX-RS apontando para a URL dinâmica do container (`javacredBaseUri()` → `http://<host>:<porta-mapeada>/javacred/rest`). Não há host/porta fixos: o Testcontainers mapeia a porta 8080 do container para uma porta livre do host.

#### `ContratoBeanTest`

Consome `GET /contrato/{id}` e `GET /contrato/todos`:

```java
// Objeto único
MeuContrato c = response.readEntity(MeuContrato.class);

// Coleção tipada
List<Contrato> contratos = mapper.readValue(json, new TypeReference<>(){});
```

#### `SimuladorFinanciamentoTest`

Invoca `GET /financiamento/simular?valor=12000&meses=10` e valida o resultado (`1320.0`).

#### `CotacaoMoedasBeanTest` — REST Assíncrono e Reativo

| Abordagem | API |
|-----------|-----|
| Síncrona | `.get()` |
| Assíncrona | `.async().get()` → `Future<Response>` |
| Reativa | `.rx().get()` → `CompletionStage<Response>` |
| Composição | `thenApply` + `thenCombine` para encadear conversões de moeda |

Exemplo de cadeia reativa:

```java
CompletionStage<Double> euroDolar = clientEUR.rx().get()
    .thenApply(r -> r.readEntity(Cotacao.class).getValor());

CompletionStage<Double> dolarReal = clientUSD.rx().get()
    .thenApply(r -> r.readEntity(Cotacao.class).getValor());

euroDolar.thenCombine(dolarReal, (e, d) -> e * d)
         .thenAccept(System.out::println);
```

#### `CotacaoMoedasSSETest` — Server-Sent Events

Conecta ao endpoint SSE do `javacred`, coleta eventos por 20 segundos e os imprime:

```java
SseEventSource source = SseEventSource.target(target).build();
source.register(
    event  -> System.out.println(event.readData(Cotacao.class)),
    error  -> System.err.println("Erro: " + error),
    ()     -> System.out.println("Stream finalizado")
);
```

### `restclient/bean/` — DTOs do Cliente

| Classe | Anotação | Descrição |
|--------|----------|-----------|
| `MeuContrato` | `@JsonIgnoreProperties(ignoreUnknown=true)` | Representação local de `Contrato` |
| `MeuCliente` | — | Cliente aninhado em `MeuContrato` |

---

> [↑ Voltar ao topo](#microsserviços-com-ejbs--casa-do-código)
