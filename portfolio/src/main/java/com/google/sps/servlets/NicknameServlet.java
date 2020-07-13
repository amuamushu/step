package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@WebServlet("/nickname")
public class NicknameServlet extends HttpServlet {
  private static final String NICKNAME = "nickname";
  private static final String ID = "id";
  private static final String USER_INFO = "userInfo";

  private static final String BOTTOM_OF_PAGE = "/index.html#connect-container";

  private static UserService userService;
  private static DatastoreService datastore;
  
  @Override
  public void init() {
    this.userService = UserServiceFactory.getUserService();
    this.datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<h1>Set Nickname</h1>");

    this.userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      String loginUrl = userService.createLoginURL("/nickname");
      out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
      return;
    }

    String nickname = getUserNickname(userService.getCurrentUser().getUserId()).orElse("");
    response.getWriter().println("<style>#comment-form {display:none;}</style>");
    out.println("<p>Set your nickname here:</p>");
    out.println("<form method=\"POST\" action=\"/nickname\">");
    out.println("<input name=\"nickname\" value=\"" + nickname + "\" />");
    out.println("<br/>");
    out.println("<button>Submit</button>");
    out.println("</form>");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    this.userService = UserServiceFactory.getUserService();
    if (!this.userService.isUserLoggedIn()) {
      response.sendRedirect("/nickname");
      return;
    }

    String nickname = request.getParameter(NICKNAME);
    String id = this.userService.getCurrentUser().getUserId();

    this.datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity(USER_INFO, id);
    entity.setProperty(ID, id);
    entity.setProperty(NICKNAME, nickname);
    // The put() function automatically inserts new data or updates existing data based on ID.
    this.datastore.put(entity);

    response.sendRedirect(BOTTOM_OF_PAGE);
  }

  /** Returns an Optional object containing the nickname of the user with {@code id},
   * or an empty Optional if the user has not set a nickname. */
  private Optional<String> getUserNickname(String id) {
    this.datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query(USER_INFO)
            .setFilter(new Query.FilterPredicate(ID, Query.FilterOperator.EQUAL, id));
    PreparedQuery results = this.datastore.prepare(query);
    Optional<Entity> optionalEntity = Optional.ofNullable(results.asSingleEntity());
    
    return optionalEntity.map(entity->(String)entity.getProperty(NICKNAME));
  }
}