import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
//TODO english support
public class Main  {
    private static ChromeDriver driver;
    private static WebElement iframe;
    private static String videoURL = "https://youtu.be/JDGa-jzY-O8";
    private static String commentAuthor = "cjoemex";
    private static String commentText = "ice block + fire ball. go face";

    private static HashMap<String, String> getAccounts() {
        HashMap<String, String> accounts = new HashMap<String, String>();
        File file = new File("settings\\accounts.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String delims = "[:]+";
        try {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(delims);
                accounts.put(tokens[0], tokens[1]);
            }
        } finally {
            scanner.close();
        }
        return accounts;
    }

    private static void logout() {
        driver.switchTo().parentFrame();
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        buttons.get(3).click();
        driver.findElement(By.partialLinkText("Выйти")).click();
    }

    private static void login(String login, String password) {
        //TODO without main page
        driver.get("http://www.youtube.com");
        WebElement element = driver.findElement(By.className("yt-uix-button-content"));
        element.click();
        element = driver.findElement(By.name("Email"));
        element.sendKeys("grayjoy1@gmail.com");
        element.submit();
        element = driver.findElement(By.name("Passwd"));
        element.sendKeys("gena181818");
        element.submit();
    }

    private static void findAndLike () {
        driver.get(videoURL);
        driver.executeScript("window.scrollBy(0,500)", "");
        List<WebElement> iframes = driver.findElements(By.xpath("//iframe"));
        System.out.println("iframes "+iframes.size());
        for(WebElement e : iframes) {
            if(e.getAttribute("id") != null && e.getAttribute("id").startsWith("I0_")) {
                // switch to iframe which contains comments
                driver.switchTo().frame(e);
                System.out.println("inside if statement");
                break;
            }
        }
        boolean commentNotFount = true;
       while(commentNotFount) {
           List<WebElement> comments = driver.findElements(By.xpath("//div[@class='comment-renderer-content']"));
           System.out.println("Comments located: "+comments.size());
           for (WebElement e : comments) {
               if (e.getText().contains(commentAuthor) && e.getText().contains(commentText)) {
                   List<WebElement> buttons = e.findElements(By.tagName("button"));
                   WebElement likebutton = buttons.get(3);
                   driver.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0].dispatchEvent(evt);", likebutton);
                   System.out.println("comment liked");
                   commentNotFount = false;
                   break;
               }
           }
         if(commentNotFount) {
             List<WebElement> buttons = driver.findElements(By.tagName("button"));
             WebElement showmore = null;
             if (showmore == null) {
                 for (int i = buttons.size()-1; i>0; i--) {
                     if (buttons.get(i).getText().contains("Показать ещё")) {
                         showmore= buttons.get(i);
                         break;
                     }
                 }
             }
             showmore.click();
         }
       }
    }

    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "lib\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        HashMap<String, String> accounts = getAccounts();
        for (Map.Entry<String, String> entry : accounts.entrySet()) {

//            entry.getKey()
        }

        //login();
        //findAndLike();
        //logout();



        System.out.println("Page title is: " + driver.getTitle());

        //Close the browser
       // driver.quit();
    }
}
