CREATE TABLE IF NOT EXISTS grupos_terapeuticos(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid UUID DEFAULT uuid_generate_v4() UNIQUE,
    id_dono INT REFERENCES funcionarios(id) NOT NULL,
    tema VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    CONSTRAINT is_tecnico_dono_grupo_terapeutico CHECK (is_tecnico(id_dono))
);

CREATE TABLE IF NOT EXISTS fichas(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid UUID DEFAULT uuid_generate_v4(),
    nome VARCHAR(25) NOT NULL,
    id_responsavel BIGINT REFERENCES funcionarios(id) ON DELETE CASCADE,
    id_grupo_terapeutico BIGINT REFERENCES grupos_terapeuticos(id) ON DELETE SET NULL
);

CREATE OR REPLACE function pode_coordenar(id_funcionario INT,
    id_atendimento_grupo INT) RETURNS boolean AS $$
        declare is_participante boolean;
        declare id_grupo INT;
        BEGIN
            SELECT atendimentos_grupo.id_grupo_terapeutico FROM atendimentos_grupo
            WHERE id = id_atendimento_grupo LIMIT 1 INTO id_grupo;
            SELECT id_funcionario IN (SELECT participacao_grupo_terapeutico.id_funcionario
                FROM participacao_grupo_terapeutico
                WHERE participacao_grupo_terapeutico.id_grupo_terapeutico=id_grupo)
            INTO is_participante;
            RETURN is_participante;
        END;
    $$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS atendimentos_grupo(
    id INT PRIMARY KEY REFERENCES atividades(id) ON DELETE CASCADE,
    id_grupo_terapeutico INT REFERENCES grupos_terapeuticos(id) ON DELETE CASCADE NOT NULL
);

CREATE OR REPLACE function ficha_pode_participar_atendimento_grupo(id_ficha INT,
    id_atendimento_grupo INT) RETURNS boolean AS $$
        declare is_participante boolean;
        declare this_id_grupo_terapeutico INT;
        BEGIN
            SELECT atendimentos_grupo.id_grupo_terapeutico FROM atendimentos_grupo
            WHERE atendimentos_grupo.id = id_atendimento_grupo LIMIT 1 INTO this_id_grupo_terapeutico;
            SELECT id_ficha IN (SELECT fichas.id FROM fichas WHERE
                fichas.id_grupo_terapeutico = this_id_grupo_terapeutico)
            INTO is_participante;
            RETURN is_participante;
        END;
    $$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS ficha_atendimento_grupo(
    id_ficha INT REFERENCES fichas(id) ON DELETE CASCADE,
    id_atendimento_grupo INT REFERENCES atendimentos_grupo(id) ON DELETE CASCADE,
    CONSTRAINT pk_ficha_atendimento_grupo PRIMARY KEY (id_ficha, id_atendimento_grupo),
    CONSTRAINT pode_participar CHECK
    (ficha_pode_participar_atendimento_grupo(id_ficha, id_atendimento_grupo))
);

CREATE TABLE IF NOT EXISTS coordenacao_atendimento_grupo(
    id_funcionario INT REFERENCES funcionarios(id) ON DELETE CASCADE,
    id_atendimento_grupo INT REFERENCES atendimentos_grupo(id) ON DELETE CASCADE,
    CONSTRAINT pk_coordenacao_atendimento_grupo
        PRIMARY KEY (id_funcionario, id_atendimento_grupo),
    CONSTRAINT participa_grupo_terapeutico CHECK
    (pode_coordenar(id_funcionario, id_atendimento_grupo))
);

CREATE TABLE IF NOT EXISTS participacao_grupo_terapeutico(
    id_funcionario INT REFERENCES funcionarios(id) ON DELETE CASCADE,
    id_grupo_terapeutico INT REFERENCES grupos_terapeuticos(id) ON DELETE CASCADE,
    CONSTRAINT pk_participacao_grupo_terapeutico
        PRIMARY KEY (id_funcionario, id_grupo_terapeutico)
);