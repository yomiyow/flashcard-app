package project.model;

import static androidx.core.content.ContextCompat.startActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import project.authentication.R;
import project.main.FlashcardOpenActivity;
import project.main.HomeActivity;


public class FlashcardRecyclerAdapter extends RecyclerView.Adapter<FlashcardRecyclerAdapter.FLashcardViewHolder> {

    private final Context context;
    private final List<FlashcardModel> flashcardList;

    public FlashcardRecyclerAdapter(Context context, List<FlashcardModel> flashcardList) {
        this.context = context;
        this.flashcardList = flashcardList;
    }

    @NonNull
    @Override
    public FlashcardRecyclerAdapter.FLashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View flashcardPreview = inflater.inflate(R.layout.flashcard_preview, parent, false);

        return new FLashcardViewHolder(flashcardPreview);
    }

    // Open flashcard that has been clicked
    private void openFlashcard(FlashcardModel flashcard) {
        Intent intent = new Intent(context, FlashcardOpenActivity.class);
        intent.putExtra("flashcard", flashcard);
        startActivity(context, intent, null);
    }

    // Delete flashcard
    private void deleteFlashcard(FlashcardModel flashcard, int position) {
        int flashcardId = flashcard.getFlashcardId();

        try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
            boolean success = dbHelper.deleteFlashcard(flashcardId);
            if (success) {
                flashcardList.remove(flashcard);
                notifyItemRemoved(position);
                Toast.makeText(context, flashcard.getTitle() + " deleted successful..", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT).show();
            }
        }

        if (flashcardList.isEmpty()) {
            ViewSwitcher viewSwitcher = ((HomeActivity) context).findViewById(R.id.home_view_switcher);
            viewSwitcher.setDisplayedChild(HomeActivity.EMPTY_VIEW);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardRecyclerAdapter.FLashcardViewHolder holder, int position) {
        FlashcardModel flashcard = flashcardList.get(position);
        holder.titleTV.setText(flashcard.getTitle());
        holder.numOfTermsTV.setText(flashcard.getNumberOfTerms() + " terms");

        holder.itemView.setOnClickListener((v) -> openFlashcard(flashcard));
        holder.deleteBtn.setOnClickListener((v) -> deleteFlashcard(flashcard, position));
    }

    @Override
    public int getItemCount() {
        return flashcardList.size();
    }

    public static class FLashcardViewHolder extends RecyclerView.ViewHolder {

         private final TextView titleTV;
         private final TextView numOfTermsTV;
         private final ImageButton deleteBtn;

        public FLashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.title);
            numOfTermsTV = itemView.findViewById(R.id.no_of_terms);
            deleteBtn = itemView.findViewById(R.id.delete_flashcard);
        }
    }
}
