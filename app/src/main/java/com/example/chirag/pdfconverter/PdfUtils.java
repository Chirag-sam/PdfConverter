package com.example.chirag.pdfconverter;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by admin on 7/30/2017.
 */

public final class PdfUtils {

    public static final String BASE_PDF_DIRECTORY = android.os.Environment.getExternalStorageDirectory().toString();

    public static void covertToPdf(ArrayList<String> imagePaths){
        Document document=new Document();
        String dirpath=android.os.Environment.getExternalStorageDirectory().toString();
        try {
            PdfWriter.getInstance(document,new FileOutputStream(dirpath+"/example.pdf")); //  Change pdf's name.
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        document.open();
        for(int i = 0; i< imagePaths.size(); i++){
            Image img = null;  // Change image's name and extension.
            try {
                img = Image.getInstance(imagePaths.get(i));
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / img.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
            img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            //img.setAlignment(Image.LEFT| Image.TEXTWRAP);

 /* float width = document.getPageSize().width() - document.leftMargin() - document.rightMargin();
 float height = document.getPageSize().height() - document.topMargin() - document.bottomMargin();
 img.scaleToFit(width, height)*/ //Or try this.

            try {
                document.add(img);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

        document.close();
    }
}
