package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BookList extends ArrayList implements Parcelable {

    // ArrayList of Book objects
    // Is there anything special about this class?
    ArrayList<Book> bookArrayList;

    // Not sure what Parcel in is
    protected BookList(Parcel in) {
        bookArrayList = new ArrayList<>();
    }

    // Overloading or whatever the term was for when I don't need to make from Parcel
    public BookList(){
        bookArrayList = new ArrayList<>();
    }

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

//    public void add(Book book){
//        Log.d("myTag", book.getTitle());
//        bookArrayList.add(book);
//    }

    public void remove(Book book){
        bookArrayList.remove(book);
    }

    public ArrayList<Book> getBookArrayList(){
        return bookArrayList;
    }

    public Book get(int position){
        return bookArrayList.get(position);
    }

    public String getBookString(int position){
        Book book = get(position);
        String toPrint = book.getTitle() + " by " + book.getAuthor();
        return toPrint;
    }
    public int size(){
        return bookArrayList.size();
    }

}
