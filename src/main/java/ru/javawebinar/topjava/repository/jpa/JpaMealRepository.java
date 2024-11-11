package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)  // Добавляем для оптимизации производительности
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        if (!meal.isNew() && get(meal.getId(), userId) == null) {
            return null;
        }
        meal.setUser(em.getReference(User.class, userId));  // Используем getReference вместо find для оптимизации
        if (meal.isNew()) {
            em.persist(meal);
            return meal;
        } else {
            return em.merge(meal);
        }
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createQuery("DELETE FROM Meal m WHERE m.id=:id AND m.user.id=:userId")
                .setParameter("id", id)
                .setParameter("userId", userId)
                .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        System.out.println("Searching for meal with id=" + id + " and userId=" + userId);  // отладочный вывод

        List<Meal> meals = em.createQuery(
                        "SELECT m FROM Meal m WHERE m.id=:id AND m.user.id=:userId", Meal.class)
                .setParameter("id", id)
                .setParameter("userId", userId)
                .getResultList();

        if (meals.isEmpty()) {
            System.out.println("No meal found!");  // отладочный вывод
            return null;
        }

        Meal meal = meals.get(0);
        System.out.println("Found meal: " + meal);  // отладочный вывод
        return meal;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createQuery(
                        "SELECT m FROM Meal m WHERE m.user.id=:userId ORDER BY m.dateTime DESC", Meal.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return em.createQuery(
                        "SELECT m FROM Meal m WHERE m.user.id=:userId " +
                                "AND m.dateTime >= :startDateTime AND m.dateTime < :endDateTime ORDER BY m.dateTime DESC", Meal.class)
                .setParameter("userId", userId)
                .setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime)
                .getResultList();
    }
}