CREATE TABLE IF NOT EXISTS grupos_estudo(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid UUID DEFAULT uuid_generate_v4(),
    tema VARCHAR(100) NOT NULL
);

CREATE OR REPLACE function pode_participar_grupo_estudo(id_funcionario INT, id_encontro INT)
    RETURNS boolean AS $$
    declare is_participante boolean;
    declare id_grupo_estudo INT;
BEGIN
    SELECT id_grupo_estudo FROM encontros WHERE id = id_encontro
    LIMIT 1 INTO id_grupo_estudo;
    SELECT id_funcionario IN (SELECT id_participante FROM participacao_grupos_estudo
        WHERE participacao_grupos_estudo.id_grupo_estudo=id_grupo_estudo) INTO is_participante;
    RETURN is_participante;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS participacao_grupos_estudo(
    id_grupo_estudo INT REFERENCES grupos_estudo(id),
    id_participante INT REFERENCES funcionarios(id),
    CONSTRAINT pk_participacoes_grupos_estudo
        PRIMARY KEY (id_grupo_estudo, id_participante)
);

CREATE TABLE IF NOT EXISTS encontros(
    id INT PRIMARY KEY REFERENCES atividades(id),
    id_grupo_estudo INT REFERENCES grupos_estudo(id)
);

CREATE TABLE IF NOT EXISTS comparecimento_encontros(
    id_encontro INT REFERENCES encontros(id),
    id_participante INT REFERENCES funcionarios(id),
    CONSTRAINT pk_comparecimentos_encontros PRIMARY KEY (id_encontro, id_participante),
    CONSTRAINT pode_participar CHECK (pode_participar_grupo_estudo(id_participante, id_encontro))
);