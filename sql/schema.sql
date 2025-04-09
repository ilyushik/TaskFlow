CREATE TABLE user_role (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       role VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE user (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role INT REFERENCES user_role(id)
);

CREATE TABLE project (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          deadline DATE,
                          priority INT DEFAULT 0,
                          owner_id INT NOT NULL, -- из Auth Service
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_role (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           role VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE project_member (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 project_id INT NOT NULL,
                                 user_id INT NOT NULL,
                                 role INT REFERENCES project_role(id),
                                 FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE TABLE task_status (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           status VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE task (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       status INT REFERENCES task_status(id),
                       priority INT DEFAULT 0,
                       due_date DATE,
                       project_id INT NOT NULL,
                       assigned_user_id INT, -- из Auth Service
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE notification_type (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           type VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE notification (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               message TEXT NOT NULL,
                               type INT REFERENCES notification_type(id),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE report_status (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           status VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE report (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         user_id INT NOT NULL,           
                         type INT REFERENCES report_status(id),
                         parameters TEXT,                    
                         result TEXT,                       
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO user_role (role) VALUES ('ROLE_ADMIN'), ('ROLE_USER'), ('ROLE_MANAGER');

INSERT INTO project (name, description, deadline, priority, owner_id) VALUES
('Project Alpha', 'First big project', '2025-12-31', 1, 1),
('Project Beta', 'Second test project', '2025-11-30', 2, 2);

INSERT INTO project_role (role) VALUES ('OWNER'), ('MEMBER'), ('VIEWER');

INSERT INTO project_member (project_id, user_id, role) VALUES
(1, 1, 1),
(1, 2, 2), 
(2, 3, 1);

INSERT INTO task_status (status) VALUES ('TO DO'), ('IN PROGRESS'), ('DONE');

--INSERT INTO task (title, description, status, priority, due_date, project_id, assigned_user_id) VALUES
--('Setup Backend', 'Set up Spring Boot backend for Alpha', 1, 1, '2025-06-01', 1, 2),
--('Design UI', 'Create mockups for UI', 2, 2, '2025-05-20', 1, 2),
--('Write Documentation', 'Initial draft', 1, 3, '2025-06-15', 2, 3);

--INSERT INTO notification_type (type) VALUES ('INFO'), ('WARNING'), ('ALERT');

--INSERT INTO notification (user_id, message, type) VALUES
--(2, 'Task assigned: Setup Backend', 1),
--(3, 'New comment on task', 2),
--(1, 'System maintenance scheduled', 3);

--INSERT INTO report_status (status) VALUES ('PENDING'), ('IN PROGRESS'), ('COMPLETED'), ('FAILED');
--
--
--INSERT INTO report (user_id, type, parameters, result) VALUES
--(1, 3, '{"project":"Alpha"}', 'Success'),
--(2, 1, '{"date_range":"2025-01-01 to 2025-03-01"}', NULL),
--(3, 4, '{"metric":"user_activity"}', 'Error: Unauthorized');