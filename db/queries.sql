CREATE TABLE users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(20) NOT NULL,
                    role ENUM('faculty', 'student') NOT NULL

);

CREATE TABLE students (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT UNIQUE NOT NULL,
                          reg_no INT UNIQUE NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          roll VARCHAR(50) NOT NULL
);


CREATE TABLE attendances (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             student_id INT NOT NULL,
                             date DATE NOT NULL DEFAULT CURRENT_DATE,
                             course_id INT NOT NULL,
                             status ENUM('present', 'absent') NOT NULL,
                             FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE courses (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         code VARCHAR(50) NOT NULL,
                         semester INT NOT NULL
);

CREATE TABLE student_courses (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 student_id INT NOT NULL,
                                 course_id INT NOT NULL,
                                 FOREIGN KEY (student_id) REFERENCES students(id),
                                 FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE student_leaves (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                student_id INT NOT NULL,
                                date DATE NOT NULL,
                                course_id INT NOT NULL,
                                status ENUM('accepted','rejected','pending') NOT NULL,
                                reason VARCHAR(255),
                                FOREIGN KEY (student_id) REFERENCES students(id),
                                FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE faculty (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 user_id INT NOT NULL,
                                 name VARCHAR(100) NOT NULL,
                                 expertise VARCHAR(100) NOT NULL,
                                 designation VARCHAR(100) NOT NULL,
                                 last_seen DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE faculty_courses (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 faculty_id INT NOT NULL,
                                 course_id INT NOT NULL,
                                 FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE CASCADE,
                                 FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);


-- Insert sample students
INSERT INTO students (user_id, roll, name, reg_no) VALUES
                                                       (1, 'CSE/23101', 'Alice Johnson', '1149'),
                                                       (2, 'CSE/23102', 'Bob Smith', '1150'),
                                                       (3, 'CSE/23103', 'Charlie Brown', '1151'),
                                                       (4, 'CSE/23104', 'David Lee', '1152'),
                                                       (5, 'CSE/23105', 'Emma Watson', '1153');

-- Insert random attendance records
INSERT INTO attendances (student_id, course_id, status) VALUES
                                                            (1, 1, 'present'),
                                                            (1, 2, 'absent'),
                                                            (1, 3, 'present'),
                                                            (2, 1, 'present'),
                                                            (2, 2, 'absent'),
                                                            (3, 3, 'present'),
                                                            (3, 3, 'present'),
                                                            (4, 2, 'absent'),
                                                            (4, 1, 'present'),
                                                            (5, 3, 'present');

-- 4) Insert into COURSES
INSERT INTO courses (name, code, semester) VALUES
                                     ('Mathematics IV', 'MAC401', 4),
                                     ('Humanities II',  'HUC401', 4),
                                     ('Data Structures', 'CSE301', 3),
                                     ('Operating Systems', 'CSE302', 3);


-- 6) Insert into STUDENT_COURSES (which student is enrolled in which course)
INSERT INTO student_courses (student_id, course_id) VALUES
                                                        (1, 1),
                                                        (1, 2),
                                                        (1, 3),
                                                        (2, 1),
                                                        (2, 3),
                                                        (3, 2),
                                                        (3, 4);

-- 8) Insert some STUDENT_LEAVES
INSERT INTO student_leaves (student_id, date, course_id, status, reason) VALUES
                                                                             (1, '2025-03-05', 1, 'pending',  'Medical check-up'),
                                                                             (1, '2025-03-10', 2, 'accepted', 'Family emergency'),
                                                                             (2, '2025-03-07', 1, 'rejected', 'Vacation request'),
                                                                             (3, '2025-03-08', 2, 'pending',  'Traveling'),
                                                                             (3, '2025-03-12', 4, 'accepted', 'Attending a conference');


-- Assigning teachers to courses
INSERT INTO faculty_courses (faculty_id, course_id) VALUES
                                                        (1, 1),
                                                        (1, 2),
                                                        (2, 2),
                                                        (2, 3),
                                                        (3, 4),
                                                        (4, 1),
                                                        (4, 3);

INSERT INTO faculty (user_id, name, expertise, designation) VALUES
                                                                (6, 'Dr. Alice Smith', 'Data Science, Machine Learning', 'Professor'),
                                                                (7, 'Dr. Bob Johnson', 'Computer Networks, Security', 'Associate Professor'),
                                                                (8, 'Dr. Charlie Brown', 'Algorithms, Complexity', 'Professor'),
                                                                (9, 'Dr. Daniel Williams', 'Operating Systems, Distributed Systems', 'Assistant Professor'),
                                                                (10, 'Dr. Emily Davis', 'Artificial Intelligence, Robotics', 'Professor');


INSERT INTO users (username, email, password, role) VALUES
                                                        ('alicejohnson', 'alice.johnson@example.com', 'pass123', 'student'),
                                                        ('bobsmith', 'bob.smith@example.com', 'pass123', 'student'),
                                                        ('charliebrown', 'charlie.brown@example.com', 'pass123', 'student'),
                                                        ('davidlee', 'david.lee@example.com', 'pass123', 'student'),
                                                        ('emmawatson', 'emma.watson@example.com', 'pass123', 'student'),

                                                        -- Faculty (user_id values should correspond to those used in the faculty table)
                                                        ('dr.alicesmith', 'dr.alice@example.com', 'pass123', 'faculty'),
                                                        ('dr.bobjohnson', 'dr.bob@example.com', 'pass123', 'faculty'),
                                                        ('dr.charliebrown', 'dr.charlie@example.com', 'pass123', 'faculty'),
                                                        ('dr.danielwilliams', 'dr.daniel@example.com', 'pass123', 'faculty'),
                                                        ('dr.emilydavis', 'dr.emily@example.com', 'pass123', 'faculty');

