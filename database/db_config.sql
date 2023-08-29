DROP TABLE IF EXISTS dev.Ticket;
DROP TABLE IF EXISTS dev.Ticket_Categories;
DROP TABLE IF EXISTS dev.Event;
DROP TABLE IF EXISTS dev.Event_Organiser;
DROP TABLE IF EXISTS dev.App_User;

CREATE TABLE dev.App_User (
    user_id SERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    user_created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    profile_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE dev.Admin {
    admin_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    
}

CREATE TABLE dev.Event_Organiser (
    organiser_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    verified BOOLEAN,
    logo_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE dev.Event (
    event_id SERIAL PRIMARY KEY,
    organiser_id INTEGER REFERENCES dev.Event_Organiser(organiser_id),
    event_name VARCHAR(255) NOT NULL,
    event_description TEXT,
    event_date TIMESTAMP NOT NULL,
    event_location VARCHAR(255),
    other_event_info TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE dev.Ticket_Categories (
    category_id SERIAL PRIMARY KEY,
    event_id INTEGER REFERENCES dev.Event(event_id),
    category_name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    available_tickets INTEGER NOT NULL,
    CONSTRAINT positive_price CHECK (price >= 0),
    CONSTRAINT positive_available_tickets CHECK (available_tickets >= 0)
);

CREATE TABLE dev.Ticket (
    ticket_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES dev.App_User(user_id),
    event_id INTEGER REFERENCES dev.Event(event_id),
    category_id INTEGER REFERENCES dev.Ticket_Categories(category_id)
);





