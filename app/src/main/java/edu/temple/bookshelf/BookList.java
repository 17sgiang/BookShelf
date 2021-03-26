package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BookList extends ArrayList implements Parcelable {

    // ArrayList of Book objects
    // Is there anything special about this class?
    ArrayList<Book> bookArrayList = new ArrayList<Book>();

    // Not sure what Parcel in is
    protected BookList(Parcel in) {
    }

    // Overloading or whatever the term was for when I don't need to make from Parcel
    protected BookList(){}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookList> CREATOR = new Creator<BookList>() {
        @Override
        public BookList createFromParcel(Parcel in) {
            return new BookList(in);
        }

        @Override
        public BookList[] newArray(int size) {
            return new BookList[size];
        }
    };

    public void add(Book book){
        bookArrayList.add(book);
    }

    public void remove(Book book){
        bookArrayList.remove(book);
    }

    public Book get(int position){
        return bookArrayList.get(position);
    }

    public int size(){
        return bookArrayList.size();
    }

}
