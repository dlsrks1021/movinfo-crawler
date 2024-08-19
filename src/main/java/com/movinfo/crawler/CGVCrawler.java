package com.movinfo.crawler;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CGVCrawler
{
    private WebDriver firefoxDriver;
    private static final String CGV_IMAX_URL = "http://www.cgv.co.kr/reserve/show-times/?areacode=01&theaterCode=0013&date=";

    public CGVCrawler(){
        // initDriver();
    }

    private WebDriver initDriver(){

        FirefoxOptions options = new FirefoxOptions();
        firefoxDriver = new FirefoxDriver(options);

        return firefoxDriver;
    }

    private static void accessToCGVWeb(WebDriver driver, LocalDate checkDate){
        String cgvUrl = CGV_IMAX_URL + checkDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        driver.get(cgvUrl);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("ifrm_movie_time_table")));
    }

    private static int getCurrentHour(){
        return Integer.parseInt(LocalTime.now().format(DateTimeFormatter.ofPattern("HH")));
    }

    private static boolean isCheckDateUrlOpen(WebDriver driver, LocalDate checkDate){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("on")));

        List<WebElement> elementCurrentUrlDay = driver.findElements(By.className("on"));

        for (WebElement e : elementCurrentUrlDay){
            String urlDay = e.findElement(By.tagName("strong")).getText();
            if (!urlDay.isBlank() && checkDate.getDayOfMonth() == Integer.parseInt(urlDay)){
                return true;
            }
        }

        return false;
    }

    private static List<String> getOpenMovieList(WebDriver driver, LocalDate checkDate){
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

    public static void checkImaxMovie(WebDriver driver, LocalDate checkDate){
        // Return false if cur time is not work hour
        int hour = getCurrentHour();
        if (hour >= 22 || hour < 6){
            return;
        }

        accessToCGVWeb(driver, checkDate);

        if (isCheckDateUrlOpen(driver, checkDate)){
            List<String> openMovieList = getOpenMovieList(driver, checkDate);
            for (String openMovie : openMovieList){
                System.out.println(openMovie);
            }            
        }
    }
}

