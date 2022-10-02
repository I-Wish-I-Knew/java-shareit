CREATE TABLE IF NOT EXISTS users
(
    user_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    user_name
    VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    512
) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY
(
    user_id
),
    CONSTRAINT uq_user_email UNIQUE
(
    email
)
    );

CREATE TABLE IF NOT EXISTS items
(
    item_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    item_name
    VARCHAR
(
    255
) NOT NULL,
    description VARCHAR
(
    1000
) NOT NULL,
    owner_id BIGINT NOT NULL,
    available BOOLEAN NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY
(
    item_id
),
    CONSTRAINT fk_items_users FOREIGN KEY
(
    owner_id
) REFERENCES users
(
    user_id
)
    );

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    start_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    end_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    booker_id
    BIGINT
    NOT
    NULL,
    item_id
    BIGINT
    NOT
    NULL,
    status
    VARCHAR
    NOT
    NULL,
    CONSTRAINT
    pk_booking
    PRIMARY
    KEY
(
    booking_id
),
    CONSTRAINT fk_bookings_users FOREIGN KEY
(
    booker_id
) REFERENCES users
(
    user_id
),
    CONSTRAINT fk_bookings_items FOREIGN KEY
(
    item_id
) REFERENCES items
(
    item_id
)
    );

CREATE TABLE IF NOT EXISTS comments
(
    comment_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    text
    VARCHAR
    NOT
    NULL,
    author_id
    BIGINT
    NOT
    NULL,
    item_id
    BIGINT
    NOT
    NULL,
    created
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    CONSTRAINT
    pk_comment
    PRIMARY
    KEY
(
    comment_id
),
    CONSTRAINT fk_comments_users FOREIGN KEY
(
    author_id
) REFERENCES users
(
    user_id
),
    CONSTRAINT fk_comment_items FOREIGN KEY
(
    item_id
) REFERENCES items
(
    item_id
)
    );

