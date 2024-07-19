CREATE TABLE atendimentos_individuais(
    id INT PRIMARY KEY REFERENCES atividades(id) ON DELETE CASCADE,
    id_ficha INT REFERENCES fichas(id) ON DELETE CASCADE,
    id_terapeuta INT REFERENCES funcionarios(id) ON DELETE CASCADE
);

