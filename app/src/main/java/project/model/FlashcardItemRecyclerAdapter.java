package project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import project.authentication.R;

public class FlashcardItemRecyclerAdapter extends RecyclerView.Adapter<FlashcardItemRecyclerAdapter.FlashcardViewHolder> {

    Context context;
    int itemCount;

    public FlashcardItemRecyclerAdapter(Context context, int itemCount) {
        this.context = context;
        this.itemCount = itemCount;
    }

    @NonNull
    @Override
    public FlashcardItemRecyclerAdapter.FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View flashcardItem = inflater.inflate(R.layout.flashcard_new_item, parent, false);

        return new FlashcardViewHolder(flashcardItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardItemRecyclerAdapter.FlashcardViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public static class FlashcardViewHolder extends RecyclerView.ViewHolder {

        EditText termET;
        EditText definitionET;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            termET = itemView.findViewById(R.id.term_et);
            definitionET = itemView.findViewById(R.id.definition_et);
        }

        public EditText getTermET() {
            return termET;
        }

        public EditText getDefinitionET() {
            return definitionET;
        }

    }

}
