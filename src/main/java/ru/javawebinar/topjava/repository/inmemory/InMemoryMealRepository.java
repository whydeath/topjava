package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    // Потокобезопасная карта для хранения всех приемов пищи
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();

    // Атомарный счетчик для генерации ID
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        }

        // Проверяем, что обновляемая еда принадлежит пользователю
        return get(meal.getId(), userId) == null ? null :
                repository.compute(meal.getId(), (id, oldMeal) -> {
                    meal.setUserId(userId);
                    return meal;
                });
    }

    @Override
    public boolean delete(int id, int userId) {
        return repository.remove(id, get(id, userId));
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        return meal != null && meal.getUserId() == userId ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getAllFiltered(userId, meal -> true);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return getAllFiltered(userId, meal -> {
            LocalDateTime dateTime = meal.getDateTime();
            return dateTime.compareTo(startDateTime) >= 0 && dateTime.compareTo(endDateTime) <= 0;
        });
    }

    private List<Meal> getAllFiltered(int userId, Predicate<Meal> filter) {
        return repository.values().stream()
                .filter(meal -> meal.getUserId() == userId)
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}