package edu.temple.bookshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

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


    }
}