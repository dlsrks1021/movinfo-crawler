package com.movinfo.crawler;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.movinfo.model.Movie;

public class CGVCrawler
{
    private WebDriver driver;
    private static final String CGV_IMAX_URL = "http://www.cgv.co.kr/reserve/show-times/?areacode=01&theaterCode=0013&date=";

    public CGVCrawler(){
        try {
            initDriver();    
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public CGVCrawler(WebDriver driver){
        this.driver = driver;
    }

    private void initDriver() throws MalformedURLException{
        FirefoxOptions options = new FirefoxOptions();

        URL remoteUrl = new URL("http://selenium:4444/wd/hub");
        driver = new RemoteWebDriver(remoteUrl, options);
        
    }

    private boolean accessToCGVWeb(LocalDate checkDate){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try{
            if (driver.getCurrentUrl().equals("about:blank")){
                String cgvUrl = CGV_IMAX_URL + checkDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                driver.get(cgvUrl);
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("ifrm_movie_time_table")));
            }
        } catch (TimeoutException e){
            System.out.println("Timeout to access CGV");
            return false;
        }

        // Check the target date is open or not
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("item")));
        List<WebElement> itemElements = driver.findElements(By.className("item"));
        for (WebElement item : itemElements){
            if (item.findElement(By.tagName("li")).isDisplayed()){
                for (WebElement li : item.findElements(By.tagName("li"))){
                    WebElement dayElement = li.findElement(By.tagName("strong"));
                    if (checkDate.getDayOfMonth() == Integer.parseInt(dayElement.getText())){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private List<Movie> getOpenMovieList(List<Movie> openMovieList, String dateString){
        List<WebElement> elementList = driver.findElements(By.className("col-times"));
        for(WebElement element : elementList){
            String movieName = element.findElement(By.tagName("a")).getText();

            List<WebElement> screentypeElementList = element.findElements(By.className("screentype"));
            if (screentypeElementList.size() == 0){
                Movie movie = new Movie(movieName, dateString, "default", null);
                openMovieList.add(movie);
            } else {
                for (WebElement screenElement : screentypeElementList){
                    Movie movie = new Movie(movieName, dateString, screenElement.getText(), null);
                    openMovieList.add(movie);
                }
            }
        }
        return openMovieList;
    }

    private void moveToNextDay(LocalDate checkDate) {
        boolean isItemChecked = false;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        List<WebElement> itemElements = driver.findElements(By.className("item-wrap"));
        for (WebElement item : itemElements){
            if (isItemChecked){
                WebElement nextButton = driver.findElement(By.className("btn-next"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);
                wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(item, By.tagName("li")));
            }
            
            if (item.findElement(By.tagName("li")).isDisplayed()){
                isItemChecked = true;
                for (WebElement li : item.findElements(By.tagName("li"))){
                    WebElement dayElement = li.findElement(By.tagName("strong"));
                    if (checkDate.getDayOfMonth() == Integer.parseInt(dayElement.getText())){
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", li.findElement(By.tagName("a")));
                        wait.until(ExpectedConditions.stalenessOf(li));
                        
                        return;
                    }
                }
            }
        }
    }

    public List<Movie> getOpenMovies(LocalDate checkDate){
        List<Movie> openMovieList = new LinkedList<>();

        while (accessToCGVWeb(checkDate)){
            String dateString = checkDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            getOpenMovieList(openMovieList, dateString);

            if (!openMovieList.isEmpty()){
                System.out.println("["+dateString+"]");
                for (Movie movie : openMovieList){
                    System.out.println(movie.getName());
                }
            }

            checkDate = checkDate.plusDays(1);
            moveToNextDay(checkDate);
        }

        return openMovieList;
    }

    public void cleanUp(){
        if (driver != null){
            driver.quit();
        }
    }
}

