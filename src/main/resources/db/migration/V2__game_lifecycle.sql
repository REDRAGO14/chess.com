RENAME TABLE game TO games;

ALTER TABLE games
    ADD COLUMN white_player_id BIGINT NULL,
    ADD COLUMN black_player_id BIGINT NULL,
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    ADD COLUMN fen VARCHAR(100) NOT NULL DEFAULT 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
    ADD CONSTRAINT fk_game_white FOREIGN KEY (white_player_id) REFERENCES user(id),
    ADD CONSTRAINT fk_game_black FOREIGN KEY (black_player_id) REFERENCES user(id);
