package com.vix.circustelegramchat.repository;

import com.vix.circustelegramchat.model.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Integer> {

    Operator findByChatId(String chatId);
}
