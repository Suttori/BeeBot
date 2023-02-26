package com.suttori.demobottty3.dao;

import com.suttori.demobottty3.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

    List<Channel> findChannelByUserId(@Param("user_id") Long id);

}
