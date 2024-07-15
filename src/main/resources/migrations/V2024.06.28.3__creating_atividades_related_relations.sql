CREATE TYPE tipo_sala AS ENUM ('INDIVIDUAL', 'GRUPO', 'INFANTIL');

CREATE TABLE salas(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nome VARCHAR(25) UNIQUE,
    tipo tipo_sala
);

CREATE TABLE atividades(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid UUID default uuid_generate_v4() UNIQUE,
    id_sala INT REFERENCES salas(id),
    tempo_inicio TIMESTAMP NOT NULL,
    tempo_fim TIMESTAMP NOT NULL,
    CONSTRAINT tempo_valido CHECK (tempo_inicio < atividades.tempo_fim)
);