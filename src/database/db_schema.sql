CREATE DATABASE gearrent_pro;
USE gearrent_pro;

CREATE TABLE branches (
    branch_id INT PRIMARY KEY AUTO_INCREMENT,
    branch_code VARCHAR(20) UNIQUE NOT NULL,
    branch_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_number VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'BRANCH_MANAGER', 'STAFF') NOT NULL,
    branch_id INT,
    full_name VARCHAR(100),
    email VARCHAR(100),
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
);

CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    base_price_factor DECIMAL(4, 2) NOT NULL,
    weekend_multiplier DECIMAL(4, 2) DEFAULT 1.0,
    default_late_fee DECIMAL(10, 2),
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE equipment (
    equipment_id INT PRIMARY KEY AUTO_INCREMENT,
    equipment_code VARCHAR(30) UNIQUE NOT NULL,
    category_id INT NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    purchase_year INT,
    daily_base_price DECIMAL(10, 2) NOT NULL,
    security_deposit DECIMAL(10, 2) NOT NULL,
    status ENUM('AVAILABLE', 'RESERVED', 'RENTED', 'UNDER_MAINTENANCE') DEFAULT 'AVAILABLE',
    branch_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
);

CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(20) UNIQUE NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    nic_passport VARCHAR(50) UNIQUE NOT NULL,
    contact_number VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    membership_level ENUM('REGULAR', 'SILVER', 'GOLD') DEFAULT 'REGULAR',
    deposit_limit DECIMAL(15, 2) DEFAULT 500000.00,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE membership_discounts (
    discount_id INT PRIMARY KEY AUTO_INCREMENT,
    membership_level ENUM('REGULAR', 'SILVER', 'GOLD') UNIQUE NOT NULL,
    discount_percentage DECIMAL(5, 2) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_code VARCHAR(30) UNIQUE NOT NULL,
    equipment_id INT NOT NULL,
    customer_id INT NOT NULL,
    branch_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id)
);

CREATE TABLE rentals (
    rental_id INT PRIMARY KEY AUTO_INCREMENT,
    rental_code VARCHAR(30) UNIQUE NOT NULL,
    equipment_id INT NOT NULL,
    customer_id INT NOT NULL,
    branch_id INT NOT NULL,
    reservation_id INT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    actual_return_date DATE,
    daily_rate DECIMAL(10, 2) NOT NULL,
    rental_amount DECIMAL(15, 2) NOT NULL,
    security_deposit DECIMAL(10, 2) NOT NULL,
    membership_discount DECIMAL(10, 2) DEFAULT 0,
    long_rental_discount DECIMAL(10, 2) DEFAULT 0,
    final_payable_amount DECIMAL(15, 2) NOT NULL,
    payment_status ENUM('PAID', 'PARTIALLY_PAID', 'UNPAID') DEFAULT 'UNPAID',
    rental_status ENUM('ACTIVE', 'RETURNED', 'OVERDUE', 'CANCELLED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (branch_id) REFERENCES branches(branch_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id)
);

CREATE TABLE return_details (
    return_id INT PRIMARY KEY AUTO_INCREMENT,
    rental_id INT NOT NULL UNIQUE,
    damage_description VARCHAR(500),
    damage_charge DECIMAL(10, 2) DEFAULT 0,
    late_fee DECIMAL(10, 2) DEFAULT 0,
    total_charges DECIMAL(10, 2) DEFAULT 0,
    refund_amount DECIMAL(10, 2) DEFAULT 0,
    additional_payment_required DECIMAL(10, 2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rental_id) REFERENCES rentals(rental_id)
);

CREATE TABLE configuration (
    config_id INT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert Default Configuration
INSERT INTO configuration (config_key, config_value, description) VALUES
('LONG_RENTAL_MIN_DAYS', '7', 'Minimum days for long rental discount'),
('LONG_RENTAL_DISCOUNT', '10', 'Long rental discount percentage'),
('LATE_FEE_PER_DAY', '500', 'Default late fee per day in LKR'),
('MAX_RENTAL_DURATION', '30', 'Maximum rental duration in days'),
('CUSTOMER_DEPOSIT_LIMIT', '500000', 'Maximum deposit limit per customer in LKR');

-- Insert Branches
INSERT INTO branches (branch_code, branch_name, address, contact_number, email) VALUES
('BR001', 'Panadura Branch', '123 Main Street, Panadura', '0341234567', 'panadura@gearrent.com'),
('BR002', 'Galle Branch', '456 Beach Road, Galle', '0912345678', 'galle@gearrent.com'),
('BR003', 'Colombo Branch', '789 City Center, Colombo', '0112345678', 'colombo@gearrent.com');

-- Insert Categories
INSERT INTO categories (category_name, description, base_price_factor, weekend_multiplier, default_late_fee) VALUES
('Camera', 'Professional Video & Photo Cameras', 1.5, 1.2, 2000),
('Lens', 'Camera Lenses and Accessories', 1.0, 1.1, 1000),
('Drone', 'Aerial Photography Drones', 2.0, 1.5, 3000),
('Lighting', 'Studio Lighting Equipment', 1.2, 1.1, 1500),
('Audio', 'Professional Audio Equipment', 1.3, 1.2, 1800);

-- Insert Membership Discounts
INSERT INTO membership_discounts (membership_level, discount_percentage) VALUES
('REGULAR', 0),
('SILVER', 5),
('GOLD', 10);

-- Insert Users
INSERT INTO users (username, password, role, branch_id, full_name, email) VALUES
('admin', 'admin123', 'ADMIN', NULL, 'Admin User', 'admin@gearrent.com'),
('manager1', 'manager123', 'BRANCH_MANAGER', 1, 'John Manager', 'manager1@gearrent.com'),
('manager2', 'manager123', 'BRANCH_MANAGER', 2, 'Jane Manager', 'manager2@gearrent.com'),
('staff1', 'staff123', 'STAFF', 1, 'Staff Member 1', 'staff1@gearrent.com'),
('staff2', 'staff123', 'STAFF', 2, 'Staff Member 2', 'staff2@gearrent.com');

-- Insert Sample Equipment (20+ items)
INSERT INTO equipment (equipment_code, category_id, brand, model, purchase_year, daily_base_price, security_deposit, status, branch_id) VALUES
-- Panadura Branch (BR001)
('EQ001', 1, 'Canon', 'EOS R5', 2023, 15000, 150000, 'AVAILABLE', 1),
('EQ002', 1, 'Sony', 'A7R IV', 2022, 14000, 140000, 'AVAILABLE', 1),
('EQ003', 2, 'Canon', 'RF 28-70mm', 2023, 5000, 50000, 'AVAILABLE', 1),
('EQ004', 2, 'Sony', 'FE 24-70mm', 2022, 4500, 45000, 'AVAILABLE', 1),
('EQ005', 3, 'DJI', 'Air 3S', 2023, 20000, 200000, 'AVAILABLE', 1),
('EQ006', 3, 'DJI', 'Mini 3 Pro', 2022, 12000, 120000, 'RENTED', 1),
('EQ007', 4, 'Neewer', 'LED Panel Kit', 2023, 8000, 80000, 'AVAILABLE', 1),
('EQ008', 4, 'Aputure', 'MC 4-Light', 2022, 10000, 100000, 'AVAILABLE', 1),
('EQ009', 5, 'Rode', 'Wireless GO II', 2023, 6000, 60000, 'AVAILABLE', 1),
('EQ010', 5, 'Sennheiser', 'EW 100 G4', 2022, 7000, 70000, 'AVAILABLE', 1),
-- Galle Branch (BR002)
('EQ011', 1, 'Nikon', 'Z9', 2023, 16000, 160000, 'AVAILABLE', 2),
('EQ012', 1, 'Panasonic', 'S1H', 2022, 13000, 130000, 'AVAILABLE', 2),
('EQ013', 2, 'Nikon', 'Z 24-70mm', 2023, 5500, 55000, 'RESERVED', 2),
('EQ014', 2, 'Panasonic', 'S 24-105mm', 2022, 4800, 48000, 'AVAILABLE', 2),
('EQ015', 3, 'Auterion', 'Freefly Alta X', 2023, 25000, 250000, 'UNDER_MAINTENANCE', 2),
('EQ016', 3, 'Yuneec', 'H520E', 2021, 11000, 110000, 'AVAILABLE', 2),
('EQ017', 4, 'Godox', 'SL-60W', 2023, 9000, 90000, 'AVAILABLE', 2),
('EQ018', 4, 'Manfrotto', 'Litemat Spectrum', 2022, 7500, 75000, 'AVAILABLE', 2),
('EQ019', 5, 'Audio-Technica', 'AT4022', 2023, 8000, 80000, 'AVAILABLE', 2),
('EQ020', 5, 'Shure', 'KSM141/32', 2022, 7500, 75000, 'AVAILABLE', 2),
-- Colombo Branch (BR003)
('EQ021', 1, 'Blackmagic', 'Ursa Mini Pro', 2023, 18000, 180000, 'AVAILABLE', 3),
('EQ022', 1, 'RED', 'Komodo', 2022, 20000, 200000, 'RENTED', 3),
('EQ023', 2, 'Canon', 'RF 70-200mm', 2023, 6000, 60000, 'AVAILABLE', 3),
('EQ024', 2, 'Sony', 'FE 70-200mm', 2022, 5500, 55000, 'AVAILABLE', 3),
('EQ025', 3, 'Freefly', 'Alta X', 2023, 30000, 300000, 'AVAILABLE', 3);

-- Insert Sample Customers (10+)
INSERT INTO customers (customer_code, customer_name, nic_passport, contact_number, email, address, membership_level) VALUES
('CUST001', 'Ramesh Perera', '200123456789', '0771234567', 'ramesh@email.com', '123 Colombo Road', 'GOLD'),
('CUST002', 'Kamala Silva', '199654321098', '0772345678', 'kamala@email.com', '456 Galle Road', 'SILVER'),
('CUST003', 'Nuwan Jayasekara', '200234567890', '0773456789', 'nuwan@email.com', '789 Kandy Road', 'REGULAR'),
('CUST004', 'Priya Menon', '199765432109', '0774567890', 'priya@email.com', '321 Jaffna Street', 'GOLD'),
('CUST005', 'Arjun Kapoor', '200345678901', '0775678901', 'arjun@email.com', '654 Matara Avenue', 'SILVER'),
('CUST006', 'Samantha Wijesinghe', '199876543210', '0776789012', 'samantha@email.com', '987 Batticaloa Lane', 'REGULAR'),
('CUST007', 'Vikram Reddy', '200456789012', '0777890123', 'vikram@email.com', '111 Trincomalee Road', 'GOLD'),
('CUST008', 'Nisha Sharma', '199987654321', '0778901234', 'nisha@email.com', '222 Ampara Street', 'SILVER'),
('CUST009', 'Rohit Bhat', '200567890123', '0779012345', 'rohit@email.com', '333 Kurunegala Avenue', 'REGULAR'),
('CUST010', 'Anjali Desai', '200678901234', '0780123456', 'anjali@email.com', '444 Ratnapura Road', 'GOLD');