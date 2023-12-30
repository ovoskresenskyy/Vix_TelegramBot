package com.vix.circustelegramchat.service;

import com.vix.circustelegramchat.model.Ticket;
import com.vix.circustelegramchat.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket findById(int id) {
        return ticketRepository.findById(id).orElse(Ticket.builder().build());
    }

    public List<Ticket> findAllByVisitorId(int id) {
       return ticketRepository.findAllByVisitorId(id);
    }
}
