-- =====================================================================
--  College Placement Management System : MySQL 8 schema + sample data
--  Run:  mysql -u root -p < db/schema.sql
--  This DROPS and recreates the database `placement_db`.
-- =====================================================================

DROP DATABASE IF EXISTS placement_db;
CREATE DATABASE placement_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE placement_db;

-- ---------------------------------------------------------------------
-- Login accounts for every role
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    role          ENUM('ADMIN','STUDENT','COMPANY') NOT NULL,
    active        BOOLEAN NOT NULL DEFAULT TRUE,   -- company accounts wait for admin approval
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Student profile (1:1 with users)
-- ---------------------------------------------------------------------
CREATE TABLE students (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL UNIQUE,
    roll_no      VARCHAR(30) NOT NULL UNIQUE,
    department   VARCHAR(20) NOT NULL,            -- CSE, IT, ECE, EEE, MECH, CIVIL
    batch_year   INT NOT NULL,                    -- graduation year
    cgpa         DECIMAL(4,2) NOT NULL DEFAULT 0,
    backlogs     INT NOT NULL DEFAULT 0,
    phone        VARCHAR(20),
    skills       VARCHAR(500),
    resume_file  VARCHAR(255),                    -- stored file name inside upload.dir
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_cgpa CHECK (cgpa BETWEEN 0 AND 10)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Recruiting company profile (1:1 with users)
-- ---------------------------------------------------------------------
CREATE TABLE companies (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL UNIQUE,
    name        VARCHAR(150) NOT NULL,
    industry    VARCHAR(100),
    website     VARCHAR(200),
    location    VARCHAR(100),
    description TEXT,
    CONSTRAINT fk_companies_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Job / internship drives posted by companies
-- ---------------------------------------------------------------------
CREATE TABLE jobs (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    company_id           INT NOT NULL,
    title                VARCHAR(150) NOT NULL,
    description          TEXT,
    job_type             ENUM('FULL_TIME','INTERNSHIP') NOT NULL DEFAULT 'FULL_TIME',
    ctc_lpa              DECIMAL(6,2) NOT NULL,       -- package in lakhs per annum (stipend for internships)
    location             VARCHAR(100),
    min_cgpa             DECIMAL(4,2) NOT NULL DEFAULT 0,
    max_backlogs         INT NOT NULL DEFAULT 0,
    eligible_departments VARCHAR(200) NOT NULL DEFAULT 'ALL',   -- comma list e.g. 'CSE,IT' or 'ALL'
    deadline             DATE NOT NULL,
    status               ENUM('OPEN','CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jobs_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    INDEX idx_jobs_status_deadline (status, deadline)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- A student's application to a job, with its pipeline status
-- ---------------------------------------------------------------------
CREATE TABLE applications (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    job_id      INT NOT NULL,
    student_id  INT NOT NULL,
    status      ENUM('APPLIED','SHORTLISTED','INTERVIEW','SELECTED','REJECTED') NOT NULL DEFAULT 'APPLIED',
    applied_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_application UNIQUE (job_id, student_id),
    CONSTRAINT fk_app_job     FOREIGN KEY (job_id)     REFERENCES jobs(id)     ON DELETE CASCADE,
    CONSTRAINT fk_app_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Interview rounds for an application
-- ---------------------------------------------------------------------
CREATE TABLE interviews (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    round_name     VARCHAR(100) NOT NULL,          -- Aptitude, Technical, HR ...
    scheduled_at   DATETIME NOT NULL,
    mode           ENUM('ONLINE','OFFLINE') NOT NULL DEFAULT 'OFFLINE',
    venue          VARCHAR(255),                    -- room or meeting link
    result         ENUM('PENDING','PASSED','FAILED') NOT NULL DEFAULT 'PENDING',
    notes          VARCHAR(500),
    CONSTRAINT fk_interview_app FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Final placement record, written when an application becomes SELECTED
-- ---------------------------------------------------------------------
CREATE TABLE placements (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL UNIQUE,
    student_id     INT NOT NULL,
    company_id     INT NOT NULL,
    ctc_lpa        DECIMAL(6,2) NOT NULL,
    offer_date     DATE NOT NULL,
    CONSTRAINT fk_pl_app     FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_pl_student FOREIGN KEY (student_id)     REFERENCES students(id)     ON DELETE CASCADE,
    CONSTRAINT fk_pl_company FOREIGN KEY (company_id)     REFERENCES companies(id)    ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Notices from the placement cell to students
-- ---------------------------------------------------------------------
CREATE TABLE announcements (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(200) NOT NULL,
    body       TEXT NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ann_user FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Reporting view: one row per student with placement outcome
-- ---------------------------------------------------------------------
CREATE VIEW v_student_placement AS
SELECT s.id AS student_id, u.name, s.roll_no, s.department, s.batch_year, s.cgpa,
       p.ctc_lpa, c.name AS company_name, p.offer_date
FROM students s
JOIN users u            ON u.id = s.user_id
LEFT JOIN placements p  ON p.student_id = s.id
LEFT JOIN companies c   ON c.id = p.company_id;

-- =====================================================================
--  SAMPLE DATA  (company names are fictional)
--  Logins:  admin@college.edu / admin123
--           any student email  / student123
--           any company email  / company123
-- =====================================================================

INSERT INTO users (id, name, email, password_hash, role, active) VALUES
 (1,  'Placement Officer', 'admin@college.edu',      '120000:cx7FNIH0+mmfjc3xWq4Z6A==:TuV992X07N7u1I+5lKKG99K7GmigCRHSUAhnLTAWHEQ=', 'ADMIN',   TRUE),
 (2,  'Arun Kumar',        'arun@student.college.edu',   '120000:bKZBEOTJRa2Efqyq9g5Tbw==:uXKQgchpku7zs2ZARdYUPd0QE+3dFyjIlEMxjT94dy4=', 'STUDENT', TRUE),
 (3,  'Priya Sharma',      'priya@student.college.edu',  '120000:bKZBEOTJRa2Efqyq9g5Tbw==:uXKQgchpku7zs2ZARdYUPd0QE+3dFyjIlEMxjT94dy4=', 'STUDENT', TRUE),
 (4,  'Karthik Raja',      'karthik@student.college.edu','120000:bKZBEOTJRa2Efqyq9g5Tbw==:uXKQgchpku7zs2ZARdYUPd0QE+3dFyjIlEMxjT94dy4=', 'STUDENT', TRUE),
 (5,  'Divya Nair',        'divya@student.college.edu',  '120000:bKZBEOTJRa2Efqyq9g5Tbw==:uXKQgchpku7zs2ZARdYUPd0QE+3dFyjIlEMxjT94dy4=', 'STUDENT', TRUE),
 (6,  'Rahul Verma',       'rahul@student.college.edu',  '120000:bKZBEOTJRa2Efqyq9g5Tbw==:uXKQgchpku7zs2ZARdYUPd0QE+3dFyjIlEMxjT94dy4=', 'STUDENT', TRUE),
 (7,  'Sneha Reddy',       'sneha@student.college.edu',  '120000:bKZBEOTJRa2Efqyq9g5Tbw==:uXKQgchpku7zs2ZARdYUPd0QE+3dFyjIlEMxjT94dy4=', 'STUDENT', TRUE),
 (8,  'Mohammed Irfan',    'irfan@student.college.edu',  '120000:bKZBEOTJRa2Efqyq9g5Tbw==:uXKQgchpku7zs2ZARdYUPd0QE+3dFyjIlEMxjT94dy4=', 'STUDENT', TRUE),
 (9,  'Lakshmi Priya',     'lakshmi@student.college.edu','120000:bKZBEOTJRa2Efqyq9g5Tbw==:uXKQgchpku7zs2ZARdYUPd0QE+3dFyjIlEMxjT94dy4=', 'STUDENT', TRUE),
 (10, 'Nimbus HR',         'hr@nimbustech.example',      '120000:pynOjXgkn0aubtWdPN0Uyg==:g+Iyw3YLBWJzHS74WxEeOHhg1r9ntwTtr5WJ+bz+Ylo=', 'COMPANY', TRUE),
 (11, 'Orbit Recruiting',  'jobs@orbitanalytics.example','120000:pynOjXgkn0aubtWdPN0Uyg==:g+Iyw3YLBWJzHS74WxEeOHhg1r9ntwTtr5WJ+bz+Ylo=', 'COMPANY', TRUE),
 (12, 'Kaveri Talent',     'talent@kaverimotors.example','120000:pynOjXgkn0aubtWdPN0Uyg==:g+Iyw3YLBWJzHS74WxEeOHhg1r9ntwTtr5WJ+bz+Ylo=', 'COMPANY', TRUE),
 (13, 'BlueLeaf Careers',  'careers@blueleaf.example',   '120000:pynOjXgkn0aubtWdPN0Uyg==:g+Iyw3YLBWJzHS74WxEeOHhg1r9ntwTtr5WJ+bz+Ylo=', 'COMPANY', FALSE);

INSERT INTO students (id, user_id, roll_no, department, batch_year, cgpa, backlogs, phone, skills) VALUES
 (1, 2, '21CSE001', 'CSE',  2025, 8.70, 0, '9876500001', 'Java, Spring Boot, MySQL, DSA'),
 (2, 3, '21CSE014', 'CSE',  2025, 9.10, 0, '9876500002', 'Python, Machine Learning, SQL'),
 (3, 4, '21ECE007', 'ECE',  2025, 7.40, 1, '9876500003', 'Embedded C, VLSI, MATLAB'),
 (4, 5, '21IT022',  'IT',   2025, 8.20, 0, '9876500004', 'React, Node.js, MongoDB'),
 (5, 6, '21MECH03', 'MECH', 2025, 7.10, 0, '9876500005', 'AutoCAD, SolidWorks, ANSYS'),
 (6, 7, '21CSE031', 'CSE',  2025, 6.80, 2, '9876500006', 'C++, HTML, CSS'),
 (7, 8, '21EEE011', 'EEE',  2025, 7.90, 0, '9876500007', 'Power Systems, PLC, Python'),
 (8, 9, '21IT009',  'IT',   2025, 8.90, 0, '9876500008', 'Java, AWS, Docker, Kubernetes');

INSERT INTO companies (id, user_id, name, industry, website, location, description) VALUES
 (1, 10, 'Nimbus Technologies', 'IT Services',     'https://nimbustech.example',     'Chennai',    'Cloud and enterprise software services.'),
 (2, 11, 'Orbit Analytics',     'Data & AI',       'https://orbitanalytics.example', 'Bengaluru',  'Analytics products and ML consulting.'),
 (3, 12, 'Kaveri Motors',       'Automotive',      'https://kaverimotors.example',   'Coimbatore', 'Two-wheeler and EV manufacturer.'),
 (4, 13, 'BlueLeaf Systems',    'Embedded / IoT',  'https://blueleaf.example',       'Hyderabad',  'IoT devices and firmware. (pending approval)');

INSERT INTO jobs (id, company_id, title, description, job_type, ctc_lpa, location, min_cgpa, max_backlogs, eligible_departments, deadline, status) VALUES
 (1, 1, 'Software Engineer',        'Backend development in Java and Spring.',         'FULL_TIME',  6.50, 'Chennai',    7.00, 0, 'CSE,IT',          DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'OPEN'),
 (2, 2, 'Data Analyst',             'SQL, Python and dashboarding for clients.',       'FULL_TIME',  8.00, 'Bengaluru',  8.00, 0, 'CSE,IT,ECE',      DATE_ADD(CURDATE(), INTERVAL 12 DAY), 'OPEN'),
 (3, 3, 'Graduate Engineer Trainee','Production and quality engineering.',             'FULL_TIME',  4.80, 'Coimbatore', 6.50, 1, 'MECH,EEE',        DATE_ADD(CURDATE(), INTERVAL 25 DAY), 'OPEN'),
 (4, 1, 'Cloud Intern',             '6-month internship on AWS operations.',          'INTERNSHIP', 2.40, 'Remote',     6.00, 1, 'ALL',             DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'OPEN'),
 (5, 2, 'ML Engineer',              'Build and deploy ML models.',                    'FULL_TIME', 12.00, 'Bengaluru',  8.50, 0, 'CSE,IT',          DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'CLOSED'),
 (6, 3, 'EV Systems Engineer',      'Battery management and motor control.',          'FULL_TIME',  7.20, 'Coimbatore', 7.00, 0, 'EEE,ECE,MECH',    DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'OPEN');

INSERT INTO applications (id, job_id, student_id, status) VALUES
 (1, 5, 2, 'SELECTED'),     -- Priya   -> ML Engineer (placed)
 (2, 5, 8, 'REJECTED'),     -- Lakshmi -> ML Engineer
 (3, 1, 1, 'INTERVIEW'),    -- Arun    -> Software Engineer
 (4, 1, 4, 'SHORTLISTED'),  -- Divya   -> Software Engineer
 (5, 1, 8, 'APPLIED'),      -- Lakshmi -> Software Engineer
 (6, 3, 5, 'INTERVIEW'),    -- Rahul   -> GET
 (7, 6, 7, 'SELECTED'),     -- Irfan   -> EV Systems (placed)
 (8, 4, 3, 'APPLIED'),      -- Karthik -> Cloud Intern
 (9, 2, 4, 'APPLIED');      -- Divya   -> Data Analyst

INSERT INTO interviews (application_id, round_name, scheduled_at, mode, venue, result, notes) VALUES
 (1, 'Technical',  DATE_SUB(NOW(), INTERVAL 14 DAY), 'ONLINE',  'https://meet.example/orbit-ml', 'PASSED', 'Strong ML fundamentals'),
 (1, 'HR',         DATE_SUB(NOW(), INTERVAL 12 DAY), 'ONLINE',  'https://meet.example/orbit-hr', 'PASSED', NULL),
 (3, 'Aptitude',   DATE_SUB(NOW(), INTERVAL 2 DAY),  'OFFLINE', 'Seminar Hall A',                'PASSED', NULL),
 (3, 'Technical',  DATE_ADD(NOW(), INTERVAL 3 DAY),  'OFFLINE', 'Placement Cell, Room 204',      'PENDING', NULL),
 (6, 'Technical',  DATE_ADD(NOW(), INTERVAL 5 DAY),  'OFFLINE', 'Mech Block Lab 2',              'PENDING', NULL),
 (7, 'Technical',  DATE_SUB(NOW(), INTERVAL 6 DAY),  'OFFLINE', 'EEE Seminar Hall',              'PASSED', NULL);

INSERT INTO placements (application_id, student_id, company_id, ctc_lpa, offer_date) VALUES
 (1, 2, 2, 12.00, DATE_SUB(CURDATE(), INTERVAL 10 DAY)),
 (7, 7, 3,  7.20, DATE_SUB(CURDATE(), INTERVAL 4 DAY));

INSERT INTO announcements (title, body, created_by) VALUES
 ('Placement season 2025 is open', 'Update your profile and upload your latest resume before applying to any drive.', 1),
 ('Nimbus Technologies drive', 'Aptitude test for Software Engineer applicants on campus. Carry your college ID card.', 1);

-- ---------------------------------------------------------------------
-- Application user with only the rights the app needs.
-- Change the password here AND in src/main/resources/db.properties.
-- ---------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'placement_app'@'localhost' IDENTIFIED BY 'change_me';
GRANT SELECT, INSERT, UPDATE, DELETE ON placement_db.* TO 'placement_app'@'localhost';
FLUSH PRIVILEGES;
