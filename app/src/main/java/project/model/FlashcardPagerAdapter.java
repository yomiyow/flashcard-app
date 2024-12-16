package project.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import project.authentication.R;

public class FlashcardPagerAdapter extends RecyclerView.Adapter<FlashcardPagerAdapter.FlashcardViewHolder> {

    Context context;
    private final List<FlashcardModel.TermDefinition> termDefinitionList;

    public FlashcardPagerAdapter(Context context, List<FlashcardModel.TermDefinition> termDefinitionList) {
        this.context = context;
        this.termDefinitionList = termDefinitionList;
    }

    @NonNull
    @Override
    public FlashcardPagerAdapter.FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View flashcardItem = inflater.inflate(R.layout.flascard_item, parent, false);

        return new FlashcardViewHolder(flashcardItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardPagerAdapter.FlashcardViewHolder holder, int position) {
        FlashcardModel.TermDefinition termDefinition = termDefinitionList.get(position);
        holder.termTV.setText(termDefinition.getTerm());
    }

    @Override
    public int getItemCount() {
        return termDefinitionList.size();
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {

        TextView termTV;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            termTV = itemView.findViewById(R.id.term);
        }
    }
}
