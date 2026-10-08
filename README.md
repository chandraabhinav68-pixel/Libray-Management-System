# 📚 Library Management System (Java Swing GUI + SQLite JDBC)

A robust, modular, desktop GUI-based Library Management System built using core Java OOP principles, SQLite database connectivity via JDBC PreparedStatements, multithreading for asynchronous background tasks, and clean Data Access Object (DAO) architecture.

---

## 📋 Evaluation Rubric Alignment Matrix

| Evaluation Component | Key Implementation Details & Location | Status |
| :--- | :--- | :---: |
| **OOP Implementation** (Polymorphism, Inheritance, Exception Handling, Interfaces) | • **Interfaces**: [`Borrowable`](src/com/library/interfaces/Borrowable.java), [`Searchable`](src/com/library/interfaces/Searchable.java), [`GenericDAO<T, ID>`](src/com/library/interfaces/GenericDAO.java)<br>• **Inheritance & Polymorphism**: Abstract [`LibraryItem`](src/com/library/model/LibraryItem.java) derived into [`Book`](src/com/library/model/Book.java) & [`EBook`](src/com/library/model/EBook.java); Abstract [`User`](src/com/library/model/User.java) derived into [`StudentUser`](src/com/library/model/StudentUser.java) & [`FacultyUser`](src/com/library/model/FacultyUser.java). Dynamic overdue fine calculations (`calculateOverdueFine`) & limit enforcement (`getMaxBorrowLimit`).<br>• **Custom Exception Handling**: [`LibraryException`](src/com/library/exceptions/LibraryException.java), [`BookNotAvailableException`](src/com/library/exceptions/BookNotAvailableException.java), [`UserLimitExceededException`](src/com/library/exceptions/UserLimitExceededException.java), [`DatabaseException`](src/com/library/exceptions/DatabaseException.java). | ✅ Complete |
| **Collections & Generics** | • **Generics**: Generic interface `GenericDAO<T, ID>` implemented across all repository classes.<br>• **Collections**: `List<LibraryItem>`, `List<User>`, `List<Transaction>`, in-memory indexing `Map<String, LibraryItem>` and `Map<String, User>` in [`LibraryService`](src/com/library/service/LibraryService.java). | ✅ Complete |
| **Multithreading & Synchronization** | • **Multithreading**: [`FineCalculationTask`](src/com/library/service/FineCalculationTask.java) (implements `Runnable`) dynamically computes overdue fines in the background without UI blocking. [`BackupTask`](src/com/library/service/BackupTask.java) (implements `Callable<Boolean>`) exports catalog data asynchronously.<br>• **Synchronization**: Thread safety via `ReentrantLock` & `synchronized` methods in [`LibraryService`](src/com/library/service/LibraryService.java) preventing race conditions during concurrent book issues. | ✅ Complete |
| **Classes for Database Operations (DAO)** | • **DAO Architecture**: Clean separation of database logic into [`BookDAO`](src/com/library/db/BookDAO.java), [`UserDAO`](src/com/library/db/UserDAO.java), and [`TransactionDAO`](src/com/library/db/TransactionDAO.java). Full CRUD operations decoupled from UI logic. | ✅ Complete |
| **Database Connectivity (JDBC)** | • **SQLite JDBC Integration**: Implemented in [`DatabaseManager`](src/com/library/db/DatabaseManager.java). Utilizes `DriverManager`, `PreparedStatement` (SQL injection prevention), `ResultSet` mapping, and automated table schema initialization & data seeding. | ✅ Complete |

---

## 📁 Repository Structure

```
library-management-system/
├── src/
│   └── com/
│       └── library/
│           ├── main/
│           │   └── MainApp.java                    # Entry point launching Swing GUI on EDT
│           ├── model/
│           │   ├── LibraryItem.java                # Abstract base class for inventory
│           │   ├── Book.java                       # Physical Book subclass
│           │   ├── EBook.java                      # Digital EBook subclass
│           │   ├── User.java                       # Abstract base user class
│           │   ├── StudentUser.java                # Student user subclass (3 book limit)
│           │   ├── FacultyUser.java                # Faculty user subclass (10 book limit, 50% fine discount)
│           │   └── Transaction.java                # Issue/Return transaction model
│           ├── interfaces/
│           │   ├── Borrowable.java                 # Contract for issue/return
│           │   ├── Searchable.java                 # Contract for search filtering
│           │   └── GenericDAO.java                 # Generic CRUD DAO interface
│           ├── exceptions/
│           │   ├── LibraryException.java           # Base domain exception
│           │   ├── BookNotAvailableException.java  # Thrown when copy count is 0
│           │   ├── UserLimitExceededException.java # Thrown when user quota exceeded
│           │   └── DatabaseException.java          # Wrapper for SQL errors
│           ├── db/
│           │   ├── DatabaseManager.java            # SQLite JDBC connection & schema setup
│           │   ├── BookDAO.java                    # Database operations for items
│           │   ├── UserDAO.java                    # Database operations for users
│           │   └── TransactionDAO.java             # Database operations for borrow records
│           ├── service/
│           │   ├── LibraryService.java             # Core service with thread-locks
│           │   ├── FineCalculationTask.java        # Runnable thread task for overdue fines
│           │   └── BackupTask.java                 # Callable worker thread for CSV export
│           └── ui/
│               ├── MainFrame.java                  # Main Swing window with tabs
│               ├── BookManagementPanel.java        # Inventory CRUD & search panel
│               ├── UserManagementPanel.java        # Student & Faculty directory panel
│               ├── IssueReturnPanel.java           # Book issue and return operations
│               ├── AnalyticsPanel.java             # Real-time metrics & thread worker console
│               └── AddEditBookDialog.java          # Item input dialog form
├── lib/
│   └── sqlite-jdbc-3.45.1.0.jar                    # SQLite JDBC Driver
├── presentation/
│   ├── Library_Management_System_Presentation.html # Interactive Presentation Slide Deck
│   ├── lms_gui_dashboard.jpg                       # Dashboard UI screenshot
│   └── lms_analytics_view.jpg                      # Multithreading console screenshot
├── build.bat                                       # Windows build compilation script
├── run.bat                                         # Windows application launcher script
└── README.md                                       # Documentation & setup guide
```

---

## 🛠️ System Requirements & Prerequisite Setup

- **Java Development Kit (JDK)**: JDK 17 or JDK 21+
- **Database**: Embedded SQLite (Self-contained via `sqlite-jdbc-3.45.1.0.jar` included in `lib/`)
- **OS**: Windows / macOS / Linux

---

## 🚀 How to Build and Run

### Method 1: Using Windows Batch Files (Recommended)
1. Double click or execute `build.bat` in the terminal to compile all Java source files:
   ```cmd
   build.bat
   ```
2. Run `run.bat` to start the application:
   ```cmd
   run.bat
   ```

### Method 2: Manual Terminal Execution (All Platforms)
1. **Compile**:
   ```bash
   mkdir -p bin
   javac -cp "lib/sqlite-jdbc-3.45.1.0.jar" -d bin src/com/library/interfaces/*.java src/com/library/exceptions/*.java src/com/library/model/*.java src/com/library/db/*.java src/com/library/service/*.java src/com/library/ui/*.java src/com/library/main/*.java
   ```
2. **Run**:
   ```bash
   java -cp "bin:lib/sqlite-jdbc-3.45.1.0.jar" com.library.main.MainApp
   ```
   *(Note: On Windows terminal, use semicolon `;` instead of colon `:` in classpath)*
