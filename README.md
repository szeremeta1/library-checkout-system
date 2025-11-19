# Szeremeta Library System

Monmouth County Libraries

A library checkout management system built in Java that demonstrates how data structures and algorithms work in real-world applications. This project shows how to use custom implementations of **HashMap**, **LinkedList**, and **PriorityQueue** to build a complete system for managing books, members, and checkouts.

## What Does This Project Do?

Imagine you work at a library. You need to:

- Keep track of all the books and how many copies you have
- Let people register as members
- Record when someone checks out a book and when they need to return it
- Find books that are overdue and calculate late fees
- See statistics about what's checked out

This project automates all of that! It provides two ways to interact with it:

- **GUI (Graphical Interface)**: Click buttons and fill in forms
- **CLI (Command Line Interface)**: Type commands in a terminal

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

## Understanding the Data Structures

The project uses three different data structures, each optimized for different tasks.

### HashMap: Fast Book Lookups

**What it does:** Stores books by their ISBN so you can find any book instantly.

**How it works:** Imagine 16 filing cabinets. When you add a book, the system calculates which cabinet it should go in using a hash function. If two books end up in the same cabinet, they sit together in a chain. To find a book, go to the right cabinet and search that chain.

**Why it matters:** Finding a book by ISBN is **O(1) on average**—super fast! Without this, you'd have to check every single book.

**The code:** `HashMap<String, Book> books` stores each book's ISBN as the key.

### LinkedList: Tracking Checkouts

**What it does:** Keeps a chain of all checkouts in order.

**How it works:** Imagine a chain of paper cards. Each card holds a checkout and points to the next card. To add a new checkout, create a new card and insert it into the chain. Each card also knows what card came before it (doubly-linked).

**Why it matters:** You can add or remove checkouts from the middle of the chain in **O(1) time if you already know where** it is. But finding a specific checkout requires scanning through the chain: **O(n)**.

**The code:** `LinkedList<Checkout> checkouts` stores each checkout, with pointers to the next/previous checkout.

### PriorityQueue: Finding Most Overdue Books

**What it does:** Always keeps the most urgent item (most overdue book) at the top.

**How it works:** Uses a binary heap structure (like a tree stored in an array). When you add a new overdue book, it bubbles up or down until it's in the right spot relative to urgency. The most overdue book is always at position [0].

**Why it matters:** You can always get the most overdue book instantly with **O(log n) insertion and removal**. Much faster than checking all overdue items!

**The code:** `PriorityQueue<Checkout> overdueCheckouts` keeps checkouts sorted by due date.

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

## The GUI Design

The graphical interface uses Java Swing (built into the JDK) and features:

- **Tabbed layout:** Easy switching between different functions
- **Sortable tables:** Click column headers to sort
- **Real-time search:** Filter books instantly
- **Dialog forms:** Simple pop-ups for adding/editing data
- **Status messages:** Clear feedback on success or errors

Everything works without needing to install extra libraries—just Java!

## Future Enhancements

Possible improvements to the system:

- Add a SQL database instead of file serialization
- Implement a REST API for remote access
- Add book reservations (hold a book until it's returned)
- Implement a fine payment system
- Create member login accounts
- Add book recommendations based on checkout history
- Send email notifications for overdue items
- Add barcode scanning support
- Generate receipts and reports