CREATE TABLE atendimentos_individuais(
    id INT PRIMARY KEY REFERENCES atividades(id),
    id_ficha INT REFERENCES fichas(id),
    id_terapeuta INT REFERENCES funcionarios(id)
);

