package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class HomeServlet extends HttpServlet {
  private static final String HOME_PATH = "/";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();


    if (!userService.isUserLoggedIn()) {
      String loginUrl = userService.createLoginURL(HOME_PATH);

      response.getWriter().println("<style>form {display:none;}</style>");
      response.getWriter().println("<p>Hello friend.</p>");
      response.getWriter().println("<p>Please log in to Add a comment.</p>");
      response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
      return;
    }

    // If user has not set a nickname, redirect to nickname page
    String nickname = getUserNickname(userService.getCurrentUser().getUserId());
    if (nickname == null) {
      response.sendRedirect("/nickname");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();
    String logoutUrl = userService.createLogoutURL(HOME_PATH);

    response.getWriter().println("<p>Hello " + nickname + "!</p>");
    response.getWriter().println("<button onclick=\"changeNickname();\">Change nickname</button>");
    response.getWriter().println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
  }

  /** Returns the nickname of the user with id, or null if the user has not set a nickname. */
  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("userInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }
}
