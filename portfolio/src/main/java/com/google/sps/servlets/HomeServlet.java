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
  private static final String USER_INFO = "userInfo";
  private static final String ID = "id";
  private static final String COMMENT_NICKNAME = "nickname";
  // Hard-coded email of webpage admin.
  private static final String ADMIN_EMAIL = "amytn@google.com";
  private static final String NICKNAME_SERVLET = "/nickname";

  private static String userEmail;
  private static String nickname;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();


    if (!userService.isUserLoggedIn()) {
      String loginUrl = userService.createLoginURL(HOME_PATH);

      response.getWriter().println("<style>#comment-form {display:none;}</style>");
      response.getWriter().println("<p>Hello friend.</p>");
      response.getWriter().println("<p>Please log in to Add a comment.</p>");
      response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
      return;
    }

    // If user has not set a nickname, redirect to nickname page
    nickname = getUserNickname(userService.getCurrentUser().getUserId());
    if (nickname == null) {
      response.sendRedirect(NICKNAME_SERVLET);
      return;
    }

    userEmail = userService.getCurrentUser().getEmail();
    String logoutUrl = userService.createLogoutURL(HOME_PATH);

    response.getWriter().println("<p>Hello " + nickname + "!</p>");
    response.getWriter().println("<button onclick=\"changeNickname();\">Change nickname</button>");
    
    // Allows the user to delete all of the comments only if the email is the same as the admin email.
    if (userEmail.equals(ADMIN_EMAIL)) {
      response.getWriter().println("<button onclick=\"deleteAllComments();\">Delete all Comments</button>");
    }

    response.getWriter().println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
  }

  /** Returns the nickname of the user with id, or null if the user has not set a nickname. */
  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query(USER_INFO)
            .setFilter(new Query.FilterPredicate(ID, Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String nickname = (String) entity.getProperty(COMMENT_NICKNAME);
    return nickname;
  }

  /** Returns the nickname of the current user when needed in other classes. */
  public static String getUserNickname() {
    return nickname;
  }
}
