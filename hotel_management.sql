-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 15, 2025 at 02:43 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hotel_management`
--

-- --------------------------------------------------------

--
-- Table structure for table `checkouts`
--

CREATE TABLE `checkouts` (
  `checkout_id` int(11) NOT NULL,
  `reservation_id` int(11) NOT NULL,
  `customer_id` varchar(10) NOT NULL,
  `room_number` varchar(10) NOT NULL,
  `check_in_date` datetime NOT NULL,
  `check_out_date` datetime DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `checkouts`
--

INSERT INTO `checkouts` (`checkout_id`, `reservation_id`, `customer_id`, `room_number`, `check_in_date`, `check_out_date`, `total_amount`, `status`) VALUES
(31, 54, 'W07', '101', '2025-03-29 15:49:51', '2025-03-29 15:51:44', 4499.00, 'Checked-out'),
(32, 55, '001', '105', '2025-03-29 15:50:35', '2025-03-29 16:17:18', 1999.00, 'Checked-out'),
(33, 56, '001', '106', '2025-03-29 15:50:35', '2025-03-29 15:52:02', 1499.00, 'Checked-out'),
(34, 58, '001', '102', '2025-03-29 16:01:38', '2025-04-06 12:31:59', 38493.00, 'Checked-out'),
(35, 57, '001', '101', '2025-03-29 16:01:38', '2025-03-29 16:02:52', 4499.00, 'Checked-out'),
(36, 59, 'W05', '106', '2025-03-29 16:02:25', '2025-04-06 12:31:59', 10493.00, 'Checked-out'),
(37, 60, '001', '105', '2025-03-29 16:15:15', '2025-03-29 16:17:18', 1999.00, 'Checked-out'),
(38, 61, '001', '108', '2025-03-29 16:15:15', '2025-04-06 12:31:59', 31493.00, 'Checked-out'),
(45, 69, '006', '105', '2025-04-12 00:00:00', '2025-04-13 00:00:00', 1999.00, 'Checked-in'),
(46, 70, 'W08', '102', '2025-04-12 21:04:17', '2025-04-13 21:04:20', 5499.00, 'Checked-in'),
(47, 71, '006', '202', '2025-04-12 00:00:00', '2025-04-15 00:00:00', 5499.00, 'Checked-in');

-- --------------------------------------------------------

--
-- Table structure for table `check_history`
--

CREATE TABLE `check_history` (
  `history_id` int(11) NOT NULL,
  `customer_id` varchar(50) NOT NULL,
  `customer_name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `room_number` varchar(10) NOT NULL,
  `room_type` varchar(50) NOT NULL,
  `capacity` varchar(10) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `check_in_date` datetime NOT NULL,
  `check_out_date` datetime NOT NULL,
  `days_stayed` int(11) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `recorded_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `check_history`
--

INSERT INTO `check_history` (`history_id`, `customer_id`, `customer_name`, `phone`, `email`, `address`, `room_number`, `room_type`, `capacity`, `price`, `check_in_date`, `check_out_date`, `days_stayed`, `total_amount`, `recorded_at`) VALUES
(33, 'W07', 'asdasd', '09121212121', '', 'asdasd', '101', 'Deluxe Room', '6', 4499.00, '2025-03-29 15:49:51', '2025-03-29 15:51:44', 1, 4499.00, '2025-03-29 07:51:50'),
(34, '001', 'Aaron Perez', '09123456777', 'aaron@gmail.com', 'bagumbong caloocan city', '106', 'Single Room', '2', 1499.00, '2025-03-29 15:50:35', '2025-03-29 15:52:02', 1, 1499.00, '2025-03-29 07:52:04'),
(35, '001', 'Aaron Perez', '09123456777', 'aaron@gmail.com', 'bagumbong caloocan city', '105', 'Double Room', '4', 1999.00, '2025-03-29 15:50:35', '2025-03-29 15:52:16', 1, 1999.00, '2025-03-29 07:52:16'),
(36, '001', 'Aaron Perez', '09123456777', 'aaron@gmail.com', 'bagumbong caloocan city', '101', 'Deluxe Room', '6', 4499.00, '2025-03-29 16:01:38', '2025-03-29 16:02:52', 1, 4499.00, '2025-03-29 08:02:57'),
(38, '001', 'Aaron Perez', '09123456777', 'aaron@gmail.com', 'bagumbong caloocan city', '105', 'Double Room', '4', 1999.00, '2025-03-29 16:15:15', '2025-03-29 16:17:18', 1, 1999.00, '2025-03-29 08:17:19'),
(39, '001', 'Aaron Perez', '09123456777', 'aaron@gmail.com', 'bagumbong caloocan city', '102', 'Suite', '8', 5499.00, '2025-03-29 16:01:38', '2025-04-06 12:31:59', 7, 38493.00, '2025-04-06 04:31:59'),
(40, 'W05', 'asdasd', '09999111111', NULL, 'cascasdas', '106', 'Single Room', '2', 1499.00, '2025-03-29 16:02:25', '2025-04-06 12:31:59', 7, 10493.00, '2025-04-06 04:31:59'),
(41, '001', 'Aaron Perez', '09123456777', 'aaron@gmail.com', 'bagumbong caloocan city', '108', 'Deluxe Room', '6', 4499.00, '2025-03-29 16:15:15', '2025-04-06 12:31:59', 7, 31493.00, '2025-04-06 04:31:59');

-- --------------------------------------------------------

--
-- Table structure for table `check_ins`
--

CREATE TABLE `check_ins` (
  `check_in_id` int(11) NOT NULL,
  `reservation_id` int(11) DEFAULT NULL,
  `customer_id` varchar(3) DEFAULT NULL,
  `customer_name` varchar(100) DEFAULT NULL,
  `customer_phone` varchar(20) DEFAULT NULL,
  `room_number` varchar(10) DEFAULT NULL,
  `check_in_date` datetime DEFAULT NULL,
  `status` enum('checked-in','checked-out') DEFAULT 'checked-in'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `check_ins`
--

INSERT INTO `check_ins` (`check_in_id`, `reservation_id`, `customer_id`, `customer_name`, `customer_phone`, `room_number`, `check_in_date`, `status`) VALUES
(13, 54, 'W07', 'asdasd', '09121212121', '101', '2025-03-29 15:49:51', 'checked-in'),
(14, 55, '001', 'Aaron Perez', '09123456777', '105', '2025-03-29 15:50:35', 'checked-in'),
(15, 56, '001', 'Aaron Perez', '09123456777', '106', '2025-03-29 15:50:35', 'checked-in'),
(16, 58, '001', 'Aaron Perez', '09123456777', '102', '2025-03-29 16:01:38', 'checked-in'),
(17, 57, '001', 'Aaron Perez', '09123456777', '101', '2025-03-29 16:01:38', 'checked-in'),
(18, 59, 'W05', 'asdasd', '09999111111', '106', '2025-03-29 16:02:25', 'checked-in'),
(19, 60, '001', 'Aaron Perez', '09123456777', '105', '2025-03-29 16:15:15', 'checked-in'),
(20, 61, '001', 'Aaron Perez', '09123456777', '108', '2025-03-29 16:15:15', 'checked-in'),
(27, 69, '006', 'JM Boticario', '09281234567', '105', '2025-04-12 00:00:00', 'checked-in'),
(28, 70, 'W08', 'LeBron', '09281334789', '102', '2025-04-12 21:04:17', 'checked-in'),
(29, 71, '006', 'JM Boticario', '09281234567', '202', '2025-04-12 00:00:00', 'checked-in');

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `customer_id` varchar(10) NOT NULL,
  `customer_fullname` varchar(100) NOT NULL,
  `customer_email` varchar(100) DEFAULT NULL,
  `customer_phoneNumber` varchar(15) NOT NULL,
  `customer_address` varchar(255) NOT NULL,
  `customer_password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`customer_id`, `customer_fullname`, `customer_email`, `customer_phoneNumber`, `customer_address`, `customer_password`) VALUES
('001', 'Aaron Perez', 'aaron@gmail.com', '09123456777', 'bagumbong caloocan city', '123'),
('002', 'Jhonalyn Perez', 'jho@gmail.com', '09123456788', 'llano, caloocan city', '123'),
('003', 'Ethan Perez', 'ethan123@gmail.com', '09992222222', 'bagumbong caloocan city', 'ethan123'),
('004', 'Kierance Perez', 'kier@gmail.com', '09912121212', 'quezon city', 'kier123'),
('006', 'JM Boticario', 'jm11@gmail.com', '09281234567', 'Camarin Caloocan City', 'jm@11'),
('007', 'James Boticario', 'james11@gmail.com', '09223456789', 'Deparo, Caloocan City', 'james@11'),
('W01', 'kiier', NULL, '09123456789', 'caloocanm', NULL),
('W02', 'ethan', NULL, '09912345678', 'caloocan', NULL),
('W03', 'kier', NULL, '09191919191', 'bagumbong', NULL),
('W05', 'asdasd', NULL, '09999111111', 'cascasdas', NULL),
('W06', 'asdasdasd', NULL, '09912121212', 'calooasd', NULL),
('W07', 'asdasd', NULL, '09121212121', 'asdasd', NULL),
('W08', 'LeBron', NULL, '09281334789', 'Novaliches, Quezon City', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `deleted_rooms`
--

CREATE TABLE `deleted_rooms` (
  `room_number` varchar(10) DEFAULT NULL,
  `room_type` varchar(50) DEFAULT NULL,
  `capacity` int(11) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `date_added` date DEFAULT NULL,
  `deleted_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `deleted_rooms`
--

INSERT INTO `deleted_rooms` (`room_number`, `room_type`, `capacity`, `price`, `status`, `date_added`, `deleted_at`) VALUES
('203', 'Single Room', 2, 1499, 'Available', '2025-04-15', '2025-04-15 00:41:20');

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `employee_id` varchar(3) NOT NULL,
  `employee_fullname` varchar(100) NOT NULL,
  `employee_phoneNumber` varchar(15) NOT NULL,
  `employee_email` varchar(100) NOT NULL,
  `employee_address` varchar(255) NOT NULL,
  `employee_password` varchar(255) NOT NULL,
  `employee_role` varchar(50) NOT NULL,
  `default_salary` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`employee_id`, `employee_fullname`, `employee_phoneNumber`, `employee_email`, `employee_address`, `employee_password`, `employee_role`, `default_salary`) VALUES
('1', 'Jasper', '09123456333', 'manager@gmail.com', 'Hilltop, Lagro Quezon City', '1234', 'Manager', 35000),
('2', 'Aaron Sean', '09123456789', 'admin@gmail.com', '123 Admin St', 'admin123', 'Admin', 40000),
('4', 'Sean', '09991212123', 'sean123@gmail.com', 'caloocan', '123', 'Manager', 35000),
('5', 'asdasd', '09999999999', 'asd@gmail.com', 'asdasd', 'asd', 'Stock', 15000),
('6', 'asd', '09900000000', 'aaron1@gmail.com', 'asdasd', '123', 'Maintenance', 20000);

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `reservation_id` int(11) NOT NULL,
  `customer_id` varchar(10) DEFAULT NULL,
  `room_number` varchar(10) NOT NULL,
  `check_in_date` datetime NOT NULL,
  `status` varchar(20) DEFAULT NULL,
  `customer_name` varchar(100) DEFAULT NULL,
  `customer_phone` varchar(20) DEFAULT NULL,
  `room_type` varchar(50) DEFAULT NULL,
  `room_capacity` int(11) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `check_out_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservations`
--

INSERT INTO `reservations` (`reservation_id`, `customer_id`, `room_number`, `check_in_date`, `status`, `customer_name`, `customer_phone`, `room_type`, `room_capacity`, `price`, `check_out_date`) VALUES
(1, '001', '105', '2025-03-19 18:01:16', 'approved', NULL, NULL, NULL, NULL, NULL, '2025-04-16 00:00:00'),
(2, '001', '105', '2025-03-19 19:04:08', 'approved', NULL, NULL, NULL, NULL, NULL, '2025-04-16 00:00:00'),
(3, '001', '108', '2025-03-20 22:41:28', 'declined', NULL, NULL, NULL, NULL, NULL, NULL),
(4, '001', '105', '2025-03-14 22:44:16', 'declined', NULL, NULL, NULL, NULL, NULL, NULL),
(5, '001', '101', '2025-03-20 22:56:36', 'approved', NULL, NULL, NULL, NULL, NULL, '2025-03-31 00:00:00'),
(6, '001', '105', '2025-03-21 00:02:27', 'Declined', NULL, NULL, NULL, NULL, NULL, '2025-03-22 00:02:32'),
(7, '001', '108', '2025-03-20 00:05:45', 'Checked-in', NULL, NULL, NULL, NULL, NULL, '2025-04-03 00:00:00'),
(8, '001', '105', '2025-03-20 00:10:36', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-16 00:00:00'),
(9, '001', '101', '2025-03-27 08:42:01', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-03-31 00:00:00'),
(10, '001', '108', '2025-03-26 11:50:41', 'Checked-in', NULL, NULL, NULL, NULL, NULL, '2025-04-03 00:00:00'),
(11, '001', '105', '2025-03-27 12:11:08', 'Checked-in', NULL, NULL, NULL, NULL, NULL, '2025-04-16 00:00:00'),
(12, '001', '102', '2025-03-28 19:41:26', 'Declined', NULL, NULL, NULL, NULL, NULL, '2025-03-29 19:41:28'),
(13, '001', '106', '2025-03-28 19:46:06', 'Declined', NULL, NULL, NULL, NULL, NULL, '2025-03-29 19:46:08'),
(14, '001', '105', '2025-03-28 19:54:57', 'Declined', NULL, NULL, NULL, NULL, NULL, '2025-03-29 19:54:58'),
(15, '001', '102', '2025-03-28 20:06:42', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(16, '001', '105', '2025-03-29 20:11:46', 'Declined', NULL, NULL, NULL, NULL, NULL, '2025-03-30 20:11:47'),
(17, '001', '102', '2025-03-28 21:37:28', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(18, '001', '105', '2025-03-28 21:52:11', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-16 00:00:00'),
(19, '001', '102', '2025-03-29 11:31:06', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(20, '001', '108', '2025-03-29 11:32:46', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-03 00:00:00'),
(27, '001', '102', '2025-03-29 12:25:04', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(28, 'W02', '102', '2025-03-29 12:46:07', 'Walk-in', NULL, NULL, NULL, NULL, NULL, NULL),
(29, 'W03', '102', '2025-03-29 00:00:00', 'Walk-in', NULL, NULL, NULL, NULL, NULL, NULL),
(30, '001', '102', '2025-03-29 12:52:55', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(31, 'W01', '105', '2025-03-29 12:53:55', 'Walk-in', NULL, NULL, NULL, NULL, NULL, NULL),
(32, '001', '102', '2025-03-29 13:01:50', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(33, '001', '105', '2025-03-29 13:01:50', 'Declined', NULL, NULL, NULL, NULL, NULL, '2025-03-30 13:01:48'),
(34, 'W01', '108', '2025-03-29 13:02:43', 'Walk-in', NULL, NULL, NULL, NULL, NULL, NULL),
(35, '001', '102', '2025-03-29 14:38:45', 'Declined', NULL, NULL, NULL, NULL, NULL, '2025-03-31 14:38:44'),
(36, '001', '105', '2025-03-29 14:38:45', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-16 00:00:00'),
(37, '001', '102', '2025-03-29 14:44:39', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(38, '001', '105', '2025-03-29 14:44:39', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-16 00:00:00'),
(40, '001', '102', '2025-03-29 14:49:03', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(41, '001', '105', '2025-03-30 14:49:03', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-16 00:00:00'),
(42, 'W05', '108', '2025-03-30 14:52:28', 'Walk-in', NULL, NULL, NULL, NULL, NULL, NULL),
(43, '001', '102', '2025-03-29 15:01:03', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(44, '001', '108', '2025-03-30 15:01:03', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-03 00:00:00'),
(45, '001', '102', '2025-03-29 15:13:39', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(46, '001', '106', '2025-03-29 15:13:39', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-29 00:00:00'),
(47, 'W06', '108', '2025-03-30 15:18:58', 'Walk-in', NULL, NULL, NULL, NULL, NULL, NULL),
(48, '001', '102', '2025-03-29 15:21:24', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(49, '001', '106', '2025-03-30 15:26:12', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-29 00:00:00'),
(50, '001', '101', '2025-03-29 15:29:58', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-03-31 00:00:00'),
(51, '001', '101', '2025-03-29 15:33:52', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-03-30 15:33:54'),
(52, '001', '102', '2025-03-30 15:33:52', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(54, 'W07', '101', '2025-03-29 15:49:51', 'Walk-in', NULL, NULL, NULL, NULL, NULL, '2025-03-31 15:49:54'),
(55, '001', '105', '2025-03-29 15:50:35', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-16 00:00:00'),
(56, '001', '106', '2025-03-29 15:50:35', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-03-31 15:50:37'),
(57, '001', '101', '2025-03-29 16:01:38', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-03-30 16:01:40'),
(58, '001', '102', '2025-03-29 16:01:38', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-01 00:00:00'),
(59, 'W05', '106', '2025-03-29 16:02:25', 'Walk-in', NULL, NULL, NULL, NULL, NULL, '2025-04-01 16:02:27'),
(60, '001', '105', '2025-03-29 16:15:15', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-03-30 16:15:16'),
(61, '001', '108', '2025-03-29 16:15:15', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-03-31 16:15:16'),
(69, '006', '105', '2025-04-12 00:00:00', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-13 00:00:00'),
(70, 'W08', '102', '2025-04-12 21:04:17', 'Walk-in', NULL, NULL, NULL, NULL, NULL, '2025-04-13 21:04:20'),
(71, '006', '202', '2025-04-12 00:00:00', 'Approved', NULL, NULL, NULL, NULL, NULL, '2025-04-15 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `rooms`
--

CREATE TABLE `rooms` (
  `room_number` varchar(10) NOT NULL,
  `room_type` varchar(50) NOT NULL,
  `capacity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL,
  `date_added` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `rooms`
--

INSERT INTO `rooms` (`room_number`, `room_type`, `capacity`, `price`, `status`, `date_added`) VALUES
('101', 'Deluxe Room', 6, 4499.00, 'Not Available', '2025-03-19'),
('102', 'Suite', 8, 5499.00, 'Occupied', '2025-03-18'),
('105', 'Double Room', 4, 1999.00, 'Occupied', '2025-03-18'),
('106', 'Single Room', 2, 1499.00, 'Available', '2025-03-19'),
('108', 'Deluxe Room', 6, 4499.00, 'Not Available', '2025-03-19'),
('201', 'Queen Room', 6, 3499.00, 'Available', '2025-04-07'),
('202', 'Suite', 8, 5499.00, 'Occupied', '2025-04-09');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `checkouts`
--
ALTER TABLE `checkouts`
  ADD PRIMARY KEY (`checkout_id`),
  ADD KEY `reservation_id` (`reservation_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `room_number` (`room_number`);

--
-- Indexes for table `check_history`
--
ALTER TABLE `check_history`
  ADD PRIMARY KEY (`history_id`);

--
-- Indexes for table `check_ins`
--
ALTER TABLE `check_ins`
  ADD PRIMARY KEY (`check_in_id`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `email` (`customer_email`),
  ADD UNIQUE KEY `customer_email` (`customer_email`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`employee_id`),
  ADD UNIQUE KEY `employee_email` (`employee_email`);

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`reservation_id`),
  ADD KEY `room_number` (`room_number`),
  ADD KEY `reservations_ibfk_1` (`customer_id`);

--
-- Indexes for table `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`room_number`),
  ADD UNIQUE KEY `room_number` (`room_number`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `checkouts`
--
ALTER TABLE `checkouts`
  MODIFY `checkout_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT for table `check_history`
--
ALTER TABLE `check_history`
  MODIFY `history_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT for table `check_ins`
--
ALTER TABLE `check_ins`
  MODIFY `check_in_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `reservation_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=72;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `checkouts`
--
ALTER TABLE `checkouts`
  ADD CONSTRAINT `checkouts_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`),
  ADD CONSTRAINT `checkouts_ibfk_3` FOREIGN KEY (`room_number`) REFERENCES `rooms` (`room_number`);

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`room_number`) REFERENCES `rooms` (`room_number`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
