CREATE TABLE IF NOT EXISTS posts (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    id_autor INT REFERENCES funcionarios(id) ON DELETE CASCADE,
    titulo VARCHAR(50) NOT NULL,
    imagem_post VARCHAR(255),
    data_publicacao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    conteudo TEXT,
    CONSTRAINT can_post CHECK (is_tecnico(id_autor))
);

CREATE TABLE IF NOT EXISTS comentarios (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    id_post INT REFERENCES posts(id) ON DELETE CASCADE,
    id_autor INT REFERENCES funcionarios(id) ON DELETE CASCADE,
    conteudo TEXT NOT NULL
);
