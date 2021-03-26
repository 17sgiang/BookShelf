package edu.temple.bookshelf;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BookListAdapter extends BaseAdapter {

    Context context;
    BookList bookList;

    public BookListAdapter(@NonNull Context context, @NonNull BookList bookList) {

        this.context = context;
        this.bookList = bookList;
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

//            textView.setPadding(12,20,0,20);

        } else {
            linearLayout = (LinearLayout) convertView;
            titleView = (TextView)linearLayout.getChildAt(0);
            authorView = (TextView) linearLayout.getChildAt(1);
        }

        Book myBook = bookList.get(position);

        titleView.setText(myBook.getTitle());
        authorView.setText(myBook.getAuthor());

        return linearLayout;
    }
}
