package ru.javawebinar.topjava.repository.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
//@Transactional
@Sql(scripts = "classpath:db/populateDB.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class JpaMealRepositoryTest {

    @Autowired
    private MealRepository repository;

    // Константы с правильными ID из базы данных
    private static final int USER_ID = 100000;    // ID обычного пользователя
    private static final int ADMIN_ID = 100001;   // ID админа
    private static final int FIRST_MEAL_ID = 100003;  // ID первого приема пищи

    @Test
    public void delete() {
        repository.delete(FIRST_MEAL_ID, USER_ID);
        assertNull(repository.get(FIRST_MEAL_ID, USER_ID));
    }

    @Test
    public void get() {
        Meal meal = repository.get(FIRST_MEAL_ID, USER_ID);
        assertNotNull("Meal must not be null", meal);

        // Проверяем все поля
        assertEquals(FIRST_MEAL_ID, meal.getId().intValue());
        assertEquals("Завтрак", meal.getDescription());
        assertEquals(500, meal.getCalories());
        assertEquals(LocalDateTime.of(2020, 1, 30, 10, 0), meal.getDateTime());
    }

    @Test
    public void getNotFound() {
        assertNull(repository.get(FIRST_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void save() {
        Meal created = new Meal(LocalDateTime.now(), "Created meal", 1000);
        Meal saved = repository.save(created, USER_ID);
        assertNotNull(saved.getId());
        assertEquals(created.getCalories(), saved.getCalories());
        assertEquals(created.getDescription(), saved.getDescription());
    }

    @Test
    public void getAll() {
        List<Meal> all = repository.getAll(USER_ID);
        assertFalse(all.isEmpty());
    }

    @Test
    public void getBetween() {
        LocalDateTime start = LocalDateTime.of(2020, 1, 30, 10, 0);
        LocalDateTime end = LocalDateTime.of(2020, 1, 31, 10, 0);
        List<Meal> meals = repository.getBetweenHalfOpen(start, end, USER_ID);
        assertFalse(meals.isEmpty());
    }
}