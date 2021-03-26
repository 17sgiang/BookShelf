package edu.temple.bookshelf;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class BookListAdapter extends ArrayAdapter {

    Context context;
    BookList bookList;

    // TODO Find out why getCount() is returning 0
    public BookListAdapter(@NonNull Context context, int resource, @NonNull BookList bookList) {
        super(context, resource, bookList);
        this.context = context;
        this.bookList = bookList;
        Log.d("myTag", "getCount(): " + getCount() + " ");
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Return a View that displays the book title and author for the position given
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("myTag", "Entered BookListAdapter.getView");
        // Figure out what parent does here, if I can use it to get rid of the linearLayout or not
        LinearLayout linearLayout;

        TextView titleView;
        TextView authorView;

        if(convertView == null){

            linearLayout = new LinearLayout(context);
            titleView = new TextView(context);
            authorView = new TextView(context);
            titleView.setTextSize(22);
            authorView.setTextSize(20);
            linearLayout.addView(titleView);
            linearLayout.addView(authorView);
//            textView.setPadding(12,20,0,20);

        } else {
            linearLayout = (LinearLayout) convertView;
            titleView = (TextView)linearLayout.getChildAt(0);
            authorView = (TextView) linearLayout.getChildAt(1);
        }

        Book myBook = bookList.get(position);
        Log.d("myTag", myBook.getTitle());
        titleView.setText(myBook.getTitle());
        authorView.setText(myBook.getAuthor());

        return linearLayout;
    }
}
