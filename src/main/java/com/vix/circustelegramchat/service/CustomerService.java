package com.vix.circustelegramchat.service;

import com.vix.circustelegramchat.config.Constants;
import com.vix.circustelegramchat.model.Customer;
import com.vix.circustelegramchat.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements Constants {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findByChatId(String chatId) {
        return customerRepository.findByChatId(chatId).orElseGet(() -> Customer.builder()
                .chatId(chatId)
                .state(STATE_EMPTY)
                .build());
    }

    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    public void changeState(Customer customer, String state, String text) {
        switch (state) {
            case STATE_PHONE_NUMBER_ENTERED -> customer.setPhoneNumber(text);
            case STATE_FIRST_NAME_ENTERED -> customer.setFirstName(text);
            case STATE_REGISTERED -> customer.setLastName(text);
        }

        customer.setState(state);
        save(customer);
    }

    public void changeState(Customer customer, String state) {
        changeState(customer, state, "");
    }
}
