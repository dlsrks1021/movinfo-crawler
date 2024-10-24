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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.movinfo.model.Movie;
import com.movinfo.model.Screen;

public class CGVCrawler
{
    private WebDriver driver;

    private static final String EMPTY_URL = "about:blank";
    private static final String TIMETABLE_URL = "http://www.cgv.co.kr/reserve/show-times/?areacode=01&theaterCode=0013&date=";
    private static final String MOVIECHART_URL = "http://www.cgv.co.kr/movies/";
    private static final String PREMOVIECHART_URL = "http://www.cgv.co.kr/movies/pre-movies.aspx";

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

    private boolean accessToTimeTable(LocalDate checkDate){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try{
            if (driver.getCurrentUrl().equals(EMPTY_URL)){
                String timeTableUrl = TIMETABLE_URL + checkDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                driver.get(timeTableUrl);
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

    private List<Screen> getScreenList(List<Screen> screenList, String dateString){
        List<WebElement> elementList = driver.findElements(By.className("col-times"));
        for(WebElement element : elementList){
            String movieName = element.findElement(By.tagName("a")).getText();

            List<WebElement> screentypeElementList = element.findElements(By.className("screentype"));

            try {
                Date screenDate = parseDateString(dateString, "yyyyMMdd");
                Screen screen = new Screen(movieName, screenDate);
    
                if (screentypeElementList.isEmpty()){
                    screen.addScreentype("default");
                } else {
                    for (WebElement screenElement : screentypeElementList){
                        screen.addScreentype(screenElement.getText());
                    }
                }
                screenList.add(screen);   
            } catch (ParseException e) {
                continue;
            }
        }
        return screenList;
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

    public List<Screen> getOpenScreens(LocalDate checkDate){
        List<Screen> screenList = new LinkedList<>();

        if (!driver.getCurrentUrl().equals(EMPTY_URL)){
            driver.get(EMPTY_URL);
        }
        
        while (accessToTimeTable(checkDate)){
            String dateString = checkDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            getScreenList(screenList, dateString);

            checkDate = checkDate.plusDays(1);
            moveToNextDay(checkDate);
        }

        return screenList;
    }

    private void accessToMovieChart(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(MOVIECHART_URL);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("box-image")));
    }

    private void accessToPreMovieChart(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(PREMOVIECHART_URL);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("box-image")));
    }

    private Date parseDateString (String dateString, String format) throws ParseException{
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = dateFormat.parse(dateString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private void getMovieList(List<Movie> movieList){
        List<WebElement> olElements = driver.findElements(By.tagName("ol"));
        olElements.forEach((olElement) -> {
            List<WebElement> movieBoxList = olElement.findElements(By.tagName("li"));
            movieBoxList.forEach(movieBox -> {
                try {
                    String movieName = movieBox.findElement(By.className("title")).getText();
                    String date = movieBox.findElement(By.className("txt-info")).getText();
                    String imageSource = movieBox.findElement(By.tagName("img")).getAttribute("src");

                    // Change Date Format
                    Date dateOpen = parseDateString(date, "yyyy.MM.dd");

                    Movie movie = new Movie(movieName, dateOpen, imageSource);
                    movieList.add(movie);   
                } catch (NoSuchElementException | ParseException e){
                    return;
                }
            });
        });
    }

    public List<Movie> getOpenMovies(){
        List<Movie> movieList = new LinkedList<>();

        accessToMovieChart();
        getMovieList(movieList);

        accessToPreMovieChart();
        getMovieList(movieList);

        return movieList;
    }

    public void cleanUp(){
        if (driver != null){
            driver.quit();
        }
    }
}

