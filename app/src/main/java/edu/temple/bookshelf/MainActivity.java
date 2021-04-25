package edu.temple.bookshelf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity
        extends AppCompatActivity
        implements BookListFragment.BookListFragmentInterface ,
        ControlFragment.ControlFragmentInterface {

    FragmentManager fm;
    ControlFragment controlFragment;
    BookDetailsFragment bookDetailsFragment;
    BookListFragment bookListFragment;

    Handler progressHandler;
    AudiobookService.MediaControlBinder mediaControlBinder;
    boolean isConnected;

    // IntentService for threads
    boolean twoPane;
    Book selectedBook, playingBook;
    BookList bookList;
    Button searchButton;

    SeekBar seekBar;
    TextView nowPlayingTextView;
    Intent serviceIntent;
    ServiceConnection serviceConnection;

    private final String TAG_BOOKLIST = "booklist", TAG_BOOKDETAILS = "bookdetails", TAG_CONTROL = "control";
    private final String KEY_SELECTED_BOOK = "selectedBook";
    private final String KEY_BOOKLIST = "booksHere";
    private final String KEY_PROGRESS = "progress";
    private final String KEY_PLAYING_BOOK = "playingBook";
    public static final int SEARCH_REQUEST_CODE = 12434;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fetch selected book if there was one
        if(savedInstanceState != null){
            // Fetch selected book if there was one
            selectedBook = savedInstanceState.getParcelable(KEY_SELECTED_BOOK);

            // Fetch previously searched books if one was previously retrieved
            bookList = savedInstanceState.getParcelable(KEY_BOOKLIST);

        } else {
            bookList = new BookList();
        }

        twoPane = findViewById(R.id.container_2) != null;
        fm = getSupportFragmentManager();

        // IBinder is an interface, describes the interface of the service.
        // Connect by calling bindService()
        progressHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if(seekBar != null && msg.obj != null) {
                    seekBar.setProgress(((AudiobookService.BookProgress)msg.obj).getProgress());
                }
                return true;
                // If done with message after this, then return true
            }
        });


        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                mediaControlBinder = (AudiobookService.MediaControlBinder) binder;
                mediaControlBinder.setProgressHandler(progressHandler);
                isConnected = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isConnected = false;
            }
        };

        serviceIntent = new Intent(MainActivity.this, AudiobookService.class);
//        serviceIntent.putExtra();
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

        controlFragment = ControlFragment.newInstance();
        fm.beginTransaction()
                .add(R.id.control_container, controlFragment, TAG_CONTROL)
                .commit();


        Fragment fragment1 = fm.findFragmentById(R.id.container_1);
        // Fragment fragment1 = fm.findFragmentByTag();     // Circumvents timing issues

        if(fragment1 instanceof BookDetailsFragment){
            fm.popBackStack();
        } else if (!(fragment1 instanceof BookListFragment)){
            // If bookList hasn't been initiated then can't call this
            bookListFragment = BookListFragment.newInstance(bookList);
            fm.beginTransaction()
                    .replace(R.id.container_1, bookListFragment, TAG_BOOKLIST)
                    .commit();
        }

        // If two containers available, load single instance of BookDetailsFragment to display all selected books
        bookDetailsFragment = (selectedBook == null) ? new BookDetailsFragment() : BookDetailsFragment.newInstance(selectedBook);

        if(twoPane){
            fm.beginTransaction()
                    .replace(R.id.container_2, bookDetailsFragment, TAG_BOOKDETAILS)
                    .commit();
        } else if (selectedBook != null){
            // If a book was selected and we have a single container
            // Replace BookListFragment with BookDetailsFragment, making the transaction reversible
            fm.beginTransaction()
                    .replace(R.id.container_1, bookDetailsFragment, TAG_BOOKDETAILS)
                    .addToBackStack(null)
                    .commit();

        }

        // using .add instead of .replace makes the fragments persist upon rotation, and aren't cleaned up.
        // inefficient, creates a fragment that was already there.
        // Find a solution that doesn't require recreation of the fragment
        searchButton = findViewById(R.id.search_open_button);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Create Intent
                Intent intent = new Intent(MainActivity.this, BookSearchActivity.class);
                // Putting data into the intent
                // Starting Activity
                startActivityForResult(intent, SEARCH_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEARCH_REQUEST_CODE && resultCode == RESULT_OK){

            bookList.clear();
            bookList.addAll((BookList) data.getParcelableExtra(BookSearchActivity.BOOKLIST_KEY));

            if(bookList.size() == 0){
                Toast.makeText(this, "No books matched your search", Toast.LENGTH_SHORT).show();
            }
            // showNewBooks() notifies the BookListFragment of the data set changing
            showNewBooks();

        }
    }

    // MainActivity should check the layout through presence of containers
    // Depending on check, different actions should be taken when items are clicked
    // If mobile layout, should replace with BookDetails Fragment to the BackStack

    @Override
    public void onResume() {
        super.onResume();
        if(selectedBook != null) {
            seekBar.setMax(selectedBook.getDuration());

        }
    }

    // One of the BookListFragmentInterface methods
    public void bookSelected(int index){
        // Store the selected book to use later if activity restarts
        selectedBook = bookList.get(index);

        if(twoPane){
            // Display selected book using previously attached fragment
            bookDetailsFragment.displayBook(selectedBook);
        } else {
            // Display book using new fragment
            fm.beginTransaction()
                    .replace(R.id.container_1, BookDetailsFragment.newInstance(selectedBook), TAG_BOOKDETAILS)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showNewBooks(){
        if((fm.findFragmentByTag(TAG_BOOKDETAILS) instanceof BookDetailsFragment)){
            fm.popBackStack();
        }
        ((BookListFragment) fm.findFragmentByTag(TAG_BOOKLIST)).showNewBooks();
    }

    // ControlFragmentInterface method implementation
    @Override
    public void bookPlay() {
        // Default
        if(selectedBook != null){
            playingBook = selectedBook;

            if(isConnected){
                mediaControlBinder.play(playingBook.getId());
            }
            startService(serviceIntent);
        }
    }

    @Override
    public void bookPause() {
        if(isConnected) {
            mediaControlBinder.pause();
        }
    }

    @Override
    public void bookStop() {
        mediaControlBinder.stop();
        // Update now playing
        seekBar.setProgress(0);
        nowPlayingTextView.setText("");
    }

    @Override
    public void updateSeekProgress(int progress){
        if(playingBook != null) {
            mediaControlBinder.seekTo(progress);
        }
    }


    @Override
    public void updateNowPlaying(){
        if(playingBook != null){
            // Update now playing
            seekBar.setMax(playingBook.getDuration());
            String displayText = "Now playing: " + playingBook.getTitle();
            nowPlayingTextView.setText(displayText);
        } else {
            // No book
            nowPlayingTextView.setText("");
        }
    }

    @Override
    public void setControlReferences(SeekBar seekBar, TextView nowPlayingTextView){
        this.seekBar = seekBar;
        this.nowPlayingTextView = nowPlayingTextView;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        // Saves selected book
        outState.putParcelable(KEY_SELECTED_BOOK, selectedBook);
        outState.putParcelable(KEY_BOOKLIST, bookList);
        outState.putParcelable(KEY_PROGRESS, seekBar.onSaveInstanceState());
        outState.putParcelable(KEY_PLAYING_BOOK, playingBook);
    }


    @Override
    public void onBackPressed() {
        // If the user hits the back button, clear the selected book
        super.onBackPressed();
        selectedBook = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

}