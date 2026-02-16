package com.ats.resumescreener.util;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class PdfUtil {

    public static String extractText(MultipartFile file) throws IOException {

        PDDocument doc = PDDocument.load(file.getInputStream());

        PDFTextStripper stripper = new PDFTextStripper();   

        String text = stripper.getText(doc);

        doc.close();

        return text;
    }
}
