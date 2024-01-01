package com.vix.circustelegramchat.service;

import com.vix.circustelegramchat.model.Operator;
import com.vix.circustelegramchat.repository.OperatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorRepository operatorRepository;

    public Operator findByChatId(String chatId) {
        return operatorRepository.findByChatId(chatId);
    }

    public Operator getRandomOperator() {
        List<Operator> operators = operatorRepository.findAll();
        return operators.get(new Random().nextInt(operators.size()));
    }
}
