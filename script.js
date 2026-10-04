 let habits = [];


// ==============================
// ELEMENTS
// ==============================

const habitForm =
    document.getElementById("habitForm");

const habitName =
    document.getElementById("habitName");

const habitList =
    document.getElementById("habitList");

const completedTodayList =
    document.getElementById("completedTodayList");

const historyButton =
    document.getElementById("historyButton");

const historyList =
    document.getElementById("historyList");

const clearHistoryButton =
    document.getElementById("clearHistoryButton");


// ==============================
// ADD NEW HABIT
// ==============================

habitForm.addEventListener(
    "submit",
    function(event) {

        event.preventDefault();


        const name =
            habitName.value.trim();


        if (name === "") {

            alert(
                "Please enter a habit name!"
            );

            return;
        }


        fetch(
            "http://localhost:8082/habits?name=" +
            encodeURIComponent(name),
            {
                method: "POST"
            }
        )

        .then(
            response =>
                response.text()
        )

        .then(data => {

            console.log(data);

            habitName.value = "";

            loadHabits();

        })

        .catch(error => {

            console.error(
                "Error adding habit:",
                error
            );

            alert(
                "Could not add habit!"
            );

        });

    }
);


// ==============================
// LOAD HABITS
// ==============================

function loadHabits() {

    fetch(
        "http://localhost:8082/habits"
    )

    .then(
        response =>
            response.text()
    )

    .then(data => {

        console.log(
            "Habits received:",
            data
        );


        if (data.trim() === "") {

            habits = [];

        } else {

            const lines =
                data.trim().split("\n");


            habits =
                lines.map(
                    function(line) {

                        const parts =
                            line.split("|");


                        return {

                            id:
                                Number(
                                    parts[0]
                                ),

                            name:
                                parts[1],

                            createdDate:
                                parts[2],

                            completed:
                                parts[3] === "true",

                            completionDate:
                                parts[4] || null

                        };

                    }
                );

        }


        displayHabits();

    })

    .catch(error => {

        console.error(
            "Error loading habits:",
            error
        );

    });

}


// ==============================
// DISPLAY HABITS
// ==============================

function displayHabits() {

    habitList.innerHTML = "";

    completedTodayList.innerHTML = "";


    // ==============================
    // ACTIVE HABITS
    // ==============================

    const activeHabits =
        habits.filter(
            function(habit) {

                return !habit.completed;

            }
        );


    if (activeHabits.length === 0) {

        habitList.innerHTML =
            '<p class="empty-message">' +
            '🎉 All habits completed for today!' +
            '</p>';

    }


    activeHabits.forEach(
        function(habit) {

            const habitDiv =
                document.createElement("div");


            habitDiv.className =
                "habit-item";


            // ==============================
            // INFORMATION
            // ==============================

            const information =
                document.createElement("div");


            information.className =
                "habit-information";


            const name =
                document.createElement("h3");


            name.className =
                "habit-name";


            name.textContent =
                habit.name;


            const created =
                document.createElement("p");


            created.className =
                "habit-time";


            created.textContent =
                "Added: " +
                formatDateTime(
                    habit.createdDate
                );


            information.appendChild(
                name
            );


            information.appendChild(
                created
            );


            // ==============================
            // ACTIONS
            // ==============================

            const actions =
                document.createElement("div");


            actions.className =
                "habit-actions";


            // ==============================
            // COMPLETE BUTTON
            // ==============================

            const completeButton =
                document.createElement("button");


            completeButton.className =
                "complete-btn";


            completeButton.textContent =
                "Complete";


            completeButton.addEventListener(
                "click",
                function() {

                    completeHabit(
                        habit.id
                    );

                }
            );


            // ==============================
            // DELETE BUTTON
            // ==============================

            const deleteButton =
                document.createElement("button");


            deleteButton.className =
                "delete-btn";


            deleteButton.textContent =
                "Delete";


            deleteButton.addEventListener(
                "click",
                function() {

                    const confirmDelete =
                        confirm(
                            "Are you sure you want to delete this habit?"
                        );


                    if (!confirmDelete) {

                        return;

                    }


                    fetch(
                        "http://localhost:8082/delete?id=" +
                        habit.id
                    )

                    .then(
                        response =>
                            response.text()
                    )

                    .then(data => {

                        console.log(data);

                        loadHabits();

                    })

                    .catch(error => {

                        console.error(
                            "Error deleting habit:",
                            error
                        );

                        alert(
                            "Could not delete habit!"
                        );

                    });

                }
            );


            actions.appendChild(
                completeButton
            );


            actions.appendChild(
                deleteButton
            );


            habitDiv.appendChild(
                information
            );


            habitDiv.appendChild(
                actions
            );


            habitList.appendChild(
                habitDiv
            );

        }
    );


    // ==============================
    // COMPLETED TODAY
    // ==============================

    const completedHabits =
        habits.filter(
            function(habit) {

                return habit.completed;

            }
        );


    if (completedHabits.length === 0) {

        completedTodayList.innerHTML =
            '<p class="empty-message">' +
            'No habits completed today yet.' +
            '</p>';

    }


    completedHabits.forEach(
        function(habit) {

            const completedItem =
                document.createElement("div");


            completedItem.className =
                "completed-item";


            const completedName =
                document.createElement("span");


            completedName.className =
                "completed-name";


            completedName.textContent =
                "✅ " + habit.name;


            const completedTime =
                document.createElement("span");


            completedTime.className =
                "completed-time";


            completedTime.textContent =
                "Completed: " +
                formatDateTime(
                    habit.completionDate
                );


            completedItem.appendChild(
                completedName
            );


            completedItem.appendChild(
                completedTime
            );


            completedTodayList.appendChild(
                completedItem
            );

        }
    );


    updateDashboard();

}


// ==============================
// COMPLETE HABIT
// ==============================

function completeHabit(id) {

    fetch(
        "http://localhost:8082/complete?id=" +
        id
    )

    .then(
        response =>
            response.text()
    )

    .then(data => {

        console.log(data);

        loadHabits();

    })

    .catch(error => {

        console.error(
            "Error completing habit:",
            error
        );

        alert(
            "Could not complete habit!"
        );

    });

}


// ==============================
// FORMAT DATE AND TIME
// ==============================

function formatDateTime(dateTime) {

    if (!dateTime) {

        return "Unknown";

    }


    const date =
        new Date(dateTime);


    return date.toLocaleString();

}


// ==============================
// UPDATE DASHBOARD
// ==============================

function updateDashboard() {

    const total =
        habits.length;


    const completed =
        habits.filter(
            function(habit) {

                return habit.completed;

            }
        ).length;


    const percentage =
        total === 0
            ? 0
            : Math.round(
                (completed / total) * 100
            );


    document.getElementById(
        "totalHabits"
    ).textContent =
        total;


    document.getElementById(
        "completedHabits"
    ).textContent =
        completed;


    document.getElementById(
        "progress"
    ).textContent =
        percentage + "%";

}


// ==============================
// VIEW COMPLETED HISTORY
// ==============================

historyButton.addEventListener(
    "click",
    function() {

        fetch(
            "http://localhost:8082/history"
        )

        .then(
            response =>
                response.text()
        )

        .then(data => {

            historyList.innerHTML = "";


            if (data.trim() === "") {

                historyList.innerHTML =
                    '<p class="empty-message">' +
                    'No past completed habits yet.' +
                    '</p>';

                return;

            }


            const lines =
                data.trim().split("\n");


            lines.forEach(
                function(line) {

                    const parts =
                        line.split("|");


                    const habitName =
                        parts[0];


                    const completionDate =
                        parts[1];


                    const historyItem =
                        document.createElement(
                            "div"
                        );


                    historyItem.className =
                        "history-item";


                    const name =
                        document.createElement(
                            "h3"
                        );


                    name.textContent =
                        "✅ " +
                        habitName;


                    const date =
                        document.createElement(
                            "p"
                        );


                    date.textContent =
                        "Completed: " +
                        formatDateTime(
                            completionDate
                        );


                    historyItem.appendChild(
                        name
                    );


                    historyItem.appendChild(
                        date
                    );


                    historyList.appendChild(
                        historyItem
                    );

                }
            );

        })

        .catch(error => {

            console.error(
                "Error loading history:",
                error
            );

            alert(
                "Could not load completed history!"
            );

        });

    }
);


// ==============================
// CLEAR ALL COMPLETED HISTORY
// ==============================

clearHistoryButton.addEventListener(
    "click",
    function() {

        const confirmDelete =
            confirm(
                "Are you sure you want to delete all completed habits?"
            );


        if (!confirmDelete) {

            return;

        }


        fetch(
            "http://localhost:8082/clear-history"
        )

        .then(
            response =>
                response.text()
        )

        .then(data => {

            console.log(data);


            historyList.innerHTML =
                '<p class="empty-message">' +
                'No completed history yet.' +
                '</p>';


            loadHabits();

        })

        .catch(error => {

            console.error(
                "Error clearing completed habits:",
                error
            );

            alert(
                "Could not clear completed habits!"
            );

        });

    }
);


// ==============================
// START APPLICATION
// ==============================

loadHabits();