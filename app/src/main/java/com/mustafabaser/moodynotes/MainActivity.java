package com.mustafabaser.moodynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mustafabaser.moodynotes.authentication.Login;
import com.mustafabaser.moodynotes.authentication.Register;
import com.mustafabaser.moodynotes.model.Note;
import com.mustafabaser.moodynotes.note.AddNote;
import com.mustafabaser.moodynotes.note.EditNote;
import com.mustafabaser.moodynotes.note.NoteDetails;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView noteLists;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        /*
        FIREBASE QUERY STRUCTURE
        Query: notes > uuid > notlarim (Firestore note structure)
        */
        Query query = fStore.collection("notes").document(user.getUid()).collection("notlarim").orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, @SuppressLint("RecyclerView") final int i, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId(); // get.Snapshot(noteViewHolder.getBindingAdapterPosition())
                noteViewHolder.view.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), NoteDetails.class);
                    intent.putExtra("title", note.getTitle());
                    intent.putExtra("content", note.getContent());
                    intent.putExtra("noteId", docId);
                    v.getContext().startActivity(intent);
                });

                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(v -> {
                    final String docId1 = noteAdapter.getSnapshots().getSnapshot(i).getId();
                    PopupMenu menu = new PopupMenu(v.getContext(), v);
                    menu.setGravity(Gravity.END);
                    menu.getMenu().add(R.string.edit).setOnMenuItemClickListener(item -> {
                        Intent intent = new Intent(v.getContext(), EditNote.class);
                        intent.putExtra("title", note.getTitle());
                        intent.putExtra("content", note.getContent());
                        intent.putExtra("noteId", docId1);
                        startActivity(intent);
                        return false;
                    });

                    menu.getMenu().add(R.string.delete).setOnMenuItemClickListener(item -> {
                        DocumentReference docRef = fStore.collection("notes").document(user.getUid()).collection("notlarim").document(docId1);
                        docRef.delete().addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(MainActivity.this, R.string.note_not_deleted, Toast.LENGTH_SHORT).show());
                        return false;
                    });
                    menu.show();
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        noteLists = findViewById(R.id.noteList);
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        /*
        GRID: "Notes" design layout
        If I set spanCount: 1, the notes are displayed one by one in the grid layout.
        If I set the spanCount: 2, two notes will appear side by side.
        */
        noteLists.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(noteAdapter);

        View headerView = nav_view.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.userDisplayName);
        TextView navUserEmail = headerView.findViewById(R.id.userDisplayEmail);

        if (user.isAnonymous()) {
            navUserEmail.setVisibility(View.GONE);
            navUserName.setText(R.string.not_logged_in);
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.sync).setVisible(true);
        } else {
            navUserName.setText(user.getDisplayName());
            navUserEmail.setText(user.getEmail());
            navigationView.getMenu().findItem(R.id.logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.sync).setVisible(false);
        }

        FloatingActionButton fab = findViewById(R.id.addNoteFloat);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(view.getContext(), AddNote.class));
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch ((menuItem.getItemId())) {
            case R.id.notes:
                //startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.addNote:
                startActivity(new Intent(this, AddNote.class));
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                break;

            case R.id.sync:
                if (user.isAnonymous()) {
                    startActivity(new Intent(this, Login.class));
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                } else {
                    Toast.makeText(this, R.string.already_sync, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rating:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;

            case R.id.shareapp:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                    String shareMessage = getString(R.string.shareapp_message);
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.shareapp_choose_one)));
                } catch (Exception e) {
                    Toast.makeText(this, R.string.error_try_again, Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;
            case R.id.logout:
                checkUser();
                break;

            default:
                Toast.makeText(this, "Moody Notes", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /*
    CHECK
    Is user anonymous or registered?
    */
    private void checkUser() {
        if (user.isAnonymous()) {
            displayAlert();
        } else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Splash.class));
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle(R.string.areyousure)
                .setMessage(R.string.logout_alert)
                .setPositiveButton(R.string.register, (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), Register.class));
                    finish();
                }).setNegativeButton(R.string.clear, (dialog, which) -> user.delete().addOnSuccessListener(aVoid -> {
                    startActivity(new Intent(getApplicationContext(), Splash.class));
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                }));
        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.writeNote) {
            startActivity(new Intent(this, AddNote.class));
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        }
        return super.onOptionsItemSelected(item);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    /*
    App-Closing Alert
    After clicking back button when there is not any other previous activity.
    */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.areyousure)
                .setMessage(R.string.closingAppAlert)
                .setPositiveButton(R.string.close, (dialog, which) -> finish())
                .setNegativeButton(R.string.No, null)
                .show();
    }
}