# User stories

## 1. Log a workout
As a gym-goer, I want to log a workout with the exercises I did so that I have a record of my training.

**Acceptance criteria**
- A workout has a date and contains one or more exercises.
- The user can choose the exercise type: strength, cardio or bodyweight.

## 2. Different exercise types
As a gym-goer who does both weights and cardio, I want each exercise type to store the data that fits it so that my records make sense for every kind of training.

**Acceptance criteria**
- Strength exercises store sets, reps and weight.
- Cardio exercises store distance and duration.
- Bodyweight exercises store sets and reps.
- Each type calculates its own training volume.

## 3. Reject invalid data
As a user, I want the app to reject impossible values, so
that my training history stays correct.

**Acceptance criteria**
- Negative weight, zero sets or reps and empty exercise names are rejected.
- The app explains what was wrong instead of crashing.

## 4. View my training history
As a gym-goer, I want to list my previous workouts so that I can see what I did last time.

**Acceptance criteria**
- The list shows each workout's date, exercises and total volume.

## 5. Sort my workouts
As a gym-goer, I want to sort my workouts in different ways so that I can quickly find my newest or hardest sessions.

**Acceptance criteria**
- Workouts can be sorted by date and by total volume.
- The user picks the sorting option from the menu.

## 6. Personal record notification
As a gym-goer, I want to be notified when I beat my previous best on an exercise so that I can see my progress and stay motivated.

**Acceptance criteria**
- A message appears when an exercise's volume exceeds the previous best.
- No message appears for a normal non-record set.

## 7. Delete a workout
As a user, I want to delete a workout I logged by mistake so that my history only contains real sessions.

**Acceptance criteria**
- The user can delete a workout by choosing it from the list.
- Trying to delete a non-existing workout shows an error message.

## 8. Keep my data between runs
As a user, I want my workouts to be saved when I close the app so that I don't lose my training history.

**Acceptance criteria**
- After restarting the app previously logged workouts are still listed.
- A missing data file on first start does not cause an error.