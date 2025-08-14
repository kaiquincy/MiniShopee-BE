package com.example.demo.repository;

import com.example.demo.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
      SELECT cr FROM ChatRoom cr
      WHERE (cr.userA.id = :u1 AND cr.userB.id = :u2)
         OR (cr.userA.id = :u2 AND cr.userB.id = :u1)
    """)
    Optional<ChatRoom> findDirectBetween(Long u1, Long u2);

    @Query("""
      SELECT cr FROM ChatRoom cr
      WHERE cr.userA.id = :uid OR cr.userB.id = :uid
      ORDER BY cr.createdAt DESC
    """)
    List<ChatRoom> findAllOfUser(Long uid);
}
