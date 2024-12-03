package com.petalaura.library.utils;


import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class ReportGenerator {

    public String generateProductStatsPdf(List<Object[]> productStats, String from, String to) {
        String fileName = UUID.randomUUID().toString();
        String rootPath = System.getProperty("user.dir");
        String uploadDir = rootPath + "/src/main/resources/static/reports/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = uploadDir + fileName + ".pdf";
        Document document = new Document(PageSize.A1);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Paragraph title = new Paragraph("Product Stats Report");
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            Paragraph period = new Paragraph("From " + from + " to " + to);
            period.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(period);

            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(5);
            table.addCell("Product ID");
            table.addCell("Product Name");
            table.addCell("Category");
            table.addCell("Total Quantity Sold");
            table.addCell("Total Revenue");

            for (Object[] productStat : productStats) {
                table.addCell(productStat[0].toString());
                table.addCell(productStat[1].toString());
                table.addCell(String.valueOf(productStat[2]));
                table.addCell(String.valueOf(productStat[3]));
                table.addCell(String.valueOf(productStat[4]));
            }

            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return filePath;
    }

    public String generateProductStatsCsv(List<Object[]> productStats,String from, String to) {
        // Generate a unique file name using current time (for uniqueness)
        String fileName = "product_report_" + System.currentTimeMillis() + ".csv";

        // Define the directory where CSV will be stored (consider using an absolute path or external directory in production)
        String rootPath = System.getProperty("user.dir");  // Get the root directory of the project
        String uploadDir = rootPath + "/src/main/resources/static/reports/";  // Directory where the file will be stored

        // Ensure the directory exists, create it if it doesn't
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean dirsCreated = dir.mkdirs(); // Ensure directories are created
            if (!dirsCreated) {
                System.err.println("Failed to create directories: " + uploadDir);
                return null;
            }
        }

        // Full path to the CSV file
        String filePath = uploadDir + fileName;

        // Try to write the CSV file
        try (PrintWriter writer = new PrintWriter(filePath)) {
            // Write CSV header
            writer.println("Product ID,Product Name,Category,Total Quantity Sold,Total Revenue");

            // Loop through product stats and write each row
            for (Object[] productStat : productStats) {
                writer.printf("%s,%s,%s,%s,%s%n",
                        productStat[0].toString(),  // Product ID
                        productStat[1].toString(),  // Product Name
                        productStat[2].toString(),  // Category
                        productStat[3].toString(),  // Total Quantity Sold
                        productStat[4].toString()); // Total Revenue
            }

        } catch (IOException e) {
            // Handle file errors (e.g., permissions, IO exceptions)
            e.printStackTrace();
            return null;  // Return null if there was an error writing the file
        }

        // Return the generated file's path
        return filePath;
    }
}