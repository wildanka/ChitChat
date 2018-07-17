package com.example.dan.chitchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dan.chitchat.Model.Chat;
import com.example.dan.chitchat.adapter.ChatAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 1;

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ArrayList<Chat> mChatList;

    private ProgressBar mProgressBar;
    private RecyclerView mMessageRecyclerView;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;
    private ChatAdapter mChatAdapter;
    private Context mContext;

    private String mUsername;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUsername = ANONYMOUS;
        mContext = this;

        // Firebase Connection
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mChatDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        // initialize view reference
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);

        // initialize message recyclerview and its adapter
        mChatList = new ArrayList<>();
//        generateMessage();
        mChatAdapter = new ChatAdapter(mContext,mChatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mMessageRecyclerView.setLayoutManager(linearLayoutManager);
        mMessageRecyclerView.setAdapter(mChatAdapter);

        //initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //TODO : before edittext changed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //TODO : after edittext changed
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)
        });

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: send messages on click
                Chat chatMessage = new Chat(mMessageEditText.getText().toString(),mUsername,null);
                mChatDatabaseReference.push().setValue(chatMessage);
                //Clear input box
                mMessageEditText.setText("");
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
             // Check if user is signed in or not
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    // User is signed in
                    onSignedInInitialize(user.getDisplayName());
                    Toast.makeText(MainActivity.this, "You're now signed in. Enjoy some ChitChat",Toast.LENGTH_SHORT).show();
                }else{
                    // User is sign out
                    onSignOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){
                Toast.makeText(this,"Signed in!",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Sign in cancelled",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_sign_out:
                // sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void generateMessage(){
        mChatList.add(
                new Chat(mUsername, "halo","nulllll")
        );
        mChatList.add(
                new Chat(mUsername, "halo","nulllll")
        );
        mChatList.add(
                new Chat(mUsername, "halo","nulllll")
        );

    }

    private void onSignedInInitialize(String username){
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void onSignOutCleanUp(){
        mUsername = ANONYMOUS;
        detachDatabaseReadListener();
        mChatList.clear();
    }

    private void attachDatabaseReadListener(){

        if (mChildEventListener == null){
            // Create Listener for Listen the Child Event
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //called when a new message is inserted on to the list
                    Chat chatMessage = dataSnapshot.getValue(Chat.class); // Deserialize dataSnapshot into our Plain Java Model
                    mChatAdapter.addChat(chatMessage);
//                    mChatList.add(chatMessage);
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //when Content of existing child changed
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    //when existing message is deleted
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //if one of the child is changed position on the list
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // If some error occurred when we make changes
                }
            };
            mChatDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if (mChildEventListener != null){
            mChatDatabaseReference.removeEventListener(mChildEventListener);
        }

    }
}
