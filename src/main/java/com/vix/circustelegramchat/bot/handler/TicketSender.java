package com.vix.circustelegramchat.bot.handler;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class is responsible for handling request from user to send the ticket.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TicketSender implements Constants {

    private final TicketService ticketService;
    private final PerformanceService performanceService;

    /**
     * This method is responsible for getting SendDocument with the ticket
     *
     * @param visitor  - The visitor who ask fow the ticket
     * @param ticketId - ID of ticket to be sent
     * @return The SendDocument with the PDF ticket inside
     */
    public SendDocument getTicket(Visitor visitor, int ticketId) {
        Ticket ticket = ticketService.findById(ticketId);
        Performance performance = performanceService.findById(ticket.getPerformanceId());
        File pdfTicket = getPDFTicket(ticket, performance);

        return SendDocument.builder()
                .chatId(visitor.getChatId())
                .document(new InputFile(pdfTicket))
                .build();
    }

    /**
     * This method prepares PDF file based on ticket and performance
     *
     * @param ticket      - Ticket on the basis of which the file will be made
     * @param performance - Performance on the basis of which the file will be made
     * @return PDF file of the ticket
     */
    private File getPDFTicket(Ticket ticket, Performance performance) {

        Document document = new Document();

        File directory = getTicketsDirectory();
        String ticketName = getTicketName(ticket.getId(), performance);
        File ticketPDF = new File(directory, ticketName);
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

    /**
     * This method fills the ticket with the received data
     *
     * @param document - The document to be filled
     * @param text     - The text to be filled into the file
     */
    private void fillTheTicket(Document document, String text) {
        Paragraph p = new Paragraph();
        p.add(text);
        try {
            document.add(p);
        } catch (DocumentException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }

    /**
     * This method prepares the text which will be inputted into the file
     *
     * @param ticket      - Ticket on the basis of which the text will be made
     * @param performance - Performance on the basis of which the text will be made
     * @return Prepared text for the ticket
     */
    private String getTicketText(Ticket ticket, Performance performance) {
        return "Performance: " + performance.getName()
                + "\nDate: " + performance.getDate() + " " + performance.getTime()
                + "\n\nVisitor"
                + "\nFirst name: " + ticket.getVisitorFirstName()
                + "\nLast name: " + ticket.getVisitorLastName()
                + "\nPhone number: " + ticket.getVisitorPhoneNumber();

    }

    /**
     * This method is responsible for getting directory where asked tickets will be saved
     *
     * @return Directory, where tickets will be saved
     */
    private File getTicketsDirectory() {
        Path directory = Path.of(TICKETS_DIRECTORY);

        if (!Files.exists(directory) || Files.isDirectory(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                log.error("Error while creating tickets directory: " + e.getMessage());
            }
        }

        return new File(TICKETS_DIRECTORY);
    }

    /**
     * This method prepares the name of new file of the ticket
     *
     * @param ticketId    - ID of ticket on the basis of which the file name will be made
     * @param performance - Performance on the basis of which the file name will be made
     * @return The file name for the PDF file ticket
     */
    private String getTicketName(int ticketId, Performance performance) {
        return File.separator
                + ticketId
                + "_" + performance.getName()
                + "_" + performance.getDate()
                + ".pdf";
    }
}
