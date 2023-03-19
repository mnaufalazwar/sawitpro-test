package com.naufal.sawitpro.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.naufal.sawitpro.reader.ImageReader;
import com.naufal.sawitpro.writer.WordWriter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class HomepageController {

  private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

  private static final String USER_IDENTIFIER_KEY = "MY_USER";
  private static final String TOKENS_DIRECTORY_PATH = "tokens";
  private static final Map<String, String> imageResources = new HashMap<>();

  @Value("${google.oauth.callback.uri}")
  private String CALLBACK_URI;

  @Value("${google.secret.key.path}")
  private Resource gdSecretKeys;

  private GoogleAuthorizationCodeFlow flow;
  @Autowired
  private ImageReader imageReader;
  @Autowired
  private WordWriter wordWriter;

  @PostConstruct
  public void init() throws Exception{
    GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(gdSecretKeys.getInputStream()));
    flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH))).build();

    imageResources.put("image1-jpg", "/home/naufal.azwar/project/personal/sawitpro/src/main/resources/images/ImageWithWords1.jpg");
    imageResources.put("image2-png", "/home/naufal.azwar/project/personal/sawitpro/src/main/resources/images/ImageWithWords2.png");
    imageResources.put("image3-jpg", "/home/naufal.azwar/project/personal/sawitpro/src/main/resources/images/ImageWithWords3.jpg");
    imageResources.put("image4-jpg", "/home/naufal.azwar/project/personal/sawitpro/src/main/resources/images/ImageWithWords4.jpg");
  }

  @GetMapping(value = "/")
  public String showHomePage() throws Exception{
    boolean isUserAuthenticated = false;

    Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
    if(credential != null){
      boolean tokenValid = credential.refreshToken();
      if(tokenValid){
        isUserAuthenticated = true;
      }
    }

    return isUserAuthenticated ? "dashboard.html" : "index.html";
  }

  @GetMapping(value = {"/googlesignin"})
  public void doGoogleSignIn(HttpServletResponse response) throws Exception{
    GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
    String redirectURL = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
    response.sendRedirect(redirectURL);
  }
  
  @GetMapping(value = {"/oauth"})
  public String saveAuthorizationCode(HttpServletRequest request) throws Exception{
    String code = request.getParameter("code");
    if(code != null){
      saveToken(code);
      return "dashboard.html";
    }
    return "index.html";
  }

  private void saveToken(String code) throws Exception{
    GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
    flow.createAndStoreCredential(response, USER_IDENTIFIER_KEY);
  }

  @GetMapping(value = { "/uploadImages" })
  public void createFile(HttpServletResponse response) throws Exception {

    List<String> imageNames = imageResources.keySet().stream().toList();

    for (int i = 0; i < imageNames.size(); i++){
      String fileName = imageNames.get(i);
      String[] fileNameType = fileName.split("-");
      String fileNameWithType = fileNameType[0] + "." + fileNameType[1];
      String fileType = "image/" + fileNameType[1];

      log.info("start upload image - {}", fileNameWithType);

      Credential cred = flow.loadCredential(USER_IDENTIFIER_KEY);

      Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
          .setApplicationName("sawitpro-test").build();

      File file = new File();
      file.setName(fileNameWithType);
      file.setParents(Arrays.asList("1kanfYX4cGyxIaPPNbecngd_PCjyALl5H"));

      String filePath = imageResources.get(fileName);
      FileContent content = new FileContent(
          fileType,
          new java.io.File(filePath));
      File uploadedFile = drive.files().create(file, content).setFields("id").execute();

      String fileReference = String.format("{fileID: '%s'}", uploadedFile.getId());
      response.getWriter().write(fileReference);

      log.info("file " + fileName + " uploaded {}", fileReference);
    }
  }

  @GetMapping(value = { "/readImageText" })
  public void readImage(HttpServletResponse response) throws Exception {

    List<String> imageNames = imageResources.keySet().stream().toList();

    String textEnglish = "";
    String textChinese = "";
    for (int i = 0; i < imageNames.size(); i++) {
      String fileName = imageNames.get(i);
      String[] fileNameType = fileName.split("-");
      String fileNameWithType = fileNameType[0] + "." + fileNameType[1];
      String filePath = imageResources.get(fileName);

      textEnglish = textEnglish + imageReader.getImageEnglishText(filePath) + " ";
      textChinese = textChinese + imageReader.getImageChineseText(filePath) + " ";
    }
    wordWriter.writeEnglishWordsToHtmlFile(textEnglish);
    wordWriter.writeChineseWordsToHtmlFile(textChinese);
  }

}
