Here is an overview of this api and it's endpoints.

---
### **Advanced E-Library Management System**

**Problem Statement:**  
Design an API for an advanced e-library system. Features to include:

- **User roles** (admin, librarian, member), where admins can manage books and events, librarians manage borrowing, and members can borrow and reserve books.
- **Book borrowing and reservations**: Users can borrow books, and if unavailable, they can reserve them for a later date.
- **Book reviews and ratings**: Members can submit reviews and rate books.
- **Library fine system**: Calculate and track fines for overdue books.
- **Event management**: Schedule events like author talks and book launches, allowing users to register for them.
- **Advanced search functionality**: Users can filter books by title, author, genre, availability, or ratings.
- **Analytics and reports**: Admins can track library usage, overdue fines, number of reservations, and other statistics.

**Expected Endpoints:**

1. **User Management:**

   - [x] `POST /users/register`: Register a new libraryUser.
   - [x] `POST /users/login`: Log in a libraryUser, returning a token for authentication.
   - [x] `GET /users/{id}/profile`: Get libraryUser profile details.
2. **Book Management:**

    - [x] `GET /books/get-books`: List all books with optional filters (title, author, genre, availability, rating).
    - [x] `GET /books/get-book/{id}`: Get details of a specific book.
    - [x] `POST /books/add`: Add a new book (admin/librarian only).
    - [x] `PATCH /books/update/{id}`: Update book details (admin/librarian only).
    - [x] `DELETE /books/delete/{id}`: Remove a book from the library (admin/librarian only).
3. **Book Borrowing and Reservations:**

    - [x] `POST /borrow`: Borrow a book (check availability and update the libraryUser's borrow history).
    - [ ] `POST /return`: Return a borrowed book, calculate overdue fines if necessary.
    - [ ] `POST /reserve`: Reserve a book if it is currently unavailable (notify when available).
4. **Book Reviews and Ratings:**

    - [ ] `POST /reviews`: Submit a review and rating for a book.
    - [ ] `GET /reviews/{book_id}`: List reviews for a specific book.
5. **Library Fine System:**

    - [ ] `GET /users/{id}/fines`: Get a list of overdue fines for a libraryUser.
    - [ ] `POST /pay-fine`: Pay a libraryUser's overdue fine.
6. **Event Management:**

    - [ ] `GET /events`: List all upcoming library events (author talks, book launches, etc.).
    - [ ] `POST /events`: Create a new event (admin only).
    - [ ] `POST /register-event`: Register a libraryUser for an upcoming event.
    - [ ] `GET /events/{id}/registrations`: View all registrations for an event (admin only).
7. **Library Analytics:**

    - [ ] `GET /admin/analytics`: Get analytics on library usage (total books borrowed, number of reservations, overdue fines, etc.).
    - [ ] `GET /admin/overdue-books`: Get a list of all overdue books and their borrowers.
8. **Notifications:**

    - [ ] `POST /notifications`: Send notifications to users about upcoming events, overdue books, etc.
    - [ ] `GET /users/{id}/notifications`: List notifications for a specific libraryUser.
