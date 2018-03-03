import java.util.HashMap;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.concurrent.ThreadLocalRandom;

public class ElevatorControlSystem {

	private HashMap<Integer, ElevatorInformation> elevators = new HashMap<Integer, ElevatorInformation>();

	int numberOfFloors = 20;

	// Querying the state of the elevators (what floor are they on and where
	// they are going)
	void state() {
		elevators.forEach((k,
				v) -> System.out.println("Elevator Id: " + k + ", Current floor: " + elevators.get(k).currentFloor
						+ ", Current direction: " + elevators.get(k).currentDirection + ", Floors in queue: "
						+ elevators.get(k).currentQueue + ", Editable: " + elevators.get(k).editable));
	}

	// receiving an update about the status of an elevator
	void update(int id) {
		System.out.println("Elevator Id: " + id + ", Current floor: " + elevators.get(id).currentFloor
				+ ", Current direction: " + elevators.get(id).currentDirection + ", Floors in queue: "
				+ elevators.get(id).currentQueue + ", Editable: " + elevators.get(id).editable);
	}

	// receiving a pickup request
	void pickUp(int id, SortedSet<Integer> queuedFloors, int currentFloor, ElevatorInformation.Direction d) {
		ElevatorInformation ei = new ElevatorInformation();
		ei.currentDirection = d;
		ei.currentFloor = currentFloor;
		ei.currentQueue = queuedFloors;
		this.elevators.put(id, ei);
	}

	// Time-stepping the simulation
	void step() {
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			int checker = 0;
			if (entry.getValue().currentDirection == ElevatorInformation.Direction.UP
					&& !entry.getValue().currentQueue.isEmpty()) {
				// check to see if the queue contains only down values
				for (int x : entry.getValue().currentQueue) {
					if (x < 0)
						checker++;
				}
				if (checker == entry.getValue().currentQueue.size()) {
					// set the current floor to the top floor it is assumed that
					// the elevator will do nothing till it reaches it so I skip
					// steps and just sent it straight to the top
					entry.getValue().currentFloor = entry.getValue().currentQueue.first() * -1;
					entry.getValue().currentDirection = ElevatorInformation.Direction.DOWN;
					entry.getValue().editable = true;
					entry.getValue().currentQueue.remove(entry.getValue().currentFloor * -1);
				} else {
					// regular check: add one to current floor and remove from
					// queue if it exists in the queue
					if (entry.getValue().currentFloor < getNumberOfFloors()) {
						if (entry.getValue().currentQueue.contains(entry.getValue().currentFloor))
							entry.getValue().currentQueue.remove(entry.getValue().currentFloor);
						entry.getValue().currentFloor = entry.getValue().currentFloor + 1;
						if (entry.getValue().currentQueue.contains(entry.getValue().currentFloor))
							entry.getValue().currentQueue.remove(entry.getValue().currentFloor);
					} else {
						if (entry.getValue().currentQueue.contains(entry.getValue().currentFloor))
							entry.getValue().currentQueue.remove(entry.getValue().currentFloor);
						entry.getValue().currentDirection = ElevatorInformation.Direction.DOWN;
						entry.getValue().editable = true;
					}
				}
			} else if (entry.getValue().currentDirection == ElevatorInformation.Direction.DOWN
					&& !entry.getValue().currentQueue.isEmpty()) {
				// check to see if the queue contains only up values
				for (int x : entry.getValue().currentQueue) {
					if (x > 0)
						checker++;
				}
				if (checker == entry.getValue().currentQueue.size()) {
					// set the current floor to the bottom floor it is assumed
					// that the elevator will do nothing till it reaches it so I
					// skip steps and just sent it straight to the bottom
					entry.getValue().currentFloor = entry.getValue().currentQueue.first();
					entry.getValue().currentDirection = ElevatorInformation.Direction.UP;
					entry.getValue().editable = true;
					entry.getValue().currentQueue.remove(entry.getValue().currentFloor);
				} else {
					// regular check: subtract one to current floor and remove
					// from queue if it exists in the queue
					if (entry.getValue().currentFloor > 1) {
						if (entry.getValue().currentQueue.contains(-entry.getValue().currentFloor))
							entry.getValue().currentQueue.remove(-entry.getValue().currentFloor);
						entry.getValue().currentFloor = entry.getValue().currentFloor - 1;
						if (entry.getValue().currentQueue.contains(-entry.getValue().currentFloor))
							entry.getValue().currentQueue.remove(-entry.getValue().currentFloor);
					} else {
						if (entry.getValue().currentQueue.contains(-entry.getValue().currentFloor))
							entry.getValue().currentQueue.remove(-entry.getValue().currentFloor);
						entry.getValue().currentDirection = ElevatorInformation.Direction.UP;
						entry.getValue().editable = true;
					}
				}
			}
		}
		state();
	}

	void generateElevators(int numberOfElevators) {
		for (int i = 1; i <= numberOfElevators; i++) {
			ElevatorInformation ei = new ElevatorInformation();
			ei.currentDirection = ElevatorInformation.Direction.UP;
			ei.currentFloor = 1;
			ei.editable = true;
			this.elevators.put(i, ei);
		}
		state();
	}

	void directionsSelected(boolean up, boolean down, int currentFloor) {
		System.out.println("Looking to send elevator to: " + currentFloor + " | Up: " + up + ", Down: " + down);
		// if user is on the top floor then elevator can only go down UI does
		// not count for this but the system does
		if (currentFloor == getNumberOfFloors())
			searchClosestElevator(ElevatorInformation.Direction.DOWN, currentFloor);
		// if user is on the bottom floor then elevator can only go up UI does
		// not count for this but the system does
		else if (currentFloor == 1)
			searchClosestElevator(ElevatorInformation.Direction.UP, currentFloor);
		else if (up && down) {
			searchClosestElevator(ElevatorInformation.Direction.UP, currentFloor);
			searchClosestElevator(ElevatorInformation.Direction.DOWN, currentFloor);
		} else if (up)
			searchClosestElevator(ElevatorInformation.Direction.UP, currentFloor);
		else if (down)
			searchClosestElevator(ElevatorInformation.Direction.DOWN, currentFloor);
	}

	public int generateRandomCurrentFloor() {
		// number of floors is initialized as 20
		// ThreadLocalRandom: This approach has the advantage of not needing to
		// explicitly initialize a java.util.Random instance, which can be a
		// source of confusion and error if used inappropriately.
		int randomNum = ThreadLocalRandom.current().nextInt(1, getNumberOfFloors() + 1);
		return randomNum;
	}

	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	public void searchClosestElevator(ElevatorInformation.Direction d, int destinationFloor) {
		if (d == ElevatorInformation.Direction.UP)
			upSearch(d, destinationFloor);
		if (d == ElevatorInformation.Direction.DOWN)
			downSearch(d, destinationFloor);
	}

	public void upSearch(ElevatorInformation.Direction d, int destinationFloor) {
		// Pick an elevator that...
		System.out.print("Up ");
		int queueTemp = 100;
		int key = 100;
		// Search 1 + 2
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			// is on its floor and is going in the same direction (pick the
			// first one)
			if (entry.getValue().currentDirection == d && entry.getValue().currentFloor == destinationFloor
					&& entry.getValue().editable) {
				entry.getValue().currentQueue.add(destinationFloor);
				System.out.print("Search 1 produced(");
				System.out.println("Elevator Id: " + entry.getKey() + ", Current floor: "
						+ entry.getValue().currentFloor + ", Current direction: " + entry.getValue().currentDirection
						+ ", Floors in queue: " + entry.getValue().currentQueue + ") to pick up");
				return;
			}
			// has the current floor in its queue and is going in the same
			// direction
			else if (entry.getValue().currentDirection == d && entry.getValue().currentQueue.contains(destinationFloor)
					&& entry.getValue().editable) {
				// find the closet elevator to that position
				if (queueTemp > entry.getValue().currentQueue.headSet(destinationFloor).size()) {
					queueTemp = entry.getValue().currentQueue.headSet(destinationFloor).size();
					key = entry.getKey();
				}
			}
		}
		if (key != 100) {
			elevators.get(key).currentQueue.add(destinationFloor);
			System.out.print("Search 2 produced(");
			System.out.println("Elevator Id: " + key + ", Current floor: " + elevators.get(key).currentFloor
					+ ", Current direction: " + elevators.get(key).currentDirection + ", Floors in queue: "
					+ elevators.get(key).currentQueue + ") to pick up");
			return;
		}
		// Search 3
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			// is going past the floor at some point in the queue and is going
			// in the same direction with multiple values in queue
			if (entry.getValue().currentDirection == d && !entry.getValue().currentQueue.isEmpty()
					&& entry.getValue().currentQueue.last() > destinationFloor
					&& entry.getValue().currentQueue.first() < destinationFloor && entry.getValue().editable) {
				// find the elevator with the smallest number of floors in the
				// queue
				if (queueTemp > entry.getValue().currentQueue.size()) {
					queueTemp = entry.getValue().currentQueue.size();
					key = entry.getKey();
				}
			}
		}
		if (key != 100) {
			elevators.get(key).currentQueue.add(destinationFloor);
			System.out.print("Search 3 produced(");
			System.out.println("Elevator Id: " + key + ", Current floor: " + elevators.get(key).currentFloor
					+ ", Current direction: " + elevators.get(key).currentDirection + ", Floors in queue: "
					+ elevators.get(key).currentQueue + ") to pick up");
			return;
		}
		// Search 4
		// has an empty queue
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			if (entry.getValue().currentQueue.isEmpty() && entry.getValue().editable) {
				entry.getValue().currentQueue.add(destinationFloor);
				entry.getValue().currentDirection = d;
				System.out.print("Search 4 produced(");
				System.out.println("Elevator Id: " + entry.getKey() + ", Current floor: "
						+ entry.getValue().currentFloor + ", Current direction: " + entry.getValue().currentDirection
						+ ", Floors in queue: " + entry.getValue().currentQueue + ") to pick up");
				return;
			}
		}

		// Search 5
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			// There is no queue with a current direction that we need so we'll
			// look for a queue thats first floor is closet to the requested
			// floor
			if (entry.getValue().currentDirection != d && entry.getValue().editable) {
				if (queueTemp > Math.abs(entry.getValue().currentQueue.first() - destinationFloor)) {
					queueTemp = Math.abs(entry.getValue().currentQueue.first() - destinationFloor);
					key = entry.getKey();
				}
			}
		}
		if (key != 100) {
			elevators.get(key).currentQueue.add(destinationFloor);
			// added to a elevator queue with different direction direction
			elevators.get(key).editable = false;
			System.out.print("Search 5 produced(");
			System.out.println("Elevator Id: " + key + ", Current floor: " + elevators.get(key).currentFloor
					+ ", Current direction: " + elevators.get(key).currentDirection + ", Floors in queue: "
					+ elevators.get(key).currentQueue + ") to pick up");
			return;
		}

		// Search 6
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			// if all else fails add the value the elevator with the smallest
			// number of floors in the queue
			if (entry.getValue().currentDirection == d) {
				// find the elevator with the smallest number of floors in the
				// queue
				if (queueTemp > entry.getValue().currentQueue.size()) {
					queueTemp = entry.getValue().currentQueue.size();
					key = entry.getKey();
				}
			}
			if (entry.getValue().currentDirection != d) {
				if (queueTemp > entry.getValue().currentQueue.size()) {
					queueTemp = entry.getValue().currentQueue.size();
					key = entry.getKey();
				}
			}
		}
		if (key != 100) {
			if (elevators.get(key).currentDirection != d)
				elevators.get(key).editable = false;
			elevators.get(key).currentQueue.add(destinationFloor);
			System.out.print("Search 6 produced(");
			System.out.println("Elevator Id: " + key + ", Current floor: " + elevators.get(key).currentFloor
					+ ", Current direction: " + elevators.get(key).currentDirection + ", Floors in queue: "
					+ elevators.get(key).currentQueue + ") to pick up");
			return;
		}

	}

	public void downSearch(ElevatorInformation.Direction d, int destinationFloor) {
		// Pick an elevator that...
		System.out.print("Down ");
		int queueTemp = 100;
		int key = 100;
		// Search 1 + 2
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			// is on its floor and is going in the same direction (pick the
			// first one)
			if (entry.getValue().currentDirection == d && entry.getValue().currentFloor == destinationFloor
					&& entry.getValue().editable) {
				entry.getValue().currentQueue.add(-destinationFloor);
				System.out.print("Search 1 produced(");
				System.out.println("Elevator Id: " + entry.getKey() + ", Current floor: "
						+ entry.getValue().currentFloor + ", Current direction: " + entry.getValue().currentDirection
						+ ", Floors in queue: " + entry.getValue().currentQueue + ") to pick up");
				return;
			}
			// has the current floor in its queue and is going in the same
			// direction
			else if (entry.getValue().currentDirection == d && entry.getValue().currentQueue.contains(-destinationFloor)
					&& entry.getValue().editable) {
				// find the closet elevator to that position
				if (queueTemp > entry.getValue().currentQueue.headSet(-destinationFloor).size()) {
					queueTemp = entry.getValue().currentQueue.headSet(-destinationFloor).size();
					key = entry.getKey();
				}
			}
		}
		if (key != 100) {
			elevators.get(key).currentQueue.add(-destinationFloor);
			System.out.print("Search 2 produced(");
			System.out.println("Elevator Id: " + key + ", Current floor: " + elevators.get(key).currentFloor
					+ ", Current direction: " + elevators.get(key).currentDirection + ", Floors in queue: "
					+ elevators.get(key).currentQueue + ") to pick up");
			return;
		}
		// Search 3
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			// is going past the floor at some point in the queue and is going
			// in the same direction
			if (entry.getValue().currentDirection == d && !entry.getValue().currentQueue.isEmpty()
					&& entry.getValue().currentQueue.last() < -destinationFloor
					&& entry.getValue().currentQueue.first() > -destinationFloor && entry.getValue().editable) {
				// find the elevator with the smallest number of floors in the
				// queue
				if (queueTemp > entry.getValue().currentQueue.size()) {
					queueTemp = entry.getValue().currentQueue.size();
					key = entry.getKey();
				}
			}
		}
		if (key != 100) {
			elevators.get(key).currentQueue.add(-destinationFloor);
			System.out.print("Search 3 produced(");
			System.out.println("Elevator Id: " + key + ", Current floor: " + elevators.get(key).currentFloor
					+ ", Current direction: " + elevators.get(key).currentDirection + ", Floors in queue: "
					+ elevators.get(key).currentQueue + ") to pick up");
			return;
		}
		// Search 4
		// has an empty queue
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			if (entry.getValue().currentQueue.isEmpty() && entry.getValue().editable) {
				entry.getValue().currentQueue.add(-destinationFloor);
				// keep the direction up because the stepper handles the
				// elevator being taken to proper floor
				System.out.print("Search 4 produced(");
				System.out.println("Elevator Id: " + entry.getKey() + ", Current floor: "
						+ entry.getValue().currentFloor + ", Current direction: " + entry.getValue().currentDirection
						+ ", Floors in queue: " + entry.getValue().currentQueue + ") to pick up");
				return;
			}
		}

		// Search 5
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			// There is no queue with a current direction that we need so we'll
			// look for a queue thats last state is closet to the destination
			// floor
			if (entry.getValue().currentDirection != d && entry.getValue().editable) {
				if (queueTemp > Math.abs(entry.getValue().currentQueue.last() - destinationFloor)) {
					queueTemp = Math.abs(entry.getValue().currentQueue.last() - destinationFloor);
					key = entry.getKey();
				}
			}
		}
		if (key != 100) {
			elevators.get(key).currentQueue.add(-destinationFloor);
			// added to a elevator queue with different direction direction
			elevators.get(key).editable = false;
			System.out.print("Search 5 produced(");
			System.out.println("Elevator Id: " + key + ", Current floor: " + elevators.get(key).currentFloor
					+ ", Current direction: " + elevators.get(key).currentDirection + ", Floors in queue: "
					+ elevators.get(key).currentQueue + ") to pick up");
			return;
		}
		// Search 6
		for (Entry<Integer, ElevatorInformation> entry : elevators.entrySet()) {
			// if all else fails add the value the elevator with the smallest
			// number of floors in the queue
			if (entry.getValue().currentDirection == d) {
				// find the elevator with the smallest number of floors in the
				// queue
				if (queueTemp > entry.getValue().currentQueue.size()) {
					queueTemp = entry.getValue().currentQueue.size();
					key = entry.getKey();
				}
			}
			if (entry.getValue().currentDirection != d) {
				if (queueTemp > entry.getValue().currentQueue.size()) {
					queueTemp = entry.getValue().currentQueue.size();
					key = entry.getKey();
				}
			}
		}
		if (key != 100) {
			if (elevators.get(key).currentDirection != d)
				elevators.get(key).editable = false;
			elevators.get(key).currentQueue.add(-destinationFloor);
			System.out.print("Search 6 produced(");
			System.out.println("Elevator Id: " + key + ", Current floor: " + elevators.get(key).currentFloor
					+ ", Current direction: " + elevators.get(key).currentDirection + ", Floors in queue: "
					+ elevators.get(key).currentQueue + ") to pick up");
			return;
		}
	}

	public static void main(String[] args) {
		try {
			Interface frame = new Interface();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
