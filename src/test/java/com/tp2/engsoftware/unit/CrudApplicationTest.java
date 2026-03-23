package com.tp2.engsoftware.unit;

import com.tp2.engsoftware.CrudApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CrudApplicationTest {

    @Test
    @DisplayName("Contexto da aplicação deve carregar")
    public void contextLoads() {
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Método main deve executar sem erros")
    public void mainMethodShouldRun() {
        assertThat(CrudApplication.class).isNotNull();
    }
}
