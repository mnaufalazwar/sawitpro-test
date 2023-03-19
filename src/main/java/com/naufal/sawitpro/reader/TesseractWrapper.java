package com.naufal.sawitpro.reader;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Component;

@Component
public class TesseractWrapper {

  @Getter
  private ITesseract iTesseractEng;
  @Getter
  private ITesseract iTesseractChi;

  @PostConstruct
  private void init(){
    iTesseractEng = new Tesseract();
    iTesseractEng.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");

    iTesseractChi = new Tesseract();
    iTesseractChi.setLanguage("chi_sim");
    iTesseractChi.setDatapath("/home/naufal.azwar/project/personal/sawitpro/src/main/resources/tessdata");
  }


}
