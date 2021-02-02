package flashcard.ui;

import com.google.gson.Gson;
import flashcard.core.Category;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.codehaus.httpcache4j.uri.QueryParam;
import org.codehaus.httpcache4j.uri.URIBuilder;



public class WebClientCategoryClient {

  private static final Gson gson = new Gson();
  private static final URI uri = URI.create("http://localhost:8080/");

  /**
   * Retrieves a category object from the database.
   * @param name - name of the category we want to retrieve
   * @return - Category object matching the name
   * @throws IOException - If the connection fails or the category doesn't exist
   * @throws InterruptedException - If the connection is interrupted
   */
  public Category getCategory(String name) throws IOException, InterruptedException {
    URIBuilder uriBuilder = URIBuilder.fromURI(uri);
    URI uri1 = uriBuilder.addPath("getCategory")
            .addParameter(new QueryParam("name", name))
            .toURI();
    HttpRequest request = HttpRequest.newBuilder(uri1)
            .GET()
            .build();
    HttpResponse<String> response = HttpClient.newBuilder()
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString());
    return gson.fromJson(response.body(), Category.class);
  }

  /**
   * Creates a new category document in the database.
   * @param category - The new category to be stored in the database
   * @throws IOException - If the connection fails
   * @throws InterruptedException - If the connection is interrupted
   */
  public void createCategory(Category category) throws IOException, InterruptedException {
    URIBuilder uriBuilder = URIBuilder.fromURI(uri);
    URI uri1 = uriBuilder.addPath("createCategory")
            .addParameter(new QueryParam("category", gson.toJson(category)))
            .toURI();
    HttpRequest request = HttpRequest.newBuilder(uri1)
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
    HttpClient.newBuilder()
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString());
  }

  /**
   * Deletes a category stored in the database.
   * @param categoryName - Name of category to be deleted
   * @throws IOException - If connection fails or category doesn't exist
   * @throws InterruptedException - If connection is interrupted
   */
  public void deleteCategory(String categoryName) throws IOException, InterruptedException {
    URIBuilder uriBuilder = URIBuilder.fromURI(uri);
    URI uri1 = uriBuilder.addPath("deleteCategory")
            .addParameter(new QueryParam("name", categoryName))
            .toURI();
    HttpRequest request = HttpRequest.newBuilder(uri1)
            .DELETE()
            .build();
    HttpClient.newBuilder()
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString());
  }

  /**
   * Returns a list of Strings containing all the names of categories stored in the database.
   * @return - A list of Strings matching the category names
   * @throws IOException - If connection fails
   * @throws InterruptedException - If connection is interrupted
   */
  public List<String> getCategories() throws IOException, InterruptedException {
    URIBuilder uriBuilder = URIBuilder.fromURI(uri);
    URI uri1 = uriBuilder.addPath("getCategories")
            .toURI();
    HttpRequest request = HttpRequest.newBuilder(uri1)
            .GET()
            .build();
    HttpResponse<String> response = HttpClient.newBuilder()
            .build()
            .send(request, HttpResponse.BodyHandlers.ofString());
    return gson.fromJson(response.body(), List.class);
  }

  /**
   * Updates the name of a Category stored in the database.
   * @param oldName - Current name of the category to be updated
   * @param newName - The new name we want to update the Category with
   * @return int - Returns an integer corresponding to a Http-status code
   * @throws IOException - If the category doesn't exist or the connection to localhost fails
   * @throws InterruptedException - If the connection to localhost or the server is interrupted
   */
  public int updateCategoryName(String oldName, String newName)
          throws IOException, InterruptedException {
    URIBuilder uriBuilder = URIBuilder.fromURI(uri);
    URI uri1 = uriBuilder.addPath("updateCategoryName")
            .addPath(oldName)
            .addParameter(new QueryParam("newName", newName))
            .toURI();
    HttpRequest request = HttpRequest.newBuilder(uri1)
            .PUT(HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> response =
            HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
    return response.statusCode();
  }

  /**
   * Updates a category object stored in the database.
   * @param category - Category that is to be updated
   * @throws IOException - If the category doesn't exist or the connection to localhost fails
   * @throws InterruptedException - If the connection to localhost or the server is interrupted
   */
  public void updateCategory(Category category) throws IOException, InterruptedException {
    URIBuilder uriBuilder = URIBuilder.fromURI(uri);
    URI uri1 = uriBuilder.addPath("updateCategory")
            .addParameter(new QueryParam("category", gson.toJson(category)))
            .toURI();
    HttpRequest request = HttpRequest.newBuilder(uri1)
            .PUT(HttpRequest.BodyPublishers.noBody())
            .build();
    HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
  }
}
