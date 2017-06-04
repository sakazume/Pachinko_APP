package rakuen;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by gyutr20 on 2015/12/24.
 */
public class Main {

    static String pachi = "a:nth-child(2)";
    static String slot = "a[href=\"./D0301.do?pmc=22021007&clc=03&urt=2000&pan=1\"]";

    public static void main(String... args) throws IOException, URISyntaxException {
//        URL driverUrl = Main.class.getClassLoader().getResource("driver/chromedriver");
//        File file = new File(driverUrl.toURI());
//
//
//        System.setProperty("webdriver.chrome.driver", file.getPath());
//        WebDriver driver = new ChromeDriver();
//        driver.get("http://www.d-deltanet.com/pc/D2301.do?pmc=22021021&clc=01&urt=-1&mdc=024634&bn=1");
//        WebElement element = driver.findElement(By.cssSelector("a:nth-child(2)"));
//        element.click();
//        driver.findElement(By.cssSelector(slot)).click();
        Document doc = Jsoup.connect("https://www.d-deltanet.com/pc/HallSelectLink.do?hallcode=23013001")
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("https://www.d-deltanet.com/pc/FreeDataOpenHallList.do?cpn=1")
                .get();
        doc.select("table.slot").get(0);
        System.out.println(doc.html());
    }


    /**
     * 一番最初の機種一覧から大当たり一覧の取得
     * @param el
     */
    public static void 機種一覧(Element el) {

    }
}
