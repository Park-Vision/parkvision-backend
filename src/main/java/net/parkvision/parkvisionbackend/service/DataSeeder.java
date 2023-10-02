package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.repository.DroneRepository;
import net.parkvision.parkvisionbackend.repository.ParkingModeratorRepository;
import net.parkvision.parkvisionbackend.repository.ParkingRepository;
import net.parkvision.parkvisionbackend.repository.ParkingSpotRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataSeeder {
    private final DroneRepository _droneRepository;
    private final ParkingRepository _parkingRepository;

    private final ParkingSpotRepository _parkingSpotRepository;

    private final ParkingModeratorRepository _parkingModeratorRepository;


    public DataSeeder(DroneRepository _droneRepository, ParkingRepository _parkingRepository, ParkingSpotRepository parkingSpotRepository, ParkingModeratorRepository parkingModeratorRepository) {
        this._droneRepository = _droneRepository;
        this._parkingRepository = _parkingRepository;
        _parkingSpotRepository = parkingSpotRepository;
        _parkingModeratorRepository = parkingModeratorRepository;
    }

    public void seedData() {
        seedDroneData();
        seedParkingData();
        SeedParkingModeratorData();
//        SeedParkingSpotData(); //todo add repository service and controller maybe...
    }


    public void seedDroneData() {
        long droneCount = _droneRepository.count();

        if (droneCount == 0) {
            System.out.println("Seeding data...");

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
            System.out.println("Seeding data...");

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
            System.out.println("Seeding data...");

            ParkingModerator parkingModerator1 = new ParkingModerator();
            parkingModerator1.setFirstname("Jan");
            parkingModerator1.setLastname("Kowalski");
            parkingModerator1.setEmail("jan.k@onet.pl");
            parkingModerator1.setPassword("123456");
            parkingModerator1.setParking(_parkingRepository.getReferenceById(1L));
            parkingModerator1.setRole(Role.PARKING_MANAGER);
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
            System.out.println("Seeding data...");

            Parking parking1 = _parkingRepository.getReferenceById(1L);

            ParkingSpot parkingSpot1 = new ParkingSpot();
            parkingSpot1.setSpotNumber("1");
            parkingSpot1.setActive(true);
            parkingSpot1.setOccupied(false);
            parkingSpot1.setParking(parking1);
            List<Point> points = new ArrayList<>();


            // For example, create some sample points for demonstration purposes
            points.add(new Point(1L, 51.11004209878706, 17.059438251268123));
            points.add(new Point(2L, 51.1100815908722, 17.059408051544636));
            points.add(new Point(3L, 51.1100931271854, 17.059440904600674));
            points.add(new Point(4L, 51.11005154402975, 17.05946944677171));
            parkingSpot1.setPoints(points);
            _parkingSpotRepository.save(parkingSpot1);

            ParkingSpot parkingSpot2 = new ParkingSpot();
            parkingSpot2.setSpotNumber("2");
            parkingSpot2.setActive(true);
            parkingSpot2.setOccupied(false);
            parkingSpot2.setParking(parking1);
            List<Point> points2 = new ArrayList<>();

            points2.add(new Point(5L, 51.109954944636904, 17.059500184666277));
            points2.add(new Point(6L, 51.10999477283431, 17.059470253075084));
            points2.add(new Point(7L, 51.11000116835047, 17.059502890077006));
            points2.add(new Point(8L, 51.10996302172416, 17.05953245609047));
            parkingSpot2.setPoints(points2);
            _parkingSpotRepository.save(parkingSpot2);

        } else {
            System.out.println("Data already exists.");
        }


    }
}
