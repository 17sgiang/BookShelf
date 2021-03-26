package edu.temple.bookshelf;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailsFragment extends Fragment {
//    public static final String ARG_BOOKLIST = "bookList";
    public static final String ARG_ID = "frag_id";
    public static final String ARG_BOOK = "book";

//    private BookList bookList;
    private Book book;

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance(int id, Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();

        // Set arguments in Bundle
        args.putParcelable(ARG_BOOK, (Parcelable)book);

        fragment.setArguments(args);
        return fragment;
    }

    // Setting up the information
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle myBundle = getArguments();
        if (myBundle != null) {
            // Get fragment id
            // Get Book
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_details, container, false);

    }

    // BookDetailsFragment should eventually already have an instance of bookList
    // This method should instead just change the displayed book depending on position given

    // Current implementation just displays a book given
    public TextView displayBook(Book book){
        // This TextView should be already created in the corresponding layout xml file
        TextView textView = new TextView(getActivity());
        return null;
    }

    // Don't need interface maybe

}