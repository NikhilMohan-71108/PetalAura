package com.petalaura.library.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.petalaura.library.dto.DailyEarning;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfGenerator {

    private List<DailyEarning> orders;

    public void generate(HttpServletResponse response) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTiltle.setSize(20);
        Paragraph paragraph = new Paragraph("Daily report", fontTiltle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100f);
        table.setWidths(new int[]{3, 3, 3});
        table.setSpacingBefore(5);

        PdfPCell cell = new PdfPCell();

        cell.setBackgroundColor(CMYKColor.MAGENTA);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);

        cell.setPhrase(new Phrase("Date", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Earning", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Total Orders", font));
        table.addCell(cell);

        for (DailyEarning order : orders) {
            Date sqlDate = (Date) order.getDate();  // Assume this is java.sql.Date

            // Convert java.sql.Date to java.util.Date
            java.util.Date utilDate = new java.util.Date(sqlDate.getTime());

            // Convert java.util.Date to LocalDate
            LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Format LocalDate as String
            String formattedDate = formatDateToDateString(localDate);

            table.addCell(formattedDate);
            table.addCell(String.valueOf(order.getEarnings()));
            table.addCell(String.valueOf(order.getTotelOrder()));
        }
        document.add(table);

        document.close();
    }

    private String formatDateToDateString(LocalDate localDate) {
        return localDate.toString();  // Assumes the default format of "yyyy-MM-dd"
    }
}
