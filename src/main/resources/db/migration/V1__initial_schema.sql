CREATE TABLE aircraft
(
    id                    UUID NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    registration_number   VARCHAR(255),
    model                 VARCHAR(255),
    capacity              INTEGER,
    status                VARCHAR(255),
    last_maintenance_date TIMESTAMP WITHOUT TIME ZONE,
    next_maintenance_date TIMESTAMP WITHOUT TIME ZONE,
    total_flight_hours    BIGINT,
    version               BIGINT,
    CONSTRAINT pk_aircraft PRIMARY KEY (id)
);

CREATE TABLE app_user
(
    id            UUID    NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    username      VARCHAR(255),
    email         VARCHAR(255),
    password_hash VARCHAR(255),
    role          VARCHAR(255),
    full_name     VARCHAR(255),
    is_active     BOOLEAN NOT NULL,
    last_login    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_app_user PRIMARY KEY (id)
);

CREATE TABLE audit_log
(
    id           UUID NOT NULL,
    entity_id    UUID,
    entity_type  VARCHAR(255),
    action       VARCHAR(255),
    old_value    VARCHAR(255),
    new_value    VARCHAR(255),
    performed_by VARCHAR(255),
    ip_address   VARCHAR(255),
    request_id   VARCHAR(255),
    perform_at   TIMESTAMP WITHOUT TIME ZONE,
    details      VARCHAR(255),
    CONSTRAINT pk_audit_log PRIMARY KEY (id)
);

CREATE TABLE crew_member
(
    id                   UUID NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE,
    employee_id          VARCHAR(255),
    first_name           VARCHAR(255),
    last_name            VARCHAR(255),
    email                VARCHAR(255),
    phone_number         VARCHAR(255),
    role                 VARCHAR(255),
    status               VARCHAR(255),
    last_flight_end      TIMESTAMP WITHOUT TIME ZONE,
    total_flight_minutes INTEGER,
    version              BIGINT,
    CONSTRAINT pk_crew_member PRIMARY KEY (id)
);

CREATE TABLE flight
(
    id                    UUID         NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    flight_number         VARCHAR(10)  NOT NULL,
    departure_airport     VARCHAR(3)   NOT NULL,
    destination_airport   VARCHAR(3)   NOT NULL,
    departure_time        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    arrival_time          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    actual_departure_time TIMESTAMP WITHOUT TIME ZONE,
    actual_arrival_time   TIMESTAMP WITHOUT TIME ZONE,
    sequence_order        INTEGER,
    gate_number           VARCHAR(255),
    status                VARCHAR(255) NOT NULL,
    aircraft_id           UUID,
    parent_flight_id      UUID,
    version               BIGINT,
    CONSTRAINT pk_flight PRIMARY KEY (id)
);

CREATE TABLE flight_crew_assignment
(
    id                UUID NOT NULL,
    crew_member_id    UUID,
    role_on_flight    VARCHAR(255),
    flight_id         UUID,
    assignment_status VARCHAR(255),
    assigned_at       TIMESTAMP WITHOUT TIME ZONE,
    removed_at        TIMESTAMP WITHOUT TIME ZONE,
    removal_reason    VARCHAR(255),
    assigned_by_id    UUID,
    CONSTRAINT pk_flight_crew_assignment PRIMARY KEY (id)
);

CREATE TABLE flight_delay
(
    id                      UUID    NOT NULL,
    flight_id               UUID,
    delay_reason            VARCHAR(255),
    delay_reason_detail     VARCHAR(255),
    original_departure_time TIMESTAMP WITHOUT TIME ZONE,
    new_departure_time      TIMESTAMP WITHOUT TIME ZONE,
    new_arrival_time        TIMESTAMP WITHOUT TIME ZONE,
    delay_minutes           INTEGER,
    is_high_risk            BOOLEAN NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE,
    reported_by_id          UUID,
    CONSTRAINT pk_flight_delay PRIMARY KEY (id)
);

CREATE TABLE flight_status_history
(
    id            UUID NOT NULL,
    flight_id     UUID,
    old_status    VARCHAR(255),
    new_status    VARCHAR(255),
    change_reason VARCHAR(255),
    change_time   TIMESTAMP WITHOUT TIME ZONE,
    changed_by    VARCHAR(255),
    CONSTRAINT pk_flight_status_history PRIMARY KEY (id)
);

CREATE TABLE notification
(
    id          UUID    NOT NULL,
    type        VARCHAR(255),
    severity    VARCHAR(255),
    title       VARCHAR(255),
    message     VARCHAR(255),
    target_type VARCHAR(255),
    target_id   UUID,
    is_read     BOOLEAN NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    user_id     UUID,
    CONSTRAINT pk_notification PRIMARY KEY (id)
);

ALTER TABLE aircraft
    ADD CONSTRAINT uc_aircraft_registrationnumber UNIQUE (registration_number);

ALTER TABLE app_user
    ADD CONSTRAINT uc_app_user_email UNIQUE (email);

ALTER TABLE app_user
    ADD CONSTRAINT uc_app_user_username UNIQUE (username);

ALTER TABLE crew_member
    ADD CONSTRAINT uc_crew_member_email UNIQUE (email);

ALTER TABLE crew_member
    ADD CONSTRAINT uc_crew_member_employeeid UNIQUE (employee_id);

ALTER TABLE crew_member
    ADD CONSTRAINT uc_crew_member_phonenumber UNIQUE (phone_number);

ALTER TABLE flight_crew_assignment
    ADD CONSTRAINT uc_f26c9fa4b8742c29d00b63389 UNIQUE (flight_id, crew_member_id);

ALTER TABLE flight_crew_assignment
    ADD CONSTRAINT FK_FLIGHT_CREW_ASSIGNMENT_ON_ASSIGNEDBY FOREIGN KEY (assigned_by_id) REFERENCES app_user (id);

ALTER TABLE flight_crew_assignment
    ADD CONSTRAINT FK_FLIGHT_CREW_ASSIGNMENT_ON_CREWMEMBER FOREIGN KEY (crew_member_id) REFERENCES crew_member (id);

ALTER TABLE flight_crew_assignment
    ADD CONSTRAINT FK_FLIGHT_CREW_ASSIGNMENT_ON_FLIGHT FOREIGN KEY (flight_id) REFERENCES flight (id);

ALTER TABLE flight_delay
    ADD CONSTRAINT FK_FLIGHT_DELAY_ON_FLIGHT FOREIGN KEY (flight_id) REFERENCES flight (id);

ALTER TABLE flight_delay
    ADD CONSTRAINT FK_FLIGHT_DELAY_ON_REPORTEDBY FOREIGN KEY (reported_by_id) REFERENCES app_user (id);

ALTER TABLE flight
    ADD CONSTRAINT FK_FLIGHT_ON_AIRCRAFT FOREIGN KEY (aircraft_id) REFERENCES aircraft (id);

ALTER TABLE flight
    ADD CONSTRAINT FK_FLIGHT_ON_PARENT_FLIGHT FOREIGN KEY (parent_flight_id) REFERENCES flight (id);

ALTER TABLE flight_status_history
    ADD CONSTRAINT FK_FLIGHT_STATUS_HISTORY_ON_FLIGHT FOREIGN KEY (flight_id) REFERENCES flight (id);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_USER FOREIGN KEY (user_id) REFERENCES app_user (id);