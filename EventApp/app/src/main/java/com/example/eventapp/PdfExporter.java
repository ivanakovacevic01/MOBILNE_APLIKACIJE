package com.example.eventapp;

import com.example.eventapp.model.PdfItem;
import com.itextpdf.io.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class PdfExporter {
    public static void exportToPdf(ArrayList<PdfItem> priceLists, String filePath) {
        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph title = new Paragraph("CENOVNIK");
            document.add(title);

            Table table = new Table(6);
            table.addCell(new Cell().add(new Paragraph("Redni broj")));
            table.addCell(new Cell().add(new Paragraph("Tip")));
            table.addCell(new Cell().add(new Paragraph("Naziv")));
            table.addCell(new Cell().add(new Paragraph("Cena")));
            table.addCell(new Cell().add(new Paragraph("Popust (%)")));
            table.addCell(new Cell().add(new Paragraph("Cena sa popustom")));

            for (PdfItem priceList : priceLists) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(priceList.getNumber()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(priceList.getType()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(priceList.getName()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(priceList.getPrice()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(priceList.getDiscount()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(priceList.getDiscountPrice()))));
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
