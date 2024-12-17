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
        LayoutInflater inflater = LayoutInflater.from(context);
        View flashcardItem = inflater.inflate(R.layout.flascard_item, parent, false);

        return new FlashcardViewHolder(flashcardItem);
    }

    private void flipCard(View front, View back) {
        AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_out);
        AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_in);

        flipOut.setTarget(front);
        flipIn.setTarget(back);

        flipOut.start();
        flipIn.start();

        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                front.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardPagerAdapter.FlashcardViewHolder holder, int position) {
        FlashcardModel.TermDefinition termDefinition = termDefinitionList.get(position);
        holder.termTV.setText(termDefinition.getTerm());
        holder.definitionTV.setText(termDefinition.getDefinition());

        holder.itemView.setOnClickListener((v) -> {
            if (holder.termTV.getVisibility() == View.VISIBLE) {
                flipCard(holder.termTV, holder.definitionTV);
            } else {
                flipCard(holder.definitionTV, holder.termTV);
            }
        });
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
