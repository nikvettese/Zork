/*
 * Class Room - a room in an adventure game.
 *
 * Author:  Michael Kolling
 * Version: 1.1
 * Date:    August 2000
 * 
 * This class is part of Zork. Zork is a simple, text based adventure game.
 *
 * "Room" represents one location in the scenery of the game.  It is 
 * connected to at most four other rooms via exits.  The exits are labelled
 * north, east, south, west.  For each direction, the room stores a reference
 * to the neighbouring room, or null if there is no exit in that direction.
 */
import java.util.*;

public class Room {
  private String roomName;
  private String description;
  private HashMap<String, Room> exits; // stores exits of this room.
  private List<Item> items; // stores list of items in the store
  private List<Character> characters; // stores list of character in the room

  private List<RequiredAction> requiredActions;
  private List<String> possibleActions;
  /**
   * Create a room described "description". Initially, it has no exits.
   * "description" is something like "a kitchen" or "an open court yard".
   */
  public Room(String description) {
    this.description = description;
    exits = new HashMap<String, Room>();
  }

  public Room() {
    // default constructor.
    roomName = "DEFAULT ROOM";
    description = "DEFAULT DESCRIPTION";
    exits = new HashMap<String, Room>();

    items = new ArrayList<Item>();
    characters = new ArrayList<Character>();

    requiredActions = new ArrayList<RequiredAction>();
    possibleActions = new ArrayList<String>();
  }

  public void setExit(char direction, Room r) throws Exception {
    String dir = "";
    switch (direction) {
      case 'E':
        dir = "east";
        break;
      case 'W':
        dir = "west";
        break;
      case 'S':
        dir = "south";
        break;
      case 'N':
        dir = "north";
        break;
      case 'U':
        dir = "up";
        break;
      case 'D':
        dir = "down";
        break;
      default:
        throw new Exception("Invalid Direction");
    }

    exits.put(dir, r);
  }

  /**
   * Define the exits of this room. Every direction either leads to another room
   * or is null (no exit there).
   */
  public void setExits(Room north, Room east, Room south, Room west, Room up, Room down) {
    if (north != null)
      exits.put("north", north);
    if (east != null)
      exits.put("east", east);
    if (south != null)
      exits.put("south", south);
    if (west != null)
      exits.put("west", west);
    if (up != null)
      exits.put("up", up);
    if (up != null)
      exits.put("down", down);
  }

  /**
   * Take an item from the room
   * @param itemName
   * @return
   */
  public Item takeItem(String itemName){
    for(int i=0;i<items.size();i++){
      if(items.get(i).getName().equals(itemName)){
        // Get the item
        Item item = items.get(i);
        // Remove the item from the room
        items.remove(i);
        return item;
      }
    }

    return null;
  }

  public void placeItem(Item item) {
    items.add(item);
  }

  /**
   * Remove a character from a room
   * @param characterName
   * @return
   */
  public Character removeCharacter(String characterName){
    for(int i=0;i<characters.size();i++){
      if(characters.get(i).getName().equals(characterName)){
        // Get the character
        Character character = characters.get(i);
        // Remove the character from the room
        characters.remove(i);
        return character;
      }
    }

    return null;
  }

  /**
   * A character is activated or enter the room
   */
  public void addCharacter(Character character){
    characters.add(character);
  }

  /**
   * Remove the required action of the room
   * @param command
   */
  public void removeRequiredAction(Command command){
    for(int i=0;i<requiredActions.size();i++){
      Command action = requiredActions.get(i).getCommand();

      if(action.getCommandWord().equals(command.getCommandWord()) && action.getSecondWord().equals(command.getSecondWord())){
        requiredActions.remove(i);
      }
    }
  }

  /**
   * Print out required actions of the room
   */
  public void printRequiredActions(){
    for(int i=0;i<requiredActions.size(); i++){
      System.out.println(requiredActions.get(i).getDescription());
    }

    System.out.println();
  }

  public void printPossibleActions(){
    System.out.print("Following actions are allowed in this room: ");
    for(int i=0;i<possibleActions.size();i++){
      System.out.printf("%s, ", possibleActions.get(i));
    }

    System.out.println();
  }

  /**
   * Return the description of the room (the one that was defined in the
   * constructor).
   */
  public String shortDescription() {
    return "Room: " + roomName + "\n\n" + description;
  }

  /**
   * Return a long description of this room, on the form: You are in the kitchen.
   * Exits: north west
   */
  public String longDescription() {
    return "Room: " + roomName + "\n\n" + description + "\n" + exitString();
  }


  /**
   * To check if the room is able to enter or not.
   * If all required actions are done, then it's good to enter
   * @return
   */
  public boolean isClearRequiredActions(){
    return this.requiredActions.isEmpty();
  }

  /**
   * Return a string describing the room's exits, for example "Exits: north west
   * ".
   */
  private String exitString() {
    String returnString = "Exits:";
    Set keys = exits.keySet();
    for (Iterator iter = keys.iterator(); iter.hasNext();)
      returnString += " " + iter.next();
    return returnString;
  }

  /**
   * Return the room that is reached if we go from this room in direction
   * "direction". If there is no room in that direction, return null.
   */
  public Room nextRoom(String direction) {
    return (Room) exits.get(direction);
  }

  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setRoomItem(List<Item> items){
    this.items = items;
  }

  public void setCharacters(List<Character> characters){
    this.characters = characters;
  }

  public Command getRequiredAction(Command command) {
    for (int i = 0; i < requiredActions.size(); i++) {
      RequiredAction requiredAction = requiredActions.get(i);
      Command requiredCommand = requiredAction.getCommand();
      if(requiredCommand.getCommandWord().equals(command.getCommandWord())) {
        return requiredCommand;
      }
    }
    return null;
  }

  public void setRequiredActions(List<RequiredAction> requiredActions){
    this.requiredActions = requiredActions;
  }

  public void setPossibleActions(List<String> possibleActions){
    this.possibleActions = possibleActions;
  }

  public boolean isDestination() {
    if(roomName.contains("Destination")) {
      return true;
    }
    return false;
  }

  public List<Character> getCharacters() {
    return this.characters;
  }
}
