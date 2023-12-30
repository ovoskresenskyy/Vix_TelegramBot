package com.vix.circustelegramchat.bot.handler;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.vix.circustelegramchat.bot.util.BotUtil;
import com.vix.circustelegramchat.bot.Constants;
import com.vix.circustelegramchat.model.Visitor;
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

    public SendDocument handle(Visitor visitor, String callBackData) {
        if (callBackData.contains(CBD_GET_TICKET_ID_)) {
            return getTicket(visitor, callBackData);
        }

        return SendDocument.builder().build();
    }

    private SendDocument getTicket(Visitor visitor, String callBackData) {
        Ticket ticket = ticketService.findById(botUtil.extractId(callBackData));
        Performance performance = performanceService.findById(ticket.getPerformanceId());
        File pdfTicket = getPDFTicket(ticket, performance);

        return SendDocument.builder()
                .chatId(visitor.getChatId())
                .document(new InputFile(pdfTicket))
                .build();
    }

    private File getPDFTicket(Ticket ticket, Performance performance) {

        Document document = new Document();
        File ticketPDF = new File(getTicketName(ticket.getId(), performance));
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(ticketPDF);
            PdfWriter.getInstance(document, fileOutputStream);
        } catch (DocumentException | IOException e) {
            log.error("Error occurred: " + e.getMessage());
        }

        document.open();
        String ticketText = getTicketText(ticket, performance);
        fillTheTicket(document, ticketText);
        document.close();

        return ticketPDF;
    }

    private void fillTheTicket(Document document, String text) {
        Paragraph p = new Paragraph();
        p.add(text);
        try {
            document.add(p);
        } catch (DocumentException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }

    private String getTicketText(Ticket ticket, Performance performance) {
        return "Performance: " + performance.getName()
                + "\nDate: " + performance.getDate() + " " + performance.getTime()
                + "\n\nVisitor"
                + "\nFirst name: " + ticket.getVisitorFirstName()
                + "\nLast name: " + ticket.getVisitorLastName()
                + "\nPhone number: " + ticket.getVisitorPhoneNumber();

    }

    private String getTicketName(int ticketId, Performance performance) {
        return "tickets"
                + File.separator
                + ticketId
                + "_" + performance.getName()
                + "_" + performance.getDate()
                + ".pdf";
    }
}
