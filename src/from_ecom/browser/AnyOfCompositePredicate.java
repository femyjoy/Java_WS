package from_ecom.browser;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static java.util.Arrays.asList;

public class AnyOfCompositePredicate implements Predicate<WebDriver> {
    private final List<Predicate<WebDriver>> predicates;

    public AnyOfCompositePredicate(List<Predicate<WebDriver>> predicates) {
        this.predicates = predicates;
    }

    public AnyOfCompositePredicate(Predicate<WebDriver>... predicates) {
        this.predicates = asList(predicates);
    }

    @Override
    public boolean apply(WebDriver input) {
        for (Predicate<WebDriver> predicate : predicates) {
            if (predicate.apply(input)) {
                return true;
            }
        }
        return false;
    }
}
