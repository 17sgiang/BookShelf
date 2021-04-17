package edu.temple.bookshelf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface {

    FragmentManager fm;
    ControlFragment controlFragment;
    BookDetailsFragment bookDetailsFragment;
    BookListFragment bookListFragment;


    boolean twoPane;
    Book selectedBook;
    BookList bookList;
    Button searchButton;

    private final String TAG_BOOKLIST = "booklist", TAG_BOOKDETAILS = "bookdetails", TAG_CONTROL = "control";
    private final String KEY_SELECTED_BOOK = "selectedBook";
    private final String KEY_BOOKLIST = "booksHere";
    public static final int SEARCH_REQUEST_CODE = 12434;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fetch selected book if there was one
        if(savedInstanceState != null){
            // Fetch selected book if there was one
            selectedBook = savedInstanceState.getParcelable(KEY_SELECTED_BOOK);

            // Fetch previously searched books if one was previously retrieved
            bookList = savedInstanceState.getParcelable(KEY_BOOKLIST);

        } else {
            bookList = new BookList();
        }

        twoPane = findViewById(R.id.container_2) != null;
        fm = getSupportFragmentManager();

        Fragment fragment1 = fm.findFragmentById(R.id.container_1);
        // Fragment fragment1 = fm.findFragmentByTag();     // Circumvents timing issues

        if(fragment1 instanceof BookDetailsFragment){
            fm.popBackStack();
        } else if (!(fragment1 instanceof BookListFragment)){
            // If bookList hasn't been initiated then can't call this
            bookListFragment = BookListFragment.newInstance(bookList);
            fm.beginTransaction()
                    .add(R.id.container_1, bookListFragment, TAG_BOOKLIST)
                    .commit();
        }

        // If two containers available, load single instance of BookDetailsFragment to display all selected books
        bookDetailsFragment = (selectedBook == null) ? new BookDetailsFragment() : BookDetailsFragment.newInstance(selectedBook);

        if(twoPane){
            fm.beginTransaction()
                    .replace(R.id.container_2, bookDetailsFragment, TAG_BOOKDETAILS)
                    .commit();
        } else if (selectedBook != null){
            // If a book was selected and we have a single container
            // Replace BookListFragment with BookDetailsFragment, making the transaction reversible
            fm.beginTransaction()
                    .replace(R.id.container_1, bookDetailsFragment, TAG_BOOKDETAILS)
                    .addToBackStack(null)
                    .commit();

        }

        controlFragment = ControlFragment.newInstance();

        fm.beginTransaction()
                .add(R.id.control_container, controlFragment, TAG_CONTROL)
                .commit();

        // using .add instead of .replace makes the fragments persist upon rotation, and aren't cleaned up.
        // inefficient, creates a fragment that was already there.
        // Find a solution that doesn't require recreation of the fragment
        searchButton = findViewById(R.id.search_open_button);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Create Intent
                Intent intent = new Intent(MainActivity.this, BookSearchActivity.class);
                // Putting data into the intent
                // Starting Activity
                startActivityForResult(intent, SEARCH_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEARCH_REQUEST_CODE && resultCode == RESULT_OK){

            bookList.clear();
            bookList.addAll((BookList) data.getParcelableExtra(BookSearchActivity.BOOKLIST_KEY));

            if(bookList.size() == 0){
                Toast.makeText(this, "No books matched your search", Toast.LENGTH_SHORT).show();
            }
            showNewBooks();

            // Notify the BookListFragment of the data set changing
//            bookListFragment.updateBookList();
        }
    }

    // MainActivity should check the layout through presence of containers
    // Depending on check, different actions should be taken when items are clicked
    // If mobile layout, should replace with BookDetails Fragment to the BackStack


    // One of the BookListFragmentInterface methods
    public void bookSelected(int index){
        // Store the selected book to use later if activity restarts
        selectedBook = bookList.get(index);

        if(twoPane){
            // Display selected book using previously attached fragment
            bookDetailsFragment.displayBook(selectedBook);
        } else {
            // Display book using new fragment
            fm.beginTransaction()
                    .replace(R.id.container_1, BookDetailsFragment.newInstance(selectedBook), TAG_BOOKDETAILS)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showNewBooks(){
        if((fm.findFragmentByTag(TAG_BOOKDETAILS) instanceof BookDetailsFragment)){
            fm.popBackStack();
        }
        ((BookListFragment) fm.findFragmentByTag(TAG_BOOKLIST)).showNewBooks();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        // Saves selected book
        outState.putParcelable(KEY_SELECTED_BOOK, selectedBook);
        outState.putParcelable(KEY_BOOKLIST, bookList);
    }

    @Override
    public void onBackPressed() {
        // If the user hits the back button, clear the selected book
        selectedBook = null;
        super.onBackPressed();

    }
}