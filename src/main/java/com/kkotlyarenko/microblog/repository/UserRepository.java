package com.kkotlyarenko.microblog.repository;

import com.kkotlyarenko.microblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Репозиторий пользователей.
 *
 * <p>Все методы Spring Data JPA формируют параметризованные (prepared) SQL-запросы.
 * Конкатенация пользовательского ввода в SQL не используется нигде — это базовая
 * защита от SQL-инъекций (OWASP A03:2021 Injection).</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Безопасный поиск по логину. Параметр передаётся как bind-параметр,
     * а не подставляется в текст запроса.
     */
    Optional<User> findByUsername(String username);

    /**
     * Пример явного JPQL-запроса с именованным параметром.
     * Подчёркиваем: значение :username НЕ конкатенируется в строку запроса.
     */
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameSafe(@Param("username") String username);

    boolean existsByUsername(String username);
}
