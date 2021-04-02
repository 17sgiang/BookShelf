package edu.temple.bookshelf;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class BookListAdapter extends BaseAdapter {

    Context context;
    BookList books;

    public BookListAdapter(Context context, BookList books) {

        this.context = context;
        this.books = books;

    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Return a View that displays the book title and author for the position given
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView titleTextView, authorTextView;

        if(!(convertView instanceof LinearLayout)){
            // Inflate predefined layout file that includes 2 text views
            // Need a layout file for this
            // This is an easier alternative to creating the layout in code like I did last time
            convertView = LayoutInflater.from(context).inflate(R.layout.book_list_adapter_layout, parent, false);
        }

        titleTextView = convertView.findViewById(R.id.titleTextView);
        authorTextView = convertView.findViewById(R.id.authorTextView);

        titleTextView.setText(((Book) getItem(position)).getTitle());
        authorTextView.setText(((Book) getItem(position)).getAuthor());

        return convertView;
    }
}
