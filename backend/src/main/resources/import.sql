-- Inserir dados iniciais para testes
INSERT INTO flight_plans (
    aircraft_registration, 
    aircraft_type, 
    departure_airport, 
    destination_airport, 
    departure_time, 
    estimated_arrival_time, 
    pilot_name, 
    flight_rules, 
    flight_type, 
    aircraft_color, 
    remarks, 
    fuel_on_board_hours, 
    number_of_passengers, 
    created_at, 
    updated_at
) VALUES 
('PT-ABC', 'C172', 'SBSP', 'SBGR', '2025-09-03 10:00:00', '2025-09-03 10:45:00', 'João Silva', 'VFR', 'TREINO', 'Branco e Azul', 'Voo de treinamento', 4, 1, NOW(), NOW()),
('PT-XYZ', 'PA28', 'SBGR', 'SBSP', '2025-09-03 11:30:00', '2025-09-03 12:15:00', 'Maria Oliveira', 'VFR', 'PARTICULAR', 'Vermelho', 'Voo particular', 5, 2, NOW(), NOW()),
('PP-123', 'C182', 'SBKP', 'SBGL', '2025-09-04 08:00:00', '2025-09-04 10:00:00', 'Carlos Santos', 'IFR', 'LINHA', 'Branco', 'Voo comercial', 6, 3, NOW(), NOW());
