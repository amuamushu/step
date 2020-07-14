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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


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
  private static final String COMMENT_NAME = "name";
  private static final String COMMENT_LENGTH = "length";
  private static final String COMMENT_EMAIL = "email";
  private static final String COMMENT_NICKNAME = "nickname";
  
  private static final String ANONYMOUS_AUTHOR = "anonymous";
  
  // Constants for the sort order of comments.
  private static final String SORT = "sort";
  private static final String OLDEST_FIRST = "Oldest First";
  private static final String NEWEST_FIRST = "Newest First";
  private static final String LONGEST_FIRST = "Longest First";

  private static final String ID = "ID";
  private static final String USER_INFO = "userInfo";

  private static DatastoreService datastore;
  
  @Override
  public void init() {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int maxComments = Integer.parseInt(request.getParameter(COMMENT_AMOUNT));
    String sort = request.getParameter(SORT);

    // TODO: Move addSort() code to its own method.
    Query query = new Query(COMMENT_ENTITY);
    if (sort.equals(OLDEST_FIRST)) {
      query.addSort(COMMENT_TIMESTAMP, SortDirection.ASCENDING);
    } else if (sort.equals(NEWEST_FIRST)) {
      query.addSort(COMMENT_TIMESTAMP, SortDirection.DESCENDING);
    } else if (sort.equals(LONGEST_FIRST)) {
      query.addSort(COMMENT_LENGTH, SortDirection.DESCENDING);
    }

    PreparedQuery results = this.datastore.prepare(query);

    ArrayList<Comment> comments = new ArrayList<>();

    int commentCounter = 0;
    for (Entity comment : results.asIterable()) {
      if (commentCounter == maxComments) {
        break;
      }
      
      // TODO: Move getProperty code to its own method.
      long id = comment.getKey().getId();
      String text = (String) comment.getProperty(COMMENT_TEXT);
      long timestamp = (long) comment.getProperty(COMMENT_TIMESTAMP);
      String name = (String) comment.getProperty(COMMENT_NAME);
      String nickname = (String) comment.getProperty(COMMENT_NICKNAME);
      String email = (String) comment.getProperty(COMMENT_EMAIL);

      comments.add(Comment.create(id, text, timestamp, name, email, nickname));
      commentCounter++;
    } 
    
    String json = convertToJsonUsingGson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
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
    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();

    String text = request.getParameter(COMMENT_INPUT);
    long timestamp = System.currentTimeMillis();
    String name = (String) request.getParameter(COMMENT_NAME);
    String nickname = HomeServlet.userNickname();

    if (name.isEmpty()) {
      name = ANONYMOUS_AUTHOR;
    }
    Entity commentEntity = new Entity(COMMENT_ENTITY);
    commentEntity.setProperty(COMMENT_TEXT, text);
    commentEntity.setProperty(COMMENT_TIMESTAMP, timestamp);
    commentEntity.setProperty(COMMENT_NAME, name);
    commentEntity.setProperty(COMMENT_LENGTH, text.length());
    commentEntity.setProperty(COMMENT_EMAIL, email);
    commentEntity.setProperty(COMMENT_NICKNAME, nickname);
    
    this.datastore.put(commentEntity);

    // Redirects to the bottom of the current page to see new comment added.
    response.sendRedirect(BOTTOM_OF_PAGE);
  }
}
