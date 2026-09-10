CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_name VARCHAR(255),
                       password VARCHAR(255),
                       email VARCHAR(255),
                       created_at DATETIME,
                       updated_at DATETIME
);

CREATE TABLE game (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      created_at DATETIME
);

CREATE TABLE moves (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       game_id BIGINT NOT NULL,
                       player_id BIGINT NOT NULL,
                       move_number INT NOT NULL,
                       from_square VARCHAR(2) NOT NULL,
                       to_square VARCHAR(2) NOT NULL,
                       piece_moved VARCHAR(20) NOT NULL,
                       captured_piece VARCHAR(20),
                       created_at DATETIME,

                       CONSTRAINT fk_move_game
                           FOREIGN KEY (game_id) REFERENCES game(id),

                       CONSTRAINT fk_move_player
                           FOREIGN KEY (player_id) REFERENCES users(id),

                       CONSTRAINT uk_game_move_number
                           UNIQUE (game_id, move_number)
);