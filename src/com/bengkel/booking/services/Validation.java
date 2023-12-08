package com.bengkel.booking.services;

import com.bengkel.booking.models.Customer;
import com.bengkel.booking.repositories.CustomerRepository;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Validation {
    private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
    
    //validasi input
    public static String validasiInput(String question, String errorMessage, String regex) {
	Scanner input = new Scanner(System.in);
	String result;
	boolean isLooping = true;
	do {
	    System.out.print(question);
	    result = input.nextLine();
            
	    //validasi menggunakan matches
	    if (result.matches(regex)) {
                isLooping = false;
	    }else {
	        System.out.println(errorMessage);
	    }
	} while (isLooping);
	return result;
    }

    //validasi number
    public static int validasiNumberWithRange(String question, String errorMessage, String regex, int max, int min) {
        int result;
	boolean isLooping = true;
	do {
	    result = Integer.valueOf(validasiInput(question, errorMessage, regex));
	    if (result >= min && result <= max) {
	        isLooping = false;
	    }else {
	        System.out.println("Pilihan angka " + min + " s.d " + max);
	    }
	} while (isLooping);
	return result;
    }
    
    //validasi login
    public static Customer validateLogin(String customerId, String password) {
        if (customerId == null || password == null) {
           System.out.println("Customer ID and password tidak boleh kosong");
        }   
        for (Customer customer : listAllCustomers) {
            if (customer.getCustomerId().equals(customerId)) {
                if (customer.getPassword().equals(password)) {
                    System.out.println("Login berhasil. Selamat datang, " + customer.getName() + "!");
                    System.out.println();
                    return customer;
                } else {
                    System.out.println("Password salah.");
                    return null; 
                }
            }
        }
        System.out.println("Customer Id salah atau tidak terdaftar.");
        return null; 
    }
    
    //validasi Ya atau Tidak
    static String validateInputYorT(Scanner input) {
        String userInput;
        do {
            System.out.println("Apakah anda ingin menambahkan Service Lainnya? (Y/T)");
            userInput = input.nextLine().toUpperCase();
            if (!userInput.equals("Y") && !userInput.equals("T")) {
                System.out.println("Input tidak valid. Harap masukkan 'Y' atau 'T'.");
            }
        } while (!userInput.equals("Y") && !userInput.equals("T"));

        return userInput;
    }
    
    //vaidasi
    static boolean isValidPaymentMethod(String paymentMethod) {
        return paymentMethod.equals("SALDO COIN") || paymentMethod.equals("CASH");
    }
    
    //validasi top up saldo
    public static double ValidatedTopUp() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Masukkan besaran Top Up: ");
            String input = scanner.nextLine();

            try {
                double topUpAmount = Double.parseDouble(input);

                if (topUpAmount > 0) {
                    return topUpAmount;
                } else {
                    System.out.println("Jumlah Top Up harus lebih dari 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input hanya boleh angka");
            }
        }
    }
    
    public static String formatToRupiah(double amount) {
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    return currencyFormatter.format(amount);
}
}
