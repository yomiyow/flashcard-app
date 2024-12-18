package project.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class FlashcardModel implements Parcelable {

    private int flashcardId;
    private String title;
    private List<TermDefinition> termDefinitions;
    private int numberOfTerms;

    public FlashcardModel(int flashcardId, String title, List<TermDefinition> termDefinitions, int numberOfTerms) {
        this.flashcardId = flashcardId;
        this.title = title;
        this.termDefinitions = termDefinitions;
        this.numberOfTerms = numberOfTerms;
    }

    public FlashcardModel(int flashcardId, String title, int numberOfTerms) {
        this.flashcardId = flashcardId;
        this.title = title;
        this.numberOfTerms = numberOfTerms;
    }

    protected FlashcardModel(Parcel in) {
        flashcardId = in.readInt();
        title = in.readString();
        numberOfTerms = in.readInt();
    }

    public FlashcardModel() {}

    public int getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(int flashcardId) {
        this.flashcardId = flashcardId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TermDefinition> getTermDefinitions() {
        return this.termDefinitions;
    }

    public void setTermDefinitions(List<TermDefinition> termDefinitions) {
        this.termDefinitions = termDefinitions;
    }

    public int getNumberOfTerms() {
        return numberOfTerms;
    }

    public void setNumberOfTerms(int numberOfTerms) {
        this.numberOfTerms = numberOfTerms;
    }

    public static final Creator<FlashcardModel> CREATOR = new Creator<FlashcardModel>() {
        @Override
        public FlashcardModel createFromParcel(Parcel in) {
            return new FlashcardModel(in);
        }

        @Override
        public FlashcardModel[] newArray(int size) {
            return new FlashcardModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(flashcardId);
        parcel.writeString(title);
        parcel.writeInt(numberOfTerms);
    }

    @Override
    public String toString() {
        return "FlashcardModel{" +
                "termDefinitions=" + termDefinitions +
                ", flashcardId=" + flashcardId +
                '}';
    }

    // Inner class
    public static class TermDefinition {

        private int termDefinitionId;
        private String term;
        private String definition;

        public TermDefinition(int termDefinitionId, String term, String definition) {
            this.termDefinitionId = termDefinitionId;
            this.term = term;
            this.definition = definition;
        }

        public TermDefinition(String term, String definition) {
            this.term = term;
            this.definition = definition;
        }

        @Override
        public String toString() {
            return "TermDefinition{" +
                    "id='" + termDefinitionId + '\'' +
                    ", term='" + term + '\'' +
                    ", definition='" + definition + '\'' +
                    '}';
        }

        public int getTermDefinitionId() {
            return termDefinitionId;
        }

        public void setTermDefinitionId(int termDefinitionId) {
            this.termDefinitionId = termDefinitionId;
        }

        public String getTerm() {
            return term;
        }

        public void setTerm(String term) {
            this.term = term;
        }

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }
    }

}
