# Relatório TP4 - Engenharia de Software
ß
## Fase 1 - Refatoração Profunda

### 1.1 Extração de validações (SRP)
- Criada a classe `br.com.faculdade.tp3.util.RhValidator`.
- Métodos movidos do `RhService`:
  - `sanitizarTexto`
  - `sanitizarTextoHumano`
  - `sanitizarEmail`
  - `sanitizarCpf`
- Regex centralizadas no `RhValidator`.

### 1.2 Lógica de negócio no modelo
- A regra de reajuste salarial foi movida para `Salario.aplicarAumento(BigDecimal percentual)`.
- O `RhService` passou a apenas orquestrar chamadas de domínio.

### 1.3 Substituição de primitivo (obsessão por primitivos)
- Implementado Value Object `Cpf` com autovalidação.
- `RhService` agora trabalha com `Cpf` na normalização da entrada, reduzindo acoplamento com regras de formato.

## Fase 2 - Integração de Sistemas

### 2.1 Mesclagem TP2 + TP3
- Pacotes de `com.tp2.engsoftware` incorporados ao projeto principal.
- Incluídos:
  - entidade `Produto`;
  - `ProdutoRepository`;
  - controladores web e REST;
  - serviço e estrutura CQRS (`command`/`query` + handlers);
  - testes de `src/test/java/com/tp2/engsoftware`.

### 2.2 Unificação de banco e telas
- Aplicação unificada usando o mesmo banco H2 configurado no `application.properties`.
- Menu/layout atualizado para navegação entre:
  - RH (`/rh/funcionarios`);
  - Produtos (`/produtos` e `/produtos/novo`).
- `BootstrapDataConfig` atualizado para garantir carga mínima nos dois domínios (RH e Produtos).

### 2.3 Interface de integração (rubrica)
- Criada interface `EntidadeRastreavel` com:
  - `getCriadoEm()`
  - `getAtualizadoEm()`
- Implementada por:
  - `Funcionario`
  - `Produto`

## Fase 3 - CI/CD (GitHub Actions)
- Criado `.github/workflows/main.yml`.
- Runner definido: `ubuntu-latest`.
- Marketplace actions integradas:
  - `actions/setup-java@v4`
  - `madrapps/jacoco-report@v1`
- Pipeline executa `mvn -B clean verify` em `push` e `pull_request`.

## Fase 4 - Validação e testes
- Novos testes da refatoração:
  - `RhValidatorTest`
  - `CpfTest`
  - `SalarioTest`
- Migração dos testes do TP2 realizada em `src/test/java/com/tp2/engsoftware`.
- Execução consolidada no projeto unificado:
  - `mvn test`
  - `mvn verify`
- Resultado consolidado (execução de 23/03/2026):
  - **122 testes executados**
  - **0 falhas**
  - **0 erros**
  - JaCoCo: `All coverage checks have been met.`

## Fase 5 - Entrega
- `README.md` atualizado com:
  - rotas de RH (`/api/rh`) e Produtos (`/api/produtos`);
  - instruções de execução e testes;
  - visualização do pipeline no GitHub Actions.
- Relatório consolidado em Markdown para anexação na entrega.

## Arquivos adicionados/alterados (resumo)
- `src/main/java/br/com/faculdade/tp3/util/RhValidator.java`
- `src/main/java/br/com/faculdade/tp3/model/Cpf.java`
- `src/main/java/br/com/faculdade/tp3/model/EntidadeRastreavel.java`
- `src/main/java/br/com/faculdade/tp3/model/Salario.java`
- `src/main/java/br/com/faculdade/tp3/model/Funcionario.java`
- `src/main/java/br/com/faculdade/tp3/service/RhService.java`
- `src/main/java/br/com/faculdade/tp3/config/BootstrapDataConfig.java`
- `src/main/java/com/tp2/engsoftware/**`
- `src/test/java/com/tp2/engsoftware/**`
- `src/main/resources/templates/produtos/**`
- `src/main/resources/templates/layout.html`
- `src/test/java/br/com/faculdade/tp3/unit/RhValidatorTest.java`
- `src/test/java/br/com/faculdade/tp3/unit/CpfTest.java`
- `src/test/java/br/com/faculdade/tp3/unit/SalarioTest.java`
- `.github/workflows/main.yml`
- `README.md`
- `docs/RELATORIO_TP4.md`
