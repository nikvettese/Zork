public class Character {
    private String name;
    private String reaction;

    public Character() {
    }

    public Character(String name, String reaction) {
        this.name = name;
        this.reaction = reaction;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReaction() {
        return this.reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
