package com.bengkel.booking.services;

import com.bengkel.booking.models.BookingOrder;
import java.util.List;

import com.bengkel.booking.models.Car;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Motorcyle;
import com.bengkel.booking.models.Vehicle;
import com.bengkel.booking.repositories.CustomerRepository;
import static com.bengkel.booking.services.BengkelService.generateBookingOrderId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrintService {
    private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
    
    //tampilan menu
    public static void printMenu(String[] listMenu, String title) {
        String line = "+---------------------------------+";
	int number = 1;
	String formatTable = " %-2s. %-25s %n";	
	System.out.printf("%-25s %n", title);
	System.out.println(line);
        
	for (String data : listMenu) {
            if (number < listMenu.length) {
		System.out.printf(formatTable, number, data);
            }else {
		System.out.printf(formatTable, 0, data);
            }
            number++;
	}
            System.out.println(line);
            System.out.println();
    }
	
    //tampilan semua data vehicles
    public static void showAllVehicles(List<Vehicle> vehicles) {
        System.out.println("List Vechicles : ");
        String formatTable = "| %-3s | %-15s | %-10s | %-15s | %-15s |%n";
        String line = "+-----+-----------------+------------+-----------------+-----------------+%n";
        System.out.format(line);
        System.out.format(formatTable, "No", "Vehicle Id", "Warna", "Tipe Kendaraan", "Tahun");
        System.out.format(line);
        int number = 1;
        for (Vehicle vehicle : vehicles) {
            String vehicleType;
            if (vehicle instanceof Car) {
                vehicleType = "Car";
            } else {
                vehicleType = "Motorcycle";
            } 
            System.out.format(formatTable, number++, vehicle.getVehiclesId(), vehicle.getColor(),
                    vehicleType, vehicle.getYearRelease());
        }
        System.out.printf(line);
    }

    //tampilan semua data service
    public static void showAllService(List<ItemService> listItemService, Vehicle selectedVehicle) {
        System.out.println("List Service yang Tersedia untuk Kendaraan " + selectedVehicle.getVehicleType() + ":");
        String formatTable = "| %-3s | %-10s | %-20s | %-15s | %-15s |%n";
        String line = "+-----+------------+----------------------+-----------------+-----------------+%n";
        System.out.format(line);
        System.out.format(formatTable, "No", "Service Id", "Nama Service", "Tipe Kendaraan", "Harga");
        System.out.format(line);
        int number = 1;
        for (ItemService itemService : listItemService) {
        if (itemService.getVehicleType().equalsIgnoreCase(selectedVehicle.getVehicleType())) {
            String formattedPrice = Validation.formatToRupiah(itemService.getPrice());
            System.out.format(formatTable,
                    number++, itemService.getServiceId(), itemService.getServiceName(),
                    itemService.getVehicleType(), formattedPrice);
        }
    }
        System.out.printf(line);
    } 
}
