--Falta añadir STOCK

INSERT INTO TIENDAS (nombre)
    VALUES ('Dulces sueños'), ('Descanso eterno'), ('Mimir'), ('Hora de dormir');


INSERT INTO ALMOHADAS (peso, altura, ancho, grosor, tacto, firmeza)
    VALUES
        (1500, 30, 90, 5, 'SUAVE', 'Blanda'),
        (1200, 25, 70, 2, 'MUY_SUAVE', 'Blanda'),
        (1600, 35, 95, 6, 'SUAVE', 'Media'),
        (1800, 30, 80, 5, 'NORMAL', 'Dura'),
        (2000, 40, 100, 4, 'RUGOSO', 'Dura'),
        (1300, 28, 75, 5, 'SUAVE', 'Blanda'),
        (1700, 32, 85, 6, 'NORMAL', 'Media');

-- Datos de ejemplo USUARIOS
-- Contraseña: Admin1
-- No está asociado a ninguna tienda
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Admin', 'Admin Admin', 'admin', 'admin@prueba.net',
        '$2a$10$vPaqZvZkz6jhb7U7k/V/v.5vprfNdOnh4sxi/qpPRkYTzPmFlI9p2');

insert into USER_ROLES (user_id, roles)
values (1, 'USER');
insert into USER_ROLES (user_id, roles)
values (1, 'ADMIN');

-- Contraseña: User1
insert into USUARIOS (nombre, apellidos, username, email, password, tienda_id)
values ('Jose', 'Jose User', 'jose', 'user@prueba.net',
        '$2a$12$RUq2ScW1Kiizu5K4gKoK4OTz80.DWaruhdyfi2lZCB.KeuXTBh0S.', 2);
insert into USER_ROLES (user_id, roles)
values (2, 'USER');

-- Contraseña: Test1
insert into USUARIOS (nombre, apellidos, username, email, password)
values ('Test', 'Test Test', 'test', 'test@prueba.net',
        '$2a$10$Pd1yyq2NowcsDf4Cpf/ZXObYFkcycswqHAqBndE1wWJvYwRxlb.Pu');
insert into USER_ROLES (user_id, roles)
values (2, 'USER');

-- Contraseña: Otro1
insert into USUARIOS (nombre, apellidos, username, email, password, tienda_id)
values ('María', 'María Otro', 'maría', 'otro@prueba.net',
        '$2a$12$3Q4.UZbvBMBEvIwwjGEjae/zrIr6S50NusUlBcCNmBd2382eyU0bS', 3);
insert into USER_ROLES (user_id, roles)
values (3, 'USER');