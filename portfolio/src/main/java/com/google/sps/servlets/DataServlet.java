// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that writes and returns comments data. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  // Constants for certain areas of the DOM.
  private static final String COMMENT_INPUT = "comment-input";
  private static final String BOTTOM_OF_PAGE = "/index.html#connect-container";
  
  // Constants for Entity instances.
  private static final String COMMENT_ENTITY = "Comment";
  private static final String COMMENT_AMOUNT = "amount";
  private static final String COMMENT_TEXT = "text";
  private static final String COMMENT_TIMESTAMP = "timestamp";
  private static final String COMMENT_MOOD = "mood";
  private static final String COMMENT_LENGTH = "length";
  private static final String COMMENT_EMAIL = "email";
  private static final String COMMENT_NICKNAME = "nickname";
  private static final String COMMENT_IMAGE_URL = "image";
  private static final String COMMENT_SENTIMENT = "sentiment";
  
  private static final String ANONYMOUS_AUTHOR = "anonymous";
  
  // Constants for the sort order of comments.
  private static final String SORT = "sort";
  private static final String OLDEST_FIRST = "Oldest First";
  private static final String NEWEST_FIRST = "Newest First";
  private static final String LONGEST_FIRST = "Longest First";

  private static final String ID = "ID";
  private static final String USER_INFO = "userInfo";

  private static final String JPEG = "image/jpeg";
  private static final String PNG = "image/png";
  private static final String TIFF = "image/tiff";

  private DatastoreService datastore;
  
  @Override
  public void init() {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int maxComments = Integer.parseInt(request.getParameter(COMMENT_AMOUNT));
    Query query = sortedQuery(request);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // TODO: Create helper method for creating all comments.
    ArrayList<Comment> comments = new ArrayList<>();
    int commentCounter = 0;
    for (Entity comment : results.asIterable()) {
      if (commentCounter == maxComments) {
        break;
      }

      comments.add(createComment(comment));
      commentCounter++;
    } 
    
    String json = convertToJsonUsingGson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
   * Creates a Comment instance using properties from {@code comment}.
   */
  private Comment createComment(Entity comment) {
    long id = comment.getKey().getId();
    String text = (String) comment.getProperty(COMMENT_TEXT);
    long timestamp = (long) comment.getProperty(COMMENT_TIMESTAMP);
    String mood = (String) comment.getProperty(COMMENT_MOOD);
    String nickname = (String) comment.getProperty(COMMENT_NICKNAME);
    String imageUrl = (String) comment.getProperty(COMMENT_IMAGE_URL);
    double sentiment = (double) comment.getProperty(COMMENT_SENTIMENT);

    return Comment.builder().setId(id).setText(text).setTimestamp(timestamp)
              .setMood(mood).setNickname(nickname).setImageUrl(imageUrl)
              .setSentiment(sentiment).build();
  }

  /**
   * Returns a query sorted based on input from {@code request}.
   */
  private Query sortedQuery(HttpServletRequest request) {
    String sort = request.getParameter(SORT);
    Query query = new Query(COMMENT_ENTITY);

    if (sort.equals(OLDEST_FIRST)) {
      query.addSort(COMMENT_TIMESTAMP, SortDirection.ASCENDING);
    } else if (sort.equals(NEWEST_FIRST)) {
      query.addSort(COMMENT_TIMESTAMP, SortDirection.DESCENDING);
    } else if (sort.equals(LONGEST_FIRST)) {
      query.addSort(COMMENT_LENGTH, SortDirection.DESCENDING);
    }
    return query;
  }

  /**
   * Converts {@code toConvert} into a JSON string using GSON.
   */
  private String convertToJsonUsingGson(List toConvert) {
    Gson gson = new Gson();
    String json = gson.toJson(toConvert);
    return json;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = request.getParameter(COMMENT_INPUT);
    long timestamp = System.currentTimeMillis();
    String mood = (String) request.getParameter(COMMENT_MOOD);
    String nickname = HomeServlet.userNickname();
    String imageUrl = getUploadedFileUrl(request, COMMENT_IMAGE_URL).orElse("");
    double sentiment = calculateSentiment(text);

    Entity commentEntity = new Entity(COMMENT_ENTITY);
    commentEntity.setProperty(COMMENT_TEXT, text);
    commentEntity.setProperty(COMMENT_TIMESTAMP, timestamp);
    commentEntity.setProperty(COMMENT_MOOD, mood);
    commentEntity.setProperty(COMMENT_LENGTH, text.length());
    commentEntity.setProperty(COMMENT_NICKNAME, nickname);
    commentEntity.setProperty(COMMENT_IMAGE_URL, imageUrl);
    commentEntity.setProperty(COMMENT_SENTIMENT, sentiment);
    
    this.datastore.put(commentEntity);

    // Redirects to the bottom of the current page to see new comment added.
    response.sendRedirect(BOTTOM_OF_PAGE);
  }

  /** 
   * Returns a sentiment score ranging from -1 to 1 based on {@code text}, where -1 is
   * very negative and 1 is very positive.
   */
  public float calculateSentiment(String text) throws IOException {
    Document document =
        Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(document).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();

    return score;
  }

  /** 
   * Returns a URL that points to the uploaded file based on user input in 
   * {@code formInputElementName}. If the user didn't upload a file, returns an empty Optional.
   */
  private Optional<String> getUploadedFileUrl(HttpServletRequest request,
      String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    Optional<List<BlobKey>> blobKeys = Optional.ofNullable(blobs.get("image"));
    
    // Cannot get a URL because the user submitted the form without selecting a file.
    if (!blobKeys.isPresent() || blobKeys.get().isEmpty()) {
      return Optional.empty();
    }

    // Gets the first index because the comment form only takes in one file input.
    BlobKey blobKey = blobKeys.get().get(0);

    // Cannot get a URL because the user submitted the form on the live server without 
    // selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return Optional.empty();
    }

    String fileInfo = blobInfo.getContentType();

    // Return empty optional if file is not a jpg, png or tiff image.
    if (!fileInfo.equals(JPEG) && !fileInfo.equals(PNG) && !fileInfo.equals(TIFF)) {
      return Optional.empty();
    }

    return Optional.of(blobKey.getKeyString());
  }
}
