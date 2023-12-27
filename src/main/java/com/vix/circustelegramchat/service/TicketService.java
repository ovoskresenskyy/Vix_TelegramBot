package com.vix.circustelegramchat.service;

import com.vix.circustelegramchat.model.Ticket;
import com.vix.circustelegramchat.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public void save(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    public Ticket findById(int id) {
        return ticketRepository.findById(id).orElse(Ticket.builder().build());
    }
}
