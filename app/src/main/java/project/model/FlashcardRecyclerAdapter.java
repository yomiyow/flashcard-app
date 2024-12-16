package project.model;

import static androidx.core.content.ContextCompat.startActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import project.authentication.R;
import project.main.FlashcardOpenActivity;


public class FlashcardRecyclerAdapter extends RecyclerView.Adapter<FlashcardRecyclerAdapter.FLashcardViewHolder> {

    Context context;
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
        intent.putExtra("flashcardTitle", flashcard.getTitle());
        intent.putExtra("numberOfTerms", flashcard.getNumberOfTerms() + " terms");
        startActivity(context, intent, null);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardRecyclerAdapter.FLashcardViewHolder holder, int position) {
        FlashcardModel flashcard = flashcardList.get(position);
        holder.titleTV.setText(flashcard.getTitle());
        holder.numOfTermsTV.setText(flashcard.getNumberOfTerms() + " terms");
        holder.itemView.setOnClickListener((v) -> openFlashcard(flashcard));
    }

    @Override
    public int getItemCount() {
        return flashcardList.size();
    }

    public static class FLashcardViewHolder extends RecyclerView.ViewHolder {

         TextView titleTV;
         TextView numOfTermsTV;

        public FLashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.title);
            numOfTermsTV = itemView.findViewById(R.id.no_of_terms);
        }
    }
}
