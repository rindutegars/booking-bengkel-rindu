package com.bengkel.booking.services;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;
import static com.bengkel.booking.services.PrintService.showAllVehicles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class BengkelService {
    private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
    private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
    private static List<ItemService> selectedServices;
    private static Customer loggedInCustomer; 
    private static Scanner input = new Scanner(System.in);
    
    //Silahkan tambahkan fitur-fitur utama aplikasi disini
    
    // Login
    public static void login() {
        boolean isValidLogin = false;
        int attempts = 3;
        do {
            System.out.println();
            System.out.println("==============================================");
            System.out.println("|                    Login                   |");
            System.out.println("==============================================");
            System.out.print("Masukkan Customer ID: ");
            String customerId = input.nextLine();
            System.out.print("Masukkan Password: ");
            String password = input.nextLine();
            Customer loggedInCustomer = Validation.validateLogin(customerId, password);
            if (loggedInCustomer != null) {
                isValidLogin = true;
                setLoggedInCustomer(loggedInCustomer);
            } else {
                attempts--;
                if (attempts > 0) {
                    System.out.println("Login gagal! Sisa upaya login: " + attempts);
                } else {
                    System.out.println("Terlalu banyak upaya login yang tidak valid. Aplikasi berakhir!!!");
                    System.exit(0);
                }
            }
        } while (!isValidLogin);
    }

    //Info Customer 
    public static void setLoggedInCustomer(Customer customer) {
        loggedInCustomer = customer;
    }
    
    public static void printCustomerInformation() {
        System.out.println();
            System.out.println("==============================================");
            System.out.println("|             Customer Profil                 |");
            System.out.println("==============================================");
        if (loggedInCustomer != null) {
            System.out.println("Customer Id: " + loggedInCustomer.getCustomerId());
            System.out.println("Nama: " + loggedInCustomer.getName());
            System.out.println("Customer Status: " + getCustomerStatus(loggedInCustomer));
            System.out.println("Alamat: " + loggedInCustomer.getAddress());
             if (loggedInCustomer instanceof MemberCustomer) {
                MemberCustomer memberCustomer = (MemberCustomer) loggedInCustomer;
                double saldoCoin = memberCustomer.getSaldoCoin();
                String formattedSaldo = Validation.formatToRupiah(saldoCoin);
                System.out.println("Saldo Coin: " + formattedSaldo);
            }
            showAllVehicles(loggedInCustomer.getVehicles());
        } else {
            System.out.println("Tidak ada customer yang login.");
        }
    }
	
    public static String getCustomerStatus(Customer customer) {
        if (customer instanceof MemberCustomer) {
            return "Member";
        } else {
            return "Non-Member";
        }
    }
    
    //Booking atau Reservation 
    public static void createBookingBengkel() {
        System.out.println("==============================================");
        System.out.println("|             Booking Bengkel                |");
        System.out.println("==============================================");
        showAllVehicles(loggedInCustomer.getVehicles());
        Vehicle selectedVehicle = selectVehicle(input);
        PrintService.showAllService(listAllItemService, selectedVehicle);
        List<ItemService> selectedServices = selectServices(input);
        processPayment(input, selectedServices);
    }

    private static Vehicle selectVehicle(Scanner input) {
        Vehicle selectedVehicle;
        while (true) {
            System.out.println("Masukkan Vehicle Id:");
            String vehicleId = input.nextLine();
            selectedVehicle = findVehicleById(loggedInCustomer.getVehicles(), vehicleId);
            if (selectedVehicle != null) {
                break;
            } else {
                System.out.println("Vehicle Id tidak valid. Silahkan coba lagi.");
            }
        }
        return selectedVehicle;
    }

    private static List<ItemService> selectServices(Scanner input) {
        List<ItemService> selectedServices = new ArrayList<>();
        Set<String> selectedServiceIds = new HashSet<>();
        int maxServices;
        if (loggedInCustomer != null && "Member".equals(getCustomerStatus(loggedInCustomer))) {
            maxServices = 2;
        } else {
            maxServices = 1;
        }
        
        while (selectedServices.size() < maxServices) {
            System.out.println("Silahkan masukkan Service Id:");
            String serviceId = input.nextLine();
            if (!selectedServiceIds.contains(serviceId)) {
                ItemService selectedService = findServiceById(serviceId);
                if (selectedService != null) {
                    selectedServices.add(selectedService);
                    selectedServiceIds.add(serviceId);
                    if (selectedServices.size() >= maxServices) {
                        System.out.println("Anda telah mencapai jumlah maksimal layanan.");
                        break;
                    }
                    String addMore = Validation.validateInputYorT(input);
                    if (!addMore.equals("Y")) {
                        break;
                    }
                } else {
                    System.out.println("Service Id tidak valid. Silahkan coba lagi.");
                }
            } else {
                System.out.println("Service Id sudah dimasukkan sebelumnya. Silahkan coba lagi.");
            }
        }
        return selectedServices;
    }

    private static void processPayment(Scanner input, List<ItemService> selectedServices) {
        while (true) {
            System.out.println("Silahkan Pilih Metode Pembayaran (Saldo Coin atau Cash):");
            String paymentMethod = input.nextLine().toUpperCase();
            if (loggedInCustomer != null) {
                if ("Member".equals(getCustomerStatus(loggedInCustomer))) {
                    processMemberPayment(input, selectedServices, paymentMethod);
                    break;
                } else {
                    processNonMemberPayment(input, selectedServices, paymentMethod);
                    break;
                }
            } else {
                System.out.println("Tidak ada customer yang login.");
                return;
            }
        }
    }

    private static void processMemberPayment(Scanner scanner, List<ItemService> selectedServices, String paymentMethod) {
        double totalHarga = calculateTotalPrice(selectedServices);
        double discount = 0.0;
        if (Validation.isValidPaymentMethod(paymentMethod)) {
            if (paymentMethod.equals("SALDO COIN")) {
                double saldoCoin = ((MemberCustomer) loggedInCustomer).getSaldoCoin();
                if (saldoCoin >= totalHarga) {
                    discount = 0.1;
                    double totalPembayaran = totalHarga - (totalHarga * discount);
                    ((MemberCustomer) loggedInCustomer).setSaldoCoin(saldoCoin - totalPembayaran);
                    displayBookingResult(totalHarga, totalPembayaran, paymentMethod, selectedServices);
                } else {
                    System.out.println("Saldo Coin tidak mencukupi. Silahkan pilih metode pembayaran lain.");
                    return;
                }
            } else if (paymentMethod.equals("CASH")) {
                displayBookingResult(totalHarga, totalHarga, paymentMethod, selectedServices);
            } else {
                System.out.println("Metode pembayaran tidak valid. Silahkan coba lagi.");
                return;
            }
        } else {
            System.out.println("Metode pembayaran tidak valid. Silahkan coba lagi.");
            return;
        }
    }


    private static void processNonMemberPayment(Scanner scanner, List<ItemService> selectedServices, String paymentMethod) {
        while (true) {
            if (Validation.isValidPaymentMethod(paymentMethod)) {
                if (paymentMethod.equals("CASH")) {
                    double totalHarga = calculateTotalPrice(selectedServices);
                    displayBookingResult(totalHarga, totalHarga, paymentMethod, selectedServices);
                    break; 
                } else if (paymentMethod.equals("SALDO COIN")) {
                    System.out.println("Pembayaran hanya bisa menggunakan metode Cash.");
                } else {
                    System.out.println("Metode pembayaran tidak valid. Silahkan coba lagi.");
                }
            }
            System.out.println("Silahkan Pilih Metode Pembayaran (Cash atau Saldo Coin):");
            paymentMethod = scanner.nextLine().toUpperCase();
        }
    }

    private static List<BookingOrder> bookingOrders = new ArrayList<>();
    private static void displayBookingResult(double totalHarga, double totalPembayaran, String paymentMethod, List<ItemService> selectedServices) {
        System.out.println();
        System.out.println("Booking Berhasil!!!");
        System.out.println("==============================================");
        System.out.println("Total Harga Service: " + Validation.formatToRupiah(totalHarga));
        System.out.println("Total Pembayaran: " + Validation.formatToRupiah(totalPembayaran));
        System.out.println("Service yang Dipilih:");
        for (ItemService service : selectedServices) {
            System.out.println(service.getServiceName());
        }
        System.out.println("==============================================");
        String bookingOrderId = generateBookingOrderId();
        Customer customer;
        if (loggedInCustomer != null) {
            customer = loggedInCustomer;
        } else {
            customer = new Customer();
        }
        BookingOrder bookingOrder = new BookingOrder(
                bookingOrderId,
                customer,
                selectedServices, 
                paymentMethod, 
                totalHarga,
                totalPembayaran
        );
        bookingOrders.add(bookingOrder);
    }

    public static void displayBookingOrders() {
        System.out.println("==============================================");
        System.out.println("|            Booking Order Menu              |");
        System.out.println("==============================================");
        String formatTable = "| %-3s | %-20s | %-20s | %-15s | %-20s | %-20s | %-30s |%n";
        String line = "+-----+----------------------+----------------------+-----------------+----------------------+----------------------+--------------------------------+%n";
        System.out.format(line);
        System.out.format(formatTable, "No", "Booking Id", "Nama Customer", "Payment Method", "Total Service", "Total Payment", "List Service");
        System.out.format(line);

        int number = 1;
        for (int i = 0; i < bookingOrders.size(); i++) {
        BookingOrder bookingOrder = bookingOrders.get(i);
        Customer customer = bookingOrder.getCustomer();
        List<ItemService> services = bookingOrder.getServices();
        StringBuilder serviceList = new StringBuilder();
        for (ItemService service : services) {
            serviceList.append(service.getServiceName()).append(", ");
        }
        String formattedServiceList = serviceList.substring(0, serviceList.length() - 2);
        System.out.format(formatTable,
            number++, generateBookingOrderId(), bookingOrder.getCustomer().getName(), bookingOrder.getPaymentMethod(),
            Validation.formatToRupiah(bookingOrder.getTotalServicePrice()), 
            Validation.formatToRupiah(bookingOrder.getTotalPayment()), 
            formattedServiceList);
        }
        System.out.printf(line);
    }

    private static int bookingOrderCounter = 1; // Initialize counter
    public static String generateBookingOrderId() {
        String timestamp = Long.toString(System.currentTimeMillis());
        String customerId = (loggedInCustomer != null) ? loggedInCustomer.getCustomerId() : "NOCUST";
        String uniqueNumber = String.format("%03d", bookingOrderCounter++); // Use counter with leading zeros
        return "Book-" + customerId + "-" + uniqueNumber;
    }

    public static double calculateTotalPrice(List<ItemService> selectedServices) {
        double totalHarga = 0.0;
        for (ItemService selectedService : selectedServices) {
            totalHarga += selectedService.getPrice();
        }
        return totalHarga;
    }

    public static ItemService findServiceById(String serviceId) {
        for (ItemService itemService : listAllItemService) {
            if (itemService.getServiceId().equalsIgnoreCase(serviceId)) {
                return itemService;
            }
        }
        return null;
    }
    
    public static Vehicle findVehicleById(List<Vehicle> listVehicles, String vehicleId) {
        for (Vehicle vehicle : listVehicles) {
            if (vehicle.getVehiclesId().equalsIgnoreCase(vehicleId)) {
                return vehicle;
            }
        }
        return null;
    }

    //Top Up Saldo Coin Untuk Member Customer
    public static void topUpSaldoCoin() {
        if (loggedInCustomer != null) {
            if ("Member".equals(getCustomerStatus(loggedInCustomer))) {
                MemberCustomer memberCustomer = (MemberCustomer) loggedInCustomer;
                System.out.println("==============================================");
                System.out.println("|            Top Up Saldo Koin               |");
                System.out.println("==============================================");
                double topUpAmount = Validation.ValidatedTopUp();

                double currentSaldoCoin = memberCustomer.getSaldoCoin();
                memberCustomer.setSaldoCoin(currentSaldoCoin + topUpAmount);
                System.out.println("Top Up Saldo Coin berhasil!");
                System.out.println("Saldo Coin sekarang: " + Validation.formatToRupiah(memberCustomer.getSaldoCoin()));
            } else {
                System.out.println("Maaf, fitur ini hanya untuk Member saja!");
            }
        } else {
            System.out.println("Tidak ada customer yang login.");
        }
    }
    
    public static List<BookingOrder> getBookingOrders() {
        return bookingOrders;
    }
    //Logout	
}
