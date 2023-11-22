package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.*;


@Service
public class DataSeeder {
    private final DroneRepository _droneRepository;
    private final ParkingRepository _parkingRepository;

    private final ParkingSpotRepository _parkingSpotRepository;

    private final ParkingManagerRepository _parkingManagerRepository;

    private final PointRepository _pointRepository;
    private final ClientRepository _clientRepository;
    private final CarRepository _carRepository;
    private final ReservationRepository _reservationRepository;


    public DataSeeder(DroneRepository _droneRepository, ParkingRepository _parkingRepository,
                      ParkingSpotRepository parkingSpotRepository,
                      ParkingManagerRepository parkingManagerRepository, PointRepository pointRepository,
                      ClientRepository clientRepository, CarRepository carRepository, ReservationRepository reservationRepository) {
        this._droneRepository = _droneRepository;
        this._parkingRepository = _parkingRepository;
        _parkingSpotRepository = parkingSpotRepository;
        _parkingManagerRepository = parkingManagerRepository;
        _pointRepository = pointRepository;
        _clientRepository = clientRepository;
        _carRepository = carRepository;
        _reservationRepository = reservationRepository;
    }

    public void seedData() {
        Parking parking1 = new Parking();
        Parking parking2 = new Parking();
        Parking parking3 = new Parking();

        long parkingCount = _parkingRepository.count();

        if (parkingCount == 0) {
            System.out.println("seedParkingData()");

            parking1.setName("Magnolia Park");
            parking1.setCity("Wrocław");
            parking1.setStreet("Legnicka 58");
            parking1.setZipCode("54-204");
            parking1.setDescription("Parking Magnolia Park to parking znajdujący się w centrum Wrocławia. " +
                    "Posiada 100 miejsc parkingowych, w tym 5 miejsc dla osób niepełnosprawnych. " +
                    "Parking jest monitorowany przez 24 godziny na dobę.");
            parking1.setStartTime(OffsetTime.of(6,0,0,0, ZoneOffset.of("+1")));
            parking1.setEndTime(OffsetTime.of(22,0,0,0, ZoneOffset.of("+1")));
            parking1.setCostRate(2.5);
            parking1.setLongitude(16.990429400497433);
            parking1.setLatitude(51.11818354620572);
            parking1.setTimeZone(ZoneOffset.of("+1"));
            parking1.setCurrency("PLN");
            _parkingRepository.save(parking1);

            parking2.setName("D20 - Politechnika Wrocławska");
            parking2.setCity("Wrocław");
            parking2.setStreet("Janiszewskiego 8");
            parking2.setZipCode("50-372");
            parking2.setDescription("Parking D20 to parking dla studentów i pracowników Politechniki Wrocławskiej. " +
                    "Posiada 50 miejsc parkingowych, w tym 2 miejsca dla osób niepełnosprawnych. " +
                    "Parking jest monitorowany przez 24 godziny na dobę.");
            parking2.setStartTime(OffsetTime.of(4,30,0,0, ZoneOffset.of("-3")));
            parking2.setEndTime(OffsetTime.of(21,0,0,0, ZoneOffset.of("-3")));
            parking2.setCostRate(3.0);
            parking2.setLatitude(51.10975855141324);
            parking2.setLongitude(17.059114686292222);
            parking2.setTimeZone(ZoneOffset.of("-3"));
            parking2.setCurrency("EUR");
            _parkingRepository.save(parking2);

            parking3.setName("Parking Wrońskiego");
            parking3.setCity("Wrocław");
            parking3.setStreet("Wrońskiego 1");
            parking3.setZipCode("50-376");
            parking3.setDescription("Parking Wrońskiego to parking Politechniki Wrocławskiej dla studentów i pracowników" +
                    " Politechniki Wrocławskiej. Posiada 50 miejsc parkingowych, w tym 2 miejsca dla osób niepełnosprawnych.");
            parking3.setStartTime(OffsetTime.of(6,30,0,0, ZoneOffset.of("-2")));
            parking3.setEndTime(OffsetTime.of(19,0,0,0, ZoneOffset.of("-2")));
            parking3.setCostRate(2.0);
            parking3.setLatitude(51.108915212046774);
            parking3.setLongitude(17.05562300818793);
            parking3.setTimeZone(ZoneOffset.of("-2"));
            parking2.setCurrency("PLN");
            _parkingRepository.save(parking3);


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


        long parkingManagerCount = _parkingManagerRepository.count();

        if (parkingManagerCount == 0) {
            System.out.println("SeedParkingManagerData()");

            ParkingManager parkingManager1 = new ParkingManager();

            parkingManager1.setFirstname("Anna");
            parkingManager1.setLastname("Nowak");
            parkingManager1.setEmail("Pmod@pv.pl");
            parkingManager1.setPassword(new BCryptPasswordEncoder().encode("Pmod123!"));
//            parkingManager1.setParking(_parkingRepository.getReferenceById(1L));
            parkingManager1.setRole(Role.PARKING_MANAGER);
            _parkingManagerRepository.save(parkingManager1);

            System.out.println("Added: " + parkingManager1.getEmail() + " " + "Pmod123");

            ParkingManager parkingManager2 = new ParkingManager();

            parkingManager2.setFirstname("Jan");
            parkingManager2.setLastname("Nowak");
            parkingManager2.setEmail("Pmod2@pv.pl");
            parkingManager2.setPassword(new BCryptPasswordEncoder().encode("Pmod123!"));
            parkingManager2.setParking(_parkingRepository.getReferenceById(1L));
            parkingManager2.setRole(Role.PARKING_MANAGER);
            _parkingManagerRepository.save(parkingManager2);

            System.out.println("Added: " + parkingManager2.getEmail() + " " + "Pmod123!");

            ParkingManager parkingManager3 = new ParkingManager();

            parkingManager3.setFirstname("Swagger");
            parkingManager3.setLastname("ParkingManager");
            parkingManager3.setEmail("string");
            parkingManager3.setPassword(new BCryptPasswordEncoder().encode("string"));
            parkingManager3.setParking(_parkingRepository.getReferenceById(3L));
            parkingManager3.setRole(Role.PARKING_MANAGER);
            _parkingManagerRepository.save(parkingManager3);

            System.out.println("Added: " + parkingManager3.getEmail() + " " + "Pmod123!");
            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }

        Client client = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        Client client4 = new Client();

        long clientCount = _clientRepository.count();

        if (clientCount == 0) {
            System.out.println("SeedClientData()");

            client.setFirstname("Annaa");
            client.setLastname("Nowaka");
            client.setEmail("anna@onet.pl");
            client.setPassword(new BCryptPasswordEncoder().encode("123456"));
            client.setRole(Role.USER);
            _clientRepository.save(client);

            client2.setFirstname("Jan");
            client2.setLastname("Kowalski");
            client2.setEmail("macki2708@gmail.com");
            client2.setPassword(new BCryptPasswordEncoder().encode("Macki123!"));
            client2.setRole(Role.USER);
            _clientRepository.save(client2);

            client3.setFirstname("filip");
            client3.setLastname("strózik");
            client3.setEmail("filipshelby@gmail.com");
            client3.setPassword(new BCryptPasswordEncoder().encode("Filip123!"));
            client3.setRole(Role.USER);
            _clientRepository.save(client3);

            client4.setFirstname("Weronika");
            client4.setLastname("Litkowska");
            client4.setEmail("weronika.lit0@gmail.com");
            client4.setPassword(new BCryptPasswordEncoder().encode("Test1234"));
            client4.setRole(Role.USER);
            _clientRepository.save(client4);



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
            parkingSpot1.setParking(parking1);

            _parkingSpotRepository.save(parkingSpot1);


            parkingSpot2.setSpotNumber("2");
            parkingSpot2.setActive(true);
            parkingSpot2.setParking(parking1);

            _parkingSpotRepository.save(parkingSpot2);

        } else {
            System.out.println("Data already exists.");
        }
        long pointCount = _pointRepository.count();

        if (pointCount == 0) {
            System.out.println("SeedPoints()");

            Point point1 = new Point();
            point1.setLatitude(51.1186521304551);
            point1.setLongitude(16.990243087061152);
            point1.setParkingSpot(parkingSpot1);
            _pointRepository.save(point1);

            Point point2 = new Point();
            point2.setLatitude(51.118680641612244);
            point2.setLongitude(16.99028263711841);
            point2.setParkingSpot(parkingSpot1);
            _pointRepository.save(point2);

            Point point3 = new Point();
            point3.setLatitude(51.11866831521476);
            point3.setLongitude(16.99030332627968);
            point3.setParkingSpot(parkingSpot1);
            _pointRepository.save(point3);

            Point point4 = new Point();
            point4.setLatitude(51.118643381038375);
            point4.setLongitude(16.99026614904272);
            point4.setParkingSpot(parkingSpot1);
            _pointRepository.save(point4);


            Point point5 = new Point();
            point5.setLatitude(51.11861171066726);
            point5.setLongitude(16.99032095102528);
            point5.setParkingSpot(parkingSpot2);
            _pointRepository.save(point5);

            Point point6 = new Point();
            point6.setLatitude(51.118633203523125);
            point6.setLongitude(16.990356220713984);
            point6.setParkingSpot(parkingSpot2);
            _pointRepository.save(point6);

            Point point7 = new Point();
            point7.setLatitude(51.11862002873555);
            point7.setLongitude(16.99038103386446);
            point7.setParkingSpot(parkingSpot2);
            _pointRepository.save(point7);

            Point point8 = new Point();
            point8.setLatitude(51.1185928473759);
            point8.setLongitude(16.99034544418599);
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
            car2.setClient(client2);
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
            reservation.setStartDate(OffsetDateTime.now());
            reservation.setEndDate(OffsetDateTime.now().plusHours(1));
            reservation.setUser(client);
            reservation.setRegistrationNumber("123");
            _reservationRepository.save(reservation);

            reservation1.setParkingSpot(parkingSpot2);
            OffsetDateTime utc = OffsetDateTime.now(ZoneId.of("UTC"));
            System.out.println(utc);
            OffsetDateTime warsaw = utc.withOffsetSameInstant(ZoneOffset.of("+1"));
            System.out.println(warsaw);
            reservation1.setStartDate(warsaw);
            reservation1.setEndDate(warsaw.plusHours(1));
            reservation1.setUser(client);
            reservation1.setRegistrationNumber("123");
            _reservationRepository.save(reservation1);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }
    }
}
