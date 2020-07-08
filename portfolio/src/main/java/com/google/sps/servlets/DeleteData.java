
package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that deletes all of the comment data. */
@WebServlet("/delete-data")
public class DeleteData extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // for (Entity comment: results.asIterable()) {
    //   Key myKey = comment.getKey();
    //   datastore.delete(myKey);
    // }

    Iterable<Entity> resultsList = results.asIterable();
    // datastore.delete(StreamSupport.stream(resultsList.spliterator(), false).map(entity->entity.getKey()));
    // datastore.delete(results.asList().stream().map(entity->entity.getKey()));

    StreamSupport.stream(resultsList.spliterator(), false).map(entity->entity.getKey()).forEach(datastore::delete);
  }

}
