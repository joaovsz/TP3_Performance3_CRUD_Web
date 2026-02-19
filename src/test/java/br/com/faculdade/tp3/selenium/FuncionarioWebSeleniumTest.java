package br.com.faculdade.tp3.selenium;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class FuncionarioWebSeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            driver = new ChromeDriver(options);
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "Chrome/Driver indisponível para execução do Selenium neste ambiente.");
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void deveContratarFuncionarioPeloFormularioWeb() {
        String sufixo = String.valueOf(Instant.now().toEpochMilli()).substring(7);
        String nome = "Mariana " + sufixo;
        String email = "mariana" + sufixo + "@empresa.com";
        String cpf = ("9" + sufixo + "1234567890").substring(0, 11);

        driver.get(baseUrl() + "/rh/funcionarios/novo");

        driver.findElement(By.id("nome")).sendKeys(nome);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("cpf")).sendKeys(cpf);
        driver.findElement(By.id("cargo")).sendKeys("Analista de QA");

        WebElement departamento = driver.findElement(By.id("departamentoId"));
        departamento.click();
        departamento.findElements(By.tagName("option")).stream()
                .filter(option -> !option.getAttribute("value").isBlank())
                .findFirst()
                .orElseThrow()
                .click();

        driver.findElement(By.id("salarioInicial")).sendKeys("4200");
        driver.findElement(By.id("btn-salvar")).click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("tabela-funcionarios")));

        assertThat(driver.getPageSource()).contains(nome);
    }

    @Test
    void deveFiltrarFuncionariosNaLista() {
        driver.get(baseUrl() + "/rh/funcionarios");

        WebElement campo = driver.findElement(By.id("filtroNome"));
        campo.clear();
        campo.sendKeys("Mariana");
        driver.findElement(By.id("btn-filtrar")).click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("tabela-funcionarios")));

        assertThat(driver.getCurrentUrl()).contains("/rh/funcionarios");
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
