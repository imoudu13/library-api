We'll Relational Database, It's perfect for E-Commerce type apps like this


##### Tables:

User
- id (int, PK)
- username (string)
- email (string)
- password (string, salted, hashed, peppered)
- firstname (string)
- lastname (string)
- role (int) //admin, librarian, member (1, 2, 3)

Book
- name (string)
- title (string)
- author (string)
- genre (string)
- availability(int)
- avgRating(float)?
- sumRating (float)?
- numberOfRatings (float)?
- id (int, PK)

BookReservation
- id (int, PK)
- date  (Date)
- active (boolean)
- userId (int, FK)

BookActivityHistory
- id (int, PK)
- type (1, 2, 3) //withdraw, return, reserve
- userId (int, FK)
- date (Date)

BookReturn
- id (int, PK)
- date (Date)
- wasOverdue(boolean)
- userId (int, FK)

BookWithdrawal
- id (int, PK)
- bookActivityId (int, FK)
- expectedReturnDate (Date)
- userId (int, FK)

Fine
- id (int, PK)
- userId (id, FK)
- amount (float)
- withdrawalId (int, FK)

BookReview
- id (int, PK)
- rating (float)
- text (Text)
- title(string)

Event
- id(int, PK)
- bookId (int, FK)
- description (string)
- date (Date)

EventRegister
- id(int, PK)
- userId(int, FK)
- eventId (int, FK)

Notification
- id(int, PK)
- userId (int, FK)
- type (int) //evemt, fine, overdue book