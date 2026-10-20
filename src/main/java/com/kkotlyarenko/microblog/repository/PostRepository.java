package com.kkotlyarenko.microblog.repository;

import com.kkotlyarenko.microblog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

/**
 * Репозиторий постов. Запросы параметризованы средствами Spring Data JPA.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Проекция записи ленты: имя автора берётся из связанной таблицы,
     * наружу отдаются только неизменяемые значения (без JPA-сущностей).
     */
    interface PostFeedView {
        Long getId();

        String getTitle();

        String getContent();

        String getAuthor();

        Instant getCreatedAt();
    }

    @Query("""
            SELECT p.id AS id, p.title AS title, p.content AS content,
                   p.createdAt AS createdAt, u.username AS author
            FROM Post p
            JOIN User u ON u.id = p.authorId
            ORDER BY p.createdAt DESC
            """)
    List<PostFeedView> findFeed();
}
