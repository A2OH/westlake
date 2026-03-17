package com.example.todolist;

/**
 * POJO representing a single TODO item.
 * Priority: 1 = low, 2 = medium, 3 = high.
 */
public class TodoItem {
    public int id;
    public String title;
    public String description;
    public boolean completed;
    public long createdAt;
    public int priority; // 1-3

    public TodoItem() {}

    public TodoItem(int id, String title, String description, boolean completed, long createdAt, int priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.createdAt = createdAt;
        this.priority = priority;
    }

    /** Returns priority as exclamation marks: "!!!" for high, "!!" for medium, "!" for low. */
    public String getPriorityString() {
        switch (priority) {
            case 3: return "!!!";
            case 2: return "!!";
            default: return "!";
        }
    }

    @Override
    public String toString() {
        return (completed ? "[x] " : "[ ] ") + title + " " + getPriorityString();
    }
}
