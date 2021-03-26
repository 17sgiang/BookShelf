package edu.temple.bookshelf;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListFragment extends Fragment {

    int id;
    private BookList bookList;
    ListView listView;
    BookListFragmentInterface parentActivity;

    public BookListFragment() {
        // Required empty public constructor
    }



    // Factory method (newInstance(BookList bookList)) that creates a fragment using provided books to set up initial state.
    public static BookListFragment newInstance(int id, BookList bookList) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);

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

        }

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_book_list, container, false);

        // When provided with the BookList object, displays title and author for each book.
        // Title and author each have their own TextView
        // When one of the books is clicked, fragment invokes a method in its parent with index of book

        // Get a reference to any Views in the layout
        listView = layout.findViewById(R.id.listView);

        TextView textView = new TextView(getActivity());
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                parentActivity.fragmentClick(getFragmentId());
            }
        });

        return layout;
    }

    public void setBookList(BookList newBookList) {
        bookList = newBookList;
    }

    public int getFragmentId(){
        return this.id;
    }


    // Allows for calling methods in ParentActivity
    interface BookListFragmentInterface{

        void fragmentClick(int id);

    }

}