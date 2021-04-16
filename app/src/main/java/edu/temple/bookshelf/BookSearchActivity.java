package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookSearchActivity extends AppCompatActivity {

    // Activity triggers a search function
    // EditText allows user to enter a search term
    // Button wll perform the search
    // Optional button to cancel search and close activity
    // Results of search should be returned to the main activity (Intent)
    // When the user performs a search, the application must always display the BookListFragment

    EditText searchBox;
    Button cancelButton, searchButton;
    public static final String BOOKLIST_KEY = "booklist";
    private final String urlPrefix = "https://kamorris.com/lab/cis3515/search.php?term=";

    // JSON object fields for a book
    private final String id = "id", title = "title", author = "author",
            cover_url = "cover_url", duration = "duration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);


        // Used for calling web APIs
        // Maybe trying to create this multiple times caused the previous version to fail
        final RequestQueue requestQueue = Volley.newRequestQueue(this);


        searchBox = findViewById(R.id.search_box);
        cancelButton = findViewById(R.id.cancel_button);
        searchButton = findViewById(R.id.accept_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchUrl = urlPrefix + ((EditText) searchBox).getText().toString();

                requestQueue.add(new JsonArrayRequest(searchUrl, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Intent resultIntent = new Intent();

                        // Return retrieved books to calling activity
                        resultIntent.putExtra(BOOKLIST_KEY, getBookListFromJsonArray(response));
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "There was an error in your search, please try again later.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }));

            }
        });

    }

    private BookList getBookListFromJsonArray(JSONArray booksArray) {
        BookList bookList = new BookList();
        JSONObject tempBook;

        // Convert all books retrieved in JSON array to books in a BookList object
        for(int i = 0; i < booksArray.length(); i++){
            try {
                tempBook = booksArray.getJSONObject(i);
                bookList.add(new Book(tempBook.getInt(id), tempBook.getString(title), tempBook.getString(author), tempBook.getString(cover_url), tempBook.getInt(duration)));

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return bookList;
    }

}