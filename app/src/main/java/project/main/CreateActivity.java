package project.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
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
import project.model.FlashcardItemRecyclerAdapter;
import project.model.FlashcardModel;

public class CreateActivity extends AppCompatActivity {

    private Context context;
    private ImageButton previousAct;
    private ImageButton saveBtn;
    private FloatingActionButton addBtn;
    private EditText flashcardTitle;
    private RecyclerView recyclerView;
    private FlashcardItemRecyclerAdapter adapter;

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
        previousAct.setOnClickListener((v) -> returnToPreviousActivity());
        addBtn.setOnClickListener((v) -> addNewEmptyFlashcardItem());
        saveBtn.setOnClickListener((v) -> saveFlashcard());
        swipeToDelete();
    }

    private void initClassVariables() {
        context = CreateActivity.this;
        flashcardTitle = findViewById(R.id.flashcard_title);
        previousAct = findViewById(R.id.previous_activity);
        addBtn = findViewById(R.id.add_btn);
        saveBtn = findViewById(R.id.save_flashcard);
        recyclerView = findViewById(R.id.flashcard_new_item_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FlashcardItemRecyclerAdapter(context);
        recyclerView.setAdapter(adapter);
    }

    private void returnToPreviousActivity() {
        Intent intent = new Intent(CreateActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void addNewEmptyFlashcardItem() {
        adapter.addTermDefinition(new FlashcardModel.TermDefinition("", ""));
    }

    @Nullable
    private FlashcardModel collectFlashcardsData() {
        // Validate the title field
        String title = flashcardTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(context, "Title cannot be empty.", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Ensure there is at least one term-definition
        if (adapter.getItemCount() == 0) {
            Toast.makeText(context, "Add at least 1 item.", Toast.LENGTH_SHORT).show();
            return null;
        };

        FlashcardModel flashcard = new FlashcardModel();
        List<FlashcardModel.TermDefinition> termDefinitionList = new ArrayList<>();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder instanceof FlashcardItemRecyclerAdapter.FlashcardViewHolder) {
                FlashcardItemRecyclerAdapter.FlashcardViewHolder flashcardItem = (FlashcardItemRecyclerAdapter.FlashcardViewHolder) viewHolder;
                String term = flashcardItem.getTermET().getText().toString();
                String definition = flashcardItem.getDefinitionET().getText().toString();

                // Ensure there is no empty term-definition
                if (term.isEmpty() || definition.isEmpty()) {
                    Toast.makeText(context, "Term and definition cannot be empty.", Toast.LENGTH_SHORT).show();
                    return null;
                }

                termDefinitionList.add(new FlashcardModel.TermDefinition(term, definition));
            }
        }

        flashcard.setTitle(title);
        flashcard.setTermDefinitions(termDefinitionList);

        return flashcard;
    }

    private void saveFlashcard() {
        FlashcardModel flashcardData = collectFlashcardsData();

        // Do not save if validation fails
        if (flashcardData == null) {
            return;
        }

        try (DatabaseHelper db = new DatabaseHelper(context)) {
            boolean success = db.insertFlashcard(flashcardData);
            if (success) {
                Toast.makeText(this, "Flashcard saved successfully.", Toast.LENGTH_SHORT).show();
            }
        }

        returnToPreviousActivity();
    }

    private void swipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
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
}