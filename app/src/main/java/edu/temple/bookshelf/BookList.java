package edu.temple.bookshelf;

import java.util.ArrayList;

public class BookList {

    // ArrayList of Book objects
    // Is there anything special about this class?
    ArrayList<Book> bookArrayList = new ArrayList<Book>();

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
