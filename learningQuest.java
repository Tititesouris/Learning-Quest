import extensions.CSVFile;
class learningQuest extends Program {
	CSVFile CSVScenes = loadCSV("scenes.csv", ';');
	Scene[] scenes = new Scene[rowCount(CSVScenes)];
	CSVFile CSVChallenges = loadCSV("challenges.csv", ';');
	Challenge[] challenges = new Challenge[rowCount(CSVChallenges)];
	CSVFile CSVItems = loadCSV("items.csv", ';');
	Item[] items = new Item[rowCount(CSVItems)];

	boolean isGameRunning = true; // Master boolean
	boolean showTips = true;
	Scene currentScene; // Scene where the player is currently located
	String currentScreen = "titleScreen"; // Screen the game is displaying
	Item[] inventory = new Item[50];
	String[] events = new String[0];

	final String DEFAULT_COLOR = ANSI_WHITE+ANSI_BLACK_BG;
	
	/*
		All the testing
	*/
	
	void testIsInteger() {
		assertEquals(isInteger("0"), true);
		assertEquals(isInteger("-25"), true);
		assertEquals(isInteger("1"), true);
		assertEquals(isInteger("-1"), true);
		assertEquals(isInteger("50"), true);
		assertEquals(isInteger("5.5"), false);
		assertEquals(isInteger("patate"), false);
		assertEquals(isInteger("-3.7"), false);
		assertEquals(isInteger("3/5"), false);
	}

	void testIsInRange() {
		assertEquals(isInRange(0, -1, 1), true);
		assertEquals(isInRange(0, -1.5, 9), true);
		assertEquals(isInRange(42, -1, 1), false);
		assertEquals(isInRange(-42, -1, 1), false);
		assertEquals(isInRange(42, -1, 42), true);
		assertEquals(isInRange(-42, -42, 42), true);
	}

	void testIsEmpty() {
		assertEquals(isEmpty("bonjour"), false);
		assertEquals(isEmpty("0"), false);
		assertEquals(isEmpty(""), true);
		assertEquals(isEmpty(null), false);
	}

	void testCount() {
		assertEquals(count("bonjour", 'b'), 1);
		assertEquals(count("bonjour", 'o'), 2);
		assertEquals(count("bonjour", 'z'), 0);
		assertEquals(count("", 'z'), 0);
	}
	void testSplit() {
		assertArrayEquals(split("Bonjour je m'appelle Toto.", ' '), new String[]{"Bonjour", "je", "m'appelle", "Toto."});
		assertArrayEquals(split("Bonjour je m'appelle Toto.", 'p'), new String[]{"Bonjour je m'a", "", "elle Toto."});
		assertArrayEquals(split("Bonjour je m'appelle Toto.", ','), new String[]{"Bonjour je m'appelle Toto."});
		assertArrayEquals(split("", ' '), new String[]{null});
	}

	void testInArray() {
		assertEquals(inArray(new String[]{"A", "B", "C"}, "A"), true);
		assertEquals(inArray(new String[]{"A", "B", "C"}, "D"), false);
		assertEquals(inArray(new String[]{""}, "A"), false);
	}

	void testAppend() {
		assertArrayEquals(append(new String[0], "toto"), new String[]{"toto"});
		assertArrayEquals(append(new String[]{"boop"}, "toto"), new String[]{"boop", "toto"});
		assertArrayEquals(append(new String[]{"boop"}, ""), new String[]{"boop", ""});
		assertArrayEquals(append(new String[0], null), new String[]{null});
	}

	void testRemove() {
		assertArrayEquals(remove(new String[]{"boop"}, 0), new String[0]);
		assertArrayEquals(remove(new String[]{"boop", "toto"}, 0), new String[]{"toto"});
		assertArrayEquals(remove(new String[]{"boop", "toto"}, 1), new String[]{"boop"});
		assertArrayEquals(remove(new String[]{"boop", "toto"}, -1), new String[]{"boop", "toto"});
		assertArrayEquals(remove(new String[]{"boop", "toto"}, 2), new String[]{"boop", "toto"});
		assertArrayEquals(remove(new String[0], 0), new String[0]);
		assertArrayEquals(remove(new String[]{null}, 0), new String[0]);
	}

	void testSwap() {
		String[] array = new String[]{"A", "B", "C"};
		swap(array, 0, 1);
		assertArrayEquals(array, new String[]{"B", "A", "C"});
		swap(array, 2, 1);
		assertArrayEquals(array, new String[]{"B", "C", "A"});
		swap(array, 0, 0);
		assertArrayEquals(array, new String[]{"B", "C", "A"});
	}

	void testToString() {
		assertEquals(toString(new String[]{"Bonjour", "je", "m'appelle", "Toto."}, " "), "Bonjour je m'appelle Toto.");
		assertEquals(toString(new String[]{"Bonjour", "je", "m'appelle", "Toto."}, "TATA"), "BonjourTATAjeTATAm'appelleTATAToto.");
		assertEquals(toString(new String[]{"Bonjour", "je", "m'appelle", "Toto."}), "Bonjour, je, m'appelle, Toto.");
		assertEquals(toString(new String[0]), "");
		assertEquals(toString(new String[]{"tata"}), "tata");
		assertEquals(toString(new String[]{"", ""}), ", ");
	}

	void testIsNull() {
		assertEquals(isNull(""), false);
		assertEquals(isNull("null"), true);
		assertEquals(isNull(null), true);
		assertEquals(isNull("banane"), false);
	}

	void testCheckChoices() {
		assertEquals(checkChoices(3, "1"), true);
		assertEquals(checkChoices(1, "1"), true);
		assertEquals(checkChoices(0, "1"), false);
		assertEquals(checkChoices(3, "a"), false);
		assertEquals(checkChoices(3, "-1"), false);
	}
	
	/*
		<--- Toolbox --->
	*/
	
	boolean isInteger(String arg) { // Returns true if the content of arg is an integer
		if(length(arg) == 0) {
			return false;
		}
		for(int i = 0; i < length(arg); i++) {
			if(!('0' <= charAt(arg, i) && charAt(arg, i) <= '9' || i == 0 && length(arg) > 1 && charAt(arg, i) == '-')) {
				return false;
			}
		}
		return true;
	}
	
	boolean isInRange(double nb, double min, double max) { // Returns true if min <= nb <= max
		return min <= nb && nb <= max;
	}

	boolean isEmpty(String str) { // Returns true if the argument is an empty String
		if(str == null) {
			return false;
		}
		return equals(str, "");
	}
	
	int count(String str, char c) { // Counts the number of occurrences of c in str
		int total = 0;
		for(int i = 0; i < length(str); i++) {
			if(charAt(str, i) == c) {
				total++;
			}
		}
		return total;
	}
	
	String[] split(String str, char c) { // Splits the string into an array
		String[] result = new String[count(str, c)+1];
		int counter = 0;
		int lastSplit = 0;
		for(int i = 0; i < length(str); i++) {
			if(charAt(str, i) == c) {
				result[counter] = substring(str, lastSplit, i);
				lastSplit = i+1;
				counter++;
			}
			result[length(result)-1] = substring(str, lastSplit, length(str));
		}
		return result;
	}
	
	boolean inArray(String[] array, String str) {
		for(int i = 0; i < length(array); i++) {
			if(equals(array[i], str)) {
				return true;
			}
		}
		return false;
	}

	String[] append(String[] array, String element) { // Adds an element in an array
		String newArray[] = new String[length(array)+1];
		for(int i = 0; i < length(array); i++) {
			newArray[i] = array[i];
		}
		newArray[length(newArray)-1] = element;
		return newArray;
	}
	
	String[] remove(String[] array, int index) { // Removes an element from an array
		if(0 <= index && index < length(array)) {
			String newArray[] = new String[length(array)-1];
			for(int i = 0; i < index; i++) {
				newArray[i] = array[i];
			}
			for(int i = index+1; i < length(array); i++) {
				newArray[i-1] = array[i];
			}
			return newArray;
		}
		return array;
	}

	void swap(String[] array, int a, int b) { // Swaps two elements of an array
		String temp = array[a];
		array[a] = array[b];
		array[b] = temp;
	}

	void shuffle(String[] array) { // Shuffles randomly an array
		for(int i = 0; i < randInt(length(array), 3*length(array)); i++) {
			swap(array, randInt(0, length(array)-1), randInt(0, length(array)-1));
		}
	}

	int randInt(int max) { // Returns a random integer between 0 and max included
		if(0 <= max) {
			return (int)(random()*(max+1));
		}
		return 0;
	}
	int randInt(int min, int max) { // Returns a random integer between min and max included
		if(min <= max) {
			return (int)(random()*(max+1-min)) + min;
		}
		return 0;
	}

	String randElement(String[] array) { // Returns a random element from the list
		return array[randInt(length(array)-1)];
	}
	char randElement(char[] array) { // Overload function
		return array[randInt(length(array)-1)];
	}
	int randElement(int[] array) { // Overload function
		return array[randInt(length(array)-1)];
	}
	
	String toString(String[] array, String delimiter) { // Merges all strings of an array into a single string, separated by delimiter
		String result = "";
		if(length(array) > 0) {
			for(int i = 0; i < length(array)-1; i++) {
				result += array[i]+delimiter;
			}
			result += array[length(array)-1];
		}
		return result;
	}
	String toString(String[] array) { // Overload function
		String result = "";
		if(length(array) > 0) {
			for(int i = 0; i < length(array)-1; i++) {
				result += array[i]+", ";
			}
			result += array[length(array)-1];
		}
		return result;
	}
	
	void printLine(String str) { // Prints the string on the screen, replacing every occurrence of "\n" by a new line
		String strip = "";
		int i = 0;
		while(i < length(str)-1) {
			if(charAt(str, i) == '\\' && charAt(str, i+1) == 'n') {
				println(strip);
				strip = "";
				i++;
			}
			else {
				strip += charAt(str, i);
			}
			i++;
		}
		println(strip+charAt(str, length(str)-1));
	}

	/*
		</--- Toolbox ---/>
	*/
	
	String toColor(String str, String color) { // Colours a string and makes it bold;
		return ANSI_BOLD+color+str+ANSI_RESET+DEFAULT_COLOR;
	}

	boolean isNull(String str) { // Returns true if the string is null or equal to "null"
		if(str == null) {
			return true;
		}
		return equals(str, "null");
	}
	
	void addEvent(String event) { // Adds an event to the event queue
		events = append(events, event);
	}

	void removeEvent() { // Removes the last event from the event queue
		events = remove(events, 0);
	}

	boolean checkChoices(int nbChoices, String index) { // Returns true if the index is a valid choice given the length of the list of choices
		if(!isEmpty(index) && isInteger(index) && isInRange(stringToInt(index), 1, nbChoices)) {
			return true;
		}
		return false;
	}

	String getChoice(String[] choices, String index) { // Returns the choice at index-1
		return choices[stringToInt(index)-1];
	}
	
	void removeChoice(Scene scene, String choice) { // Remove a choice from a scene (from Scene.choices and Scene.choiceLabels)
		String[] choices = new String[length(scene.choices)-1]; // New array of choices, 1 element shorter
		String[] choiceLabels = new String[length(scene.choiceLabels)-1]; // New array of choiceLabels, 1 element shorter
		int offset = 0;
		for(int i = 0; i < length(scene.choices); i++) {
			if(!equals(scene.choices[i], choice)) { // If the choice is NOT the one we want to remove
				choices[i-offset] = scene.choices[i]; // Add the choice ...
				choiceLabels[i-offset] = scene.choiceLabels[i]; // ... and the choiceLabel to the arrays
			}
			else {
				offset++; // Increase offset to ignore avoid outOfBounds exception
			}
		}
		if(offset > 0) { // If the choice is in this scene, in case we tried to remove something that doesn't exist
			scene.choices = choices;
			scene.choiceLabels = choiceLabels;
		}
	}
	
	void setScreen(String screen) { // Set the current screen
		currentScreen = screen;
	}

	void getScreen(String screen) { // Calls the function associated with the screen name
		switch(screen) {
			case "titleScreen":
				titleScreen();
				break;
			case "gameScreen":
				gameScreen();
				break;
			case "optionsScreen":
				optionsScreen();
				break;
			default:
				quitScreen();
				break;
		}
	}

	void setScene(Scene scene) { // Update the current scene
		currentScene = scene;
	}
	
	Scene getScene(String id) { // Get the scene with this id
		for(int i = 0; i < length(scenes); i++) {
			if(equals(scenes[i].id, id)) {
				return scenes[i];
			}
		}
		return null;
	}
	
	Item getItem(String id) { // Get the item with this id
		for(int i = 0; i < length(items); i++) {
			if(equals(items[i].id, id)) {
				return items[i];
			}
		}
		return null;
	}

	Challenge getChallenge(String id) { // Get the challenge with this id
		for(int i = 0; i < length(challenges); i++) {
			if(equals(challenges[i].id, id)) {
				return challenges[i];
			}
		}
		return null;
	}

	boolean canGetItem(Item item) { // Do you have every item needed (cost) to get this item
		if(!isNull(item.cost[0])) {
			for(int i = 0; i < length(item.cost); i++) {
				if(!isInInventory(getItem(item.cost[i]))) {
					return false;
				}
			}
		}
		return true;
	}
	
	boolean isInInventory(Item item) { // Returns whether or not an item is in the inventory
		for(int i = 0; i < length(inventory); i++) {
			if(inventory[i] == item) {
				return true;
			}
		}
		return false;
	}
	
	void pickUpItem(Item item) { // Put item in inventory
		for(int i = 0; i < length(inventory); i++) {
			if(inventory[i] == null) {
				inventory[i] = item;
				addEvent(toColor("Tu as reçu : "+item.name, ANSI_GREEN));
				break; // Yeah... I know... But it saves like 6 lines of code!
			}
		}
	}
	
	void dropItem(Item item) { // Removes an item from the inventory
		boolean found = false;
		for(int i = 0; i < length(inventory); i++) {
			if(found) {
				inventory[i-1] = inventory[i];
			}
			else if(inventory[i] == item) {
				found = true;
			}
		}
		inventory[length(inventory)-1] = null;
	}
	
	Scene newScene(String id, String choices, String name, String text, String choiceLabels, String needMessage) {
		Scene newscene = new Scene();
		newscene.id = id;
		newscene.choices = split(choices, '|');
		newscene.name = name;
		newscene.text = text;
		newscene.choiceLabels = split(choiceLabels, '|');
		newscene.needMessage = needMessage;
		return newscene;
	}

	Challenge newChallenge(String id, String intro, String complete, String fail) {
		Challenge newchallenge = new Challenge();
		newchallenge.id = id;
		newchallenge.intro = intro;
		newchallenge.complete = complete;
		newchallenge.fail = fail;
		return newchallenge;
	}

	Item newItem(String id, String cost, String challenge, String name, String need, String obtain) {
		Item newitem = new Item();
		newitem.id = id;
		newitem.cost = split(cost, '|');
		newitem.challenge = getChallenge(challenge);
		newitem.name = name;
		newitem.need = need;
		newitem.obtain = obtain;
		return newitem;
	}
	
	void load() { // Loads all the content
		for(int i = 0; i < length(scenes); i++) { // Load scenes
			scenes[i] = newScene(getCell(CSVScenes, i, 0), getCell(CSVScenes, i, 1), getCell(CSVScenes, i, 2), getCell(CSVScenes, i, 3), getCell(CSVScenes, i, 4), getCell(CSVScenes, i, 5));
		}
		setScene(scenes[0]); // Initialize currentScene

		for(int i = 0; i < length(challenges); i++) { // Load challenges
			challenges[i] = newChallenge(getCell(CSVChallenges, i, 0), getCell(CSVChallenges, i, 1), getCell(CSVChallenges, i, 2), getCell(CSVChallenges, i, 3));
		}

		for(int i = 0; i < length(items); i++) { // Load items
			items[i] = newItem(getCell(CSVItems, i, 0), getCell(CSVItems, i, 1), getCell(CSVItems, i, 2), getCell(CSVItems, i, 3), getCell(CSVItems, i, 4), getCell(CSVItems, i, 5));
		}
	}
	
	/*
		<--- Challenges --->
	*/	
	
	boolean completeChallenge(Challenge challenge) { // Returns whether or not the player completed the challenge successfully
		printLine(challenge.intro);
		boolean result = false;
		switch(challenge.id) {
			case "tidying":
				result = tidyingChallenge();
				break;
			case "bigboy":
				result = englishChallenge();
				break;
			case "countbottles":
				result = countBottlesChallenge();
				break;
			case "mum":
				result = mathsChallenge();
				break;
			case "arthur":
				result = arthurChallenge();
				break;
			case "imp_ask":
				result = nimGameChallenge();
				break;
			case "imp_ask2":
				result = oddOneOutGameChallenge();
				break;
			case "imp_ask3":
				result = definitionGameChallenge();
				break;
			case "speak_english":
				result = speakEnglishChallenge();
				break;
			case "fairy_tale":
				result = scienceChallenge();
				break;
			case "trololol":
				result = trollChallenge();
				break;
			case "pierre":
				result = pierreChallenge();
				break;
			case "fontaine":
				result = fontaineChallenge();
				break;
			case "dragon1":
				result = dragonChallenge(0);
				break;
			case "dragon2":
				result = dragonChallenge(1);
				break;
			case "dragon3":
				result = dragonChallenge(2);
				break;
			default: result = true; // If I forgot anything, the player won't be stuck
		}
		if(result) { // If the challenge was completed
			addEvent(challenge.complete); // Display congratulations
		}
		else {
			addEvent(challenge.fail);
		}
		return result;
	}
	
	boolean tidyingChallenge() {
		char[] objects = new char[]{'@', '=', '*'};
		int[] roomSize = new int[]{randInt(4, 10), randInt(4, 10)};
		char target = randElement(objects);
		char object;
		int result = 0;
		for(int y = 0; y < roomSize[1]; y++) {
			for(int x = 0; x < roomSize[0]; x++) {
				if(random() > 0.25) {
					object = ' ';
				}
				else {
					object = randElement(objects);
					if(object == target) {
						result++;
					}
				}
				print(object);
			}
			print('\n');
		}
		println("Combien y a-t-il de "+target+" dans la chambre ?");
		String input;
		do {
			input = readString();
		}
		while(!isInteger(input));
		return result == stringToInt(input);
	}

	boolean countBottlesChallenge() {
		int nbBottles = randInt(3, 8);
		String[] bottle = {
			"  =    ",
			" / \\   ",
			" ) (   ",
			" | |   ",
			" ¯¯¯   ",
		};
		String[] colors = {ANSI_RED, ANSI_GREEN, ANSI_BLUE, ANSI_RESET};
		String[] colorNames = {"rouges", "vertes", "bleues", "blanches"};

		// Select random color
		int color = randInt(length(colors)-1);
		int nbMatchingBotles = 0;

		String[] bottleColors = new String[nbBottles];
		for(int i = 0; i < nbBottles; i++) {
			bottleColors[i] = randElement(colors);
			if(equals(bottleColors[i], colors[color])) {
				nbMatchingBotles++;
			}
		}

		// Display bottles
		for(int y = 0; y < length(bottle); y++) {
			for(int x = 0; x < nbBottles; x++) {
				print(toColor(bottle[y], bottleColors[x]));
			}
			print('\n');
		}

		println("Combien y a-t-il de bouteilles "+colorNames[color]+" ?");
		String input;
		do {
			input = readString();
		}
		while(!isInteger(input));
		return stringToInt(input) == nbMatchingBotles;
	}

	boolean arthurChallenge() {
		String[] sentences = new String[]{
			"Cette épee est coinc_ dans la roche.",
			"Cette épee bri_ comme le soleil.",
			"Cette épee à l'air lour_.",
			"Cette épee semble magi_."
		};
		String[] answers = new String[]{
			"ée",
			"lle",
			"de",
			"que"
		};
		int id = randInt(length(sentences)-1);

		println("Quelles sont les lettres manquantes dans cette phrase ?");
		println("« "+sentences[id]+" »");
		String input;
		do {
			input = readString();
		}
		while(isEmpty(input));
		return equals(input, answers[id]);
	}

	boolean nimGameChallenge() { // Pretty much impossible to lose against this (non-existant) AI
		int turn = 0;
		int matchesLeft = 21;
		int pick = 1;
		println("Pour gagner ce jeu tu dois lorsque c'est ton tour piocher entre 1 et 3 allumettes, celui d'entre nous qui ramasse la dernière a perdu.");
		println("Commençons...");
		while (matchesLeft > 0) {
		    if (turn%2 == 0) {
				for (int i = 0; i<matchesLeft;i++){
				    print("| ");
				}
				print("\n");
				do {
				    print("Tu prends combien d'allumettes ? ");
				    pick = readInt();
				}
				while (pick < 1 || pick > min(3, matchesLeft));
			}
			else {
				pick = min(matchesLeft, randInt(1, 3));
				if(pick > 1) {
					println(toColor("Je prends " + pick + " allumettes.", ANSI_RED));
				}
				else {
					println(toColor("Je prends 1 allumette.", ANSI_RED));
				}
			}
			matchesLeft -= pick;
			turn += 1;
		}
		return turn%2 == 0;
	}

	boolean seriesChallenge() {
		int number = randInt(1, 25)*2;
		int operand = randInt(1, 10)*2;
		String series = "";

		for(int i = 0; i < 4; i++) {
			series += number+", ";
			number += operand;
		}
		println(series);
		String input;
		do {
			input = readString();
		}
		while(!isInteger(input));
		return stringToInt(input) == number;
	}

	boolean oddOneOutGameChallenge() {
		String[][] lists = new String[][] {
			{"Carotte", "Banane", "Orange", "Pomme", "Cerise"},
			{"Souris", "Kangourou", "Wallaby", "Koala"},
			{"Oreiller", "Manger", "Dormir", "Cacher", "Jouer"},
			{"Serpent", "Chien", "Chat", "Koala", "Ours"},
			{"Japonais", "Français", "Anglais", "Espagnol", "Allemand"},
			{"Voiture", "Vélo", "Moto", "Trotinette", "Tandem"},
			{"Le soleil", "Uranus", "La terre", "Jupiter", "Vénus"},
			{"Vert", "Marron", "Rose", "Violette", "Bleuet"},
			{"Pelican", "Giraffe", "Furet", "Lion", "Renard"}
		};
		int trio = randInt(length(lists)/3-1)*3;
		for(int i = trio; i < trio+3; i++) {
			println("Trouve l'intrus parmis cette liste :");
			String oddOneOut = lists[i][0];
			shuffle(lists[i]);
			displayChoices(lists[i]);

			String input;
			do {
				input = readString();
			}
			while(!(isInteger(input) && isInRange(stringToInt(input), 1, length(lists[i]))));
			if(!equals(oddOneOut, lists[i][stringToInt(input)-1])) {
				return false;
			}
			println("Bravo !");
		}
		return true;
	}

	boolean definitionGameChallenge() {
		String[] words = new String[] {"Hirsute", "Svelte", "Terne"};
		String[][] definitions = new String[][] {
			{"Aux cheveux ébouriffés", "Qui est petit", "Qui bouge frénétiquement de haut en bas", "À grande distance"},
			{"Qui est mince, léger", "Qui est minuscule", "Qui est dangereux", "Qui est malade"},
			{"Qui est sombre, ennuyeux", "Qui est créé à partir de terre cuite", "Qui est presque terminé", "Qui change"}
		};

		for(int i = 0; i < length(words); i++) {
			println("Trouve la définition du mot « "+words[i]+" » :");
			String definition = definitions[i][0];
			shuffle(definitions[i]);
			displayChoices(definitions[i]);

			String input;
			do {
				input = readString();
			}
			while(!(isInteger(input) && isInRange(stringToInt(input), 1, length(definitions[i]))));
			if(!equals(definition, definitions[i][stringToInt(input)-1])) {
				return false;
			}
			println("Bravo !");
		}
		return true;
	}

	boolean speakEnglishChallenge() {
		String[] questions = new String[] {
			"« Hey there young man ! »\nComment dit-on « Bonjour » en anglais ?",
			"« How old are you ? »\nComment dit-on « J'ai 10 ans » en anglais ?",
			"« Alright, have a nice day ! »\nComment dit-on « Au revoir » en anglais ?"
		};
		String[][] answers = new String[][] {
			{"hello", "hi", "hey", "good morning", "good afternoon"},
			{"i am 10", "i'm 10", "i am ten", "i'm ten", "i am 10 years old", "i'm 10 years old", "i am ten years old", "i'm ten years old"},
			{"bye", "good bye", "see you later", "see you", "bye bye", "ta-ta"}
		};

		String input;
		for(int i = 0; i < length(questions); i++) {
			printLine(questions[i]);
			do {
				input = readString();
			}
			while(isEmpty(input));
			if(!inArray(answers[i], toLowerCase(input))) {
				return false;
			}
		}
		return true;
	}

	boolean trollChallenge() {
		int brother = randInt(2, 5);
		int cousin = randInt(2, 5);
		int clue = randInt(5, 20)*brother;
		println("Mon pont a "+brother+" fois moins de pierres que celui de mon frère, et "+cousin+" fois moins que celui de mon cousin.");
		println("Le pont de mon frère est fait de "+clue+" pierres. De combien de pierres est fait celui de mon cousin ?");
		String input;
		do {
			input = readString();
		}
		while(!isInteger(input));
		return clue/brother*cousin == stringToInt(input);
	}

	boolean pierreChallenge() {
		println("« Lequel de ces objets n'est pas une pierre précieuse ? »");
		String[] rocks = new String[] {"Améthyste", "Diamant", "Émeraude", "Rubis", "Saphir", "Opale", "Topaze", "Onyx"};
		String[] fakeRocks = new String[] {"Banane", "Diamonite", "Rubonx", "Tamanzite", "Alorude", "Panile", "Almande", "Éther"};
		String fakeRock = randElement(fakeRocks);
		String[] choices = new String[4];

		choices[0] = fakeRock;
		for(int i = 1; i < length(choices); i++) {
			int rock = randInt(length(rocks)-1);
			choices[i] = rocks[rock];
			rocks = remove(rocks, rock);
		}

		shuffle(choices);
		displayChoices(choices);
		String input;
		do {
			input = readString();
		}
		while(!(isInteger(input) && isInRange(stringToInt(input), 1, 4)));
		return equals(fakeRock, choices[stringToInt(input)-1]);
	}

	boolean fontaineChallenge() {
		println("Il est joli, il te semble beau. Si son ramage se rapporte à son plumage, c'est le Phénix des hôtes de ces bois.");
		println("Qui est-il ?");
		String input;
		do {
			input = readString();
		}
		while(isEmpty(input));
		return equals("corbeau", toLowerCase(input)) || equals("le corbeau", toLowerCase(input));
	}
	
	boolean dragonChallenge(int choice) {
		String input;
		switch(choice) {
			case 0:
				do {
					input = readString();
				}
				while(isEmpty(input));
				return equals("le dragon sage", toLowerCase(input));
			case 1:
				int nbCenturies = randInt(1, 3);
				int centuries = randInt(2, 4);
				println("« Sachant que j'ai vécu "+nbCenturies+" fois "+centuries+" siècles. Combien d'années ai-je vécu ? »");
				do {
					input = readString();
				}
				while(!isInteger(input));
				return nbCenturies*centuries*100 == stringToInt(input);
			default:
				do {
					input = readString();
				}
				while(isEmpty(input));
				return equals("allumette", toLowerCase(input)) || equals("l'allumette", toLowerCase(input)) || equals("une allumette", toLowerCase(input));
		}
	}

	boolean mathsChallenge() {
		char challenge = randElement(new char[]{'+', '-', '*'});
		int a, b, result;
		switch(challenge) {
			case '+':
				a = randInt(50);
				b = randInt(50);
				result = a+b;
				break;
			case '-':
				a = randInt(50, 100);
				b = randInt(50);
				result = a-b;
				break;
			case '*':
				a = randInt(11);
				b = randInt(11);
				result = a*b;
				break;
			default:
				return false;
		}
		println("Combien font "+a+" "+challenge+" "+b+" ?");
		String input;
		do {
			input = readString();
		}
		while(!isInteger(input));
		return result == stringToInt(input);
	}

	boolean englishChallenge() {
		String[][] words = new String[][] {
			{"chat", "souris", "cheval", "cochon", "chien", "vache", "serpent"},
			{"cat", "mouse", "horse", "pig", "dog", "cow", "snake"}
		};
		int randNb = randInt(length(words, 2)-1);
		println("Comment dit-on « "+words[0][randNb]+" » en anglais ?");
		String input;
		do {
			input = readString();
		}
		while(isEmpty(input));
		return equals(input, words[1][randNb]);
	}

	boolean scienceChallenge() {
		String[] questions = new String[]{
			"Combien y a-t-il de planètes dans le système solaire ?",
			"Lequel de ces animaux n'est pas un poisson ? Dauphin, requin, anchois, bar.",
			"Vrai ou faux. La distance entre la Terre et la Lune est si grande que l'on pourrait y placer toutes les planètes du système solaire ?",
			"Quelle est la planète la plus proche du Soleil ?",
			"Comment appelle-t-on la science de l'étude du vivant ?"
		};
		String[] answers = new String[]{
			"8",
			"dauphin",
			"vrai",
			"mercure",
			"biologie"
		};

		int q = randInt(length(questions)-1);
		println(questions[q]);
		String input;
		do {
			input = readString();
		}
		while(isEmpty(input));
		return equals(toLowerCase(input), answers[q]);
	}
	
	/*
		</--- Challenges ---/>
	*/	
	
	/*
		<--- Display --->
	*/
	
	void displayInventory() {
		print(toColor("Inventaire:", ANSI_UNDERLINE)+' ');
		int i = 0;
		while(inventory[i] != null) {
			print(inventory[i].name);
			if(i < length(inventory) && inventory[i+1] != null) { // If not last item in inventory
				print(", ");
			}
			i++;
		}
	}

	void displayChoices(String[] choices) {
		for(int i = 0; i < length(choices); i++) {
			println((i+1)+". "+choices[i]);
		}
	}
	
	void displayEvents() {
		while(length(events) > 0) {
			printLine(events[0]);
			removeEvent();
		}
	}
	
	void displayGameScreen() {
		clearScreen();
		if(equals(currentScene.id, "home") && (isInInventory(getItem("treasure1")) || isInInventory(getItem("treasure2")) || isInInventory(getItem("treasure3")))) { // If the game is over
			println("Félicitations ! Tu as terminé le jeu avec grand succès.");
			println("J'espère que tu as appris quelque chose en jouant, et j'espère que ça t'as donné envie d'apprendre !");
			println("Merci beaucoup pour avoir joué !");
			readString();
			isGameRunning = false;
		}
		else {
			displayEvents();
			print('\n');
			displayInventory();
			println('\n');
			println("Lieu: "+currentScene.name+"\n\n");
			printLine(currentScene.text+"\n");
			displayChoices(currentScene.choiceLabels);
		}
	}
	
	/*
		</--- Display ---/>
	*/
	
	void completeQuest(String choice) { // Handles all the stuff when completing a quest
		Item item = getItem(choice);
		if(canGetItem(item)) { // If the player has all the items necessary to get this item in his inventory
			if(item.challenge == null || completeChallenge(item.challenge)) { // If the player doesn't have to complete a challenge, or completed the challenge successfully
				pickUpItem(item); // Get new item
				for(int i = 0; i < length(item.cost); i++) {
					dropItem(getItem(item.cost[i])); // Remove items needed to get the new item from the inventory
				}
				if(!isNull(item.obtain)) { // If there is a message to display
					addEvent(item.obtain); // Display a message when obtaining the item
				}
				removeChoice(currentScene, '*'+choice); // Remove choice to get the item
			}
		}
		else { // If the player is missing at least an item
			addEvent(item.need);
			if(showTips) { // If the difficulty is set to easy
				String[] missing = new String[0]; // List of the missing items
				for(int i = 0; i < length(item.cost); i++) {
					if(!isInInventory(getItem(item.cost[i]))) {
						missing = append(missing, getItem(item.cost[i]).name);
					}
				}
				addEvent(toColor("Il te manque: "+toString(missing), ANSI_RED)); // Inform the player of what items they need
			}
		}
	}
	
	void algorithm() { // Main function
		load();
		print(DEFAULT_COLOR); // Set the colours of the terminal to something that makes reading the text possible
		while(isGameRunning) { // Main loop
			getScreen(currentScreen);
		}
	}

	void titleScreen() { // Main menu
		clearScreen();
		String[] choices = new String[]{
			"Jouer",
			"Options",
			"Quitter"
		};
		String[] results = new String[]{
			"gameScreen",
			"optionsScreen",
			"quitScreen"
		};
		displayChoices(choices);
		String input;
		do {
			input = readString();
		}
		while(!checkChoices(length(choices), input));
		setScreen(results[stringToInt(input)-1]);
	}

	void gameScreen() { // Game
		displayGameScreen();
		
		String choice = readString();
		if(checkChoices(length(currentScene.choices), choice)) {
			choice = getChoice(currentScene.choices, choice);
			if(charAt(choice, 0) == '*') { // Use item
				completeQuest(substring(choice, 1, length(choice)));
			}
			else if(charAt(choice, 0) == '+') { // Need item
				Item key = getItem(split(choice, '*')[1]);
				Scene scene = getScene(split(substring(choice, 1, length(choice)), '*')[0]);
				if(isInInventory(key)) {
					setScene(scene);
				}
				else {
					addEvent(scene.needMessage);
					if(showTips) {
						addEvent(toColor("Il te manque: "+key.name, ANSI_RED));
					}
				}
			}
			else {
				setScene(getScene(choice));
			}
		}
	}

	void optionsScreen() { // Options menu
		clearScreen();
		if(showTips) {
			println("Difficulté: "+toColor("facile", ANSI_GREEN));
		}
		else {
			println("Difficulté: "+toColor("difficile", ANSI_RED));
		}
		println('\n');

		String[] choices = new String[]{
			"Changer difficulté",
			"Menu principal"
		};
		displayChoices(choices);
		String input;
		do {
			input = readString();
		}
		while(!checkChoices(length(choices), input));
		int choice = stringToInt(input);
		if(choice == 1) {
			showTips = !showTips;
			optionsScreen();
		}
		else { // Change difficulty
			setScreen("titleScreen");
		}
	}

	void quitScreen() { // Quit the game
		clearScreen();
		println(toColor("Merci d'avoir joué !", ANSI_GREEN));
		isGameRunning = false;
	}
}