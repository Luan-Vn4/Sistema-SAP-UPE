CREATE VIEW estagiario_supervisor AS
    SELECT est.id est_id, est.uid est_uid, est.nome est_nome, est.sobrenome est_sobrenome,
           est.email est_email, est.senha est_senha, est.url_imagem est_url_imagem,
           est.is_tecnico est_is_tecnico, est.is_ativo est_is_ativo,
           sup.id sup_id, sup.uid sup_uid, sup.nome sup_nome, sup.sobrenome sup_sobrenome,
           sup.email sup_email, sup.senha sup_senha, sup.url_imagem sup_url_imagem,
           sup.is_tecnico sup_is_tecnico, sup.is_ativo sup_is_ativo
    FROM funcionarios est
        INNER JOIN funcionarios sup ON (est.id, sup.id) IN
            (SELECT id_estagiario, id_supervisor FROM supervisoes);