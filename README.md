# pdf-api-watchdog

Aplicação Java que monitora a API de impressão de PDF da API iCFC, realizando autenticação automática e verificação periódica. Em caso de falha, envia um alerta por email.

---

## Como funciona

- Renova o token de autenticação a cada **24 horas**
- Verifica a impressão do PDF a cada **5 minutos**
- Envia **email de alerta** em caso de falha

---

## Estrutura do projeto

```
src/
├── main/
│   ├── java/br/com/pedroscheurer/
│   │   ├── configs/
│   │   │   └── EnvConfig.java              # Leitura das variáveis de ambiente
│   │   ├── models/
│   │   │   └── EmailModel.java             # Modelo do email
│   │   ├── services/
│   │   │   ├── EmailNotifier.java          # Envio de email via SMTP
│   │   │   ├── Scheduler.java             # Agendamento das tarefas
│   │   │   └── WatchdogApiClient.java     # Requisições HTTP
│   │   └── Main.java                       # Entrada da aplicação
│   └── resources/
│       └── logback.xml                     # Configuração de logging
└── test/
    └── java/br/com/pedroscheurer/
        ├── configs/
        │   └── EnvConfigTest.java
        ├── models/
        │   └── EmailModelTest.java
        └── services/
            └── WatchdogApiClientTest.java
```

---

## Tecnologias

- Java 25
- Maven
- Jakarta Mail — envio de email via SMTP
- Jackson — parsing de JSON
- SLF4J + Logback — logging com rotação diária de arquivos
- JUnit 5 + Mockito + GreenMail — testes automatizados
- Docker + Docker Compose — containerização

---

## Rodando com Docker

### Pré-requisitos

- Docker
- Docker Compose

### 1. Cria o arquivo `.env` na mesma pasta do `docker-compose.yml`

```env
MAIL_USERNAME=seu@gmail.com
MAIL_PASSWORD="xxxx xxxx xxxx xxxx"
MAIL_FROM=seu@gmail.com
MAIL_TO=destinatario@email.com
ROBO_EMAIL=robo@gmail.com
ROBO_PASSWORD=senha
```

> Para o Gmail, use uma [senha de app](https://myaccount.google.com/apppasswords) — não a senha normal da conta.

### 2. Cria o `docker-compose.yml`

```yaml
services:
  pdf-api-watchdog:
    image: pedroscheurer/pdf-api-watchdog:latest
    restart: always
    env_file:
      - .env
    volumes:
      - ./logs:/app/logs
```

### 3. Sobe o container

```bash
docker-compose up -d
```

### 4. Acompanha os logs

```bash
docker-compose logs -f
```

---

## Rodando localmente

### Pré-requisitos

- Java 25
- Maven

### 1. Clona o repositório

```bash
git clone https://github.com/pedroscheurer/pdf-api-watchdog.git
cd pdf-api-watchdog
```

### 2. Define as variáveis de ambiente na IDE

No IntelliJ: `Run → Edit Configurations → Environment Variables`

```
MAIL_USERNAME=seu@gmail.com
MAIL_PASSWORD=xxxx xxxx xxxx xxxx
MAIL_FROM=seu@gmail.com
MAIL_TO=destinatario@email.com
ROBOT_EMAIL=robo@gmail.com
ROBOT_PASSWORD=senha
```

### 3. Roda a aplicação

```bash
mvn exec:java -Dexec.mainClass="br.com.pedroscheurer.Main"
```

---

## Rodando os testes

```bash
mvn test
```

---

## Logs

Os logs são gravados em `logs/app.log` com rotação diária e retenção de 30 dias:

```
logs/
├── app.log
├── app-2026-04-28.log
└── app-2026-04-29.log
```

Formato:
```
29/04/2026 08:14:36 [INFO]  Scheduler — Token renovado com sucesso
29/04/2026 08:19:36 [INFO]  WatchdogApiClient — PDF gerado com sucesso
29/04/2026 08:24:36 [ERROR] WatchdogApiClient — Falha ao verificar PDF — status: 500
```

---

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `MAIL_USERNAME` | Usuário do SMTP |
| `MAIL_PASSWORD` | Senha de app do Gmail |
| `MAIL_FROM` | Remetente do email |
| `MAIL_TO` | Destinatário do email |
| `ROBOT_EMAIL` | Email do usuário da API ICFC |
| `ROBOT_PASSWORD` | Senha do usuário da API ICFC |
