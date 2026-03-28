# TP5 - Sistema Integrado RH + Produtos

[![CI-CD-TP5](https://github.com/joaovsz/TP3_Performance3_CRUD_Web/actions/workflows/main.yml/badge.svg)](https://github.com/joaovsz/TP3_Performance3_CRUD_Web/actions/workflows/main.yml)
[![Cobertura mínima](https://img.shields.io/badge/Cobertura-JaCoCo%2085%25%2B-brightgreen)](https://github.com/joaovsz/TP3_Performance3_CRUD_Web/actions/workflows/main.yml)

Projeto unificado de Engenharia de Software que integra:
- módulo de RH (TP5);
- módulo de Produtos com CQRS (TP2);
- esteira CI/CD com segurança e validação pós-deploy.

## Funcionalidades

### RH
- CRUD de funcionários
- Aumento salarial, promoção, demissão e exclusão definitiva
- Histórico de movimentações (`MovimentacaoRh`)
- API REST em `/api/rh`

### Produtos
- CRUD web de produtos
- API REST em `/api/produtos`
- Estrutura CQRS (`command` / `query`)

### Arquitetura e qualidade
- Refatoração com SRP (`RhValidator`)
- Value Object para CPF (`Cpf`)
- Polimorfismo para tipos de funcionário
- Regra de aumento salarial encapsulada em `Salario.aplicarAumento(...)`
- Interface comum `EntidadeRastreavel` aplicada a `Funcionario` e `Produto`
- Testes unitários e de integração no mesmo build

## Tecnologias
- Java 17
- Spring Boot 3.2.6
- Spring MVC + Thymeleaf
- Spring Data JPA + H2
- JUnit 5 + Mockito + MockMvc + Selenium
- JaCoCo

## Como executar

### 1. Subir a aplicação

```bash
mvn spring-boot:run
```

### 2. Acessar telas web
- Home/menu: `http://localhost:8080/`
- RH: `http://localhost:8080/rh/funcionarios`
- Produtos: `http://localhost:8080/produtos`

### 3. Acessar APIs
- Base RH: `http://localhost:8080/api/rh`
- Base Produtos: `http://localhost:8080/api/produtos`

Exemplos:
- `GET http://localhost:8080/api/rh/funcionarios`
- `GET http://localhost:8080/api/produtos`

### 4. Banco H2
- Console: `http://localhost:8080/h2-console`

As configurações estão em `src/main/resources/application.properties`.

### 5. Rodar testes

```bash
mvn test
```

### 6. Validar cobertura (mínimo no `pom.xml`)

```bash
mvn verify
```

Relatório JaCoCo local:
- `target/site/jacoco/index.html`

## CI/CD no GitHub Actions

- Workflow: `.github/workflows/main.yml`
- Nome: `CI-CD-TP5`
- Runner: `ubuntu-latest`
- Disparo: `push`, `pull_request`, `workflow_dispatch`

### Etapas do pipeline
1. `build-test`: build + testes + cobertura JaCoCo.
2. `codeql`: análise estática de segurança (SAST).
3. `deploy-test`: valida URL de teste + DAST com OWASP ZAP.
4. `deploy-production`: ambiente de produção com aprovação manual.
5. `post-deploy-validation`: Selenium pós-deploy (`PostDeployValidationSeleniumTest`).

### Secrets esperados
- `TEST_APP_URL`: URL pública do ambiente de teste (com `http://` ou `https://`).
- `PRODUCTION_APP_URL`: URL pública do ambiente de produção (com `http://` ou `https://`).
- `AWS_ROLE_TO_ASSUME` e `AWS_REGION` (opcional): OIDC para nuvem.

## Estrutura principal

```text
src/main/java/br/com/faculdade/tp5      # módulo RH
src/main/java/com/tp2/engsoftware       # módulo Produtos (TP2)
src/test/java/br/com/faculdade/tp5      # testes RH
src/test/java/com/tp2/engsoftware       # testes Produtos
.github/workflows/main.yml              # pipeline CI/CD
docs/RELATORIO_TP5.md                   # relatório da entrega
```
