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
                                 project_id INT NOT NULL,
                                 user_id INT NOT NULL,
                                 role INT REFERENCES project_role(id),
                                 PRIMARY KEY (project_id, user_id),
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
                               is_read BOOLEAN DEFAULT FALSE,
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