# Sistema RH - CRUD Web (TP3)

Sistema de RH desenvolvido em Java com interface web para gestão de funcionários, salários e movimentações de carreira.

## Funcionalidades

- CRUD de funcionários
  - Contratar
  - Listar
  - Editar cadastro
  - Excluir definitivamente
- Operações de RH
  - Aumentar salário
  - Promover
  - Demitir
- Entidades principais
  - `Funcionario`
  - `Salario`
  - `Departamento`
  - `MovimentacaoRh`
- API REST completa para operações de RH
- Interface web com Thymeleaf
- Tratamento robusto de erros (`fail early` e `fail gracefully`)
- Simulação de falhas
  - Timeout
  - Sobrecarga
- Testes avançados
  - Unitários
  - Integração
  - Parametrizados
  - Fuzz testing
  - Selenium (profile separado)

## Tecnologias

- Java 17
- Spring Boot 3.2.6
- Spring MVC + Thymeleaf
- Spring Data JPA + H2
- JUnit 5 + Mockito + MockMvc
- Selenium + WebDriverManager
- JaCoCo

## Como executar

### 1. Rodar aplicação

```bash
mvn spring-boot:run
```

Aplicação web:
- `http://localhost:8080/rh/funcionarios`

API REST:
- `http://localhost:8080/api/rh/funcionarios`

Console H2:
- `http://localhost:8080/h2-console`

### 2. Rodar testes automatizados

```bash
mvn test
```

### 3. Validar cobertura mínima com JaCoCo

```bash
mvn verify
```

Relatório gerado em:
- `target/site/jacoco/index.html`

### 4. Rodar testes Selenium

```bash
mvn test -Pselenium
```

Observação: requer navegador compatível com WebDriver disponível no ambiente.

## Estrutura do projeto

```text
src/main/java/br/com/faculdade/tp3
  |- config
  |- controller
  |- dto
  |- exception
  |- model
  |- repository
  |- service

src/main/resources
  |- templates/rh
  |- static/css
  |- static/js

src/test/java/br/com/faculdade/tp3
  |- unit
  |- integration
  |- fuzz
  |- selenium
```
