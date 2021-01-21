public class RequiredAction {
    private Command command;
    private String description; 

    public RequiredAction() {
    }

    public RequiredAction(Command command, String description) {
        this.command = command;
        this.description = description;
    }

    public Command getCommand() {
        return this.command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
