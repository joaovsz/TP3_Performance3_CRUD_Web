package br.com.faculdade.tp3.selenium;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class PostDeployValidationSeleniumTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = normalizarBaseUrl(System.getenv("APP_BASE_URL"));
        Assumptions.assumeTrue(baseUrl != null, "APP_BASE_URL não configurada para validação pós-deploy.");

        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            driver = new ChromeDriver(options);
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "Chrome/Driver indisponível para execução do Selenium.");
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void deveExibirHeaderGlobalNosModulosRhEProdutos() {
        driver.get(url("/rh/funcionarios"));

        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menu-rh")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menu-produtos")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menu-novo-produto")));

        assertThat(driver.findElement(By.id("menu-rh")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.id("menu-produtos")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.id("menu-novo-produto")).isDisplayed()).isTrue();
    }

    @Test
    void devePermitirNavegacaoEntreRhEProdutos() {
        driver.get(url("/produtos"));

        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        wait.until(driver -> {
            boolean tabela = !driver.findElements(By.id("tabela-produtos")).isEmpty();
            boolean vazio = !driver.findElements(By.id("msg-vazio")).isEmpty();
            return tabela || vazio;
        });

        driver.findElement(By.id("menu-rh")).click();
        wait.until(ExpectedConditions.urlContains("/rh/funcionarios"));
        assertThat(driver.getCurrentUrl()).contains("/rh/funcionarios");
    }

    private String url(String path) {
        if (path == null || path.isBlank()) {
            return baseUrl;
        }
        return baseUrl + (path.startsWith("/") ? path : "/" + path);
    }

    private String normalizarBaseUrl(String value) {
        if (value == null) {
            return null;
        }
        String normalizada = value.trim();
        if (normalizada.isEmpty()) {
            return null;
        }
        if (normalizada.endsWith("/")) {
            return normalizada.substring(0, normalizada.length() - 1);
        }
        return normalizada;
    }
}
