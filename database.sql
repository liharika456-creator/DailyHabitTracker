
CREATE TABLE Habits (
    habit_id INTEGER PRIMARY KEY,
    habit_name VARCHAR(100) NOT NULL,
    created_date DATE NOT NULL
);

CREATE TABLE Habit_Logs (
    log_id INTEGER PRIMARY KEY,
    habit_id INTEGER NOT NULL,
    completion_date DATE NOT NULL,
    is_completed BOOLEAN NOT NULL,

    FOREIGN KEY (habit_id)
        REFERENCES Habits(habit_id),

    UNIQUE (habit_id, completion_date)
);



INSERT INTO Habits
(habit_id, habit_name, created_date)
VALUES
(1, 'Reading', '2026-10-02');

INSERT INTO Habits
(habit_id, habit_name, created_date)
VALUES
(2, 'Java Practice', '2026-10-02');

INSERT INTO Habits
(habit_id, habit_name, created_date)
VALUES
(3, 'Exercise', '2026-10-02');


SELECT * FROM Habits;


INSERT INTO Habit_Logs
(log_id, habit_id, completion_date, is_completed)
VALUES
(1, 1, '2026-10-02', TRUE);


SELECT
    h.habit_name,
    l.completion_date,
    l.is_completed
FROM Habits h
JOIN Habit_Logs l
ON h.habit_id = l.habit_id
WHERE l.is_completed = TRUE;



DELETE FROM Habits
WHERE habit_id = 3;

