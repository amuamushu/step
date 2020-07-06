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


/** Servlet that writes and returns comments data. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  // Constants for certain areas of the DOM.
  private static final String COMMENT_INPUT = "comment-input";
  private static final String BOTTOM_OF_PAGE = "/index.html#connect-container";
  
  // Constants for Entity instances.
  private static final String MESSAGE_ENTITY = "Message";
  private static final String COMMENT_PROPERTY = "comment";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Message");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> messages = new ArrayList<>();
    for (Entity message : results.asIterable()) {
      String comment = (String) message.getProperty("comment");
      messages.add(comment);
    } 
    
    String json = convertToJsonUsingGson(messages);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
  * Converts {@code toConvert} into a JSON string using GSON.
  */
  private String convertToJsonUsingGson(ArrayList toConvert) {
    Gson gson = new Gson();
    String json = gson.toJson(toConvert);
    return json;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter(COMMENT_INPUT);

    Entity messageEntity = new Entity(MESSAGE_ENTITY);
    messageEntity.setProperty(COMMENT_PROPERTY, comment);
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(messageEntity);

    // Redirects to the bottom of the current page to see new comment added.
    response.sendRedirect(BOTTOM_OF_PAGE);
  }

}
