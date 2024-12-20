package project.model;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import project.authentication.R;

public class FlashcardPagerAdapter extends RecyclerView.Adapter<FlashcardPagerAdapter.FlashcardViewHolder> {

    private final Context context;
    private final List<FlashcardModel.TermDefinition> termDefinitionList;

    public FlashcardPagerAdapter(Context context, List<FlashcardModel.TermDefinition> termDefinitionList) {
        this.context = context;
        this.termDefinitionList = termDefinitionList;
    }

    @NonNull
    @Override
    public FlashcardPagerAdapter.FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(context);
        var flashcardItem = inflater.inflate(R.layout.flashcard_view_pager_item, parent, false);

        return new FlashcardViewHolder(flashcardItem);
    }

    private void flipCard(CardView cardView, TextView termTV, TextView definitionTV) {
        var flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_out);
        var flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_in);

        flipOut.setTarget(cardView);
        flipIn.setTarget(cardView);

        flipOut.start();
        flipIn.start();

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (termTV.getVisibility() == View.VISIBLE) {
                    termTV.setVisibility(View.GONE);
                    definitionTV.setVisibility(View.VISIBLE);
                } else {
                    termTV.setVisibility(View.VISIBLE);
                    definitionTV.setVisibility(View.GONE);
                }
            }
        });
    }

    private void toggleFlashcardVisibility(FlashcardPagerAdapter.FlashcardViewHolder holder) {
        flipCard((CardView) holder.itemView, holder.termTV, holder.definitionTV);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardPagerAdapter.FlashcardViewHolder holder, int position) {
        var termDefinition = termDefinitionList.get(position);
        holder.termTV.setText(termDefinition.getTerm());
        holder.definitionTV.setText(termDefinition.getDefinition());

        holder.itemView.setOnClickListener((v) -> toggleFlashcardVisibility(holder));
    }

    @Override
    public int getItemCount() {
        return termDefinitionList.size();
    }

    public static class FlashcardViewHolder extends RecyclerView.ViewHolder {

        private final TextView termTV;
        private final TextView definitionTV;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            termTV = itemView.findViewById(R.id.term);
            definitionTV = itemView.findViewById(R.id.definition);
        }
    }
}
