-- Assumindo tabela: portal(id bigserial pk, type varchar, name varchar)

insert into portal (type, name) values
('QUICK_MAINTENANCE',  'Box Rápido - Mooca'),
('SLOW_MAINTENANCE',   'Oficina Completa - Lapa'),
('POLICE_REPORT',      'Central BO - Guarulhos'),
('RECOVERED_MOTORCYCLE','Pátio Recuperadas - Barueri'),
('QUICK_MAINTENANCE',  'Box Rápido - Santo André');

-- Assumindo tabela: motorcycle(
--   id bigserial, type varchar, license_plate varchar, chassi varchar, rfid varchar,
--   portal bigint, problem_description varchar(500), entry_date date, availability_forecast date
-- )

insert into motorcycle
(type, license_plate, chassi, rfid, portal, problem_description, entry_date, availability_forecast) values
('MOTTU_SPORT_110I', 'ABC1234',  '9BWZZZ377VT004251', 'RFID-0001', 1, 'Troca de óleo e revisão expressa', '2025-09-28', '2025-10-02'),
('MOTTU_E',          'XYZ 9A22', 'WBANV93579C123456', 'RFID-0002', 2, 'Ruído no freio dianteiro em baixa velocidade', '2025-09-20', '2025-10-05'),
('MOTTU_POP',        'KLM2025',  '3FAHP0HA6AR123789', 'RFID-0003', 3, 'Registro de ocorrência e vistoria inicial', '2025-09-15', '2025-10-10'),
('MOTTU_SPORT_110I', 'DEF5678',  'JH4KA8260MC012345', 'RFID-0004', 1, 'Revisão de 5.000 km e checklist', '2025-09-30', '2025-10-01'),
('MOTTU_E',          'GHI9012',  '1HGCM82633A004352', 'RFID-0005', 2, 'Substituição de pastilhas e alinhamento', '2025-09-25', '2025-10-06'),
('MOTTU_POP',        'POP3321',  'WDBUF56X98B123654', 'RFID-0006', 4, 'Moto recuperada — inspeção completa', '2025-09-12', '2025-10-08'),
('MOTTU_E',          'SBC7777',  'SALSF2D44BA123321', 'RFID-0007', 2, 'Troca de fluido de freio e reapertos', '2025-09-22', '2025-10-03'),
('MOTTU_SPORT_110I', 'JKA 8899', '5NPEB4AC2CH123998', 'RFID-0008', 1, 'Vazamento leve na suspensão dianteira', '2025-09-18', '2025-10-04'),
('MOTTU_POP',        'CAR2025',  '2HGES16555H123741', 'RFID-0009', 3, 'Vistoria pós-ocorrência e laudo', '2025-09-10', '2025-10-12'),
('MOTTU_E',          'CT 5500',  'JTDKN3DU6A0123654', 'RFID-0010', 5, 'Inspeção elétrica e teste de rodagem', '2025-09-26', '2025-10-07');
