package com.naufal.sawitpro.reader;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class ImageReader {

  public String getImageEnglishText(String imageLocation){
    ITesseract iTesseract = new Tesseract();
    iTesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
    try {
      String imgText = iTesseract.doOCR(new File(imageLocation));
      return imgText;
    } catch (Exception e){
      log.error("Exception when reading image, ", e);
      return "Reading error";
    }
  }

  public String getImageChineseText(String imageLocation){
    ITesseract iTesseract = new Tesseract();
    iTesseract.setLanguage("chi_sim");
    iTesseract.setDatapath("/home/naufal.azwar/project/personal/sawitpro/src/main/resources/tessdata");
    try {
      String imgText = iTesseract.doOCR(new File(imageLocation));
      return imgText;
    } catch (Exception e){
      log.error("Exception when reading image, ", e);
      return "Reading error";
    }
  }
}
