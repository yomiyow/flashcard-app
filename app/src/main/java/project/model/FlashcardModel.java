package project.model;

import java.util.List;

public class FlashcardModel {

    private int flashcardId;
    private String title;
    private List<TermDefinition> termDefinitions;
    private int numberOfTerms;

    public FlashcardModel(int flashcardId, String title, int numberOfTerms) {
        this.flashcardId = flashcardId;
        this.title = title;
        this.numberOfTerms = numberOfTerms;
    }

    public FlashcardModel() {}

    @Override
    public String toString() {
        return "FlashcardModel{" +
                "title='" + title + '\'' +
                ", numberOfTerms=" + numberOfTerms +
                '}';
    }

    public int getFlashcardId() {
        return flashcardId;
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

    // Inner class
    public static class TermDefinition {
        private String term;
        private String definition;

        public TermDefinition(String term, String definition) {
            this.term = term;
            this.definition = definition;
        }

        @Override
        public String toString() {
            return "TermDefinition{" +
                    "term='" + term + '\'' +
                    ", definition='" + definition + '\'' +
                    '}';
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
