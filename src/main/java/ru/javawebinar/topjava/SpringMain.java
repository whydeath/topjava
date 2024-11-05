package ru.javawebinar.topjava;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

public class SpringMain {
    public static void main(String[] args) {
        // Инициализируем Spring контекст
        ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext(
                "spring/spring-app.xml");
        System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));

        MealRestController mealController = appCtx.getBean(MealRestController.class);

        // Создаем тестовые данные
        Meal meal = new Meal(
                LocalDateTime.of(2024, Month.JANUARY, 30, 10, 0),
                "Завтрак",
                500
        );

        // Тестируем создание meal
        System.out.println("Creating meal...");
        ResponseEntity<Meal> response = mealController.createWithLocation(meal);
        Meal created = response.getBody();
        System.out.println("Created meal: " + created);

        // Тестируем получение meal
        System.out.println("Getting meal...");
        Meal gotten = mealController.get(created.getId());
        System.out.println("Retrieved meal: " + gotten);

        // Закрываем контекст
        appCtx.close();
    }
}