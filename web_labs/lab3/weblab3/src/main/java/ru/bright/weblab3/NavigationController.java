package ru.bright.weblab3;

import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

//@Named
//@RequestScoped
@Getter
@Setter
public class NavigationController {

    public NavigationController() {

    }

    public String goToMainPage() {
        return "pretty:main-page";
    }

    public String goToStartPage() {
        return "pretty:start-page";
    }
}
