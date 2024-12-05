package com.example.eventapp;

import android.os.Environment;
import android.widget.Toast;

import com.example.eventapp.model.AgendaActivity;
import com.example.eventapp.model.Event;
import com.example.eventapp.model.PdfItem;
import com.itextpdf.io.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class AgendaPdfExporter {

    public static void exportToPdf(ArrayList<AgendaActivity> activities, Event event) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + "agenda"+new Random().nextInt(101)+".pdf";

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph title = new Paragraph("Agenda").setTextAlignment(TextAlignment.CENTER);;
            document.add(title);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, event.getDate().getYear() + 1900);
            c.set(Calendar.MONTH, event.getDate().getMonth());
            c.set(Calendar.DAY_OF_MONTH, event.getDate().getDate());
            LocalDate localDate = c.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            Paragraph eventInfo = new Paragraph("This agenda is for event " + event.getName() + " planned for " + localDate.format(formatter));
            document.add(eventInfo);

            Table table = new Table(5);
            table.addCell(new Cell().add(new Paragraph("Start Time")));
            table.addCell(new Cell().add(new Paragraph("End Time")));
            table.addCell(new Cell().add(new Paragraph("Name")));
            table.addCell(new Cell().add(new Paragraph("Description")));
            table.addCell(new Cell().add(new Paragraph("Location")));

            for (AgendaActivity activity : activities) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(activity.getStartTime()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(activity.getEndTime()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(activity.getName()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(activity.getDescription()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(activity.getLocation()))));
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
