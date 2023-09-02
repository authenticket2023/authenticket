DROP TABLE IF EXISTS dev.Ticket;
DROP TABLE IF EXISTS dev.Ticket_Categories;
DROP TABLE IF EXISTS dev.Notification;
DROP TABLE IF EXISTS dev.Artist_Event;
DROP TABLE IF EXISTS dev.Event;
DROP TABLE IF EXISTS dev.Artist;
DROP TABLE IF EXISTS dev.Venue;
DROP TABLE IF EXISTS dev.Event_Organiser;
DROP TABLE IF EXISTS dev.App_User;
DROP TABLE IF EXISTS dev.Admin;

CREATE TABLE dev.App_User (
    user_id SERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    user_created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    profile_image VARCHAR(255),
	enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE dev.Admin (
    admin_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE dev.Event_Organiser (
    organiser_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    verified_by INTEGER REFERENCES dev.Admin(admin_id),
    logo_image VARCHAR(255),
	enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE dev.Venue (
    venue_id SERIAL PRIMARY KEY,
    venue_name VARCHAR(255) NOT NULL,
    venue_location VARCHAR(255) NOT NULL,
    venue_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE dev.Artist (
    artist_id SERIAL PRIMARY KEY,
    artist_name VARCHAR(255) NOT NULL,
    artist_image VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE dev.Event (
    event_id SERIAL PRIMARY KEY,
    organiser_id INTEGER REFERENCES dev.Event_Organiser(organiser_id),
    venue_id INTEGER REFERENCES dev.Venue(venue_id),
    event_name VARCHAR(255) NOT NULL,
    event_description TEXT,
    event_date TIMESTAMP NOT NULL,
    event_location VARCHAR(255),
    other_event_info TEXT,
    total_tickets INTEGER,
    total_tickets_sold INTEGER,
    event_image VARCHAR(255),
    ticket_sale_date TIMESTAMP,
    approved_by INTEGER REFERENCES dev.Admin(admin_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
    CONSTRAINT positive_total_tickets CHECK (total_tickets >= 0),
    CONSTRAINT positive_total_tickets_sold CHECK (total_tickets_sold >= 0),
    CONSTRAINT positive_date CHECK (ticket_sale_date >= created_at)
);

CREATE TABLE dev.Artist_Event (
    event_id INTEGER REFERENCES dev.Event(event_id),
    artist_id INTEGER REFERENCES dev.Artist(artist_id)
);

CREATE TABLE dev.Notification (
    notification_id SERIAL PRIMARY KEY,
    event_id INTEGER REFERENCES dev.Event(event_id),
    notification_type VARCHAR(255)  NOT NULL,
    message VARCHAR(255) NOT NULL,
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