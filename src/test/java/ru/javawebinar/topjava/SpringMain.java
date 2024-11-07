package ru.javawebinar.topjava;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.meal.MealRestController;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

public class SpringMain {
    public static void main(String[] args) {
        // Инициализируем Spring контекст
        ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext(
                "spring/spring-app.xml");

        System.out.println("\n>>> Spring context initialized");
        System.out.println(">>> Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));

        try {
            System.out.println("\n>>> Getting bean MealRestController");
            MealRestController mealController = appCtx.getBean(MealRestController.class);
            System.out.println(">>> Successfully got MealRestController: " + mealController);

            // Тестируем получение всех meal
            System.out.println("\n>>> Getting all meals");
            List<MealTo> all = mealController.getAll();
            System.out.println(">>> All meals: " + all);

            // Тестируем get с несуществующим id
            System.out.println("\n>>> Getting meal with id 1");
            try {
                Meal meal = mealController.get(1);
                System.out.println(">>> Retrieved meal: " + meal);
            } catch (Exception e) {
                System.out.println(">>> Expected exception for not found meal: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println(">>> Exception occurred:");
            e.printStackTrace();
        }

        // Закрываем контекст
        appCtx.close();
        System.out.println("\n>>> Spring context closed");
    }
}