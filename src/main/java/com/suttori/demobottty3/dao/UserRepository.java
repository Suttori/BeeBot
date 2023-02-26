package com.suttori.demobottty3.dao;

import com.suttori.demobottty3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    User findUserByChatId(@Param("chat_id") Long id);

    @Modifying
    @Query(value = "UPDATE \"user\" SET position = ? WHERE chat_id = ?", nativeQuery = true)
    void setPosition(@Param("position") String position, @Param("id") Long id);
}
