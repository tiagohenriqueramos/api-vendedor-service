# 📦 API de Vendedores — Cadastro Assíncrono

Essa aplicação implementa uma API REST para gerenciamento de Vendedores com processamento assíncrono via RabbitMQ. O cadastro retorna imediatamente um protocolo; o processamento é realizado em background. Falhas são persistidas em uma Dead Letter Queue (DLQ) no MongoDB para rastreabilidade e auditoria.

---

## 🎯 Objetivos
- CRUD completo de Vendedores
- Aplicar regras de negócio
- Processamento assíncrono
- Observabilidade e rastreabilidade
- Simular dependências externas via mock de API

---

## 🧭 Fluxo de Cadastro Assíncrono
1. Cliente envia `POST /v1/api/vendedor`
2. API valida dados de entrada
3. Consulta dados da Filial via API externa (mock)
4. Gera protocolo e matrícula
5. Publica evento no RabbitMQ
6. Retorna HTTP `201 Created` com o protocolo
7. Listener consome a fila e persiste no MongoDB
8. Em caso de erro, a mensagem é direcionada para a DLQ

---

## 🧱 Arquitetura e Princípios
- Clean Code e SOLID
- Separação clara de responsabilidades
- Processamento assíncrono para escalabilidade
- Rastreamento por protocolo
- Persistência de falhas (DLQ) para auditabilidade

---

## 🛠 Tecnologias
- Java 17
- Spring Boot 3+
- Spring Web
- Spring Data MongoDB
- Spring AMQP (RabbitMQ)
- MongoDB
- RabbitMQ
- JUnit 5 + Mockito
- Jackson
- Maven

---

## 📌 Regras de Negócio

- Matrícula
  - Gerada automaticamente
  - Única
  - Formato: `SEQUENCIAL-SUFIXO`
  - Sufixos suportados: `-OUT`, `-CLT`, `-PJ`

- Campos obrigatórios
  - Nome
  - CPF ou CNPJ (validação de dígitos verificadores)
  - E-mail (formato válido)
  - Tipo de contratação
  - Filial

- Regras específicas
  - `CLT` e `Outsourcing` → CPF obrigatório
  - `Pessoa Jurídica` → CNPJ obrigatório

---

## 🔁 Endpoints
| Método | Endpoint | Descrição |
| --- | --- | --- |
| POST | `/v1/api/vendedor` | Cadastro assíncrono |
| GET | `/v1/api/vendedor/{id}` | Buscar por ID |
| GET | `/v1/api/vendedor/matricula/{matricula}` | Buscar por matrícula |
| GET | `/v1/api/vendedor/protocolo/{protocolo}` | Consultar status |
| GET | `/v1/api/vendedor` | Listagem paginada |
| PUT | `/v1/api/vendedor/{id}` | Atualizar vendedor |
| DELETE | `/v1/api/vendedor/{id}` | Remover vendedor |

---

## 🧪 API de Filiais (Mock)

O mock simula a API de Filiais usando `json-server` para evitar dependências externas e garantir previsibilidade nos testes.

### ▶️ Subir o mock
1. Instalar `json-server`:
```bash
npm install -g json-server
```

2. Criar `db.json` (exemplo):
```json
{
  "filiais": [
    {
      "id": "1",
      "nome": "Filial Centro SP",
      "cnpj": "12345678000199",
      "cidade": "São Paulo",
      "uf": "SP",
      "tipo": "LOJA",
      "ativo": true,
      "dataCadastro": "2022-01-10",
      "dataUpdate": "2024-12-01"
    }
  ]
}
```

3. Subir o servidor:
```bash
json-server --watch db.json --port 8089
```

Endpoint disponível:
```
GET http://localhost:8089/filiais/1
```

Configuração de integração (exemplo `application.yml`):
```yaml
integration:
  filial:
    url: http://localhost:8089
```

---

## 📜 Exemplos de Resposta

- Cadastro recebido:
```json
{
  "protocolo": "06707785-3ff4-479b-a796-60fe02d9545d",
  "mensagem": "Cadastro recebido com sucesso"
}
```

- Consulta por protocolo — Sucesso:
```json
{
  "protocolo": "a56e8995-c9b3-4f94-a04f-7ab21dd028c6",
  "status": "PROCESSADO",
  "vendedor": {
    "id": "69664b7b5e3126e8ebf377af",
    "matricula": "2026011310410713-CLT",
    "nome": "Tiago H Ramos da Silva",
    "email": "joao.silva@casasbahia.com.br",
    "tipoContratacao": "CLT",
    "protocolo": "a56e8995-c9b3-4f94-a04f-7ab21dd028c6"
  }
}
```

- Consulta por protocolo — Erro (DLQ):
```json
{
  "protocolo": "06707785-3ff4-479b-a796-60fe02d9545d",
  "status": "ERRO",
  "erro": "Erro no processamento da mensagem",
  "vendedor": null
}
```

---

## ☠ Dead Letter Queue (DLQ)

Mensagens com erro são persistidas no MongoDB contendo:
- Payload original
- Exchange
- Routing Key
- Fila
- Motivo do erro
- Timestamp

Isso garante auditabilidade e rastreabilidade completa.

---

## 🧪 Testes Automatizados
- JUnit 5
- Mockito

Cobertura esperada:
- Cadastro assíncrono
- Consulta por protocolo
- Validações de negócio
- Fluxo de erro (DLQ)

---

## ▶️ Como Executar

Pré-requisitos:
- Java 17
- MongoDB
- RabbitMQ
- Node.js (para o mock)

Executar a aplicação Spring Boot normalmente (por IDE ou `mvn spring-boot:run`).

---

## 📌 Considerações Finais

Solução projetada para simular um cenário real de sistemas distribuídos, priorizando:
- Processamento assíncrono
- Resiliência
- Observabilidade
- Código limpo e organizado
# api-vendedor-service
# api-vendedor-service
