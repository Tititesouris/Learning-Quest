class Scene {
	String id; // To identify the scene
	String[] choices; // The list of scene ids accessible from the scenes
	String name; // The name that is displayed
	String text; // The text displayed when entering the scene
	String[] choiceLabels; // The text displayed for every choice
	String needMessage; // The text displayed if the player doesn't have the item necessary to access this scene
}