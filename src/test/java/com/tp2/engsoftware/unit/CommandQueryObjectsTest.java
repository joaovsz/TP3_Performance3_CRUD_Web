package com.tp2.engsoftware.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.tp2.engsoftware.command.CreateProdutoCommand;
import com.tp2.engsoftware.command.DeleteProdutoCommand;
import com.tp2.engsoftware.command.UpdateProdutoCommand;
import com.tp2.engsoftware.query.GetProdutoByIdQuery;
import com.tp2.engsoftware.query.SearchProdutosByNomeQuery;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CommandQueryObjectsTest {

    @Test
    void devePopularCreateProdutoCommandViaSetters() {
        CreateProdutoCommand command = new CreateProdutoCommand();
        command.setNome("Notebook");
        command.setDescricao("Notebook para testes");
        command.setPreco(new BigDecimal("3500.00"));
        command.setQuantidade(10);

        assertThat(command.getNome()).isEqualTo("Notebook");
        assertThat(command.getDescricao()).isEqualTo("Notebook para testes");
        assertThat(command.getPreco()).isEqualByComparingTo("3500.00");
        assertThat(command.getQuantidade()).isEqualTo(10);
    }

    @Test
    void devePopularUpdateProdutoCommandViaSetters() {
        UpdateProdutoCommand command = new UpdateProdutoCommand();
        command.setId(1L);
        command.setNome("Monitor");
        command.setDescricao("Monitor 27");
        command.setPreco(new BigDecimal("1200.00"));
        command.setQuantidade(5);

        assertThat(command.getId()).isEqualTo(1L);
        assertThat(command.getNome()).isEqualTo("Monitor");
        assertThat(command.getDescricao()).isEqualTo("Monitor 27");
        assertThat(command.getPreco()).isEqualByComparingTo("1200.00");
        assertThat(command.getQuantidade()).isEqualTo(5);
    }

    @Test
    void devePopularDeleteProdutoCommandViaSetters() {
        DeleteProdutoCommand command = new DeleteProdutoCommand();
        command.setId(99L);

        assertThat(command.getId()).isEqualTo(99L);
    }

    @Test
    void devePopularGetProdutoByIdQueryViaSetters() {
        GetProdutoByIdQuery query = new GetProdutoByIdQuery();
        query.setId(7L);

        assertThat(query.getId()).isEqualTo(7L);
    }

    @Test
    void devePopularSearchProdutosByNomeQueryViaSetters() {
        SearchProdutosByNomeQuery query = new SearchProdutosByNomeQuery();
        query.setNome("teclado");

        assertThat(query.getNome()).isEqualTo("teclado");
    }

    @Test
    void devePopularCommandsEQueriesViaConstrutoresComArgumento() {
        CreateProdutoCommand create = new CreateProdutoCommand(
                "Mouse",
                "Mouse gamer",
                new BigDecimal("199.90"),
                3
        );
        UpdateProdutoCommand update = new UpdateProdutoCommand(
                10L,
                "Teclado",
                "Teclado mecanico",
                new BigDecimal("499.90"),
                4
        );
        DeleteProdutoCommand delete = new DeleteProdutoCommand(10L);
        GetProdutoByIdQuery byId = new GetProdutoByIdQuery(10L);
        SearchProdutosByNomeQuery byNome = new SearchProdutosByNomeQuery("teclado");

        assertThat(create.getNome()).isEqualTo("Mouse");
        assertThat(create.getDescricao()).isEqualTo("Mouse gamer");
        assertThat(create.getPreco()).isEqualByComparingTo("199.90");
        assertThat(create.getQuantidade()).isEqualTo(3);

        assertThat(update.getId()).isEqualTo(10L);
        assertThat(update.getNome()).isEqualTo("Teclado");
        assertThat(update.getDescricao()).isEqualTo("Teclado mecanico");
        assertThat(update.getPreco()).isEqualByComparingTo("499.90");
        assertThat(update.getQuantidade()).isEqualTo(4);

        assertThat(delete.getId()).isEqualTo(10L);
        assertThat(byId.getId()).isEqualTo(10L);
        assertThat(byNome.getNome()).isEqualTo("teclado");
    }
}
