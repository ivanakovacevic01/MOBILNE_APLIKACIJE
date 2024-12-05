package com.example.eventapp;

import android.os.Environment;

import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.Guest;
import com.itextpdf.io.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class GuestListPdfExporter {

    public static void exportToPdf(ArrayList<Guest> guests, Event event) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "guest_list"+new Random().nextInt(101)+".pdf";

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph title = new Paragraph("Guest List").setTextAlignment(TextAlignment.CENTER);;
            document.add(title);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, event.getDate().getYear() + 1900);
            c.set(Calendar.MONTH, event.getDate().getMonth());
            c.set(Calendar.DAY_OF_MONTH, event.getDate().getDate());
            LocalDate localDate = c.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            Paragraph eventInfo = new Paragraph("This guest list is for event " + event.getName() + " planned for " + localDate.format(formatter));
            document.add(eventInfo);

            Table table = new Table(6);
            table.addCell(new Cell().add(new Paragraph("Guest Name")));
            table.addCell(new Cell().add(new Paragraph("Guest Last Name")));
            table.addCell(new Cell().add(new Paragraph("Age Group")));
            table.addCell(new Cell().add(new Paragraph("Invited")));
            table.addCell(new Cell().add(new Paragraph("Confirmed")));
            table.addCell(new Cell().add(new Paragraph("Special request")));

            for (Guest guest : guests) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(guest.getName()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(guest.getLastName()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(guest.getAgeGroup()))));
                if(guest.isInvited())
                    table.addCell(new Cell().add(new Paragraph(String.valueOf("Yes"))));
                else
                    table.addCell(new Cell().add(new Paragraph(String.valueOf("No"))));
                if(guest.isConfirmed())
                    table.addCell(new Cell().add(new Paragraph(String.valueOf("Yes"))));
                else
                    table.addCell(new Cell().add(new Paragraph(String.valueOf("No"))));

                table.addCell(new Cell().add(new Paragraph(String.valueOf(guest.getSpecialRequest().toString()))));
            }

            document.add(table);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
