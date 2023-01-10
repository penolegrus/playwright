package selenide;

import com.browserup.bup.BrowserUpProxy;
import com.browserup.bup.proxy.CaptureType;
import com.browserup.harreader.model.HarEntry;
import com.browserup.harreader.model.HttpMethod;
import com.codeborne.selenide.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class SelenideTests {
    @BeforeClass
    public void setUp() {
        Configuration.proxyEnabled = true;
        Configuration.browser = "chrome";
        Configuration.timeout = 20000;
    }

    @Test
    public void fillManyTextSelenideBoxes(){
        Selenide.open("https://datatables.net/examples/api/form.html");
        $x("//select[@name='example_length']").selectOption("50");
        ElementsCollection textFields = $$x("//tbody//input[@type='text']");
        textFields.asFixedIterable().forEach(x->{
            x.clear();
            x.sendKeys("threadqa selenide");
        });
    }

    @Test
    public void textBoxFillSelenideTest() {
        Selenide.open("http://85.192.34.140:8081/");
        $x("//*[text()='Elements']").click();
        $x("//li[@id='item-0']/span[1]").click();
        $("[id=userName]").sendKeys("ThreadQA Test");
        $("[id=userEmail]").sendKeys("threadqa@gmail.com");
        $("[id=currentAddress]").sendKeys("somewhere");
        $("[id=submit]").click();

        $("[id=output]").should(Condition.visible);
        $("[id=name]").should(Condition.partialText("ThreadQA Test"));
    }

    @Test
    public void sliderSelenideTest() {
        Selenide.open("http://85.192.34.140:8081/");
        $x("//*[text()='Widgets']").click();
        $x("//*[text()='Slider']").click();
        SelenideElement slider = $("input[type='range']");
       // Selenide.actions().dragAndDropBy(slider, 20, 0).release().perform();
        //Двигаем слайдер с помощью отправки нажатий клавиш
        for (int i = 0; i < 20; i++) {
            slider.sendKeys(Keys.ARROW_RIGHT);
        }
        assertNotEquals("25", $("[id=sliderValue]").getAttribute("value"));
    }

    @Test
    public void alertSelenideTest() {
        Selenide.open("http://85.192.34.140:8081/");
        $x("//*[contains(text(),'Alerts, Frame')]").click();
        $x("//*[@id='item-1']//following::span[text()='Alerts']").click();
        $("[id=alertButton]").click();
        //Переключаемся на диалоговое окно
        Alert alert = Selenide.switchTo().alert();
        assertEquals("You clicked a button", alert.getText());
        alert.accept();
    }

    @Test
    public void networkSelenideTest() {
        Selenide.open("http://85.192.34.140:8081/");
        //Устанавливаем локльный прокси в браузере
        BrowserUpProxy bmp = WebDriverRunner.getSelenideProxy().getProxy();
        bmp.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
        //Мониторим запросы и ответы
        bmp.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        bmp.newHar("pofig");

        $x("//*[text()='Elements']").click();
        $x("//*[text()='Links']").click();

        $("[id=bad-request]").click();
        //Получаем список с запросами
        List<HarEntry> requests = bmp.getHar().getLog().getEntries();
        HarEntry har = requests.get(requests.size()-1);
        //Проверяем что в запросе содержится нужная информация
        assertEquals(har.getRequest().getUrl(), "http://85.192.34.140/api/bad-request");
        assertEquals(har.getRequest().getMethod(), HttpMethod.GET);
        assertEquals(har.getResponse().getStatus(), 400);
    }
}
