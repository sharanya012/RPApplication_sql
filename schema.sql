-- 1. USERS
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 2. TEMPLATES
CREATE TABLE templates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO templates (name, description) VALUES
('IEEE Conference', 'IEEE standard template for conference papers'),
('IEEE Journal', 'IEEE journal publication template'),
('Springer', 'Springer format for journal or conference submissions'),
('ACM Conference', 'ACM official conference paper format');


-- 3. RESEARCH PAPERS
CREATE TABLE research_papers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    template_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE SET NULL
);

-- 4. SECTIONS
CREATE TABLE sections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    section_title VARCHAR(255) NOT NULL,
    content TEXT,
    position INT NOT NULL,
    paper_id BIGINT NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (paper_id) REFERENCES research_papers(id) ON DELETE CASCADE
);

CREATE TABLE section_versions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    section_id BIGINT NOT NULL,
    version_number INT NOT NULL,
    content TEXT NOT NULL,
    edited_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE,
    FOREIGN KEY (edited_by) REFERENCES users(id) ON DELETE SET NULL
);


-- 5. COLLABORATORS
CREATE TABLE collaborators (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('editor', 'viewer') DEFAULT 'editor',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (paper_id) REFERENCES research_papers(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (paper_id, user_id)
);

-- 6. VERSION HISTORY
CREATE TABLE version_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    section_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    edited_by BIGINT,
    edited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE,
    FOREIGN KEY (edited_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 7. COMMENTS
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    section_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    comment TEXT NOT NULL,
    commented_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE abstract (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    lines TEXT, -- Storing list of strings (e.g., as newline-separated)
    FOREIGN KEY (paper_id) REFERENCES research_papers(id) ON DELETE CASCADE,
    UNIQUE (paper_id)
);

CREATE TABLE authors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    position INT NOT NULL, -- Position of the author (for ordering)
    full_name VARCHAR(255) NOT NULL,
    department VARCHAR(255),
    organization VARCHAR(255),
    city_country VARCHAR(255),
    contact VARCHAR(255), -- Can hold email or ORCID
    FOREIGN KEY (paper_id) REFERENCES research_papers(id) ON DELETE CASCADE
);

CREATE TABLE keywords (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    keyword VARCHAR(100) NOT NULL,
    FOREIGN KEY (paper_id) REFERENCES research_papers(id) ON DELETE CASCADE
);

-----------------------------------------
Summary of the Schema

Table Name	Purpose
users	Stores user information (login, email)
templates	Holds document formatting templates (IEEE, ACM, Springer, etc.)
research_papers	Represents each paper authored or co-authored by users
sections	Represents structured parts of a paper like Abstract, Intro, etc.
collaborators	Allows multiple users to collaborate on the same paper with roles
version_history	Tracks edits made to each section, like Git for research content
comments	Enables users to leave comments on specific sections for feedback

section_versions
Purpose:
Maintain an immutable version history for every section.

Support features like undo, compare versions, or restore previous versions.

Track who edited what and when.