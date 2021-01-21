public class CharacterEvent {
    private String characterName;
    private int time;
    private String eventDescription;
    private String currentRoom;
    private String nextRoom;

    public String getCharacterName(){
        return this.characterName;
    }

    public void setCharacterName(String characterName){
        this.characterName = characterName;
    }

    public int getTime(){
        return this.time;
    }

    public void setTime(int time){
        this.time = time;
    }

    public String getEventDescription(){
        return this.eventDescription;
    }

    public void setEventDescription(String eventDescription){
        this.eventDescription = eventDescription;
    }

    public String getCurrentRoom(){
        return this.currentRoom;
    }

    public void setCurrentRoom(String currentRoom){
        this.currentRoom = currentRoom;
    }

    public String getNextRoom(){
        return this.nextRoom;
    }

    public void setNextRoom(String nextRoom){
        this.nextRoom = nextRoom;
    }
}
