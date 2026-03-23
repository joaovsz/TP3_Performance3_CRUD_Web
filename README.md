# TP4 - Sistema Integrado RH + Produtos

Projeto unificado de Engenharia de Software que integra:
- módulo de RH (TP3);
- módulo de Produtos com CQRS (TP2);
- esteira CI/CD no GitHub Actions com validação de cobertura.

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
- Regra de aumento salarial encapsulada em `Salario.aplicarAumento(...)`
- Interface comum `EntidadeRastreavel` aplicada a `Funcionario` e `Produto`
- Testes unitários e de integração dos dois módulos no mesmo build

## Tecnologias
- Java 17
- Spring Boot 3.2.6
- Spring MVC + Thymeleaf
- Spring Data JPA + H2
- JUnit 5 + Mockito + MockMvc
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

As configurações estão em `src/main/resources/application.properties` e os dois módulos usam o mesmo datasource H2 da aplicação.

### 5. Rodar testes

```bash
mvn test
```

### 6. Validar cobertura (mínimo configurado no `pom.xml`)

```bash
mvn verify
```

Relatório JaCoCo local:
- `target/site/jacoco/index.html`

## CI/CD no GitHub Actions
- Workflow: `.github/workflows/main.yml`
- Runner: `ubuntu-latest`
- Actions usadas:
  - `actions/setup-java`
  - `madrapps/jacoco-report`
- Disparo em `push` e `pull_request`
- Build executado no pipeline: `mvn -B clean verify`

### Como visualizar o pipeline
1. Envie sua branch para o GitHub.
2. Abra um Pull Request.
3. Vá na aba **Actions** e abra o workflow **CI**.
4. No PR, verifique o comentário automático de cobertura do JaCoCo.

## Estrutura principal

```text
src/main/java/br/com/faculdade/tp3      # módulo RH
src/main/java/com/tp2/engsoftware       # módulo Produtos (TP2)
src/test/java/br/com/faculdade/tp3      # testes RH
src/test/java/com/tp2/engsoftware       # testes Produtos
.github/workflows/main.yml              # pipeline CI
docs/RELATORIO_TP4.md                   # relatório da entrega
```
