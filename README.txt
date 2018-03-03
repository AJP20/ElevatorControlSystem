Build Instructions
----------
In terminal/CMD: 1. cd to ElevatorControlSystem/src
		 2. javac ElevatorControlSystem.java
		 3. java ElevatorControlSystem

Solution
------
Initially the user generates the number of elevators available; the elevators are then stored in a hash map with the key being the elevators id (1 to number of elevator) and the value being a class called ElevatorInformation. In this class you can find the current direction (initially set to UP), current floor (initially set to 1), a sorted set of integers used for keeping track of floors in the queue (I specifically used a sorted set so when the elevators run they can just look at the queue and based on what floor its on and its direction it can pick the nearest floor), and an editable value used to more efficiently place up and down elevators. To elaborate on this efficiency the value is set to false when a request needing to go in the opposite direction is place into a queue, this prevent the queue from adding more of its original direction requests to that elevator. Next the user or users is prompted with the elevator UI this contains a typical up or down button (I assumed that all the elevators are located in the same general area), once the direction or directions is chosen the user hits the done button to send the request. I then generate a random floor number that this request is coming from in order to simulate a real life scenario (initialized with up to 20 floors, but the system does allow for this number to be changed). Due to time constraints for this assignment to simulate multiple requests once "people enter" the elevator I continuously send a new request for an elevator to fill the queues. The program is broken down into 12 searches, 6 for a up request and 6 for a down request, they are both extremely similar with the only differences being some logic due to me deciding to put the floors of a down request into the queue as negative.
Search 1: Picks the first elevator that is already on the requested floor, is going to the same direction and is editable.
Search 2: Picks the closet elevator that has the current floor in its queue, is going in the same direction and is editable.
Search 3: Picks the elevator with the smallest queue that is going past the requested floor at some point, is going in the same direction and is editable.
Search 4: Picks an elevator that has an empty queue, is editable and sets the elevators direction to the request direction.
Search 5: If the request hits this search at this point it means that there is no queue with a current direction going in the requested direction so we pick the elevator thats first floor is the closet to the requested floor and set editable to false.
Search 6: Picks the elevator with the smallest queue regardless if it is editable.
Once the search is complete the request is then placed in the queue of the elevator picked from the search, the picked elevator state is then printed in the console. On the elevator UI I added a step button which simulates traveling up and down the floors on the elevators with floors in its queue. The step button is linked to my step function which houses the logic for removing floors from queue if the elevator is on that floor, sending an elevator up or down, and changing the direction of an elevator (It is important to mention when an elevator is changing states it is assumed that the elevator does nothing until it reaches the first floor in the queue to simulate this I just set the current floor value with the first value in the queue). Each click on the step button prints out the current state after its step for each elevator in the console. My implementation improves upon the first come first served implementation as the queue is stored in numerical order so if multiple requests come in it won't go in order that the request came in but rather the numerical order of them. Also each search picks the most optimal elevator queue for that request to go in to.

tl;dr
---
Generate number of elevators
Send a request for an elevator; request floor randomly generated
Send multiple requests to fill each queue
Step through the elevators movement
Read the console to see state of the elevators