package com.event.cia103g1springboot.product.pdtorderitem.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.IOException;

public class PDFBoxExample {
    public static void main(String[] args) {
        try (PDDocument document = new PDDocument()) {
            // 創建新的 PDF 頁面
            PDPage page = new PDPage();
            document.addPage(page);

//            // 加載支持中文的字體
//            File fontFile = new File("src/main/resources/static/fonts/NotoSansCJK-Regular.ttc"); // 替換為字體路徑
//            PDType0Font font = PDType0Font.load(document, fontFile);

            // 使用 NotoSansTC 變量字體（.ttf 文件）
            File fontFile = new File("src/main/resources/static/fonts/Noto_Sans_TC/NotoSansTC-VariableFont_wght.ttf"); // 請替換為字體文件的實際路徑
            PDType0Font font = PDType0Font.load(document, fontFile);

            // 開始內容流
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(font, 16); // 使用 NotoSansTC 字體，大小 16
                contentStream.newLineAtOffset(100, 700); // 設置文字起始位置
                contentStream.showText("這是繁體中文文字！"); // 顯示繁體中文文字
                contentStream.endText();
            }

            // 保存 PDF 到檔案
            document.save("output_with_noto_sans_tc_font.pdf");
            System.out.println("PDF 生成成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}