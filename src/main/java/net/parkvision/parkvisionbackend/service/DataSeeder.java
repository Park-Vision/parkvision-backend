package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.repository.*;
import org.springframework.stereotype.Service;


@Service
public class DataSeeder {
    private final DroneRepository _droneRepository;
    private final ParkingRepository _parkingRepository;

    private final ParkingSpotRepository _parkingSpotRepository;

    private final ParkingModeratorRepository _parkingModeratorRepository;

    private final PointRepository _pointRepository;


    public DataSeeder(DroneRepository _droneRepository, ParkingRepository _parkingRepository, ParkingSpotRepository parkingSpotRepository, ParkingModeratorRepository parkingModeratorRepository, PointRepository pointRepository) {
        this._droneRepository = _droneRepository;
        this._parkingRepository = _parkingRepository;
        _parkingSpotRepository = parkingSpotRepository;
        _parkingModeratorRepository = parkingModeratorRepository;
        _pointRepository = pointRepository;
    }

    public void seedData() {
        seedDroneData();
        seedParkingData();
        SeedParkingModeratorData();
        SeedParkingSpotData();
        SeedPoints();
    }


    public void seedDroneData() {
        long droneCount = _droneRepository.count();

        if (droneCount == 0) {
            System.out.println("seedDroneData()");

            Drone drone1 = new Drone();
            drone1.setName("DJI Mavic Mini");
            drone1.setModel("Mavic Mini");
            drone1.setSerialNumber("123456789");
            _droneRepository.save(drone1);

            Drone drone2 = new Drone();
            drone2.setName("DJI Mavic PRO 2");
            drone2.setModel("Mavic PRO 2");
            drone2.setSerialNumber("987654321");
            _droneRepository.save(drone2);

            Drone drone3 = new Drone();
            drone3.setName("DJI Mavic Air 2");
            drone3.setModel("Mavic Air 2");
            drone3.setSerialNumber("123123123");
            _droneRepository.save(drone3);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }
    }

    public void seedParkingData() {

        long parkingCount = _parkingRepository.count();

        if (parkingCount == 0) {
            System.out.println("seedParkingData()");

            Parking parking1 = new Parking();
            parking1.setName("Parking Magnolia");
            parking1.setAddress("ul. Magnoliowa 1, 00-000 Wrocław");
            parking1.setDescription("Parking Magnolia to parking znajdujący się w centrum Wrocławia. " +
                    "Posiada 100 miejsc parkingowych, w tym 5 miejsc dla osób niepełnosprawnych. " +
                    "Parking jest monitorowany przez 24 godziny na dobę.");
            parking1.setOpenHours("6:00 - 22:00");
            parking1.setCostRate(2.5);
            _parkingRepository.save(parking1);

            Parking parking2 = new Parking();
            parking2.setName("Parking Rondo");
            parking2.setAddress("ul. Rondo 1, 00-000 Wrocław");
            parking2.setDescription("Parking Rondo to parking znajdujący się w centrum Wrocławia. " +
                    "Posiada 50 miejsc parkingowych, w tym 2 miejsca dla osób niepełnosprawnych. " +
                    "Parking jest monitorowany przez 24 godziny na dobę.");
            parking2.setOpenHours("4:30 - 23:00");
            parking2.setCostRate(3.0);
            _parkingRepository.save(parking2);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }
    }

    public void SeedParkingModeratorData() {
        long parkingModeratorCount = _parkingModeratorRepository.count();

        if (parkingModeratorCount == 0) {
            System.out.println("SeedParkingModeratorData()");

            ParkingModerator parkingModerator1 = new ParkingModerator();
            parkingModerator1.setFirstname("Jan");
            parkingModerator1.setLastname("Kowalski");
            parkingModerator1.setEmail("jan.k@onet.pl");
            parkingModerator1.setPassword("123456");
            parkingModerator1.setParking(_parkingRepository.getReferenceById(1L));
            parkingModerator1.setRole(Role.USER);
            _parkingModeratorRepository.save(parkingModerator1);

            ParkingModerator parkingModerator2 = new ParkingModerator();
            parkingModerator2.setFirstname("Anna");
            parkingModerator2.setLastname("Nowak");
            parkingModerator2.setEmail("anna.n@onet.pl");
            parkingModerator2.setPassword("123456");
            parkingModerator2.setParking(_parkingRepository.getReferenceById(2L));
            parkingModerator2.setRole(Role.PARKING_MANAGER);
            _parkingModeratorRepository.save(parkingModerator2);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }
    }


    public void SeedParkingSpotData() {
        long parkingSpotCount = _parkingSpotRepository.count();

        if (parkingSpotCount == 0) {
            System.out.println("SeedParkingSpotData()");

            Parking parking1 = _parkingRepository.getReferenceById(1L);

            ParkingSpot parkingSpot1 = new ParkingSpot();
            parkingSpot1.setSpotNumber("1");
            parkingSpot1.setActive(true);
            parkingSpot1.setOccupied(false);
            parkingSpot1.setParking(parking1);

            _parkingSpotRepository.save(parkingSpot1);

            ParkingSpot parkingSpot2 = new ParkingSpot();
            parkingSpot2.setSpotNumber("2");
            parkingSpot2.setActive(true);
            parkingSpot2.setOccupied(false);
            parkingSpot2.setParking(parking1);

            _parkingSpotRepository.save(parkingSpot2);

        } else {
            System.out.println("Data already exists.");
        }
    }

    public void SeedPoints() {
        long pointCount = _pointRepository.count();

        if (pointCount == 0) {
            System.out.println("SeedPoints()");

            ParkingSpot parkingSpot1 = _parkingSpotRepository.getReferenceById(1L);

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
            _pointRepository.save(point4);

            ParkingSpot parkingSpot2 = _parkingSpotRepository.getReferenceById(2L);

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
    }
}
