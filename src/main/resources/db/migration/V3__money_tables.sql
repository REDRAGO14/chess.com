CREATE TABLE wallets (
                         id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id     BIGINT NOT NULL UNIQUE,
                         balance     DECIMAL(19,2) NOT NULL DEFAULT 0.00,
                         version     BIGINT NOT NULL DEFAULT 0,
                         created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
                         updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id),
                         CONSTRAINT chk_wallet_balance CHECK (balance >= 0)
);

CREATE TABLE wallet_audit_log (
                                  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  wallet_id     BIGINT NOT NULL,
                                  type          VARCHAR(20) NOT NULL,
                                  amount        DECIMAL(19,2) NOT NULL,
                                  balance_after DECIMAL(19,2) NOT NULL,
                                  reference     VARCHAR(100) NOT NULL,
                                  created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT fk_audit_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id),
                                  CONSTRAINT uq_audit_type_ref UNIQUE (type, reference)
);

CREATE TABLE match_escrow (
                              id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                              game_id     BIGINT NOT NULL,
                              user_id     BIGINT NOT NULL,
                              amount      DECIMAL(19,2) NOT NULL,
                              status      VARCHAR(20) NOT NULL DEFAULT 'LOCKED',
                              created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT uq_escrow_game_user UNIQUE (game_id, user_id),
                              CONSTRAINT fk_escrow_game FOREIGN KEY (game_id) REFERENCES games(id),
                              CONSTRAINT fk_escrow_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE payment_transactions (
                                      id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      user_id      BIGINT NOT NULL,
                                      tx_ref       VARCHAR(100) NOT NULL UNIQUE,
                                      amount       DECIMAL(19,2) NOT NULL,
                                      currency     VARCHAR(10) NOT NULL DEFAULT 'ETB',
                                      status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                      checkout_url VARCHAR(500),
                                      version      BIGINT NOT NULL DEFAULT 0,
                                      created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_pay_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE manual_deposits (
                                 id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id        BIGINT NOT NULL,
                                 amount         DECIMAL(19,2) NOT NULL,
                                 bank_reference VARCHAR(100),
                                 receipt_file   VARCHAR(100) NOT NULL,
                                 status         VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                                 admin_note     VARCHAR(255),
                                 reviewed_by    BIGINT NULL,
                                 version        BIGINT NOT NULL DEFAULT 0,
                                 created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 reviewed_at    DATETIME NULL,
                                 CONSTRAINT fk_dep_user FOREIGN KEY (user_id) REFERENCES users(id)
);