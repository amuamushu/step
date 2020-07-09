package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class HomeServlet extends HttpServlet {
  private static String userEmail;
  private static final String HOME = "/";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = HOME;
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      response.getWriter().println("<p>Hello " + userEmail + "!</p>");
      response.getWriter().println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
    } else {
      String urlToRedirectToAfterUserLogsIn = HOME;
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      response.getWriter().println("<style>form {display:none;}</style>");
      response.getWriter().println("<p>Hello friend.</p>");
      response.getWriter().println("<p>Please log in to Add a comment.</p>");
      response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }
  }
  
  /** Returns the current user's email address.
  */
  public static String getUserEmail() {
    return userEmail;
  }
}