package pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;

import java.util.List;
import java.util.stream.Collectors;

import static com.microsoft.playwright.options.WaitForSelectorState.VISIBLE;


public class WebTablePage {
    private final Page page;
    private final String searchBoxLocator = "#searchBox";
    private final String allRowsLocator = ".rt-tr-group";

    public WebTablePage(Page page) {
        this.page = page;
    }

    /**
     * Ищет в таблице данные
     * @param query запрос для поиска
     */
    public void search(String query) {
        //заполнение текстового поля
        page.fill(searchBoxLocator, query);
        String expectedSelector = String.format("//div[@class='rt-td' and text()='%s']", query);
        //намеренное ожидание элемента на странице с использованием state.
        page.waitForSelector(expectedSelector, new Page.WaitForSelectorOptions().setState(VISIBLE));
    }

    /**
     * Получает из таблицы не пустые строчки
     * @return список с строчками
     */
    public List<String> getVisibleNames() {
        return page.querySelectorAll(allRowsLocator) //находит коллекцию элементов
                .stream()
                .map(x->x.innerText()) //достает текст из каждого элемента
                .filter(x->!x.startsWith(" ")) //условие если текст из строчки не начинается на пробел
                .collect(Collectors.toList()); //собирает результаты в список
    }
}
