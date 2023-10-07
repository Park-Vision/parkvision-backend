

-- disable reference integrity checks
SET CONSTRAINTS ALL DEFERRED;

insert into parking (cost_rate, id, address, description, name, open_hours)
values (2.50, 1, 'ul. Magnoliowa 1, 00-000 Wrocław', 'Parking Magnolia to parking znajdujący się w centrum Wrocławia.', 'Parking Magnolia', '6:00 - 22:00'),
       (3.80, 2, 'ul. Rondo 1, 00-000 Wrocław', 'Parking Rondo to parking znajdujący się w centrum Wrocławia.', 'Parking Rondo', '0:00 - 23:59');

insert into parking_spot (active, occupied, id, parking_id, spot_number)
values (true, false, 1, 1, 1),
       (true, false, 2, 1, 2),
       (true, false, 3, 1, 3),
       (true, false, 4, 1, 4),
       (true, false, 5, 2, 5);

insert into point (latitude, longitude, id, parking_spot_id)
values (51.11004209878706, 17.059438251268123, 1, 1),
       (51.1100815908722, 17.059408051544636, 2, 1),
       (51.11008848686895, 17.05944068854656, 3, 1),
       (51.11004899478381, 17.059470888270047, 4, 1),
       (51.11004209878706, 17.059438251268123, 5, 2),
       (51.1100815908722, 17.059408051544636, 6, 2),
       (51.11008848686895, 17.05944068854656, 7, 2),
       (51.11004899478381, 17.059470888270047, 8, 2),
       (51.11004209878706, 17.059438251268123, 9, 3),
       (51.1100815908722, 17.059408051544636, 10, 3),
       (51.11008848686895, 17.05944068854656, 11, 3),
       (51.11004899478381, 17.059470888270047, 12, 3),
       (51.11004209878706, 17.059438251268123, 13, 4),
       (51.1100815908722, 17.059408051544636, 14, 4),
       (51.11008848686895, 17.05944068854656, 15, 4),
       (51.11004899478381, 17.059470888270047, 16, 4),
       (51.11004209878706, 17.059438251268123, 17, 5),
       (51.1100815908722, 17.059408051544636, 18, 5),
       (51.11008848686895, 17.05944068854656, 19, 5),
       (51.11004899478381, 17.059470888270047, 20, 5);



insert into drone (id, parking_id, model, name, serial_number)
values (1, 1, 'DJI Mavic 2 Pro', 'Dron 1', '123456789'),
       (2, 2, 'DJI Mavic Mini', 'Dron 2', '123456789');

insert into drone_mission (drone_id, id, mission_end_date, mission_start_date, parking_id, mission_description,
                           mission_name, mission_status)
values (1, 1, '2023-10-06T20:31:34Z', '2023-10-06T20:20:34Z', 1, 'Udana misja', 'Misja początkowa', 'COMPLETED'),
       (1, 2, '2023-10-06T20:31:34Z', '2023-10-06T20:20:34Z', 1, 'Udana misja', 'Misja koncowa', 'COMPLETED'),
       (1, 3, '2023-10-06T20:31:34Z', '2023-10-06T20:20:34Z', 2, 'Udana misja', 'Misja początkowa', 'COMPLETED'),
       (1, 4, '2023-10-06T20:31:34Z', '2023-10-06T20:20:34Z', 2, 'Udana misja', 'Misja koncowa', 'COMPLETED');


insert into _user (id, parking_id, dtype, email, firstname, lastname, password, role)
values (1, null, 'Client', 'jan.k@wp.pl', 'jan', 'kubik', '123456','USER'),
       (2, 1, 'ParkingModerator', 'anna.nowak@wp.pl', 'anna', 'nowak', '123456','PARKING_MANAGER');

insert into car (id, user_id, brand, color, registration_number)
values (1, 1, 'LEXUS', 'GREY', 'DLU13999'),
       (2, 1, 'BMW', 'BLACK', 'DW12X912');

insert into reservation (car_id, end_date, id, parking_spot_id, start_date, user_id, registration_number)
values (2, '2023-10-06T20:31:34Z', 1, 1, '2023-10-06T20:20:34Z', 1, 'DW12X912'),
       (1, '2023-10-06T20:31:34Z', 2, 2, '2023-10-06T20:20:34Z', 1, 'DLU13999'),
       (2, '2023-10-06T20:31:34Z', 3, 3, '2023-10-06T20:20:34Z', 1, 'DW12X912'),
       (1, '2023-10-06T20:31:34Z', 4, 4, '2023-10-06T20:20:34Z', 1, 'DLU13999'),
       (2, '2023-10-06T20:31:34Z', 5, 5, '2023-10-06T20:20:34Z', 1, 'DW12X912');

insert into payment (status, id, reservation_id)
values (1, 1, 1),
       (2, 2, 2),
       (0, 3, 3);

-- enable reference integrity checks
SET CONSTRAINTS ALL IMMEDIATE;
