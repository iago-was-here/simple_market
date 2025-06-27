USE `simple_market-bd`;

CREATE TABLE tipo_usuario(
	id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(100)
);

CREATE TABLE usuario(
	id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(100) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    tipo_usuario_id INT NOT NULL,
    ativo TINYINT DEFAULT 1,
    data_cadastro DATETIME,
    KEY `fk_tipo_usuario_id` (`tipo_usuario_id`),
    CONSTRAINT `fk_tipo_usuario_id` FOREIGN KEY (`tipo_usuario_id`)
        REFERENCES `tipo_usuario` (`id`)
);

INSERT INTO tipo_usuario(description) values('caixa'), ('gerente');