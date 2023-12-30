package com.vix.circustelegramchat.repository;

import com.vix.circustelegramchat.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Integer> {

    Optional<Visitor> findByChatId(String chatId);

}
