package com.naufal.sawitpro.reader;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class ImageReader {

  @Autowired
  private TesseractWrapper tesseractWrapper;

  public String getImageEnglishText(String imageLocation){
    ITesseract iTesseract = tesseractWrapper.getITesseractEng();
    try {
      String imgText = iTesseract.doOCR(new File(imageLocation));
      return imgText;
    } catch (Exception e){
      log.error("Exception when reading image, ", e);
      return "Reading error";
    }
  }

  public String getImageChineseText(String imageLocation){
    ITesseract iTesseract = tesseractWrapper.getITesseractChi();
    try {
      String imgText = iTesseract.doOCR(new File(imageLocation));
      return imgText;
    } catch (Exception e){
      log.error("Exception when reading image, ", e);
      return "Reading error";
    }
  }
}
