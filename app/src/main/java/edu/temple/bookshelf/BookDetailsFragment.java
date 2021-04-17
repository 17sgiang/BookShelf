package edu.temple.bookshelf;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.URL;

public class BookDetailsFragment extends Fragment {

    private static final String BOOK_KEY = "book";
    private Book book;

    TextView titleTextView, authorTextView;
    ImageView coverImageView;

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();

        // Set arguments in bundle
        args.putParcelable(BOOK_KEY, book);

        fragment.setArguments(args);
        return fragment;
    }

    // Setting up the information
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            book = getArguments().getParcelable(BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_details, container, false);

        titleTextView = v.findViewById(R.id.titleTextView);
        authorTextView = v.findViewById(R.id.authorTextView);
        coverImageView = v.findViewById(R.id.coverImageView);

        // Fragment can be created with or without a book to display when attached
        // so we need to make sure we don't try to display a book if one isn't provided
        if(book!= null){
            displayBook(book);
        }
        return v;

    }

    // BookDetailsFragment should eventually already have an instance of bookList
    // This method should instead just change the displayed book depending on position given

    // Current implementation just displays a book given
    public void displayBook(Book book){
        titleTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());

        Glide.with(this).load(book.getCoverURL()).into(coverImageView);

    }

}