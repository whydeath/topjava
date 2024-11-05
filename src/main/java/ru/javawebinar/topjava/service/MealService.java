package ru.javawebinar.topjava.service;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {
    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal get(int id, int userId) {
        return checkNotFoundWithId(repository.get(id, userId), id);
    }

    public void delete(int id, int userId) {
        checkNotFoundWithId(repository.delete(id, userId), id);
    }

    public List<Meal> getBetweenDates(@Nullable LocalDate startDate, @Nullable LocalDate endDate, int userId) {
        return repository.getBetween(
                startDate != null ? startDate.atStartOfDay() : LocalDateTime.MIN,
                endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.MAX,
                userId);
    }

    public List<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public Meal create(Meal meal, int userId) {
        Assert.notNull(meal, "meal must not be null");
        return repository.save(meal, userId);
    }

    public Meal update(Meal meal, int userId) {
        Assert.notNull(meal, "meal must not be null");
        return checkNotFoundWithId(repository.save(meal, userId), meal.getId());
    }

    public Meal save(Meal meal, int userId) {
        Assert.notNull(meal, "meal must not be null");
        return meal.isNew() ? create(meal, userId) : update(meal, userId);
    }
}