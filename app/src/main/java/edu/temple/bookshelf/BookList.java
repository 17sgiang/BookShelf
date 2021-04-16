package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class BookList implements Parcelable {

    private ArrayList<Book> books;

    protected BookList(Parcel in) {
        books = in.createTypedArrayList(Book.CREATOR);
    }

    // Overloading or whatever the term was for when I don't need to make from Parcel
    public BookList(){
        books = new ArrayList<>();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(books);
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

    public void clear() {
        books.clear();
    }
    public void add(Book book){
        books.add(book);
    }

    public void addAll (BookList books) {
        for( int i = 0; i < books.size(); i++) {
            this.books.add(books.get(i));
        }
    }

//    public ArrayList<Book> getBookArrayList(){
//        return books;
//    }

    public Book get(int position){
        return books.get(position);
    }

    public int size(){
        return books.size();
    }

}
