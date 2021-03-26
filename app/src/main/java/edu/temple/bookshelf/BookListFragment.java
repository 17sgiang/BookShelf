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
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListFragment extends Fragment {
    private static final String ARG_ID = "fragId";
    private static final String ARG_BOOKLIST = "bookList";

    int id;
    private BookList bookList;
    // Since the listView is the parent layout without an id, perhaps it's just the context

    BookListFragmentInterface parentActivity;

    public BookListFragment() {
        // Required empty public constructor
    }

    // Factory method (newInstance(BookList bookList)) that creates a fragment using provided books to set up initial state.
    public static BookListFragment newInstance(int id, BookList bookList) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);

        // This implementation might not work
        args.putParcelable(ARG_BOOKLIST, (Parcelable) bookList);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

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
            id = myBundle.getInt("id");
            bookList = myBundle.getParcelable(ARG_BOOKLIST);
        } else {
            // How to generate non-duplicate ID
            // Parcel in?
            bookList = new BookList();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ListView listView = (ListView) inflater.inflate(R.layout.fragment_book_list, container,false);

//        Log.d("myTag", "Entered onCreateView");
//        Log.d("myTag", bookList.toString());
//        Log.d("myTag", container.toString());

        // TODO find out why bookList becomes null here
        BookListAdapter bookListAdapter = new BookListAdapter(getActivity(), android.R.layout.simple_list_item_1, bookList);
        listView.setAdapter(bookListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                // Call method in parentActivity that makes a BookDetailsFragment
                parentActivity.fragmentClick(getFragmentId());
            }
        });

        // When provided with the BookList object, displays title and author for each book.
        // Title and author each have their own TextView
        // When one of the books is clicked, fragment invokes a method in its parent with index of book

        // Get a reference to any Views in the layout
//        listView = layout.findViewById();

        return listView;
    }

//    public void setBookList(BookList newBookList) {
//        bookList = newBookList;
//    }

    public int getFragmentId(){
        return this.id;
    }


    // Allows for calling methods in ParentActivity
    interface BookListFragmentInterface{

        void fragmentClick(int id);

    }

}