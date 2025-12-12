# Library Checkout System — Lab Report

**Course:** Data Structures & Algorithms (DSA)  
**Project:** Library Checkout System (Option 1)  
**Author:** Alexander Szeremeta  
**Date:** December 12, 2025  

---

## Abstract
This project implements a simplified library checkout system that demonstrates core data-structure usage in a real-world scenario. Custom implementations of `HashMap`, `LinkedList`, and `PriorityQueue` support fast lookups, ordered traversal, and prioritized processing. The system provides both a Swing GUI and CLI, persists data via serialization, and enforces business rules such as checkout limits, suspensions, and overdue fee calculation. Remaining gaps include the explicit stack for recent returns and queue for waitlists called for in the original assignment; these are outlined as future work.

## Problem Statement
Libraries need fast, reliable ways to manage books, members, and circulation while enforcing policies (checkout limits, suspensions, overdue fees). The goal was to build a lightweight system—no external database—that showcases custom data structures while delivering practical functionality for librarians and patrons.

## Requirements Traceability (DSA Midterm Project v2)
**Stated requirements**
- List to store the collection of books/items (title + unique ID).
- Stack for most recently returned books.
- Queue for patrons waiting on popular books.
- Operations: add/remove items, checkout, return with due dates, view/search items.
- Additional documented feature.
- Deliverables: source code with edge-case handling and a 5–10 page lab report.

**Coverage in this build**
- Implemented: add/remove books and members; checkout/return with limits; search by title/author; overdue detection and fee; statistics; persistence; GUI + CLI; custom `HashMap`, `LinkedList`, `PriorityQueue` data structures; sample data seeding.
- Gaps: stack for recent returns (not yet); queue/waitlist (not yet); `PriorityQueue` not yet wired into overdue tracking; `removeBook`/`removeMember` do not call `saveData` immediately.

**Planned to close gaps**
1) Add a `Deque<String>` (or custom stack) in `LibraryService` to push checkout IDs on return; expose “Recently Returned” in GUI/CLI.  
2) Add per-book `Queue<String>` waitlists; on return, automatically offer next patron.  
3) Replace overdue list sort with live `PriorityQueue<Checkout>` for O(log n) maintenance.  
4) Call `saveData` inside `removeBook`/`removeMember` to persist removals.

## System Overview
- **Interfaces:**
  - Swing GUI (`LibraryGUI`, `BooksPanel`, `MembersPanel`, `CheckoutsPanel`, `StatisticsPanel`).
  - CLI (`LibrarySystem`) with text menus mirroring GUI operations.
- **Core Service:** `LibraryService` manages domain objects, business rules, and persistence.
- **Data Structures:** Custom `HashMap`, `LinkedList`, `PriorityQueue` in `com.librarysystem.data`.
- **Models:** `Book`, `Member`, `Checkout`.
- **Persistence:** Java serialization to `library_data.ser`, auto-load on startup, auto-save on mutating operations.

## Architecture & Data Flow
- GUI/CLI -> `LibraryService` -> custom data structures (`HashMap` for entities, `LinkedList` per member history, `PriorityQueue` planned for overdue) -> serialization store.
- Validation and business rules live centrally in `LibraryService` so both interfaces stay consistent.
- Sample data loads only when no prior `library_data.ser` exists, keeping restarts idempotent.

## GUI & CLI Walkthrough
- **GUI Tabs:** Books (search/add/details), Members (add/update/status), Checkouts (checkout/return/details, filter overdue), Statistics (counts).
- **CLI Menus:** Book management, member management, checkout operations, statistics; mirrors GUI flows for parity.
- **Sync:** All panels share one `LibraryService` instance; refresh hooks keep tables current after checkout/return.

## Data Structures and Their Roles
| Structure | Role | Core cost |
|-----------|------|-----------|
| `HashMap<K,V>` | ID lookups for books/members/checkouts | put/get/remove O(1) avg |
| `LinkedList<E>` | Per-member checkout history | add/remove ends O(1) |
| `PriorityQueue<E>` | Planned overdue heap | offer/poll O(log n) |
| *Missing* stack | Planned recent-return tracker | push/pop O(1) |
| *Missing* queue | Planned waitlist per book | enqueue/dequeue O(1) |

## Design and Implementation
### Book Management
- Stored in `HashMap<String, Book>` keyed by ISBN.
- Operations: add, remove, search by title/author (substring), list all, availability tracking (`totalCopies`, `availableCopies`).
- GUI: table with filtering by title/author and dialogs to add and inspect book details.

### Member Management
- Stored in `HashMap<String, Member>` keyed by member ID.
- Operations: add, update contact info, update status (ACTIVE/INACTIVE/SUSPENDED), view all, view per-member checkouts.
- GUI: table with dialogs for add/view/update.

### Checkout and Returns
- Checkouts keyed by generated ID (`CO######`), stored in `HashMap<String, Checkout>` plus per-member `LinkedList` history.
- Business rules enforced in `LibraryService.checkoutBook`:
  - Member must exist and be ACTIVE.
  - Member cannot exceed `maxCheckouts` (default 5).
  - Book must have available copies.
- Returns update checkout status, increment book availability, and persist.
- Overdue detection (`getOverdueCheckouts`) marks overdue items and sorts by due date; fee is `$1 * overdueDays`.

### Statistics
- Counts for books, available copies, checked-out copies, members, active and overdue checkouts; shown in GUI `StatisticsPanel` and CLI summary.

### Persistence and Error Handling
- `saveData` serializes books, members, checkouts, and checkout counter to `library_data.ser` after most mutating operations.
- `loadData` reconstructs maps and member checkout lists on startup; if file is missing, sample data seeds the system.
- Current gap: `removeBook`/`removeMember` do not trigger `saveData`, so removals vanish unless another save occurs.

### Additional Feature Delivered
- Dual interface (GUI + CLI) with real-time persistence and sample data seeding for first run.

### Gaps vs Assignment Requirements
- Stack for recent returns: not present. Plan to add a `Deque<String>` (or custom stack) to push checkout IDs on return and expose a "Recently Returned" view.
- Queue for waitlisted patrons: not present. Plan per-book `Queue<String>` for member IDs; integrate into checkout flow and GUI table.
- Priority queue integration: planned wiring of `PriorityQueue<Checkout>` to maintain overdue items by due date instead of sorting a list each call.

## Algorithms and Complexity
**Core data access**

| Operation | Time | Note |
|-----------|------|------|
| Add book/member | O(1) avg | Resize O(n) on growth |
| Search books | O(n) | Case-insensitive substring |
| Member lookup | O(1) avg | Direct ID fetch |

**Circulation flows**

| Operation | Time | Note |
|-----------|------|------|
| Checkout | O(1) | Validates status and copies |
| Return | O(1) | Will push to return stack (planned) |
| Overdue detect | O(n log n) | Will switch to heap maintenance |
| Member history | O(k) | k = member checkout count |

## Edge Cases & Validation
- Block checkout when member is SUSPENDED or exceeds max checkouts.
- Reject checkout when no available copies remain.
- Overdue flagging runs on read (`getOverdueCheckouts`); future heap will maintain proactively.
- Persistence guards: if `library_data.ser` is missing/corrupt, system seeds sample data and continues.
- Input handling: CLI parses integers for copies/status; GUI dialogs enforce required fields.

## Performance & Scalability Considerations
- In-memory data structures keep latency low; serialization suits a single-user desktop scope.
- Hash-based access gives O(1) average lookups for books/members even as counts grow.
- Overdue detection is currently O(n log n) due to sorting; the planned heap will drop ongoing maintenance to O(log n) per mutation and O(1) peek.
- Waitlists and recent-return stack will both be O(1) for primary operations, keeping UI responsive.

## Sample User Flow (GUI)
1) Librarian launches GUI; sample data loads if no prior save exists.  
2) Adds a new member and a new book via modal dialogs.  
3) Checks out the book: availability decreases, checkout appears in the grid.  
4) Marks return: availability increases; (future) return stack records the event.  
5) Views statistics tab to confirm totals and overdue counts.

## Risks & Mitigations
- **Data loss on abrupt exit after removals:** mitigated by adding `saveData` calls on `removeBook`/`removeMember` (planned).
- **UI desync across tabs:** mitigated by centralized `LibraryService` and refresh hooks after checkout/return.
- **Corrupt save file:** mitigated by falling back to seeded sample data and logging the error.
- **Overdue performance at scale:** mitigated by wiring the existing `PriorityQueue` for incremental maintenance.

## Testing and Results
Manual validation via GUI and CLI:
- Add books/members, search by title/author, update member status/info.
- Checkout enforces availability and active status; exceeding max checkouts raises error.
- Return updates availability and status; overdue fee calculation matches `$1/day` policy.
- Persistence: restart retains books/members/checkouts through `library_data.ser`.

Suggested automated tests (next step):
- Unit: `HashMap` resize/collisions, `LinkedList` insert/remove, `PriorityQueue` heap order.
- Service: checkout constraints, overdue fee edge cases, renewals, persistence round-trip.
- Integration: seed data load, GUI smoke (optional), waitlist/stack once added.

## Reflection
**What worked:** Custom data structures behave correctly for core operations; Swing UI and CLI share the same service; serialization keeps setup simple.  
**Challenges:** Collision handling and resizing in `HashMap`, heap index math, and keeping GUI panels synchronized were the main pain points.  
**Lessons:** Centralizing business rules in `LibraryService` simplifies both interfaces; custom structures need thorough edge-case tests.

## Future Work
1. Implement required stack for recent returns and surface it in GUI/CLI.
2. Add per-book waitlist queue and integrate into checkout/return flow.
3. Replace overdue sorting with the existing `PriorityQueue` to maintain a live min-heap of due dates.
4. Persist removals immediately by calling `saveData` inside `removeBook`/`removeMember`.
5. Add automated tests and basic CI (e.g., `mvn test` or `gradle test` after migrating to a build tool).
6. Optional: export reports (CSV/PDF) and add configurable loan periods.

## How to Run
```bash
./build.sh
java -cp out com.librarysystem.gui.LibraryGUI   # GUI
# or
java -cp out com.librarysystem.LibrarySystem    # CLI
```

---

_End of Report_
