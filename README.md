# Microsserviços com EJBs — Casa do Código

Projeto de estudo e referência de **Java EE / Jakarta EE** demonstrando EJBs, JPA, JAX-RS, JSF, transações, processamento assíncrono e comunicação entre módulos. Construído com **Java 21**, **Jakarta EE 10** e **WildFly 30**, é composto por três módulos Maven independentes.

---

## Visão Geral da Arquitetura

```
┌────────────────────────────────────────────────────┐
│              Projeto Raiz (POM)                    │
│                                                    │
│   ┌─────────────────┐   ┌──────────────────────┐   │
│   │    javacred     │   │  javacred-corretora  │   │
│   │  (WAR · EJB)    │   │   (WAR · EJB Lite)   │   │
│   │  WildFly 30     │   │   WildFly 30         │   │
│   │  Porta :8080    │   │   Porta :8080        │   │
│   └────────┬────────┘   └──────────────────────┘   │
│            │ Remote EJB / REST                     │
│   ┌────────▼────────┐                              │
│   │javacred-desktop │                              │
│   │   (JAR · CLI)   │                              │
│   └─────────────────┘                              │
└────────────────────────────────────────────────────┘
```

| Módulo | Tipo | Descrição resumida |
|--------|------|--------------------|
| [javacred](#javacred) | WAR | Sistema de crédito/empréstimos com JSF, EJBs completos, REST, SSE e JPA |
| [javacred-corretora](#javacred-corretora) | WAR | API REST de corretora com cotações, ordens e HTTP caching (EJB Lite) |
| [javacred-desktop](#javacred-desktop) | JAR | Cliente desktop que consome EJBs remotos e endpoints REST do javacred |

### Stack Tecnológico

| Camada | Tecnologia |
|--------|-----------|
| Servidor | WildFly 30.0.1 (provisionado via `wildfly-jar-maven-plugin`) |
| Plataforma | Jakarta EE 10 · Java 21 |
| EJB | Stateless, Stateful, Singleton, @Asynchronous, Timer |
| Persistência | JPA + Hibernate · H2 (file-based) |
| REST | JAX-RS + RESTEasy 6.2.6 · AsyncResponse · SSE · ETags |
| Web UI | JSF 2.3 + PrimeFaces 13.0.0 |
| Serialização | Jackson 2.16 |
| Testes | JUnit 5 · Arquillian · Mockito |

---

## Módulos

- [javacred — Sistema de Crédito](#javacred)
- [javacred-corretora — API de Corretora](#javacred-corretora)
- [javacred-desktop — Cliente Desktop / Testes de Integração](#javacred-desktop)

---

# javacred

> [↑ Voltar à visão geral](#visão-geral-da-arquitetura) · [javacred-corretora →](#javacred-corretora) · [javacred-desktop →](#javacred-desktop)

Sistema de gerenciamento de crédito e empréstimos. Demonstra o uso completo de EJBs (Stateless, Stateful, Singleton), JPA com relacionamentos, JAX-RS com resposta assíncrona e SSE, JSF com PrimeFaces e múltiplas estratégias de processamento paralelo.

## Configuração do Módulo

| Item | Valor |
|------|-------|
| Packaging | WAR |
| Java | 21 |
| Jakarta EE | 10.0.0 |
| Servidor | WildFly 30.0.1 (Bootable JAR) |
| Context Root | `/javacred` |
| Banco de dados | H2 file · `~/tmp/javacred_db` |
| JNDI datasource | `java:/datasources/JavacredDS` |

**Layers Galleon provisionados:** `jaxrs`, `ejb`, `jpa`, `jsf`, `h2-driver`

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
    │   ├── resources/META-INF/
    │   │   └── persistence.xml
    │   └── webapp/
    │       ├── WEB-INF/
    │       │   ├── web.xml
    │       │   ├── beans.xml
    │       │   ├── faces-config.xml
    │       │   └── javacred-ds.xml
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

## Configuração do Módulo

| Item | Valor |
|------|-------|
| Packaging | WAR |
| Java | 21 |
| Jakarta EE | 10.0.0 (EJB Lite) |
| Servidor | WildFly 30.0.1 (Bootable JAR) |
| Banco de dados | H2 file · `~/Temp/javacred_corretora_db` |
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

Todos herdam de `JavacredTestBase`, que inicializa o `Client` JAX-RS uma única vez (`@BeforeAll`) apontando para `http://localhost:8080/javacred/rest`.

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

## Como Executar

### Pré-requisitos

- Java 21+
- Maven não é necessário — cada módulo inclui o **Maven Wrapper** (`mvnw`/`mvnw.cmd`), que baixa automaticamente a versão correta do Maven na primeira execução

### Subindo o javacred

```bash
cd javacred
./mvnw wildfly-jar:dev-watch
# Disponível em http://localhost:8080/javacred
```

> Windows: `mvnw.cmd wildfly-jar:dev-watch`

### Subindo o javacred-corretora

```bash
cd javacred-corretora
./mvnw wildfly-jar:dev-watch
# Disponível em http://localhost:8080
```

> Windows: `mvnw.cmd wildfly-jar:dev-watch`

### Executando o desktop (EJB Remoto)

Com o `javacred` rodando:

```bash
cd javacred-desktop
./mvnw exec:java -Dexec.mainClass="br.com.casadocodigo.javacred.desktop.FinanciamentoMain"
```

> Windows: `mvnw.cmd exec:java -Dexec.mainClass="br.com.casadocodigo.javacred.desktop.FinanciamentoMain"`

### Executando os testes de integração REST

Com o `javacred` rodando:

```bash
cd javacred-desktop
./mvnw test
```

> Windows: `mvnw.cmd test`

---

> [↑ Voltar ao topo](#microsserviços-com-ejbs--casa-do-código)
