CREATE TABLE atendimentos_individuais(
    id INT PRIMARY KEY REFERENCES atividades(id),
    id_ficha VARCHAR(10) REFERENCES fichas(id),
    id_terapeuta INT REFERENCES funcionarios(id)
);

