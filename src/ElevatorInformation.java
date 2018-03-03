import java.util.SortedSet;
import java.util.TreeSet;

public class ElevatorInformation {
	enum Direction { UP, DOWN };
	Direction currentDirection;
	int currentFloor;
	SortedSet<Integer> currentQueue = new TreeSet<>();
	Boolean editable;
}
