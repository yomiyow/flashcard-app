package project.model;

public class FlashcardModel {

    private String title;
    private String  term;
    private String  description;
    public FlashcardModel(String title, String term, String description) {
        this.title = title;
        this.term = term;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
