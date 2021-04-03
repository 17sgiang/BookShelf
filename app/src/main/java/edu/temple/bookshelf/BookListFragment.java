package edu.temple.bookshelf;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class BookListFragment extends Fragment {

    private static final String BOOK_LIST_KEY = "bookList";
    private BookList books;
    private BookListAdapter bookListAdapter;
    // Since the listView is the parent layout without an id, perhaps it's just the context

    BookListFragmentInterface parentActivity;

    public BookListFragment() {
        // Required empty public constructor
    }

    // Factory method (newInstance(BookList bookList)) that creates a fragment using provided books to set up initial state.
    public static BookListFragment newInstance(BookList books) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();

        // BookList implements Parcelable
        args.putParcelable(BOOK_LIST_KEY, books);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        // Fragment needs to communicate with parent activity
        // Verify that activity implemented defined Interface
        if(context instanceof BookListFragmentInterface) {
            parentActivity = (BookListFragmentInterface) context;
        } else {
            throw new RuntimeException("Please implement the BookListFragmentInterface");
        }
    }

    // Prevent memory leaks
    @Override
    public void onDetach() {
        super.onDetach();
        parentActivity = null;
    }

    // Sets up the data
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle myBundle = getArguments();
        if(myBundle != null){
            books = myBundle.getParcelable(BOOK_LIST_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ListView listView  = (ListView) inflater.inflate(R.layout.fragment_book_list, container,false);

        bookListAdapter = new BookListAdapter(getContext(), books);

        listView.setAdapter(bookListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                // Call method in parentActivity that makes a BookDetailsFragment
                parentActivity.bookSelected(position);
            }
        });

        // When provided with the BookList object, displays title and author for each book.
        // Title and author each have their own TextView
        // When one of the books is clicked, fragment invokes a method in its parent with index of book


        return listView;
    }

    public void updateBookList() {
//        Log.d("MyTag", newBookList.toString());
//        books.clear();
//        for(int i = 0; i < newBookList.size(); i++){
//            books.add(newBookList.get(i));
//        }
        // Notify the adapter of updated data set
        bookListAdapter.notifyDataSetChanged();
    }


    // Allows for calling methods in ParentActivity
    interface BookListFragmentInterface{

        void bookSelected(int index);

    }

}