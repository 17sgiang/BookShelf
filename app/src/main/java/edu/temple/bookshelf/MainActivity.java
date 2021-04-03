package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface {

    FragmentManager fm;
    BookDetailsFragment bookDetailsFragment;

    boolean twoPane;
    Book selectedBook;

    private final String KEY_SELECTED_BOOK = "selectedBook";
    public static final String EXTRA_MESSAGE = "edu.temple.bookshelf.MESSAGE";  // Change to something easier

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fetch selected book if there was one
        if(savedInstanceState != null){
            selectedBook = savedInstanceState.getParcelable(KEY_SELECTED_BOOK);
        }

        twoPane = findViewById(R.id.container_2) != null;
        fm = getSupportFragmentManager();

        Fragment fragment1 = fm.findFragmentById(R.id.container_1);
        // Fragment fragment1 = fm.findFragmentByTag();     // Circumvents timing issues

        if(fragment1 instanceof BookDetailsFragment){
            fm.popBackStack();
        } else if (!(fragment1 instanceof BookListFragment)){
            // getTestBooks() should just be a relocation of the code I used to access resources
            fm.beginTransaction()
                    .add(R.id.container_1, BookListFragment.newInstance(getTestBooks()))
                    .commit();
        }

        // If two containers available, load single instance of BookDetailsFragment to display all selected books
        bookDetailsFragment = (selectedBook == null) ? new BookDetailsFragment() : BookDetailsFragment.newInstance(selectedBook);

        if(twoPane){
            fm.beginTransaction()
                    .replace(R.id.container_2, bookDetailsFragment)
                    .commit();
        } else if (selectedBook != null){
            // If a book was selected and we have a single container
            // Replace BookListFragment with BookDetailsFragment, making the transaction reversible
            fm.beginTransaction()
                    .replace(R.id.container_1, bookDetailsFragment)
                    .addToBackStack(null)
                    .commit();

        }




        // using .add instead of .replace makes the fragments persist upon rotation, and aren't cleaned up.
        // inefficient, creates a fragment that was already there.
        // Find a solution that doesn't require recreation of the fragment


    }

    private BookList getTestBooks(){
        // Create instance of BookList class and populate it with ten books
        BookList bookList = new BookList();


        return bookList;
    };

    // MainActivity should check the layout through presence of containers
    // Depending on check, different actions should be taken when items are clicked
    // If mobile layout, should replace with BookDetails Fragment to the BackStack


    // One of the BookListFragmentInterface methods
    public void bookSelected(int index){
        // Store the selected book to use later if activity restarts
        selectedBook = getTestBooks().get(index);

        if(twoPane){
            // Display selected book using previously attached fragment
            bookDetailsFragment.displayBook(selectedBook);
        } else {
            // Display book using new fragment
            fm.beginTransaction()
                    .replace(R.id.container_1, BookDetailsFragment.newInstance(selectedBook))
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void startSearch(){
        // Create Intent
        Intent intent = new Intent(this, BookSearchActivity.class);
        // Putting data into the intent
        // Starting Activity
        startActivity(intent);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        // Saves selected book
        outState.putParcelable(KEY_SELECTED_BOOK, selectedBook);
    }

    @Override
    public void onBackPressed() {
        // If the user hits the back button, clear the selected book
        selectedBook = null;
        super.onBackPressed();

    }
}