import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Class Game - the main class of the "Zork" game.
 *
 * Author: Michael Kolling Version: 1.1 Date: March 2000
 * 
 * This class is the main class of the "Zork" application. Zork is a very
 * simple, text based adventure game. Users can walk around some scenery. That's
 * all. It should really be extended to make it more interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * routine.
 * 
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates the commands that
 * the parser returns.
 */
public class Game {
  private Parser parser;
  private Room currentRoom;
  private int SCORE_TO_WIN = 10000;
  private int TIMEOUT = 3500;

  private ArrayList<CharacterEvent> characterEvents;
  // This is a MASTER object that contains all of the rooms and is easily
  // accessible.
  // The key will be the name of the room -> no spaces (Use all caps and
  // underscore -> Great Room would have a key of GREAT_ROOM
  // In a hashmap keys are case sensitive.
  // masterRoomMap.get("GREAT_ROOM") will return the Room Object that is the Great
  // Room (assuming you have one).
  private HashMap<String, Room> masterRoomMap;

  // Timer of the game
  private int timer;

  // backpack space
  private int inventory = 0;

  // User's backpack
  private ArrayList<Item> backpack;

  private void initRooms(String fileName) throws Exception {
    masterRoomMap = new HashMap<String, Room>();
    Scanner roomScanner;
    try {
      HashMap<String, HashMap<String, String>> exits = new HashMap<String, HashMap<String, String>>();
      roomScanner = new Scanner(new File(fileName));
      while (roomScanner.hasNext()) {
        Room room = new Room();
        // Read the Name
        String roomName = roomScanner.nextLine();
        roomName = roomName.trim();
        room.setRoomName(roomName.split(":")[1].trim());
        // Read the Description
        String roomDescription = roomScanner.nextLine();
        roomDescription = roomDescription.trim();
        room.setDescription(roomDescription.split(":")[1].replaceAll("<br>", "\n").trim());

        // Rad all Items
        String itemsString = roomScanner.nextLine();
        itemsString = itemsString.trim();
        // Array list store items of the room
        List<Item> roomItem = new ArrayList();
        // An array of strings in the format itemName-itemValue
        String[] itemStringTmp = itemsString.split(":");
        // If there are items in this room, then parse the items
        if(itemStringTmp.length > 1){
          String[] items = itemsString.split(":")[1].split(",");
          for(String ite : items){
            String itemName = ite.split("-")[0].trim();
            
            String itemValue = ite.split("-")[1].trim();
            Item item = new Item(itemName, itemValue);
            roomItem.add(item);

            // Add the item to the room
            
          }
          room.setRoomItem(roomItem);
        }

        // Read all required actions
        String requiredActionString = roomScanner.nextLine();
        requiredActionString = requiredActionString.trim();
        // Array list store required actions of the room
        List<RequiredAction> roomRequiredActions = new ArrayList();
        // An array of strings in the format commandWord-secondCommandWord-actionDescription
        String[] requiredActionsTmp = requiredActionString.split(":");

        // If there are required actions for this room, then parsed required actions
        if(requiredActionsTmp.length > 1){
          String[] requiredActions = requiredActionString.split(":")[1].split(",");
          for(String act : requiredActions){
            String actionCommandWord = act.split("-")[0].trim();
            String actionSecondWord = act.split("-")[1].trim();
            String actionDescription = act.split("-")[2].trim();

            Command command = new Command(actionCommandWord, actionSecondWord);
            RequiredAction action = new RequiredAction(command, actionDescription);

            // Add the item to the room
            roomRequiredActions.add(action);
          }
          room.setRequiredActions(roomRequiredActions);
        }

          // Read all possible actions
          String possibleActionString = roomScanner.nextLine();
          possibleActionString = possibleActionString.trim();
          // Array list store required actions of the room
          List<String> roomPossibleActions = new ArrayList();

          String[] possibleActions = possibleActionString.split(":")[1].split(",");
          for(String ac : possibleActions){
            // Add the possible action to the room
            roomPossibleActions.add(ac);
          }
          room.setPossibleActions(roomPossibleActions);


          // Read the Exits
        String roomExits = roomScanner.nextLine();
        roomExits = roomExits.trim();
        // An array of strings in the format E-RoomName
        String[] rooms = roomExits.split(":")[1].split(",");
        HashMap<String, String> temp = new HashMap<String, String>();
        for (String s : rooms) {
          temp.put(s.split("-")[0].trim(), s.split("-")[1]);
        }

        exits.put(roomName.substring(10).trim().toUpperCase().replaceAll(" ", "_"), temp);

        // This puts the room we created (Without the exits in the masterMap)
        masterRoomMap.put(roomName.toUpperCase().substring(10).trim().replaceAll(" ", "_"), room);

        // Now we better set the exits.
      }

      for (String key : masterRoomMap.keySet()) {
        Room roomTemp = masterRoomMap.get(key);
        HashMap<String, String> tempExits = exits.get(key);
        for (String s : tempExits.keySet()) {
          // s = direction
          // value is the room.

          String roomName2 = tempExits.get(s.trim());
          Room exitRoom = masterRoomMap.get(roomName2.toUpperCase().replaceAll(" ", "_"));
          roomTemp.setExit(s.trim().charAt(0), exitRoom);
        }
      }

      roomScanner.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void loadCharacterEvent(String fileName) throws Exception {
    characterEvents = new ArrayList<>();
  
    Scanner roomScanner;
    try {
      roomScanner = new Scanner(new File(fileName));
      while (roomScanner.hasNext()) {
  
        CharacterEvent characterEvent = new CharacterEvent();
        // Read the characterName
        String characterName = roomScanner.nextLine();
        characterName = characterName.trim();
        characterEvent.setCharacterName(characterName.split(":")[1].trim());
  
        // read the event
        String event = roomScanner.nextLine();
        event = event.trim();
        String time = event.split(",")[0];
        String eventDescription = event.split(",")[1].trim();
        String currentRoom = event.split(",")[2].trim();
        String nextRoom = event.split(",")[3].trim();
  
        // Check if the room Name is valid, otherwise, skip the event
        if(!currentRoom.isEmpty() && !masterRoomMap.containsKey(currentRoom.toUpperCase())){
            System.out.printf("There is no room %s.\n", currentRoom);
            continue;
        }
  
        // Check if the room Name is valid, otherwise, skip the event
        if(!nextRoom.isEmpty() && !masterRoomMap.containsKey(nextRoom.toUpperCase())){
            System.out.printf("There is no room %s.\n", nextRoom);
            continue;
        }
  
        characterEvent.setTime(Integer.parseInt(time));
        characterEvent.setEventDescription(eventDescription);
        characterEvent.setCurrentRoom(currentRoom);
        characterEvent.setNextRoom(nextRoom);
  
        characterEvents.add(characterEvent);
      }
        roomScanner.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
  }  

  /**
   * Create the game and initialise its internal map.
   */
  public Game() {
    startGame();
  }

  /**
   * Main play routine. Loops until end of play.
   */
  public void play() {
    printWelcome();
    // Enter the main command loop. Here we repeatedly read commands and
    // execute them until the game is over.

    boolean finished = false;
    while (!finished) {
      characterPerformAction();

      if(isCaughtByCharacter()) {
        System.out.println(currentRoom.getCharacters().get(0).getReaction());
        break;
      }

      printCurrentRoom();
      Command command = parser.getCommand();
      finished = processCommand(command);

      if(currentRoom.isDestination() == true) {
        finished = true;
      }
    }
    //check win or lose
    int finalValue = getTotalValue();
    String finalTime = parseTime(timer);

    System.out.println("Total value: " + finalValue + ", total time: " + finalTime);

    if(timer < TIMEOUT && finalValue >= SCORE_TO_WIN) {
      System.out.println("You win the game!");
    } else {
      System.out.println("You lose");
    }

    System.out.println("Thank you for playing.  Good bye.");
  }

  /**
   * Print out the opening message for the player.
   */
  private void printWelcome() {
    System.out.println();
    System.out.println("Welcome to Zork!");
    System.out.println("In this game, you start inside a house, and your goal is to steal cash and valuable items.");
    System.out.println("You win the game if you leave the house with at least $10,000, in under 30 minutes.");
    System.out.println("Every action takes time.");
    System.out.println("Use walk/run and a direction (north/south/west/east/up/down) to go to another room, run takes less time.");
    System.out.println("Use backpack to list out items & money in your backpack, and to determine the total value.");
    System.out.println("When placing an item: use following syntax: (itemName-Value), take just requires itemName.");
    System.out.println("There are a couple obstacles, use your wit to pass them.");
    System.out.println("You may use restart, quit, and help. Good luck");
    System.out.println();
    //System.out.println(currentRoom.longDescription());
  }

  private void printCurrentRoom(){
    System.out.println("You have been in the house for " + parseTime(timer));
    System.out.println(currentRoom.longDescription());
    if(!currentRoom.isClearRequiredActions()){
      currentRoom.printRequiredActions();
    }
    else{
      currentRoom.printPossibleActions();
    }
  }

  public void startGame(){
    try {
        // Load rooms
        initRooms("data/rooms.dat");

        timer = 0;
        backpack = new ArrayList<>();
        currentRoom = masterRoomMap.get("ROOM1");

        System.out.println("Game start!");

        // Load characters event
        loadCharacterEvent("data/characters.dat");

    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    parser = new Parser();
}


  /**
   * Given a command, process (that is: execute) the command. If this command ends
   * the game, true is returned, otherwise false is returned.
   */
  private boolean processCommand(Command command) {
    if (command.isUnknown()) {
      System.out.println("I don't know what you mean...");
      return false;
    }
    String commandWord = command.getCommandWord();
    if (commandWord.equals("help"))
      printHelp();
    else if (commandWord.equals("restart"))
      startGame();
    else if (commandWord.equals("walk"))
      walkRoom(command);
    else if (commandWord.equals("run"))
      runRoom(command);
    else if (commandWord.equals("wait"))
      wait(command);
    else if (commandWord.equals("take"))
      takeItem(command);  
    else if (commandWord.equals("place"))
      placeItem(command);
    else if (commandWord.equals("feed"))
      feedPet(command);
    else if (commandWord.equals("play"))
      playPet(command);
    else if (commandWord.equals("open"))
      openRoom(command);
    else if (commandWord.equals("backpack")) 
      displayBackpack(command);
    else if (commandWord.equals("quit")) {
      if (command.hasSecondWord())
        System.out.println("Quit what?");
      else {
        timer = 999999;
        return true; // signal that we want to quit
      }
    } 
    return false;
  }

  private void characterPerformAction(){
    List<CharacterEvent> toBeRemoved = new ArrayList<>();

    for(int i=0;i<characterEvents.size();i++){
        CharacterEvent event = characterEvents.get(i);

        // If it's time to perform action, then have character acts
        if(timer >= event.getTime()){
            System.out.println(event.getEventDescription());

            if(!event.getCurrentRoom().isEmpty()){
                Room currentRoom = masterRoomMap.get(event.getCurrentRoom().toUpperCase());
                currentRoom.removeCharacter(event.getCharacterName());
            }

            // If character is leaving the house (Room1), then no need to add character to the house
            if(!event.getNextRoom().isEmpty() && !event.getNextRoom().equals("Room1")){
                Room nextRoom = masterRoomMap.get(event.getNextRoom().toUpperCase());

                Character character = new Character(event.getCharacterName(),
                        String.format("You are caught by %s",  event.getCharacterName()));

                nextRoom.addCharacter(character);
            }

            toBeRemoved.add(event);
        }
    }

    // Remove all performed events
    for(CharacterEvent event: toBeRemoved){
        characterEvents.remove(event);
    }
  }

  private boolean isCaughtByCharacter(){
    return currentRoom.getCharacters().size() > 0;
  }


  // implementations of user commands:
  /**
   * Print out some help information. Here we print some stupid, cryptic message
   * and a list of the command words.
   */
  private void printHelp() {
    System.out.println("Use walk/run and a direction (north/south/west/east/up/down) to go to another room, run takes less time.");
    System.out.println("Use backpack to list out items & money in your backpack, and to determine the total value.");
    System.out.println("When placing an item: use following syntax: (itemName-Value), take just requires itemName.");
    System.out.println("There are a couple obstacles, use your wit to pass them.");
    System.out.println("You may use restart, quit, and help. Good luck");
    System.out.println("Your command words are:");
    parser.showCommands();
  }

  /**
   * Try to go to one direction. If there is an exit, enter the new room,
   * otherwise print an error message.
   */
  private void goRoom(Command command) {
    if (!command.hasSecondWord()) {
      // if there is no second word, we don't know where to go...
      System.out.println("Go where?");
      return;
    }
    String direction = command.getSecondWord();
    // Try to leave current room.
    Room nextRoom = currentRoom.nextRoom(direction);
    if (nextRoom == null)
      System.out.println("There is no door!");
    else {
      // There are required actions for this room, not able to enter
      if(!nextRoom.isClearRequiredActions()){
        nextRoom.printRequiredActions();
        return;
      }

      // Enter the room
      currentRoom = nextRoom;
    }
  }

  //determine how much space taken up in backpack
  private int inventorySpace() {
    for(int i = 0; i < backpack.size(); i++) {
      if(backpack.get(i).getName().equals("food") || backpack.get(i).getName().equals("dogbone") || backpack.get(i).getName().equals("map")
      || backpack.get(i).getName().equals("jewellery") || backpack.get(i).getName().equals("book")) {
        inventory = inventory + 1;
      } else if(backpack.get(i).getName().equals("box") || backpack.get(i).getName().equals("chessmen") 
      || backpack.get(i).getName().equals("fossils") || backpack.get(i).getName().equals("candle")) {
        inventory = inventory + 2;
      } else if(backpack.get(i).getName().equals("basketball") || backpack.get(i).getName().equals("sculpture") || backpack.get(i).getName().equals("lamp")
      || backpack.get(i).getName().equals("amethyst") || backpack.get(i).getName().equals("vase")) {
        inventory = inventory + 3;
      } else if(backpack.get(i).getName().equals("painting")) {
        inventory = inventory + 10;
      }
    }
    return this.inventory;
  }

  /**
  * Try to walk to one direction. Walk action takes 20 seconds .If there is an exit, enter the new room,
  * otherwise print an error message.
  */
  private void walkRoom(Command command) {
    goRoom(command);
    this.timer += 20;
  }

  /**
  * Try to run to one direction. Walk takes longer. If there is an exit, enter the new room,
  * otherwise print an error message.
  */
  private void runRoom(Command command) {
    int inventorySpace = inventorySpace();
    if(inventorySpace > 5) {
      System.out.println("Your backpack is too heavy");
      return;
    }

    goRoom(command);
    this.timer += 10;
  }

  /**
  * Take an item and put to backpack. This action takes 30 seconds.
  * @param command
  */

  //take an item from a room
  private void takeItem(Command command){
    if(!command.hasSecondWord()) {
      System.out.println("Invalid take command");
      return;
    }

    Item item = currentRoom.takeItem(command.getSecondWord());

    if(item == null){
      System.out.printf("There is no item %s in the room\n", command.getSecondWord());
      return;
    }

    if(item.getName().equals("table") || item.getName().equals("bed") || item.getName().equals("chair") || 
    item.getName().equals("couch") || item.getName().equals("dresser") || item.getName().equals("fridge")) {
      System.out.println("You may not pick up that item");
      return;
    }

    //add to backpack
    backpack.add(item);

    int inventorySpace = inventorySpace();
    if(inventorySpace > 23) {
      System.out.println("You do not have enough room in your inventory to take this item");
      backpack.remove(item);
    }

    System.out.println("You took an item: " + item.getName() + "-" + item.getValue());

    // Count timer
    timer += 30;
  }

  /**
  * Wait action takes 30 seconds. User will do nothing
  * @param command
  */
  private void wait(Command command){
    timer += 30;
  }

  //places an item in a room, removes it from backpack
  private void placeItem(Command command){
    if(!command.hasSecondWord()){
      System.out.println("Which item will be placed?");
      return;
    }
  
    String commandSecondWord = command.getSecondWord();
    String tmp[] = commandSecondWord.split("-");
    if(tmp.length < 2){
      System.out.println("Invalid item");
      return;
    }
  
    String itemName = tmp[0];
    String itemValue = tmp[1];
    Item item = new Item(itemName, itemValue);
  
    int index = -1;
    for(int i=0;i<backpack.size();i++){
      if(backpack.get(i).getName().equals(itemName) && backpack.get(i).getValue().equals(itemValue)){
        index = i;
        break;
      }
    }
  
    if(index == -1){
      System.out.println("There is no such item in the backpack.");
      return;
    }
  
    // Remove the item out of backpack
    backpack.remove(index);
  
    // Place the item in the room
    currentRoom.placeItem(item);
  
    System.out.printf("You placed item %s-%s in %s.\n", itemName, itemValue, currentRoom.getRoomName());
    timer += 30;
  }  

  private void feedPet(Command command){
    if(!command.hasSecondWord()){
        System.out.println("Feed what?");
        return;
    }

    String s[] = command.getSecondWord().split("-");
    if(s.length < 2){
      System.out.println("What is the pet and where is it?");
      return;
    }

    String pet = s[0];
    String direction = s[1];

    // Try to go the the room
    Room nextRoom = currentRoom.nextRoom(direction);
    if (nextRoom == null){
        System.out.println("There is no door!");
    }else{
        Command action = nextRoom.getRequiredAction(command);

        // There is not such pet in the room
        if(action == null || !action.getSecondWord().equals(pet)){
            System.out.printf("There is no %s in the room.\n", pet);
            return;
        }

        // Get the pet food from backpack
        Item petFood = getItemFromBackpack("food", pet);
        if(petFood == null){
            System.out.printf("You do not have food to feed the %s.\n", pet);
            return;
        }

        // Remove the required action feed pet from the room
        nextRoom.removeRequiredAction(new Command(command.getCommandWord(), pet));

        System.out.printf("You fed the cat in %s.\n", nextRoom.getRoomName());

        // Count timer;
        timer += 30;
    }
  }

  private void playPet(Command command){
    if(!command.hasSecondWord()){
        System.out.println("Play with who?");
        return;
    }

    String s[] = command.getSecondWord().split("-");
    if(s.length < 2){
      System.out.println("What is the pet and where is it?");
      return;
    }

    String pet = s[0];
    String direction = s[1];

    // Try to go the the room
    Room nextRoom = currentRoom.nextRoom(direction);
    if (nextRoom == null){
        System.out.println("There is no door!");
    }else{
        Command action = nextRoom.getRequiredAction(command);

        // There is not such pet in the room
        if(action == null || !action.getSecondWord().equals(pet)){
            System.out.printf("There is no %s in the room.\n", pet);
            return;
        }

        // Get the pet food from backpack
        Item dogBone = getItemFromBackpack("dogbone", pet);
        if(dogBone == null){
            System.out.printf("You do not have dogbone to play with the %s.\n", pet);
            return;
        }

        // Remove the required action feed pet from the room
        nextRoom.removeRequiredAction(new Command(command.getCommandWord(), pet));

        System.out.printf("You played with the dog in %s.\n", nextRoom.getRoomName());

        // Count timer;
        timer += 30;
    }
  }

  //displays info about the backpack
  private void displayBackpack(Command command){
    System.out.print("Items in backpack: ");
    for(int i = 0; i < backpack.size(); i++) {
      String itemName = backpack.get(i).getName();
      String itemValue = backpack.get(i).getValue();
      System.out.printf("%s-%s, ", itemName, itemValue);
    }
    System.out.println();
    int totValue = this.getTotalValue();
    int inventorySpace = inventorySpace();
    System.out.println("Total value: " + totValue + ", inventory /17: " + inventorySpace);
  }

  //obtains key
  private Item getKeyFromBackpackByRoomName(String roomName){
    for(int i=0;i<backpack.size();i++){
      if(backpack.get(i).getName().equals("key") && backpack.get(i).getValue().equals(roomName)){
        Item item = backpack.get(i);
        backpack.remove(i);
        return item;
      }
    }

    return null;
  }

  /**
  * Open a room with a key. This action take 30 seconds;
  * @param command
  */
  private void openRoom(Command command){
    if(!command.hasSecondWord()){
      System.out.println("You need a key to open this room");
      return;
    }

    // Command will be: "open Room6" to open "Room 6". Second word is the room number
    String roomNumber = command.getSecondWord();

    Item key = getKeyFromBackpackByRoomName(roomNumber);
    // User does not have key to open this room
    if(key == null){
      System.out.printf("You dont have key to open %s\n", roomNumber);
      return;
    }

    String tempRoom = roomNumber.toUpperCase();
    Room nextRoom = masterRoomMap.get(tempRoom);
    
    if(nextRoom == null) {
      System.out.println("There is no door");
      return;
    }

    nextRoom.removeRequiredAction(command);

    // Still there are required actions for this room, not able to enter
    if(!nextRoom.isClearRequiredActions()){
      nextRoom.printRequiredActions();
      return;
    }

    System.out.println("The room is now unlocked");

    // Enter the room
    currentRoom = nextRoom;

    // update timer
    timer += 30;
  }

  //obtain total monetary value from items in backpack
  private int getTotalValue() {
    int totalValue = 0;
    for(int i = 0; i < backpack.size(); i++) {
      String itemValue = backpack.get(i).getValue(); 

      try {
        int val = Integer.parseInt(itemValue);
        totalValue += val;
      } catch(NumberFormatException ex) {
        continue; 
      }  
    }
    return totalValue;
  }

  //take an item from a backpack, remove it from backpack
  private Item getItemFromBackpack(String itemName, String itemValue) {
    for(int i = 0; i < backpack.size(); i++) {
      Item actualItem = backpack.get(i);

      if(itemName.equals(actualItem.getName()) && itemValue.equals(actualItem.getValue())) {
        backpack.remove(i);
        return actualItem;
      }
    }
    return null;
  }

  private String parseTime(int time){
    int hour = time/3600;
    time = time - (hour * 3600);
    int minute = time/60;
    int second = time%60;

    String timeFormat = "";

    if(hour > 0) {
      timeFormat += hour + " hour ";
    }

    if(minute > 0) {
      timeFormat += minute + " minutes ";
    }

    timeFormat += second + " seconds";

    return timeFormat;
  }
}
