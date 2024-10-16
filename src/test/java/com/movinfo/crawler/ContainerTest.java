package com.movinfo.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Testcontainers
public class ContainerTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ContainerTest.class);
    private static WebDriver driver;

    @Container
    private static GenericContainer<?> seleniumContainer = new GenericContainer<>(DockerImageName.parse("selenium/standalone-firefox:latest"))
            .withLogConsumer(new Slf4jLogConsumer(logger))
            .withStartupTimeout(Duration.ofMinutes(5))
            .withExposedPorts(4444);            
    @Container
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
                .withExposedPorts(27017);
    @BeforeAll
    static void setUp() throws MalformedURLException{
        seleniumContainer.start();
        mongoDBContainer.start();
        initWebDriver();
    }

    static void initWebDriver() throws MalformedURLException {
        URL remoteUrl = new URL("http://" + seleniumContainer.getHost() + ":" + seleniumContainer.getMappedPort(4444) + "/wd/hub");
        FirefoxOptions options = new FirefoxOptions();

        driver = new RemoteWebDriver(remoteUrl, options);
    }

    @Test
    void testGetOpenMovie() throws Exception {
        CGVCrawler crawler = new CGVCrawler(driver);
        assertThat(crawler.getOpenMovies(LocalDate.now().plusDays(1)).size()).isNotZero();
    }

    @Test
    void testCheckMongo() {
        String connectionString = mongoDBContainer.getConnectionString();

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("testdb");
            MongoCollection<Document> collection = database.getCollection("users");

            Document user1 = new Document("name", "alice")
                                    .append("age", 30);

            collection.insertOne(user1);

            FindIterable<Document> users = collection.find();

            for (Document user : users){
                assertThat(user.get("age")).isEqualTo(30);
            }
        }        
    }

    @AfterAll
    static void tearDown(){
        driver.quit();
        seleniumContainer.stop();
        mongoDBContainer.stop();
        System.out.println("TEAR DOWN");
    }
}
