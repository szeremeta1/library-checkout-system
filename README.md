# Szeremeta Library System

Monmouth County Libraries

A library checkout management system built in Java that demonstrates how data structures and algorithms work in real-world applications. This project shows how to use custom implementations of **HashMap**, **LinkedList**, and **PriorityQueue** to build a complete system for managing books, members, and checkouts.

## Key Features

### Book Management

- Add books with ISBN, title, author, and genre
- Track total copies and how many are available
- Search for books by title or author

### Member Management

- Register library members with contact information
- View all members and their details
- Suspend or deactivate members when needed

### Checkout & Returns

- Process book checkouts with automatic due dates (14 days)
- Record book returns
- Prevent suspended members from checking out books

### Overdue Tracking

- Automatically identify overdue books
- Calculate late fees ($1 per day overdue)
- See which items are most overdue first

### Data Saves Automatically

- Everything you add stays saved between sessions
- Uses Java serialization to store data to disk

## Why This Project Matters (The DSA Connection)

This project isn't just a library system—it's a **showcase of data structures in action**:

- **HashMap** stores books by ISBN for super-fast lookups (O(1) average time)
- **LinkedList** tracks checkouts in order with O(1) insertions
- **PriorityQueue** uses a binary heap to always show the most overdue books first

These are the same data structures used by real companies like Google, Spotify, and Netflix!

## How It's Organized

```
library-checkout-system/
├── build.sh                  # Compile script
├── src/main/java/com/librarysystem/
│   ├── LibrarySystem.java    # Main entry for CLI
│   ├── gui/                  # Graphical interface
│   │   ├── LibraryGUI.java
│   │   ├── BooksPanel.java
│   │   ├── MembersPanel.java
│   │   ├── CheckoutsPanel.java
│   │   └── StatisticsPanel.java
│   ├── model/                # Data classes (Book, Member, Checkout)
│   ├── service/              # LibraryService - business logic
│   └── data/                 # Custom data structures!
│       ├── HashMap.java      # Generic HashMap implementation
│       ├── LinkedList.java   # Generic LinkedList implementation
│       └── PriorityQueue.java # Generic PriorityQueue implementation
└── library_data.ser          # Auto-created data file
```

**Key insight:** The data structures are in `src/main/java/com/librarysystem/data/`. These are NOT Java's built-in classes.

## Building and Running

### What You Need

- Java 17 or later (the full JDK, not just the runtime)
- A terminal/command line

### Compile the Code

From the project directory, run:

```bash
./build.sh
```

This script compiles all the Java code into an `out/` folder. (Or manually run: `javac -d out src/main/java/com/librarysystem/**/*.java`)

## Using the System

### Option 1: GUI (Easier)

```bash
java -cp out com.librarysystem.gui.LibraryGUI
```

A window opens with four tabs:

#### Books Tab

- View all books in a table
- Search by title or author
- Add new books
- See how many copies are available

#### Members Tab

- View all registered members
- Add new members
- Update email and phone
- Change member status (Active, Inactive, or Suspended)

#### Checkouts Tab

- Check out a book to a member
- Return a book
- View all active checkouts
- See overdue books and how much the fine is

#### Statistics Tab

- Quick overview: total books, available books, total members, active checkouts, overdue count

### Option 2: CLI (Terminal-Based)

```bash
java -cp out com.librarysystem.LibrarySystem
```

You get a text menu where you type commands. Choose from:

#### CLI: Book Management

- View all books
- Search by title
- Search by author
- Add a new book

#### CLI: Member Management

- View all members
- Add a new member
- Update member info
- Update member status

#### CLI: Checkout Operations

- Check out a book
- Return a book
- View active checkouts
- View overdue items

#### Statistics

- See total counts for everything

### Sample Data

The first time you run the system, it creates sample data:

Books:

- Clean Code by Robert C. Martin
- The Pragmatic Programmer by David Thomas
- Design Patterns by Gang of Four
- Thinking in Java by Bruce Eckel
- Head First Java by Kathy Sierra

Members:

- John Doe (ID: M001)
- Jane Smith (ID: M002)
- Bob Johnson (ID: M003)

## Business Rules

The system enforces these rules:

- **Default checkout period:** 14 days
- **Maximum checkouts per member:** 5 books at once
- **Overdue fee:** $1.00 per day
- **Member statuses:** ACTIVE, INACTIVE, or SUSPENDED
- **Suspended members cannot check out books**

## Key Algorithms

**Hash Function:** Converts a book's ISBN to a bucket number using modulo arithmetic

**Collision Resolution:** If two books hash to the same bucket, they sit in a linked list chain

**Binary Heap Operations:** When adding an overdue checkout, it "bubbles up" to maintain the heap property (most urgent at top)

**Separate Chaining:** Used in HashMap to handle collisions by storing multiple items in a list

## How Data Saves

Everything you add is automatically saved to a file called `library_data.ser` using Java serialization. This means:

- When you close the program and restart it, all your data is still there
- You don't need a database—just a simple file on disk
- Books, members, and checkouts all persist between sessions
