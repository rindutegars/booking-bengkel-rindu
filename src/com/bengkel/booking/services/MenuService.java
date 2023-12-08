package com.bengkel.booking.services;

import com.bengkel.booking.models.BookingOrder;
import java.util.List;
import java.util.Scanner;

import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;
import static com.bengkel.booking.services.BengkelService.displayBookingOrders;
import java.util.ArrayList;

public class MenuService {
    private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
    private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();
    private static PrintService printService = new PrintService();
    private static BengkelService bengkelService = new BengkelService();
    private static List<ItemService> selectedServices;
    private static List<BookingOrder> bookingOrders;
    private static BookingOrder bookingOrder = new BookingOrder();
    private static Customer loggedInCustomer;  
    private static Scanner input = new Scanner(System.in);
        
    public static void run() {
        boolean isLooping = true;
        do {
            displayInitialMenu();
            int choice = Validation.validasiNumberWithRange("Masukkan Pilihan Menu:", "Input Harus Berupa Angka!", "^[0-1]+$", 1, 0);
            switch (choice) {
                case 1:
                    BengkelService.login();
                    mainMenu();
                    break;
                case 0:
                    System.out.println("Aplikasi berakhir!!!");
                    isLooping = false;
                    break;
                default:
                    System.out.println("Pilih angka 1 atau 0");
                    break;
            }
        } while (isLooping);
    }

    //menu awal
    public static void displayInitialMenu() {
        String[] initialMenuOptions = {"Login", "Exit"};
        PrintService.printMenu(initialMenuOptions, "Aplikasi Booking Bengkel");
    }
	
    //main menu
    public static void mainMenu() {
        String[] listMenu = {"Informasi Customer", "Booking Bengkel", "Top Up Bengkel Coin", "Informasi Booking", "Logout"};
        int menuChoice = 0;
        boolean isLooping = true;
        do {
            System.out.println();
            PrintService.printMenu(listMenu, "Main Menu Aplikasi Booking Bengkel");
            menuChoice = Validation.validasiNumberWithRange("Masukan Pilihan Menu:", "Input Harus Berupa Angka!", "^[0-9]+$", listMenu.length-1, 0);
           
            switch (menuChoice) {
                case 1:
                    //tampilan informasi customer
                    bengkelService.printCustomerInformation();
                    break;
                case 2:
                    //tampilan membuat booking
                    bengkelService.createBookingBengkel();
                    break;
                case 3:
                    //tampilan fitur Top Up Saldo Coin
                    bengkelService.topUpSaldoCoin();
                    break;
                case 4:
                    //tampilan data booking order
                    bengkelService.displayBookingOrders();
                    break;
                default:
                    System.out.println("Logout");
                    isLooping = false;
                    break;
            }
        } while (isLooping);
    }
	//Silahkan tambahkan kodingan untuk keperluan Menu Aplikasi
}
