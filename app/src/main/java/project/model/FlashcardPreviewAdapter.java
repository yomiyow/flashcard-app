package project.model;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;
import java.util.List;

import project.authentication.R;
import project.main.FlashcardOpenActivity;
import project.main.HomeActivity;
import project.utils.ToastUtil;


public class FlashcardPreviewAdapter extends RecyclerView.Adapter<FlashcardPreviewAdapter.FLashcardViewHolder> {

    private final Context context;
    private final List<FlashcardModel> flashcardList;

    public FlashcardPreviewAdapter(Context context, List<FlashcardModel> flashcardList) {
        this.context = context;
        this.flashcardList = flashcardList;
    }

    @NonNull
    @Override
    public FlashcardPreviewAdapter.FLashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        new AlertDialog.Builder(context)
            .setTitle("Delete confirmation")
            .setMessage("Are you sure you want to delete " + flashcard.getTitle() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
                    boolean success = dbHelper.deleteFlashcard(flashcard.getFlashcardId());
                    if (success) {
                        flashcardList.remove(flashcard);
                        notifyItemRemoved(position);
                    } else {
                        ToastUtil.showToast(context, "Deletion failed");
                    }
                }

                // If the flashcard list is empty,
                // switch HomeActivity display to empty view
                if (flashcardList.isEmpty()) {
                    ViewSwitcher homeViewSwitcher = ((HomeActivity) context).findViewById(R.id.home_view_switcher);
                    homeViewSwitcher.setDisplayedChild(HomeActivity.EMPTY_VIEW);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardPreviewAdapter.FLashcardViewHolder holder, int position) {
        FlashcardModel flashcard = flashcardList.get(position);
        holder.titleTV.setText(flashcard.getTitle());
        holder.numOfTermsTV.setText(MessageFormat.format("{0} terms", flashcard.getNumberOfTerms()));

        // event listener to flashcard preview
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
