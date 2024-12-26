package project.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import project.authentication.R;
import project.model.DatabaseHelper;
import project.model.EditItemAdapter;
import project.model.FlashcardModel;
import project.utils.ToastUtil;

public class EditActivity extends AppCompatActivity {

    private Context context;
    private ImageButton checkBtn;
    private FloatingActionButton addBtn;
    private EditText titleTextView;
    private RecyclerView recyclerView;
    private EditItemAdapter adapter;
    private FlashcardModel flashcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            findViewById(R.id.flashcard_new_item_rv).setPadding(0, 0, 0, imeInsets.bottom);
            return insets;
        });

        initInstanceVariables();
        populateTermDefinitions();
        addBtn.setOnClickListener((v) -> addNewEmptyFlashcardItem());
        checkBtn.setOnClickListener((v) -> saveFlashcard());
        swipeToDelete();
        hideKeyboardWhenScrolling();
    }

    private void initInstanceVariables() {
        context = EditActivity.this;
        titleTextView = findViewById(R.id.flashcard_title);
        addBtn = findViewById(R.id.add_btn);
        checkBtn = findViewById(R.id.save_flashcard);
        recyclerView = findViewById(R.id.flashcard_new_item_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        flashcard = getIntent().getParcelableExtra("flashcard");
        adapter = new EditItemAdapter(context);
        recyclerView.setAdapter(adapter);
    }

    private void addNewEmptyFlashcardItem() {
        adapter.addTermDefinition(new FlashcardModel.TermDefinition("", ""));
    }

    private void returnToPreviousActivity() {
        var intent = new Intent(context, HomeActivity.class);
//        var updatedFlashcard = collectUpdatedFlashcardsData();
//        intent.putExtra("flashcard", updatedFlashcard);
        startActivity(intent);
    }

    @NonNull
    private FlashcardModel collectUpdatedFlashcardsData() {
        List<FlashcardModel.TermDefinition> termDefinitionList = new ArrayList<>();

        for (int i = 0; i < adapter.getItemCount(); i++) {
            var viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder instanceof EditItemAdapter.FlashcardViewHolder) {
                var flashcardViewHolder = (EditItemAdapter.FlashcardViewHolder) viewHolder;
                int termDefinitionId = flashcardViewHolder.getTermDefinitionId();
                String term = flashcardViewHolder.getTermET().getText().toString();
                String definition = flashcardViewHolder.getDefinitionET().getText().toString();

                // Ensure there is no empty term-definition
                if (term.isEmpty() || definition.isEmpty()) {
                    throw new IllegalArgumentException("Term Definition cannot be empty");
                }

                termDefinitionList.add(new FlashcardModel.TermDefinition(termDefinitionId, term, definition));
            }
        }

        // save updated flashcard data and return it
        return new FlashcardModel(
                this.flashcard.getFlashcardId(),
                titleTextView.getText().toString().trim(),
                termDefinitionList,
                termDefinitionList.size()
        );
    }

    private void saveFlashcard() {
        // Validate the title field
        String title = titleTextView.getText().toString().trim();
        if (title.isEmpty()) {
            ToastUtil.showToast(context, "Title cannot be empty.");
            return;
        }

        // Ensure there is at least one term-definition
        if (adapter.getItemCount() == 0) {
            new AlertDialog.Builder(context)
                .setTitle("Incomplete Flashcard Set")
                .setMessage("Please add at least one term and definition before saving.")
                .setPositiveButton("OK", null)
                .show();
            return;
        };

        // Do not save if term or definition is empty
        FlashcardModel updatedFlashcardData;
        try {
            updatedFlashcardData = collectUpdatedFlashcardsData();
        } catch (IllegalArgumentException ex) {
            ToastUtil.showToast(context, ex.getMessage());
            return;
        }

        try (DatabaseHelper db = new DatabaseHelper(context)) {
            boolean success = db.updateFlashcard(updatedFlashcardData);
            if (success) returnToPreviousActivity();
        }
    }

    private void swipeToDelete() {
        var itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                int termDefinitionId = adapter.getTermDefinitionList().get(position).getTermDefinitionId();
                adapter.removeTermDefinition(position);

                try (var dbHelper = new DatabaseHelper(context)) {
                    boolean success = dbHelper.deleteTermDefinition(termDefinitionId);
                    if (success) {
                        ToastUtil.showToast(context, "Term deleted");
                    }
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void populateTermDefinitions() {
        // set the flashcard title
        titleTextView.setText(this.flashcard.getTitle());

        try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            int flashcardId = this.flashcard.getFlashcardId();
            var termDefinitionList = dbHelper.getFlashcardTermAndDefinition(flashcardId);
            adapter.setTermDefinitionList(termDefinitionList);
        }
    }

    private void hideKeyboardWhenScrolling() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    View view = getCurrentFocus();
                    if (view != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
    }
}