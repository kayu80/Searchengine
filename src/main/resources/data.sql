
CREATE TABLE IF NOT EXISTS site (
                                    id BIGSERIAL PRIMARY KEY,
                                    url VARCHAR(2048) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'FAILED',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_error TEXT
    );


CREATE TABLE IF NOT EXISTS page (
                                    id BIGSERIAL PRIMARY KEY,
                                    site_id BIGINT NOT NULL REFERENCES site(id) ON DELETE CASCADE,
    path VARCHAR(2048) NOT NULL,
    code INT,
    content TEXT NOT NULL,
    content_hash VARCHAR(64),
    UNIQUE (site_id, path)
    );


CREATE TABLE IF NOT EXISTS lemma (
                                     id BIGSERIAL PRIMARY KEY,
                                     lemma VARCHAR(255) NOT NULL UNIQUE,
    frequency INT NOT NULL DEFAULT 1
    );

CREATE TABLE IF NOT EXISTS index (
                                     id BIGSERIAL PRIMARY KEY,
                                     page_id BIGINT NOT NULL REFERENCES page(id) ON DELETE CASCADE,
    lemma_id BIGINT NOT NULL REFERENCES lemma(id) ON DELETE CASCADE,
    rank FLOAT NOT NULL,
    UNIQUE (page_id, lemma_id)
    );
