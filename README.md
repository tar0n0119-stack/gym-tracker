# gym-tracker

Console workout tracker for the Prog 2 OOP project.

You can log your workouts with different exercise types, look at your training history, sort it and get a message when you beat a personal record. Everything is saved to a CSV file so your data is still there after you close the app.

## Problem statement

**Users:** People who train regularly at the gym or at home and want to follow their own progress from beginners to experienced lifters.

**Goal:** To see whether they are getting stronger over time and to know what they lifted last time, so they can plan the next session.

**Problem:** Most people track their workouts in a paper notebook, a phone note or only in their head. These records are hard to search, easy to lose and give no overview: you can't quickly compare sessions, see your total training volume or notice when you set a new personal record. Different kinds of training (weights, cardio, bodyweight exercises) are also hard to keep in one consistent format.

**Solution:** gym-tracker is a simple console application where users can log their workouts with different exercise types, review and sort their training history and get notified when they beat a personal record. All data is saved locally so the history is kept between runs.

## Prerequisites

- Java 21
- You don't need Maven installed because the project has the Maven Wrapper. It downloads Maven the first time you run it.

## Build, test and run

On Windows:

```powershell
.\mvnw.cmd clean package
java -jar target\gym-tracker-1.0.0.jar
```

On Linux or macOS:

```bash
./mvnw clean package
java -jar target/gym-tracker-1.0.0.jar
```

The `package` command compiles the code, runs all the tests and builds the jar file. If you only want to run the tests:

```bash
./mvnw test
```

The saved data goes to `data/workouts.csv`. This folder is not in the repository.

## Sample usage

```text
Loaded 0 workout(s).

=== Gym Tracker ===
1. Log workout
2. List workouts
3. Delete workout
4. Sort workouts
0. Save and exit
Choose: 1
Date (yyyy-mm-dd, empty for today): 2026-11-10
Add exercise: 1. Strength  2. Cardio  3. Bodyweight  0. Done
Choose: 1
Name: Bench press
Sets: 3
Reps: 8
Weight (kg): 80
Added: STRENGTH - Bench press: 3 x 8 @ 80.0 kg
Add exercise: 1. Strength  2. Cardio  3. Bodyweight  0. Done
Choose: 0
Saved: #1 Workout on 2026-11-10 (1 exercises, total volume: 1920.0)

Choose: 1
Date (yyyy-mm-dd, empty for today):
Choose: 1
Name: Bench press
Sets: 3
Reps: 8
Weight (kg): 85
Added: STRENGTH - Bench press: 3 x 8 @ 85.0 kg
Choose: 0
New PR on Bench press! 1920.0 -> 2040.0
Saved: #2 Workout on 2026-11-17 (1 exercises, total volume: 2040.0)

Choose: 4
Sort by:
1. Date
2. Total volume
3. Number of exercises
Choose: 2
Descending order? (y/n): y
#2 Workout on 2026-11-17 (1 exercises, total volume: 2040.0)
    STRENGTH - Bench press: 3 x 8 @ 85.0 kg
#1 Workout on 2026-11-10 (1 exercises, total volume: 1920.0)
    STRENGTH - Bench press: 3 x 8 @ 80.0 kg

Choose: 0
Data saved. Bye!
```

## Project structure

```text
src/main/java/hu/pte/mik/gymtracker
├── Main.java          starts the app and connects the parts
├── domain             workouts and exercise types and their exceptions
├── persistence        generic repository and CSV storage
├── service            business logic and sorting and personal records
└── ui                 console menu and input reading and notifications
```

## Implemented requirements

| Requirement | Where |
|---|---|
| Encapsulation | Private fields and constructors that check the data in every domain class. `Workout.getExercises()` gives back a list that can't be changed from outside |
| Abstraction | `Exercise` is an abstract class. `Repository<T>` and `Identifiable` and `PRListener` are interfaces |
| Inheritance | `StrengthExercise` and `CardioExercise` and `BodyweightExercise` extend `Exercise` |
| Runtime polymorphism | `Workout.getTotalVolume()` calls `calculateVolume()` and every exercise type calculates it in its own way |
| Relationships | Composition between `Workout` and `Exercise` (1 to 0..*). Aggregation between `PRTracker` and `PRListener` (0..*). Association between the service and the storage classes |
| Collections and generics | `InMemoryRepository<T extends Identifiable>` uses a `HashMap`. The code also uses `List` and `Map` and `Optional` |
| Sorting | `Workout` implements `Comparable<Workout>` so it sorts by date by default. `WorkoutSort` uses `Comparator.comparingDouble` and lambdas. The order can be ascending or descending |
| Exceptions | Own exceptions for domain rules: `InvalidExerciseException` and `InvalidWorkoutException`. Also `StorageException` and `WorkoutNotFoundException`. Wrong console input is asked again |
| Persistence | `CsvWorkoutStorage` saves and loads the workouts with try-with-resources. A missing or broken file doesn't crash the app |
| Design pattern | Observer (see below) |
| Tests | 34 JUnit 5 tests in `src/test/java` |
| Build | Maven with the Maven Wrapper. The jar file can be started directly |

## Design pattern: Observer

**Problem:** When the user logs a workout the app should tell them if they beat a personal record. If `WorkoutService` printed this message itself then the business logic would depend on the console. Every new kind of notification (log file or sound or GUI) would mean changing the service.

**Classes in the pattern:**

| Role | Class |
|---|---|
| Subject | `PRTracker` keeps the best volume for every exercise and a list of listeners |
| Observer | `PRListener` is an interface with the `onNewRecord(exerciseName, previousBest, newBest)` method |
| Concrete observer | `ConsoleNotifier` prints the new record to the console |

`WorkoutService` sends every logged exercise to `PRTracker.check()`. When a record is beaten the tracker tells all the listeners. `Main` adds `ConsoleNotifier` as a listener.

**Benefit:** The service and the tracker don't know how the message is shown. For a new kind of notification you only need a new class that implements `PRListener` and the old code stays the same.

The sorting also works like the **Strategy** pattern. Every `WorkoutSort` value has its own `Comparator` and the menu can switch between them without changing the list code.

## Design decisions

- **Layers:** The `ui` package only talks to `WorkoutService`. The service hides the repository and the CSV storage so the storage could be replaced without changing the menu.
- **Repository in memory and a CSV file:** While the app runs the data is in the repository. It is loaded from the file at startup and saved when you exit.
- **CSV format:** One `WORKOUT` line and after it one line for each exercise. Loading uses the same constructors as the menu so the data in the file is checked the same way.
- **Unchecked exceptions:** The domain errors extend `RuntimeException` and the menu catches them in one place.
- **IDs:** The repository gives every new workout the next ID. Loaded workouts keep their own ID and new ones continue after the highest one.

## Known limitations

- The total volume adds up different units (kg times reps for strength and km for cardio and reps for bodyweight). So it only makes sense inside one exercise type.
- A personal record is based on volume. A heavier set with fewer reps doesn't always count as a record.
- If you delete a workout its personal record stays.
- You can't edit a workout. You can only delete it and log it again.
- An exercise name can't have `;` in it because that is the CSV separator.
- It works for one user and the data is only saved on your own computer.

## Documentation

- UML class diagram: [docs/uml/class-diagram.png](docs/uml/class-diagram.png) and the source [class-diagram.puml](docs/uml/class-diagram.puml)
- Architecture overview: [docs/uml/architecture-overview.png](docs/uml/architecture-overview.png) and the source [architecture-overview.puml](docs/uml/architecture-overview.puml)
- User stories: [docs/user-stories.md](docs/user-stories.md)

## Contributor

Tomasev Áron

## License

MIT. See the [LICENSE](LICENSE) file.
