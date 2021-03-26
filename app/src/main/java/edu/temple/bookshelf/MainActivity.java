package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Accessing resources
        Resources res = getResources();
        String[] titleArray = res.getStringArray(R.array.title_array);
        String[] authorArray = res.getStringArray(R.array.author_array);

//        Log.d("myTag", "Printing Title Array");
//        Log.d("myTag", titleArray[0]);
//        Log.d("myTag", authorArray[0]);
//
//        Log.d("myTag", titleArray[1]);
//        Log.d("myTag", authorArray[1]);

        // Create instance of BookList class and populate it with ten books
        BookList bookList = new BookList();

        // Change later so it only goes so far as the shorter array
        for(int i = 0; i < titleArray.length; i++){
            bookList.add(new Book(titleArray[i], authorArray[i]));
        }
//        Log.d("myTag", "Printing bookList");
//        Log.d("myTag", bookList.toString());

        // Keeps a reference to the fragment
        // Using newInstance allows us to pass information to the fragment on creation
        Fragment myFragment = BookListFragment.newInstance(1234, bookList);

        // Determine if the fragment was already created.

//        if(getSupportFragmentManager().findFragmentById(R.id.container_1) instanceof BookListFragment){}

        // Attaches the fragment to the Activity
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_1, myFragment)
                .commit();

        // using .add instead of .replace makes the fragments persist upon rotation, and aren't cleaned up.
        // inefficient, creates a fragment that was already there.
        // Find a solution that doesn't require recreation of the fragment


    }


    // MainActivity should check the layout through presence of containers
    // Depending on check, different actions should be taken when items are clicked
    // If mobile layout, should replace with BookDetails Fragment to the BackStack

    // TODO check implementation March 16 1:10:00 recording

    // Implements fragmentClick() from BookListFragment
    public void fragmentClick(int id){


    }
}