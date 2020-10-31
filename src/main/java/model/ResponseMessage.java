package model;

public enum ResponseMessage {
   LOW("That's too low. Please guess higher: <br>"),
    HIGH("That' too high. Please guess lower <br>"),
   EQUAL("awesome, number of guesses you took are ");

    public final String label;

    private ResponseMessage(String label) {
        this.label = label;
    }
}
