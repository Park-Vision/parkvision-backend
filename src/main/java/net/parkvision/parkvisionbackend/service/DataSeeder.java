package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.*;
import net.parkvision.parkvisionbackend.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class DataSeeder {
    private final DroneRepository droneRepository;
    private final ParkingRepository parkingRepository;

    private final ParkingSpotRepository parkingSpotRepository;

    private final ParkingManagerRepository parkingManagerRepository;

    private final PointRepository pointRepository;
    private final ClientRepository clientRepository;
    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final AdminRepository adminRepository;


    public DataSeeder(DroneRepository droneRepository, ParkingRepository parkingRepository,
                      ParkingSpotRepository parkingSpotRepository,
                      ParkingManagerRepository parkingManagerRepository, PointRepository pointRepository,
                      ClientRepository clientRepository, CarRepository carRepository,
                      ReservationRepository reservationRepository, AdminRepository adminRepository) {
        this.droneRepository = droneRepository;
        this.parkingRepository = parkingRepository;
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingManagerRepository = parkingManagerRepository;
        this.pointRepository = pointRepository;
        this.clientRepository = clientRepository;
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
        this.adminRepository = adminRepository;
    }

    public void seedData() {
        Parking parking1 = new Parking();
        Parking parking2 = new Parking();
        Parking parking4 = new Parking();
        Parking parking3 = new Parking();
        Parking parking5 = new Parking();

        long parkingCount = parkingRepository.count();

        if (parkingCount == 0) {
            System.out.println("seedParkingData()");

            parking1.setName("Magnolia Park");
            parking1.setCity("Wrocław");
            parking1.setStreet("Legnicka 58");
            parking1.setZipCode("54-204");
            parking1.setDescription("Magnolia Park Parking is located in the center of Wrocław." +
                    " It has 100 parking spaces, including 5 spaces for disabled individuals.");
            parking1.setStartTime(OffsetTime.of(6, 0, 0, 0, ZoneOffset.of("+1")));
            parking1.setEndTime(OffsetTime.of(22, 0, 0, 0, ZoneOffset.of("+1")));
            parking1.setCostRate(2.5);
            parking1.setLongitude(16.990429400497433);
            parking1.setLatitude(51.11818354620572);
            parking1.setTimeZone(ZoneOffset.of("+1"));
            parking1.setCurrency("PLN");
            parkingRepository.save(parking1);

            parking2.setName("D20 - Politechnika Wrocławska");
            parking2.setCity("Wrocław");
            parking2.setStreet("Janiszewskiego 8");
            parking2.setZipCode("50-372");
            parking2.setDescription("D20 Parking is a parking facility for students and staff " +
                    "of Wrocław University of Science and Technology.");
            parking2.setStartTime(OffsetTime.of(4, 30, 0, 0, ZoneOffset.of("-3")));
            parking2.setEndTime(OffsetTime.of(21, 0, 0, 0, ZoneOffset.of("-3")));
            parking2.setCostRate(3.0);
            parking2.setLatitude(51.10975855141324);
            parking2.setLongitude(17.059114686292222);
            parking2.setTimeZone(ZoneOffset.of("-3"));
            parking2.setCurrency("EUR");
            parkingRepository.save(parking2);

            // Parking Polinka
            parking3.setCostRate(3.0);
            parking3.setEndTime(OffsetTime.of(20, 0, 0, 0, ZoneOffset.of("+01:00")));
            parking3.setLatitude(51.10749143103844);
            parking3.setLongitude(17.058374583595704);
            parking3.setStartTime(OffsetTime.of(3, 0, 0, 0, ZoneOffset.of("+01:00")));
            parking3.setTimeZone(ZoneOffset.of("+01:00"));
            parking3.setId(3L);
            parking3.setCity("Wrocław");
            parking3.setCurrency("PLN");
            parking3.setDescription("Parking of Wrocław University of Science and Technology at station " +
                    "No. 2 of the Polinka cable car.");
            parking3.setName("Parking Polinka");
            parking3.setStreet("Wybrzeże Stanisława Wyspiańskiego 27");
            parking3.setZipCode("50-370");

            parkingRepository.save(parking3);

            parking4.setName("Parking Wrońskiego");
            parking4.setCity("Wrocław");
            parking4.setStreet("Wrońskiego 1");
            parking4.setZipCode("50-376");
            parking4.setDescription("Wrońskiego Parking is the parking facility of Wrocław University " +
                    "of Science and Technology for students and staff. It has 50 parking spaces, " +
                    "including 2 spaces for disabled individuals.");
            parking4.setStartTime(OffsetTime.of(6, 30, 0, 0, ZoneOffset.of("-2")));
            parking4.setEndTime(OffsetTime.of(19, 0, 0, 0, ZoneOffset.of("-2")));
            parking4.setCostRate(2.0);
            parking4.setLatitude(51.108915212046774);
            parking4.setLongitude(17.05562300818793);
            parking4.setTimeZone(ZoneOffset.of("-2"));
            parking4.setCurrency("PLN");
            parkingRepository.save(parking4);

            parking5.setName("Zebra");
            parking5.setCity("Wrocław");
            parking5.setStreet("Rdestowa 22");
            parking5.setZipCode("54-530");
            parking5.setDescription("Parking Zebra is a parking facility that offers the possibility " +
                    "to reserve parking spaces 1000 meters from the Wrocław airport. " +
                    "It provides transportation services from the parking lot to the airport.");



            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }


        long droneCount = droneRepository.count();

        if (droneCount == 0) {

            System.out.println("seedDroneData()");

            Drone drone1 = new Drone();
            drone1.setName("DJI Mavic Mini");
            drone1.setModel("Mavic Mini");
            drone1.setSerialNumber("123456789");
            drone1.setParking(parking1);
            droneRepository.save(drone1);

            Drone drone2 = new Drone();
            drone2.setName("DJI Mavic PRO 2");
            drone2.setModel("Mavic PRO 2");
            drone2.setSerialNumber("987654321");
            drone2.setParking(parking2);
            droneRepository.save(drone2);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }

        long adminCount = adminRepository.count();

        if (adminCount == 0) {
            System.out.println("SeedAdminData()");

            User user = new User();
            user.setFirstname("Admin");
            user.setLastname("Moderator");
            user.setEmail("parkvision.info@gmail.com");
            user.setPassword(new BCryptPasswordEncoder().encode("Admin123!"));
            user.setRole(Role.ADMIN);

            System.out.println("Added: " + user.getEmail() + " " + "Admin123!");

            adminRepository.save(user);

        }

        long parkingManagerCount = parkingManagerRepository.count();

        if (parkingManagerCount == 0) {
            System.out.println("SeedParkingManagerData()");

            ParkingManager parkingManager1 = new ParkingManager();

            parkingManager1.setFirstname("Anna");
            parkingManager1.setLastname("Nowak");
            parkingManager1.setEmail("Pmod@pv.pl");
            parkingManager1.setPassword(new BCryptPasswordEncoder().encode("Pmod123!"));
//            parkingManager1.setParking(_parkingRepository.getReferenceById(1L));
            parkingManager1.setRole(Role.PARKING_MANAGER);
            parkingManagerRepository.save(parkingManager1);

            System.out.println("Added: " + parkingManager1.getEmail() + " " + "Pmod123");

            ParkingManager parkingManager2 = new ParkingManager();

            parkingManager2.setFirstname("Jan");
            parkingManager2.setLastname("Nowak");
            parkingManager2.setEmail("Pmod2@pv.pl");
            parkingManager2.setPassword(new BCryptPasswordEncoder().encode("Pmod123!"));
            parkingManager2.setParking(parkingRepository.getReferenceById(1L));
            parkingManager2.setRole(Role.PARKING_MANAGER);
            parkingManagerRepository.save(parkingManager2);

            System.out.println("Added: " + parkingManager2.getEmail() + " " + "Pmod123!");

            ParkingManager parkingManager3 = new ParkingManager();

            parkingManager3.setFirstname("Swagger");
            parkingManager3.setLastname("ParkingManager");
            parkingManager3.setEmail("string@wp.pl");
            parkingManager3.setPassword(new BCryptPasswordEncoder().encode("String1!"));
            parkingManager3.setParking(parkingRepository.getReferenceById(3L));
            parkingManager3.setRole(Role.PARKING_MANAGER);
            parkingManagerRepository.save(parkingManager3);

            System.out.println("Added: " + parkingManager3.getEmail() + " " + "Pmod123!");


            ParkingManager parkingManager4 = new ParkingManager();

            parkingManager4.setFirstname("Swagger");
            parkingManager4.setLastname("ParkingModerator");
            parkingManager4.setEmail("string2");
            parkingManager4.setPassword(new BCryptPasswordEncoder().encode("string"));
            //parkingManager4.setParking(parkingRepository.getReferenceById(4L));
            parkingManager4.setRole(Role.PARKING_MANAGER);
            parkingManagerRepository.save(parkingManager4);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }

        Client client = new Client();
        Client client2 = new Client();
        Client client3 = new Client();
        Client client4 = new Client();

        long clientCount = clientRepository.count();

        if (clientCount == 0) {
            System.out.println("SeedClientData()");

            client.setFirstname("Annaa");
            client.setLastname("Nowaka");
            client.setEmail("anna@onet.pl");
            client.setPassword(new BCryptPasswordEncoder().encode("123456"));
            client.setRole(Role.USER);
            clientRepository.save(client);

            client2.setFirstname("Jan");
            client2.setLastname("Kowalski");
            client2.setEmail("macki2708@gmail.com");
            client2.setPassword(new BCryptPasswordEncoder().encode("Macki123!"));
            client2.setRole(Role.USER);
            clientRepository.save(client2);

            client3.setFirstname("filip");
            client3.setLastname("strózik");
            client3.setEmail("filipshelby@gmail.com");
            client3.setPassword(new BCryptPasswordEncoder().encode("Filip123!"));
            client3.setRole(Role.USER);
            clientRepository.save(client3);

            client4.setFirstname("Weronika");
            client4.setLastname("Litkowska");
            client4.setEmail("weronika.lit0@gmail.com");
            client4.setPassword(new BCryptPasswordEncoder().encode("Test1234"));
            client4.setRole(Role.USER);
            clientRepository.save(client4);


            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }

        long parkingSpotCount = parkingSpotRepository.count();
        ParkingSpot parkingSpot1 = new ParkingSpot();
        ParkingSpot parkingSpot2 = new ParkingSpot();

        if (parkingSpotCount == 0) {
            System.out.println("SeedParkingSpotData()");

            parkingSpot1.setSpotNumber("1");
            parkingSpot1.setActive(true);
            parkingSpot1.setParking(parking1);

            parkingSpotRepository.save(parkingSpot1);


            parkingSpot2.setSpotNumber("2");
            parkingSpot2.setActive(true);
            parkingSpot2.setParking(parking1);

            parkingSpotRepository.save(parkingSpot2);

            List<ParkingSpot> parkingSpots = new ArrayList<>();

            String[] spotNumbers = {
                    "17", "47", "48", "49", "50", "51", "52", "53", "54", "55",
                    "56", "57", "58", "59", "60", "61", "62", "63", "64", "65",
                    "66", "67", "68", "69", "70", "71", "72", "73", "74", "75",
                    "76", "77", "78", "79", "80", "81", "82", "83", "84", "85",
                    "86", "87", "88", "89", "90", "91", "92", "93", "94",
                    "96", "97", "98", "99", "100", "101", "102", "103", "104", "105",
                    "106", "107", "108", "109"
            };

            for (String spotNumber : spotNumbers) {
                System.out.println(spotNumber);
                ParkingSpot parkingSpot = new ParkingSpot();
                parkingSpot.setActive(true);
                parkingSpot.setId(Long.parseLong(spotNumber));
                parkingSpot.setSpotNumber(parking3.getName() + "-" +spotNumber);
                parkingSpot.setParking(parking3);
                parkingSpotRepository.save(parkingSpot);
            }


            spotNumbers = new String[] {
                    "110", "111", "112", "113", "114", "115"
            };

            for (String spotNumber : spotNumbers) {
                ParkingSpot parkingSpot = new ParkingSpot();
                parkingSpot.setActive(true);
                parkingSpot.setId(Long.parseLong(spotNumber));
                parkingSpot.setSpotNumber(parking4.getName() + "-" +spotNumber);
                parkingSpot.setParking(parking4);

                parkingSpotRepository.save(parkingSpot);
            }

        } else {
            System.out.println("Data already exists.");
        }
        long pointCount = pointRepository.count();

        if (pointCount == 0) {
            System.out.println("SeedPoints()");

            Point point1 = new Point();
            point1.setLatitude(51.1186521304551);
            point1.setLongitude(16.990243087061152);
            point1.setParkingSpot(parkingSpot1);
            pointRepository.save(point1);

            Point point2 = new Point();
            point2.setLatitude(51.118680641612244);
            point2.setLongitude(16.99028263711841);
            point2.setParkingSpot(parkingSpot1);
            pointRepository.save(point2);

            Point point3 = new Point();
            point3.setLatitude(51.11866831521476);
            point3.setLongitude(16.99030332627968);
            point3.setParkingSpot(parkingSpot1);
            pointRepository.save(point3);

            Point point4 = new Point();
            point4.setLatitude(51.118643381038375);
            point4.setLongitude(16.99026614904272);
            point4.setParkingSpot(parkingSpot1);
            pointRepository.save(point4);


            Point point5 = new Point();
            point5.setLatitude(51.11861171066726);
            point5.setLongitude(16.99032095102528);
            point5.setParkingSpot(parkingSpot2);
            pointRepository.save(point5);

            Point point6 = new Point();
            point6.setLatitude(51.118633203523125);
            point6.setLongitude(16.990356220713984);
            point6.setParkingSpot(parkingSpot2);
            pointRepository.save(point6);

            Point point7 = new Point();
            point7.setLatitude(51.11862002873555);
            point7.setLongitude(16.99038103386446);
            point7.setParkingSpot(parkingSpot2);
            pointRepository.save(point7);

            Point point8 = new Point();
            point8.setLatitude(51.1185928473759);
            point8.setLongitude(16.99034544418599);
            point8.setParkingSpot(parkingSpot2);
            pointRepository.save(point8);

            // 17
            double[] latitudes = {51.107431,  51.107392, 51.107392, 51.107431};
            double[] longitudes = {17.058409,  17.058401, 17.058366, 17.058377};
            long[] pointIds = {66L, 68L, 67L, 65L};
            long parkingSpotId = 17L;
            String spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            // 47
            latitudes = new double[]{51.10743, 51.107392, 51.107392, 51.10743};
            longitudes = new double[]{17.058372, 17.058362, 17.058329, 17.058339};
            pointIds = new long[]{186L, 187L, 188L, 185L};  // Point IDs remain the same
            parkingSpotId = 47L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392,  51.107393, 51.107431, 51.107431};
            longitudes = new double[]{17.058435, 17.058405, 17.058413, 17.058443};
            pointIds = new long[]{191L, 189L, 192L, 190L};
            parkingSpotId = 48L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107393,  51.107394, 51.107431, 51.10743};
            longitudes = new double[]{17.058466,  17.058437, 17.058446, 17.058478};
            pointIds = new long[]{195L, 193L, 196L, 194L};
            parkingSpotId = 49L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392,  51.107394, 51.10743, 51.10743};
            longitudes = new double[]{17.058502,  17.058471, 17.058481, 17.058509};
            pointIds = new long[]{199L, 197L, 200L, 198L};
            parkingSpotId = 50L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392, 51.10743, 51.10743, 51.107391};
            longitudes = new double[]{17.058504, 17.058512, 17.058545, 17.058538};
            pointIds = new long[]{204L, 201L, 202L, 203L};
            parkingSpotId = 51L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.10743,  51.107391, 51.107392, 51.10743};
            longitudes = new double[]{17.058582, 17.058573, 17.058542, 17.058548};
            pointIds = new long[]{206L, 208L, 207L, 205L};
            parkingSpotId = 52L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107429,  51.107428, 51.107393, 51.107392};
            longitudes = new double[]{17.058586,  17.058615, 17.058608, 17.058577};
            pointIds = new long[]{209L, 211L, 212L, 210L};
            parkingSpotId = 53L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392,  51.107392, 51.107429, 51.10743};
            longitudes = new double[]{17.05861,  17.058638, 17.058649, 17.05862};
            pointIds = new long[]{216L, 214L, 215L, 213L};
            parkingSpotId = 54L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392, 51.10743, 51.10743, 51.107393};
            longitudes = new double[]{17.058644, 17.058655, 17.058702, 17.058694};
            pointIds = new long[]{220L, 217L, 218L, 219L};
            parkingSpotId = 55L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107528,  51.107528, 51.107488, 51.107489};
            longitudes = new double[]{17.058606,  17.058655, 17.058681, 17.058629};
            pointIds = new long[]{222L, 224L, 223L, 221L};
            parkingSpotId = 56L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107527,  51.107487, 51.107488, 51.107528};
            longitudes = new double[]{17.058562,   17.058584, 17.058617, 17.058598};
            pointIds = new long[]{228L, 226L, 227L, 225L};
            parkingSpotId = 57L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107528, 51.107487, 51.107487, 51.107527};
            longitudes = new double[]{17.058532, 17.058547, 17.058579, 17.058559};
            pointIds = new long[]{232L, 231L, 230L, 229L};
            parkingSpotId = 58L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107487, 51.107528, 51.107528, 51.107487};
            longitudes = new double[]{17.058543, 17.058529, 17.058496, 17.058512};
            pointIds = new long[]{235L, 234L, 233L, 236L};
            parkingSpotId = 59L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107487, 51.107529, 51.107529, 51.107486};
            longitudes = new double[]{17.058475, 17.058462, 17.058494, 17.05851};
            pointIds = new long[]{238L, 237L, 240L, 239L};
            parkingSpotId = 60L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107487, 51.107529, 51.107528, 51.107486};
            longitudes = new double[]{17.058473, 17.058457, 17.058427, 17.058443};
            pointIds = new long[]{243L, 244L, 241L, 242L};
            parkingSpotId = 61L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107528, 51.107527, 51.107487, 51.107487};
            longitudes = new double[]{17.058424, 17.058388, 17.058406, 17.058438};
            pointIds = new long[]{245L, 248L, 247L, 246L};
            parkingSpotId = 62L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107528, 51.107487,   51.107487 ,51.107528};
            longitudes = new double[]{17.058355, 17.058373 ,   17.058402 ,17.058384};
            pointIds = new long[]{249L, 252L, 250L, 251L};
            parkingSpotId = 63L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107528,  51.107487, 51.107487, 51.107528};
            longitudes = new double[]{17.058352, 17.058369, 17.058335, 17.058317};
            pointIds = new long[]{255L, 253L, 256L, 254L};
            parkingSpotId = 64L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107488,  51.107528, 51.107527, 51.107487};
            longitudes = new double[]{17.058332,  17.058314, 17.058286, 17.058301};
            pointIds = new long[]{259L, 257L, 258L, 260L};
            parkingSpotId = 65L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107528, 51.107527, 51.107487, 51.107487};
            longitudes = new double[]{17.05825, 17.058282, 17.058295, 17.058266};
            pointIds = new long[]{264L, 261L, 262L, 263L};
            parkingSpotId = 66L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107528, 51.107528, 51.107487, 51.107488};
            longitudes = new double[]{17.058247, 17.058217, 17.058232, 17.058263};
            pointIds = new long[]{266L, 265L, 268L, 267L};
            parkingSpotId = 67L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392, 51.107392, 51.107429, 51.10743};
            longitudes = new double[]{17.058293, 17.058322, 17.058335, 17.058302};
            pointIds = new long[]{271L, 270L, 269L, 272L};
            parkingSpotId = 68L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392, 51.10743, 51.10743, 51.107392};
            longitudes = new double[]{17.058289, 17.058297, 17.05827, 17.058259};
            pointIds = new long[]{275L, 274L, 273L, 276L};
            parkingSpotId = 69L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107385, 51.107384, 51.107363, 51.107363};
            longitudes = new double[]{17.058228, 17.058302, 17.0583, 17.058227};
            pointIds = new long[]{277L, 278L, 279L, 280L};
            parkingSpotId = 70L;

            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392, 51.107392, 51.10743, 51.10743};
            longitudes = new double[]{17.058227, 17.058254, 17.058265, 17.058234};
            pointIds = new long[]{284L, 283L, 282L, 281L};
            parkingSpotId = 71L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392, 51.10743, 51.10743, 51.107393};
            longitudes = new double[]{17.058218, 17.058227, 17.058198, 17.05819};
            pointIds = new long[]{287L, 286L, 285L, 288L};
            parkingSpotId = 72L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107528, 51.107528, 51.107487,  51.107487};
            longitudes = new double[]{17.058181, 17.058214, 17.058228,  17.058194};
            pointIds = new long[]{291L, 290L, 292L, 289L};
            parkingSpotId = 73L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107528, 51.107487, 51.107487, 51.107527};
            longitudes = new double[]{17.058177, 17.058191, 17.05816, 17.058144};
            pointIds = new long[]{296L, 293L, 294L, 295L};
            parkingSpotId = 74L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107488,  51.107527, 51.107527, 51.107488};
            longitudes = new double[]{17.058124,  17.05811, 17.058142, 17.058156};
            pointIds = new long[]{298L, 300L, 299L, 297L};
            parkingSpotId = 75L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107527,  51.107528, 51.107487, 51.107488};
            longitudes = new double[]{17.058105,  17.058074, 17.058091, 17.05812};
            pointIds = new long[]{302L, 304L, 303L, 301L};
            parkingSpotId = 76L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107358, 51.107336, 51.107339, 51.107358};
            longitudes = new double[]{17.058301, 17.0583, 17.058227, 17.058226};
            pointIds = new long[]{306L, 307L, 308L, 305L};
            parkingSpotId = 77L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107393, 51.107392, 51.107432, 51.107433};
            longitudes = new double[]{17.058031, 17.058066, 17.058079, 17.058043};
            pointIds = new long[]{311L, 312L, 309L, 310L};  // Point IDs remain the same
            parkingSpotId = 78L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107392, 51.107431, 51.107431, 51.107393};
            longitudes = new double[]{17.058103, 17.058115, 17.058084, 17.058071};
            pointIds = new long[]{315L, 314L, 313L, 316L};
            parkingSpotId = 79L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107393, 51.107431, 51.107431, 51.107394};
            longitudes = new double[]{17.058109, 17.058119, 17.058148, 17.058139};
            pointIds = new long[]{320L, 317L, 318L, 319L};  // Point IDs remain the same
            parkingSpotId = 80L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107385, 51.107363, 51.107364, 51.107387};
            longitudes = new double[]{17.058111, 17.058104, 17.058024, 17.058033};
            pointIds = new long[]{322L, 323L, 324L, 321L};
            parkingSpotId = 81L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107345, 51.107361, 51.107363, 51.107346};
            longitudes = new double[]{17.0581, 17.058101, 17.058022, 17.058016};
            pointIds = new long[]{327L, 326L, 325L, 328L};
            parkingSpotId = 82L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107342, 51.107321, 51.107323, 51.107343};
            longitudes = new double[]{17.058096, 17.058091, 17.058008, 17.058013};
            pointIds = new long[]{330L, 331L, 332L, 329L};
            parkingSpotId = 83L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107316, 51.107318, 51.107298, 51.107296};
            longitudes = new double[]{17.058087, 17.058006, 17.057998, 17.05808};
            pointIds = new long[]{334L, 333L, 336L, 335L};
            parkingSpotId = 84L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107346, 51.107385, 51.107385, 51.107347};
            longitudes = new double[]{17.058706, 17.058676, 17.058713, 17.058741};
            pointIds = new long[]{338L, 337L, 340L, 339L};
            parkingSpotId = 85L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107385, 51.107385, 51.107347, 51.107347};
            longitudes = new double[]{ 17.058639, 17.058672, 17.058702, 17.058668};
            pointIds = new long[]{344L, 341L, 343L, 342L};
            parkingSpotId = 86L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107347, 51.107347, 51.107385, 51.107385};
            longitudes = new double[]{17.058633, 17.058665, 17.058636, 17.058605};
            pointIds = new long[]{345L, 348L, 347L, 346L};
            parkingSpotId = 87L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107384, 51.107347, 51.107347, 51.107385};
            longitudes = new double[]{17.058601, 17.058631, 17.058596, 17.058568};
            pointIds = new long[]{351L, 349L, 350L, 352L};
            parkingSpotId = 88L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107385, 51.107347, 51.107347, 51.107385};
            longitudes = new double[]{17.058534, 17.058559, 17.058594, 17.058565};
            pointIds = new long[]{356L, 353L, 354L, 355L};
            parkingSpotId = 89L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107347, 51.107347, 51.107385, 51.107385};
            longitudes = new double[]{17.058522, 17.058556, 17.058531, 17.058498};
            pointIds = new long[]{360L, 357L, 358L, 359L};
            parkingSpotId = 90L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107574, 51.107575, 51.107536, 51.107535};
            longitudes = new double[]{17.05858, 17.058626, 17.058617, 17.058569};
            pointIds = new long[]{362L, 361L, 364L, 363L};
            parkingSpotId = 91L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107534,  51.107534, 51.107574, 51.107573};
            longitudes = new double[]{17.058561, 17.058531, 17.058543,  17.05857};
            pointIds = new long[]{368L, 366L, 367L, 365L};
            parkingSpotId = 92L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107536, 51.107537, 51.107575, 51.107575};
            longitudes = new double[]{17.058494, 17.058463, 17.058475, 17.058502};
            pointIds = new long[]{372L, 371L, 370L, 369L};
            parkingSpotId = 93L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107574, 51.107535, 51.107536, 51.107574};
            longitudes = new double[]{17.058466, 17.058458, 17.05843, 17.058435};
            pointIds = new long[]{373L, 376L, 375L, 374L};
            parkingSpotId = 94L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);



            latitudes = new double[]{51.107535, 51.107576, 51.107575, 51.107537};
            longitudes = new double[]{17.058392, 17.058403, 17.058432, 17.058425};
            pointIds = new long[]{384L, 381L, 382L, 383L};
            parkingSpotId = 96L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107536, 51.107536, 51.107576, 51.107577};
            longitudes = new double[]{17.058356, 17.058386, 17.058401, 17.058369};
            pointIds = new long[]{387L, 386L, 385L, 388L};
            parkingSpotId = 97L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107577,  51.107538, 51.107537, 51.107577};
            longitudes = new double[]{17.0583,  17.058292, 17.058324, 17.05833};
            pointIds = new long[]{389L, 391L, 390L, 392L};
            parkingSpotId = 98L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107537, 51.107537, 51.107577, 51.107577};
            longitudes = new double[]{17.058287, 17.058255, 17.058266, 17.058295};
            pointIds = new long[]{396L, 395L, 394L, 393L};
            parkingSpotId = 99L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107576, 51.107576, 51.107537, 51.107538};
            longitudes = new double[]{17.05826, 17.05823, 17.058219, 17.058254};
            pointIds = new long[]{400L, 399L, 398L, 397L};
            parkingSpotId = 100L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107537, 51.107537, 51.107576, 51.107576};
            longitudes = new double[]{17.058178, 17.058147, 17.058161, 17.058191};
            pointIds = new long[]{403L, 404L, 401L, 402L};
            parkingSpotId = 101L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107536, 51.107576, 51.107576, 51.107536};
            longitudes = new double[]{17.058328, 17.058334, 17.058362, 17.058353};
            pointIds = new long[]{408L, 405L, 406L, 407L};
            parkingSpotId = 102L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107537, 51.107537, 51.107576, 51.107575};
            longitudes = new double[]{17.058212, 17.058184, 17.058197, 17.058225};
            pointIds = new long[]{411L, 410L, 409L, 412L};
            parkingSpotId = 103L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107536, 51.107535,   51.107575,  51.107575};
            longitudes = new double[]{17.05811, 17.058143,  17.058154,  17.058124};
            pointIds = new long[]{415L, 416L, 414L, 413L};
            parkingSpotId = 104L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107574, 51.107575, 51.107535, 51.107535};
            longitudes = new double[]{17.058084, 17.05812, 17.058108, 17.058075};
            pointIds = new long[]{420L, 417L, 418L, 419L};
            parkingSpotId = 105L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107536,  51.107536, 51.107575, 51.107574};
            longitudes = new double[]{17.058498,  17.058525, 17.058537, 17.058505};
            pointIds = new long[]{422L, 424L, 423L, 421L};
            parkingSpotId = 106L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107676, 51.107632, 51.107632,  51.107676};
            longitudes = new double[]{17.0585,  17.05851, 17.058481, 17.058464};
            pointIds = new long[]{431L, 429L, 432L, 430L};
            parkingSpotId = 108L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.107631, 51.107631, 51.107676, 51.107676};
            longitudes = new double[]{17.058474, 17.058445, 17.058429, 17.058462};
            pointIds = new long[]{433L, 434L, 436L, 435L};
            parkingSpotId = 109L;
            spotNumber = parking3.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.108855, 51.10883, 51.108847, 51.10887};
            longitudes = new double[]{17.055863, 17.055816, 17.055795, 17.055837};
            pointIds = new long[]{438L, 439L, 440L, 437L};
            parkingSpotId = 110L;
            spotNumber = parking4.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.108844, 51.108815, 51.108829, 51.108853};
            longitudes = new double[]{17.055883, 17.055838, 17.055819, 17.055864};
            pointIds = new long[]{442L, 443L,444L, 441L};
            parkingSpotId = 111L;
            spotNumber = parking4.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.108872, 51.108848, 51.108859, 51.108885};
            longitudes = new double[]{17.055834, 17.055789, 17.055774, 17.05581};
            pointIds = new long[]{446L, 448L, 447L, 445L};
            parkingSpotId = 112L;
            spotNumber = parking4.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.108784, 51.108755, 51.108773, 51.108797};
            longitudes = new double[]{17.055773, 17.055722, 17.055704, 17.055753};
            pointIds = new long[]{450L, 452L, 451L, 449L};
            parkingSpotId = 113L;
            spotNumber = parking4.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.108811, 51.10879, 51.108777, 51.108798};
            longitudes = new double[]{17.055728, 17.055681, 17.055699, 17.055742};
            pointIds = new long[]{454L, 456L, 455L, 453L};
            parkingSpotId = 114L;
            spotNumber = parking4.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);

            latitudes = new double[]{51.108925, 51.108948, 51.108935, 51.10891};
            longitudes = new double[]{17.055781, 17.05583, 17.055852, 17.055807};
            pointIds = new long[]{457L, 458L, 459L, 460L};
            parkingSpotId = 115L;
            spotNumber = parking4.getName() + "-" +parkingSpotId;
            savePoints(latitudes, longitudes, pointIds, spotNumber);


        } else {
            System.out.println("Data already exists.");
        }

        long carCount = carRepository.count();

        Car car1 = new Car();
        Car car2 = new Car();
        if (carCount == 0) {

            System.out.println("seedCarData()");


            car1.setBrand("123");
            car1.setColor("blue");
            car1.setClient(client);
            car1.setRegistrationNumber("345");

            carRepository.save(car1);


            car2.setBrand("123");
            car2.setColor("blue");
            car2.setClient(client2);
            car2.setRegistrationNumber("567");

            carRepository.save(car2);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }

        long reservationCount = reservationRepository.count();

        Reservation reservation = new Reservation();
        Reservation reservation1 = new Reservation();

        if (reservationCount == 0) {

            System.out.println("seedReservationData()");

            reservation.setParkingSpot(parkingSpot1);
            reservation.setStartDate(OffsetDateTime.now());
            reservation.setEndDate(OffsetDateTime.now().plusHours(1));
            reservation.setUser(client);
            reservation.setRegistrationNumber("123");
            reservationRepository.save(reservation);

            reservation1.setParkingSpot(parkingSpot2);
            OffsetDateTime utc = OffsetDateTime.now(ZoneId.of("UTC"));
            System.out.println(utc);
            OffsetDateTime warsaw = utc.withOffsetSameInstant(ZoneOffset.of("+1"));
            System.out.println(warsaw);
            reservation1.setStartDate(warsaw);
            reservation1.setEndDate(warsaw.plusHours(1));
            reservation1.setUser(client);
            reservation1.setRegistrationNumber("123");
            reservationRepository.save(reservation1);

            System.out.println("Data seeded.");
        } else {
            System.out.println("Data already exists.");
        }
    }

    public void savePoints(double[] latitudes, double[] longitudes, long[] pointIds, String parkingSpotNumber) {
        // Retrieve ParkingSpot reference by ID
        ParkingSpot parkingSpot = parkingSpotRepository.findBySpotNumber(parkingSpotNumber);

        // Create and save points
        for (int i = 0; i < latitudes.length; i++) {
            Point point = new Point();
            point.setLatitude(latitudes[i]);
            point.setLongitude(longitudes[i]);
            point.setId(pointIds[i]);
            point.setParkingSpot(parkingSpot);
            pointRepository.save(point);
        }
    }
}
