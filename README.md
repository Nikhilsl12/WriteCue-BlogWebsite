# BlogApp

A RESTful API for a blog application built with Spring Boot. This application allows users to create accounts, publish blog posts, comment on posts, like posts, and receive notifications.

## Technologies Used

- Java 23
- Spring Boot 3.4.4
- Spring Data JPA
- Spring Security
- MySQL Database
- Maven
- Lombok
- Spring Boot Validation
- Spring Boot Actuator

## Features

- **User Management**
  - User registration and login
  - Profile management
  - Password change functionality
  - User deletion

- **Post Management**
  - Create, read, update, and delete blog posts
  - View all posts with pagination
  - View posts by a specific user
  - Share posts via URL

- **Comment System**
  - Add comments to posts
  - Edit and delete comments
  - View all comments on a post

- **Like System**
  - Like and unlike posts
  - View users who liked a post
  - Count likes on a post

- **Notification System**
  - Receive notifications for various interactions:
    - Comment notifications when someone comments on your post
    - Like notifications when someone likes your post
    - Registration notifications when you create an account
    - Password change notifications
    - Profile update notifications
  - Email notifications for important events
  - Mark notifications as read individually or all at once

## Setup and Installation

### Prerequisites

- Java 23 or higher
- MySQL 8.0 or higher
- Maven

### Database Setup

1. Create a MySQL database named `blogapplication`
2. Update the database configuration in `src/main/resources/application.properties` if needed:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/blogapplication
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### Building and Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
5. The application will be available at `http://localhost:8080`

## API Endpoints

### User Endpoints

- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/{id}/posts` - Get posts by a specific user
- `GET /api/users/username/{username}` - Get user by username
- `POST /api/users/register` - Register a new user
- `POST /api/users/login` - User login
- `PUT /api/users/{id}` - Update user information
- `DELETE /api/users/{id}` - Delete a user
- `PUT /api/users/{id}/change-password` - Change user password

### Post Endpoints

- `GET /api/posts` - Get all posts (paginated)
- `GET /api/posts/{id}` - Get a specific post
- `POST /api/posts` - Create a new post
- `PUT /api/posts/{id}` - Update a post
- `DELETE /api/posts/{id}` - Delete a post
- `GET /api/posts/{id}/share` - Get a shareable URL for a post

### Comment Endpoints

- `POST /api/comments/user/{userId}/post/{postId}` - Create a comment
- `PUT /api/comments/{id}` - Update a comment
- `DELETE /api/comments/{id}` - Delete a comment
- `GET /api/comments/{id}` - Get a specific comment
- `GET /api/comments/post/{id}` - Get all comments on a post

### Like Endpoints

- `POST /api/likes/user/{userId}/post/{postId}` - Like a post
- `DELETE /api/likes/user/{userId}/post/{postId}` - Unlike a post
- `GET /api/likes/post/{postId}` - Get users who liked a post
- `GET /api/likes/post/{postId}/count` - Get like count for a post

### Notification Endpoints

- `PUT /api/notifications/{id}/mark-read` - Mark a specific notification as read
- `PUT /api/notifications/mark-all-read` - Mark all notifications as read

## Security

The application uses Spring Security for authentication and authorization. Public endpoints include:
- `/api/users/register`
- `/api/users/login`

All other endpoints require authentication.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Contact

For any inquiries, please contact Nikhil Singhal, the project owner.
