package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class BookSearchActivity extends AppCompatActivity {

    // Activity triggers a search function
    // EditText allows user to enter a search term
    // Button wll perform the search
    // Optional button to cancel search and close activity
    // Results of search should be returned to the main activity (Intent)
    // When the user performs a search, the application must always display the BookListFragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        // TODO make an onclicklistener for the button that calls callAPI

    }

    private JSONObject[] callAPI(String args){

        JSONObject[] books;

        // TODO Process the response

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://kamorris.com/lab/cis3515/search.php?term=" + args;

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                // On success, turn String to JSON object


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                // Error message
            }
        });

        // Error
        return null;
    }
}