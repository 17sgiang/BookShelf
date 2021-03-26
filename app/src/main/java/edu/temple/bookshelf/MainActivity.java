package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.res.Resources;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookListFragmentInterface {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Accessing resources
        Resources res = getResources();
        String[] titleArray = res.getStringArray(R.array.title_array);
        String[] authorArray = res.getStringArray(R.array.author_array);

        // Create instance of BookList class and populate it with ten books
        BookList bookList = new BookList();

        // Change later so it only goes so far as the shorter array
        for(int i = 0; i < titleArray.length; i++){
            bookList.add(new Book(titleArray[i], authorArray[i]));

        }

        // Keeps a reference to the fragment
        // Using newInstance allows us to pass information to the fragment on creation
        Fragment myFragment = BookListFragment.newInstance(1234, bookList);


//        // Passing information to the Fragment
//        Bundle myBundle = new Bundle();
//        myBundle.putInt("id", 1234);
//        myBundle.putParcelableArrayList(null, bookList);
//        myFragment.setArguments(myBundle);

        // Attaches the fragment to the Activity
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, myFragment)
                .commit();


    }

    // Implements fragmentClick() from BookListFragment
    public void fragmentClick(int id){


    }
}