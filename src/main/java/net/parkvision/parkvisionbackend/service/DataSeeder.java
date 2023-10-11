package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class DataSeeder {
    private final DroneRepository _droneRepository;
    private final ParkingRepository _parkingRepository;

    private final ParkingSpotRepository _parkingSpotRepository;

    private final ParkingModeratorRepository _parkingModeratorRepository;

    private final PointRepository _pointRepository;
    private final ClientRepository _clientRepository;
    private final CarRepository _carRepository;
    private final ReservationRepository _reservationRepository;


    public DataSeeder(DroneRepository _droneRepository, ParkingRepository _parkingRepository,
                      ParkingSpotRepository parkingSpotRepository,
                      ParkingModeratorRepository parkingModeratorRepository, PointRepository pointRepository,
                      ClientRepository clientRepository, CarRepository carRepository, ReservationRepository reservationRepository) {
        this._droneRepository = _droneRepository;
        this._parkingRepository = _parkingRepository;
        _parkingSpotRepository = parkingSpotRepository;
        _parkingModeratorRepository = parkingModeratorRepository;
        _pointRepository = pointRepository;
        _clientRepository = clientRepository;
        _carRepository = carRepository;
        _reservationRepository = reservationRepository;
    }

    public void seedData() {
        Parking parking1 = new Parking();
        Parking parking2 = new Parking();
        long parkingCount = _parkingRepository.count();

        if (parkingCount == 0) {
            System.out.println("seedParkingData()");


            parking1.setName("Parking Magnolia");
            parking1.setAddress("ul. Magnoliowa 1, 00-000 Wrocław");
            parking1.setDescription("Parking Magnolia to parking znajdujący się w centrum Wrocławia. " +
                    "Posiada 100 miejsc parkingowych, w tym 5 miejsc dla osób niepełnosprawnych. " +
                    "Parking jest monitorowany przez 24 godziny na dobę.");
            parking1.setOpenHours("6:00 - 22:00");
            parking1.setCostRate(2.5);
            parking1.setLongitude(50.615788);
            parking1.setLatitude(19.532069);
            _parkingRepository.save(parking1);


            parking2.setName("Parking Rondo");
            parking2.setAddress("ul. Rondo 1, 00-000 Wrocław");
            parking2.setDescription("Parking Rondo to parking znajdujący się w centrum Wrocławia. " +
                    "Posiada 50 miejsc parkingowych, w tym 2 miejsca dla osób niepełnosprawnych. " +
                    "Parking jest monitorowany przez 24 godziny na dobę.");
            parking2.setOpenHours("4:30 - 23:00");
            parking2.setCostRate(3.0);
            parking1.setLongitude(50.615788);
            parking1.setLatitude(19.552069);
            _parkingRepository.save(parking2);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }


        long droneCount = _droneRepository.count();

        if (droneCount == 0) {

            System.out.println("seedDroneData()");

            Drone drone1 = new Drone();
            drone1.setName("DJI Mavic Mini");
            drone1.setModel("Mavic Mini");
            drone1.setSerialNumber("123456789");
            drone1.setParking(parking1);
            _droneRepository.save(drone1);

            Drone drone2 = new Drone();
            drone2.setName("DJI Mavic PRO 2");
            drone2.setModel("Mavic PRO 2");
            drone2.setSerialNumber("987654321");
            drone2.setParking(parking2);
            _droneRepository.save(drone2);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }

        ParkingModerator parkingModerator2 = new ParkingModerator();
        long parkingModeratorCount = _parkingModeratorRepository.count();

        if (parkingModeratorCount == 0) {
            System.out.println("SeedParkingModeratorData()");

            parkingModerator2.setFirstname("Anna");
            parkingModerator2.setLastname("Nowak");
            parkingModerator2.setEmail("anna.n@onet.pl");
            parkingModerator2.setPassword(new BCryptPasswordEncoder().encode("123456"));
            parkingModerator2.setParking(_parkingRepository.getReferenceById(1L));
            parkingModerator2.setRole(Role.PARKING_MANAGER);
            _parkingModeratorRepository.save(parkingModerator2);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }

        Client client = new Client();
        long clientCount = _clientRepository.count();

        if (clientCount == 0) {
            System.out.println("SeedClientData()");

            client.setFirstname("Annaa");
            client.setLastname("Nowaka");
            client.setEmail("anna.n@onet.pl");
            client.setPassword(new BCryptPasswordEncoder().encode("123456"));
            client.setRole(Role.USER);
            _clientRepository.save(client);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }

        long parkingSpotCount = _parkingSpotRepository.count();
        ParkingSpot parkingSpot1 = new ParkingSpot();
        ParkingSpot parkingSpot2 = new ParkingSpot();

        if (parkingSpotCount == 0) {
            System.out.println("SeedParkingSpotData()");

            parkingSpot1.setSpotNumber("1");
            parkingSpot1.setActive(true);
            parkingSpot1.setOccupied(false);
            parkingSpot1.setParking(parking1);

            _parkingSpotRepository.save(parkingSpot1);


            parkingSpot2.setSpotNumber("2");
            parkingSpot2.setActive(true);
            parkingSpot2.setOccupied(false);
            parkingSpot2.setParking(parking1);

            _parkingSpotRepository.save(parkingSpot2);

        } else {
            System.out.println("Data already exists.");
        }
        long pointCount = _pointRepository.count();

        if (pointCount == 0) {
            System.out.println("SeedPoints()");

            Point point1 = new Point();
            point1.setLatitude(51.11004209878706);
            point1.setLongitude(17.059438251268123);
            point1.setParkingSpot(parkingSpot1);
            _pointRepository.save(point1);

            Point point2 = new Point();
            point2.setLatitude(51.1100815908722);
            point2.setLongitude(17.059408051544636);
            point2.setParkingSpot(parkingSpot1);
            _pointRepository.save(point2);

            Point point3 = new Point();
            point3.setLatitude(51.11008848686895);
            point3.setLongitude(17.05944068854656);
            point3.setParkingSpot(parkingSpot1);
            _pointRepository.save(point3);

            Point point4 = new Point();
            point4.setLatitude(51.11005154402975);
            point4.setLongitude(17.05946944677171);
            point4.setParkingSpot(parkingSpot1);
            _pointRepository.save(point4);

            Point point5 = new Point();
            point5.setLatitude(51.11004209878706);
            point5.setLongitude(17.059438251268123);
            point5.setParkingSpot(parkingSpot2);
            _pointRepository.save(point5);

            Point point6 = new Point();
            point6.setLatitude(51.1100815908722);
            point6.setLongitude(17.059408051544636);
            point6.setParkingSpot(parkingSpot2);
            _pointRepository.save(point6);

            Point point7 = new Point();
            point7.setLatitude(51.11008848686895);
            point7.setLongitude(17.05944068854656);
            point7.setParkingSpot(parkingSpot2);
            _pointRepository.save(point7);

            Point point8 = new Point();
            point8.setLatitude(51.11005154402975);
            point8.setLongitude(17.05946944677171);
            point8.setParkingSpot(parkingSpot2);
            _pointRepository.save(point8);
        } else {
            System.out.println("Data already exists.");
        }

        long carCount = _carRepository.count();

        Car car1 = new Car();
        Car car2 = new Car();
        if (carCount == 0) {

            System.out.println("seedCarData()");


            car1.setBrand("123");
            car1.setColor("blue");
            car1.setClient(client);
            car1.setRegistrationNumber("345");

            _carRepository.save(car1);


            car2.setBrand("123");
            car2.setColor("blue");
            car2.setClient(client);
            car2.setRegistrationNumber("567");

            _carRepository.save(car2);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }

        long reservationCount = _reservationRepository.count();

        Reservation reservation = new Reservation();
        Reservation reservation1 = new Reservation();

        if (reservationCount == 0) {

            System.out.println("seedReservationData()");

            reservation.setParkingSpot(parkingSpot1);
            reservation.setStartDate(new Date());
            reservation.setEndDate(new Date());
            reservation.setUser(client);
            reservation.setRegistrationNumber("123");
            _reservationRepository.save(reservation);

            reservation1.setParkingSpot(parkingSpot2);
            reservation1.setStartDate(new Date());
            reservation1.setEndDate(new Date());
            reservation1.setUser(client);
            reservation1.setRegistrationNumber("123");
            _reservationRepository.save(reservation1);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }
    }
}
