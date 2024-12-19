package project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import project.authentication.R;

public class EditItemAdapter extends RecyclerView.Adapter<EditItemAdapter.FlashcardViewHolder> {

    private final Context context;
    private List<FlashcardModel.TermDefinition> termDefinitionList;

    public EditItemAdapter(Context context) {
        this.context = context;
    }

    public List<FlashcardModel.TermDefinition> getTermDefinitionList() {
        return termDefinitionList;
    }

    public void setTermDefinitionList(List<FlashcardModel.TermDefinition> termDefinitionList) {
        this.termDefinitionList = termDefinitionList;
    }

    @NonNull
    @Override
    public EditItemAdapter.FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(context);
        var flashcardItemView = inflater.inflate(R.layout.flashcard_new_item, parent, false);

        return new FlashcardViewHolder(flashcardItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EditItemAdapter.FlashcardViewHolder holder, int position) {
        var termDefinition = termDefinitionList.get(position);
        holder.termDefinitionId = termDefinition.getTermDefinitionId();
        holder.termET.setText(termDefinition.getTerm());
        holder.definitionET.setText(termDefinition.getDefinition());
    }

    @Override
    public int getItemCount() {
        return termDefinitionList.size();
    }

    public void addTermDefinition(FlashcardModel.TermDefinition termDefinition) {
        termDefinitionList.add(termDefinition);
        notifyItemInserted(termDefinitionList.size() - 1);
    }

    public void removeTermDefinition(int position) {
        termDefinitionList.remove(position);
        notifyItemRemoved(position);
    }

    public static class FlashcardViewHolder extends RecyclerView.ViewHolder {

        private int termDefinitionId;
        private final EditText termET;
        private final EditText definitionET;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            termET = itemView.findViewById(R.id.term_et);
            definitionET = itemView.findViewById(R.id.definition_et);
        }

        public int getTermDefinitionId() {
            return termDefinitionId;
        }

        public void setTermDefinitionId(int termDefinitionId) {
            this.termDefinitionId = termDefinitionId;
        }

        public EditText getTermET() {
            return termET;
        }

        public EditText getDefinitionET() {
            return definitionET;
        }

    }

}
