import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    private static ChromeDriver driver;
    private static String driverPath;
    private static String videoURL;
    private static String commentAuthor;
    private static String commentText;
    private static String delimiter;

    public static void main(String[] args) {
        System.out.println("YouBot started!");
        checkOS();
        setDriver();
        setTargetInfo();
        HashMap<String, String> accounts = getAccounts();
        for (Map.Entry<String, String> entry : accounts.entrySet()) {
            login(entry.getKey(), entry.getValue());
            findAndLike(entry.getKey());
            logout();
        }
        System.out.println("Finished!");
        driver.quit();
    }

    private static void checkOS() {
        System.out.println("Checking your OS...");
        String os = System.getProperty("os.name");
        System.out.println(os);
        if (os.contains("Mac")) {
            driverPath = "lib/chromedriver";
            delimiter = "/";
            System.out.println("Mac Chrome Driver selected");
        } else if (os.contains("Windows")) {
            driverPath = "lib\\chromedriver.exe";
            delimiter ="\\";
            System.out.println("Windows Chrome Driver selected");
        } else {
            System.out.println("Error! No drivers found for your OS " + os);
            System.exit(0);
        }
    }

    private static void setDriver() {
        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    private static void setTargetInfo() {
        File file = new File("settings"+ delimiter +"target.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String delims = "[|]+";
            String line = scanner.nextLine();
            String[] tokens = line.split(delims);
            videoURL = tokens[0];
            System.out.println("url: " + videoURL);
            commentAuthor = tokens[1];
            System.out.println("author name: "+commentAuthor);
            commentText = tokens[2];
            System.out.println("text: "+commentText);
        } finally {
            scanner.close();
        }
    }

    private static HashMap<String, String> getAccounts() {
        HashMap<String, String> accounts = new HashMap<String, String>();
        File file = new File("settings"+ delimiter +"accounts.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String delims = "[:]+";
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split(delims);
                accounts.put(tokens[0], tokens[1]);
            }
        } finally {
            scanner.close();
        }
        return accounts;
    }

    private static void login(String login, String password) {
        driver.get("https://accounts.google.com/ServiceLogin?sacu=1&continue=https%3A%2F%2Fwww.youtube.com%2Fsignin%3"
                + "Ffeature%3Dsign_in_button%26next%3D%252F%26hl%3Dru%26action_handle_signin%3Dtrue%26app%3Ddesktop&hl"
                + "=en&service=youtube#identifier");
        WebElement element = driver.findElement(By.name("Email"));
        element.sendKeys(login + "@gmail.com");
        element.submit();
        element = driver.findElement(By.name("Passwd"));
        element.sendKeys(password);
        element.submit();
        System.out.println("Logged in as "+ login);
    }

    private static void findAndLike(String login) {
        driver.get(videoURL);
        driver.executeScript("window.scrollBy(0,500)", "");
        List<WebElement> iframes = driver.findElements(By.xpath("//iframe"));
        for (WebElement e : iframes) {
            if (e.getAttribute("id") != null && e.getAttribute("id").startsWith("I0_")) {
                // switch to iframe which contains comments
                driver.switchTo().frame(e);
                System.out.println("inside if statement");
                break;
            }
        }

        boolean commentNotFount = true;
        while (commentNotFount) {
            List<WebElement> comments = driver.findElements(By.xpath("//div[@class='comment-renderer-content']"));
            for (WebElement e : comments) {
                if (e.getText().contains(commentAuthor) && e.getText().contains(commentText)) {
                    List<WebElement> buttons = e.findElements(By.tagName("button"));
                    WebElement likebutton = buttons.get(3);
                    String atr = likebutton.getAttribute("data-action-on");
                    if (atr==null) {
                        driver.executeScript("var evt = document.createEvent('MouseEvents');" + "evt.initMouseEvent('click'"
                                + ",true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" + "arguments[0]"
                                + ".dispatchEvent(evt);", likebutton);
                        System.out.println("comment liked by " + login);
                    } else {
                        System.out.println("comment already was liked by " + login);
                    }
                    commentNotFount = false;
                    break;
                }
            }
            if (commentNotFount) {
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                WebElement showmore = null;
                if (showmore == null) {
                    for (int i = buttons.size() - 1; i > 0; i--) {
                        if (buttons.get(i).getText().contains("Show more")) {
                            showmore = buttons.get(i);
                            break;
                        }
                    }
                }
                showmore.click();
            }
        }
    }

    private static void logout() {
        driver.switchTo().parentFrame();
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        buttons.get(2).click();
        driver.findElement(By.partialLinkText("Sign out")).click();
    }
}
