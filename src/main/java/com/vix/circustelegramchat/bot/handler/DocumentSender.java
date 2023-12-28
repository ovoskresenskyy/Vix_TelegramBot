package com.vix.circustelegramchat.bot.handler;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.vix.circustelegramchat.bot.util.BotUtil;
import com.vix.circustelegramchat.config.Constants;
import com.vix.circustelegramchat.model.Customer;
import com.vix.circustelegramchat.model.Performance;
import com.vix.circustelegramchat.model.Ticket;
import com.vix.circustelegramchat.service.PerformanceService;
import com.vix.circustelegramchat.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentSender implements Constants {

    private final BotUtil botUtil;
    private final TicketService ticketService;
    private final PerformanceService performanceService;

    public SendDocument handle(Customer customer, String callBackData) {
        if (callBackData.contains(CBD_GET_TICKET_ID_)) {
            return getTicket(customer, callBackData);
        }

        return SendDocument.builder().build();
    }

    private SendDocument getTicket(Customer customer, String callBackData) {
        Ticket ticket = ticketService.findById(botUtil.extractId(callBackData));
        Performance performance = performanceService.findById(ticket.getPerformanceId());

        Document ticketDPF = new Document();
        File file = new File(getTicketName(customer, performance));
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            PdfWriter.getInstance(ticketDPF, fileOutputStream);
        } catch (DocumentException | IOException e) {
            log.error("Error occurred: " + e.getMessage());
        }

        fillTheTicket(ticketDPF, getTicketText(customer, performance));

        return SendDocument.builder()
                .chatId(customer.getChatId())
                .document(new InputFile(file))
                .build();
    }

    private void fillTheTicket(Document ticket, String text) {
        ticket.open();
        Chunk chunk = new Chunk(text);
        try {
            ticket.add(chunk);
        } catch (DocumentException e) {
            log.error("Error occurred: " + e.getMessage());
        }
        ticket.close();
    }

    private String getTicketText(Customer customer, Performance performance) {
        return "Performance: " + performance.getName()
                + "\nDate: " + performance.getDate() + " " + performance.getTime()
                + "\n\nVisitor"
                + "\nFirst name: " + customer.getFirstName()
                + "\nLast name: " + customer.getLastName()
                + "\nPhone number: " + customer.getPhoneNumber();

    }

    private String getTicketName(Customer customer, Performance performance) {
        return customer.getFirstName()
                + "_" + customer.getLastName()
                + "_" + performance.getName()
                + "_" + performance.getDate()
                + "_" + performance.getTime()
                + ".pdf";
    }
}
