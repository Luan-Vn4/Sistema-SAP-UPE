CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS funcionarios (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid UUID DEFAULT uuid_generate_v4() UNIQUE,
    nome VARCHAR(50) NOT NULL,
    sobrenome VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha CHAR(60) NOT NULL,
    is_tecnico BOOL NOT NULL DEFAULT FALSE,
    url_imagem VARCHAR(255),
    is_ativo BOOL NOT NULL DEFAULT TRUE
);

CREATE OR REPLACE function is_tecnico(id INT) RETURNS boolean
    LANGUAGE plpgsql AS $$
declare tecnico boolean;
BEGIN
    SELECT id IN (SELECT funcionarios.id FROM funcionarios WHERE is_tecnico=True) INTO tecnico;
    RETURN tecnico;
END;
$$;

CREATE TABLE IF NOT EXISTS supervisoes (
    id_supervisor INT NOT NULL REFERENCES funcionarios(id)
        CHECK (is_tecnico(id_supervisor)),
    id_estagiario INT NOT NULL REFERENCES funcionarios(id) ON DELETE CASCADE
        CHECK (NOT is_tecnico(id_estagiario)),
    CONSTRAINT pk_supervisor_estagiario PRIMARY KEY (id_supervisor, id_estagiario)
);