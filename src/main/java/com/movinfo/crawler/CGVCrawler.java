package com.movinfo.crawler;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        int retryCountConnect = 2;
        for (int i = 0; i < retryCountConnect; ++i){
            // Move to CGV URL and switch to iframe
            try{
                if (driver.getCurrentUrl().equals("about:blank") || i > 0){
                    String cgvUrl = CGV_IMAX_URL + checkDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
                    if (i > 0){
                        driver.switchTo().defaultContent();
                    }
                    driver.get(cgvUrl);
                    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("ifrm_movie_time_table")));
                }
    
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("item")));
            } catch (TimeoutException e){
                System.out.println("Timeout to access CGV");
                return false;
            }

            // Check the target date is open or not
            List<WebElement> itemElements = driver.findElements(By.className("item"));
            for (WebElement item : itemElements){
                try {
                    for (WebElement li : item.findElements(By.tagName("li"))){
                        WebElement dayElement = li.findElement(By.tagName("strong"));
                        if (checkDate.getDayOfMonth() == Integer.parseInt(dayElement.getText())){
                            return true;
                        }
                    }
                } catch (NoSuchElementException | NumberFormatException e) {
                    continue;
                }
            }
        }

        return false;
    }

    private List<String> getOpenMovieList(){
        List<String> openMovieList = new ArrayList<>();
        List<WebElement> elementList = driver.findElements(By.className("col-times"));
        for(WebElement element : elementList){
            String movieName = element.findElement(By.tagName("a")).getText();
            try{
                if (element.findElement(By.className("imax")) != null){
                    if (!openMovieList.contains(movieName)){
                        openMovieList.add(movieName);
                    }
                }
            }catch(NoSuchElementException e){
                // Movie exist but is not imax
            }
        }

        return openMovieList;
    }

    private void moveToNextDay(LocalDate checkDate) {
        List<WebElement> itemElements = driver.findElements(By.className("item"));
        for (WebElement item : itemElements){
            try {
                for (WebElement li : item.findElements(By.tagName("li"))){
                    WebElement dayElement = li.findElement(By.tagName("strong"));
                    if (checkDate.getDayOfMonth() == Integer.parseInt(dayElement.getText())){
                        li.findElement(By.tagName("a")).click();
                        return;
                    }
                }
            } catch (NoSuchElementException | NumberFormatException e) {
                continue;
            }
        }
    }

    public Map<String, List<String>> checkImaxMovie(LocalDate checkDate){
        Map<String, List<String>> openMovieMap = new HashMap<>();

        while (accessToCGVWeb(checkDate)){
            List<String> openMovieList = getOpenMovieList();
            String dateString = checkDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            openMovieMap.put(dateString, openMovieList);
            checkDate = checkDate.plusDays(1);
            moveToNextDay(checkDate);
        }

        return openMovieMap;
    }
}

