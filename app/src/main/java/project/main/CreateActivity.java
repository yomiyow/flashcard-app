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
import androidx.annotation.Nullable;
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
import project.model.FlashcardItemAdapter;
import project.model.FlashcardModel;
import project.utils.ToastUtil;

public class CreateActivity extends AppCompatActivity {

    private Context context;
    private ImageButton leftArrowBtn;
    private ImageButton checkBtn;
    private FloatingActionButton addBtn;
    private EditText flashcardEditText;
    private RecyclerView recyclerView;
    private FlashcardItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.create_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initClassVariables();
        // add 1 empty item as default
        addNewEmptyFlashcardItem();
        leftArrowBtn.setOnClickListener((v) -> returnToPreviousActivity());
        addBtn.setOnClickListener((v) -> addNewEmptyFlashcardItem());
        checkBtn.setOnClickListener((v) -> saveFlashcard());
        swipeToDelete();
        hideKeyboardWhenScrolling();
    }

    private void initClassVariables() {
        context = CreateActivity.this;
        flashcardEditText = findViewById(R.id.flashcard_title);
        leftArrowBtn = findViewById(R.id.previous_activity);
        addBtn = findViewById(R.id.add_btn);
        checkBtn = findViewById(R.id.save_flashcard);
        recyclerView = findViewById(R.id.flashcard_new_item_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FlashcardItemAdapter(context);
        recyclerView.setAdapter(adapter);
    }

    private void returnToPreviousActivity() {
        var intent = new Intent(context, HomeActivity.class);
        startActivity(intent);
    }

    private void addNewEmptyFlashcardItem() {
        adapter.addTermDefinition(new FlashcardModel.TermDefinition("", ""));
    }

    @Nullable
    private FlashcardModel collectFlashcardsData() {
        List<FlashcardModel.TermDefinition> termDefinitionList = new ArrayList<>();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            var viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder instanceof FlashcardItemAdapter.FlashcardViewHolder) {
                var flashcardViewHolder = (FlashcardItemAdapter.FlashcardViewHolder) viewHolder;
                String term = flashcardViewHolder.getTermET().getText().toString();
                String definition = flashcardViewHolder.getDefinitionET().getText().toString();

                // Ensure there is no empty term-definition
                if (term.isEmpty() || definition.isEmpty()) {
                    ToastUtil.showToast(context, "Term and definition cannot be empty.");
                    return null;
                }

                // Store each term-definition data
                termDefinitionList.add(new FlashcardModel.TermDefinition(term, definition));
            }
        }

        return new FlashcardModel(flashcardEditText.getText().toString(), termDefinitionList);
    }

    private void saveFlashcard() {
        // Validate the title field
        String title = flashcardEditText.getText().toString().trim();
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

        // Do not save if validation fails
        var flashcardData = collectFlashcardsData();
        if (flashcardData == null) { return; }

        try (var db = new DatabaseHelper(context)) {
            boolean success = db.insertFlashcard(flashcardData);
            if (success) {
                ToastUtil.showToast(context, "Flashcard saved successfully.");
            }
        }

        returnToPreviousActivity();
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
                adapter.removeTermDefinition(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void hideKeyboardWhenScrolling() {
        // Hide keyboard when scrolling
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