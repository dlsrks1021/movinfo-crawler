package com.movinfo.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class ContainerTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ContainerTest.class);
    private static WebDriver driver;

    @Container
    private static GenericContainer<?> seleniumContainer = new GenericContainer<>(DockerImageName.parse("seleniarm/standalone-firefox:latest"))
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withStartupTimeout(Duration.ofMinutes(5))
            .withExposedPorts(4444);            

    @BeforeAll
    static void setUp() throws MalformedURLException{
        seleniumContainer.start();
        initWebDriver();
    }

    static void initWebDriver() throws MalformedURLException {
        URL remoteUrl = new URL("http://" + seleniumContainer.getHost() + ":" + seleniumContainer.getMappedPort(4444) + "/wd/hub");
        FirefoxOptions options = new FirefoxOptions();

        driver = new RemoteWebDriver(remoteUrl, options);
    }

    @Test
    void testCheckImaxMovie() throws Exception {
        CGVCrawler.checkImaxMovie(driver, LocalDate.now().plusDays(2));
    }

    @AfterAll
    static void tearDown(){
        driver.quit();
        seleniumContainer.stop();
        System.out.println("TEAR DOWN");
    }
}
