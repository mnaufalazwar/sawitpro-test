package com.naufal.sawitpro.writer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class WordWriter {

  private static String OUTPUT_HTML_TEMPLATE_FILE_PATH = "/home/naufal.azwar/project/personal/sawitpro/src/main/resources/templates/template.html";
  private static String ENGLISH_OUTPUT_HTML_FILE_PATH = "/home/naufal.azwar/project/personal/sawitpro/src/main/resources/templates/englishoutput.html";

  public void writeWordsToHtmlFile(String text){

    try {
      String[] words = text.split("\\s+");

      File htmlTemplateFile = new File(OUTPUT_HTML_TEMPLATE_FILE_PATH);
      String htmlString = FileUtils.readFileToString(htmlTemplateFile);
      String title = "English word";
      String body = "";

      for(String word : words){

        log.info("text = " + word);
        if(word.contains("o")){
          body = body + "<span style=\"color:blue\">" + word + " </span>";
        } else{
          body = body + "<span>" + word + " </span>";
        }
      }
      htmlString = htmlString.replace("$title", title);
      htmlString = htmlString.replace("$body", body);
      File newHtmlFile = new File(ENGLISH_OUTPUT_HTML_FILE_PATH);
      FileUtils.writeStringToFile(newHtmlFile, htmlString);
    }catch (Exception e){
      log.error("Exception when writing to file, ", e);
    }
  }
}
