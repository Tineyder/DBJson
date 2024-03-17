package org.example;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        List<Customer> customers = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        customers.add(new Customer(1, "Істомін", "Ремир", "Євгенович", new Date(90,2,22), "Херсон, просп. Ушакова, б.12", "1104468", 100.0));
        customers.add(new Customer(2, "Миронов", "Павло", "Юрійович", new Date(91,3,11), "Херсон, просп. Ушакова, б.14", "3122140", 200.0));
        customers.add(new Customer(3, "Гришин", "Євген", "Сергійович", new Date(92,4,15), "Херсон, просп. Ушакова, б.16", "2602365", 300.0));
        customers.add(new Customer(4, "Сергеєва", "Ольга", "Іванівна", new Date(92,4,15), "Херсон, просп. Ушакова, б.18", "2348569", 0.0));
        customers.add(new Customer(5, "Ємель", "Тетяна", "Іванівна", new Date(92,4,15), "Херсон, просп. Ушакова, б.20", "1667533", -300.0));

        saveCustomersToJson(customers, mapper, dateFormat);

        List<Customer> loadedCustomers = readCustomersFromJson();
        System.out.println("Завантажені клієнти з JSON:");
        for (Customer customer : loadedCustomers) {
            System.out.println(customer);
        }
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("Меню:");
            System.out.println("1. Вивести список покупців з вказаним ім'ям");
            System.out.println("2. Список покупців, у яких номер кредитної картки знаходиться в заданому інтервалі");
            System.out.println("3. Кількість та список покупців, які мають від’ємний баланс на карті в порядку зростання баланса");
            System.out.println("4. Список покупців, упорядкований за зростанням балансу рахунку, а при рівності балансів – за номером кредитної картки");
            System.out.println("5. Вихід");
            System.out.print("Ваш вибір: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scanner.nextLine();
                    System.out.print("Введіть ім'я покупця: ");
                    String searchName = scanner.nextLine();
                    List<Customer> filteredCustomersByName = filterCustomersByName(customers, searchName);
                    displayCustomers(filteredCustomersByName);
                    break;
                case 2:
                    System.out.print("Введіть початковий номер кредитної картки: ");
                    String startCardNumber = scanner.next();
                    System.out.print("Введіть кінцевий номер кредитної картки: ");
                    String endCardNumber = scanner.next();
                    List<Customer> filteredCustomersByCardNumberRange = filterCustomersByCardNumberRange(customers, startCardNumber, endCardNumber);
                    displayCustomers(filteredCustomersByCardNumberRange);
                    break;
                case 3:
                    List<Customer> negativeBalanceCustomers = findCustomersWithNegativeBalance(customers);
                    displayCustomers(negativeBalanceCustomers);
                    break;
                case 4:
                    List<Customer> sortedCustomers = sortCustomersByBalanceAndCardNumber(customers);
                    displayCustomers(sortedCustomers);
                    break;
                case 5:
                    System.out.println("Дякую за використання програми!");
                    break;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        } while (choice != 5);

        scanner.close();
    }

    private static List<Customer> filterCustomersByName(List<Customer> customers, String name) {
        return customers.stream()
                .filter(customer -> customer.getFirstName().equalsIgnoreCase(name))
                .toList();
    }

    private static List<Customer> filterCustomersByCardNumberRange(List<Customer> customers, String startCardNumber, String endCardNumber) {
        return customers.stream()
                .filter(customer -> customer.getCreditCardNumber().compareTo(startCardNumber) >= 0 && customer.getCreditCardNumber().compareTo(endCardNumber) <= 0)
                .toList();
    }

    private static List<Customer> findCustomersWithNegativeBalance(List<Customer> customers) {
        return customers.stream()
                .filter(customer -> customer.getAccountBalance() < 0)
                .sorted(Comparator.comparingDouble(Customer::getAccountBalance))
                .toList();
    }

    private static List<Customer> sortCustomersByBalanceAndCardNumber(List<Customer> customers) {
        return customers.stream()
                .sorted(Comparator.comparingDouble(Customer::getAccountBalance).thenComparing(Customer::getCreditCardNumber))
                .toList();
    }

    private static void displayCustomers(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("Список покупців порожній.");
        } else {
            System.out.println("Список покупців:");
            customers.forEach(System.out::println);
        }
    }

    private static void saveCustomersToJson(List<Customer> customers, ObjectMapper mapper, SimpleDateFormat dateFormat) {
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.setDateFormat(dateFormat);
            mapper.writeValue(new File("customers.json"), customers);
            System.out.println("Клієнтів успішно збережено у файл.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Customer> readCustomersFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType customerType = mapper.getTypeFactory().constructCollectionType(List.class, Customer.class);

        try {
            return mapper.readValue(new File("customers.json"), customerType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

class Customer {
    public int id;
    public String lastName;
    public String firstName;
    public String middleName;
    public Date birthDate;
    public String address;
    public String creditCardNumber;
    public double accountBalance;



    public Customer() {    }

    public Customer(int id, String lastName, String firstName, String middleName, Date birthDate, String address, String creditCardNumber, double accountBalance) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.birthDate = birthDate;
        this.address = address;
        this.creditCardNumber = creditCardNumber;
        this.accountBalance = accountBalance;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;}
    public String getLastName() {
        return lastName;
    }
    public void setlastName(String lastName) {
        this.lastName = lastName;}
    public String getFirstName() {
        return firstName;
    }
    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getMiddleName() {
        return middleName;
    }
    public void setmiddleName(String middleName) {
        this.middleName = middleName;
    }
    public Date getBirthDate() {
        return birthDate;
    }
    public void setbirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public String getAddress() {
        return address;
    }
    public void setaddress(String address) {
        this.address = address;
    }
    public String getCreditCardNumber() {
        return creditCardNumber;
    }
    public void setcreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
    public double getAccountBalance() {
        return accountBalance;
    }
    public void setaccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;}

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", creditCardNumber='" + creditCardNumber + '\'' +
                ", accountBalance=" + accountBalance +
                '}';
    }
}