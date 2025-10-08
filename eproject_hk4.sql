-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th10 08, 2025 lúc 07:18 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `eproject_hk4`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `addresses`
--

CREATE TABLE `addresses` (
  `id` bigint(20) NOT NULL,
  `city` varchar(120) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `district` varchar(120) DEFAULT NULL,
  `full_name` varchar(120) NOT NULL,
  `is_default` bit(1) NOT NULL,
  `line1` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `ward` varchar(120) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `addresses`
--

INSERT INTO `addresses` (`id`, `city`, `created_at`, `district`, `full_name`, `is_default`, `line1`, `phone`, `updated_at`, `ward`, `user_id`) VALUES
(1, 'TP.HCM', '2025-08-28 19:23:21.000000', 'Quận 1', 'Nguyễn Văn A', b'1', '123 Lê Lợi', '0911111111', '2025-08-28 19:25:13.000000', 'Phường Bến Nghé', 1),
(2, 'TP.HCM', '2025-08-28 19:39:15.000000', 'Quận 1', 'Nguyễn Văn A', b'0', '456 Nguyễn Huệ', '0922222222', '2025-08-28 19:39:15.000000', 'Phường Bến Thành', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `carts`
--

CREATE TABLE `carts` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `carts`
--

INSERT INTO `carts` (`id`, `user_id`) VALUES
(1, 1),
(2, 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `cart_items`
--

CREATE TABLE `cart_items` (
  `id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `cart_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `variant_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `cart_items`
--

INSERT INTO `cart_items` (`id`, `quantity`, `cart_id`, `product_id`, `variant_id`) VALUES
(70, 1, 1, 3, NULL),
(71, 1, 1, 10, 9);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `categories`
--

CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `slug` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `categories`
--

INSERT INTO `categories` (`id`, `created_at`, `name`, `slug`, `updated_at`, `parent_id`) VALUES
(1, '2025-08-07 23:07:41.000000', 'Men\'s fashion', 'men-fashion', '2025-08-07 23:07:41.000000', NULL),
(5, '2025-08-08 01:03:58.000000', 'Vest', 'men-fashion/vest', '2025-08-08 01:03:58.000000', 1),
(6, '2025-08-08 01:07:58.000000', 'Blazer', 'men-fashion/blazer', '2025-08-08 01:07:58.000000', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chat_messages`
--

CREATE TABLE `chat_messages` (
  `id` bigint(20) NOT NULL,
  `content` text DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `read_at` datetime(6) DEFAULT NULL,
  `type` enum('IMAGE','SYSTEM','TEXT') NOT NULL,
  `room_id` bigint(20) NOT NULL,
  `sender_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chat_rooms`
--

CREATE TABLE `chat_rooms` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `user_a_id` bigint(20) NOT NULL,
  `user_b_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `chat_rooms`
--

INSERT INTO `chat_rooms` (`id`, `created_at`, `user_a_id`, `user_b_id`) VALUES
(1, '2025-08-14 19:54:13.000000', 1, 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `message` varchar(255) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `reference_id` bigint(20) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `notifications`
--

INSERT INTO `notifications` (`id`, `created_at`, `message`, `is_read`, `reference_id`, `type`, `user_id`) VALUES
(1, '2025-08-08 20:17:46.000000', 'thong bao test', b'1', NULL, 'SYSTEM', 1),
(2, '2025-08-08 20:18:24.000000', 'thong bao test3333', b'0', NULL, 'SYSTEM', 1),
(3, '2025-08-14 18:50:51.000000', 'Đơn hàng #7 chuyển trạng thái: PENDING → PROCESSING', b'0', 7, 'ORDER_UPDATED', 1),
(4, '2025-08-14 18:51:43.000000', 'Đơn hàng #9 chuyển trạng thái: PENDING → CANCELLED', b'1', 9, 'ORDER_UPDATED', 1),
(5, '2025-08-14 18:51:43.000000', 'Đơn hàng #10 chuyển trạng thái: PENDING → CANCELLED', b'1', 10, 'ORDER_UPDATED', 1),
(6, '2025-08-14 18:51:43.000000', 'Đơn hàng #11 chuyển trạng thái: PENDING → CANCELLED', b'1', 11, 'ORDER_UPDATED', 1),
(7, '2025-08-14 18:51:43.000000', 'Đơn hàng #12 chuyển trạng thái: PENDING → CANCELLED', b'1', 12, 'ORDER_UPDATED', 1),
(8, '2025-08-14 18:51:43.000000', 'Đơn hàng #13 chuyển trạng thái: PENDING → CANCELLED', b'1', 13, 'ORDER_UPDATED', 1),
(9, '2025-08-14 18:51:43.000000', 'Đơn hàng #14 chuyển trạng thái: PENDING → CANCELLED', b'1', 14, 'ORDER_UPDATED', 1),
(10, '2025-08-14 18:51:43.000000', 'Đơn hàng #15 chuyển trạng thái: PENDING → CANCELLED', b'0', 15, 'ORDER_UPDATED', 1),
(11, '2025-08-14 18:51:43.000000', 'Đơn hàng #16 chuyển trạng thái: PENDING → CANCELLED', b'0', 16, 'ORDER_UPDATED', 1),
(12, '2025-08-14 18:51:43.000000', 'Đơn hàng #17 chuyển trạng thái: PENDING → CANCELLED', b'0', 17, 'ORDER_UPDATED', 1),
(13, '2025-08-19 21:55:01.000000', 'Đơn hàng #22 chuyển trạng thái: PENDING → CANCELLED', b'1', 22, 'ORDER_UPDATED', 1),
(14, '2025-08-20 01:01:18.000000', 'Đơn hàng #28 chuyển trạng thái: PENDING → CANCELLED', b'0', 28, 'ORDER_UPDATED', 1),
(15, '2025-08-20 01:16:18.000000', 'Đơn hàng #29 chuyển trạng thái: PENDING → CANCELLED', b'0', 29, 'ORDER_UPDATED', 1),
(16, '2025-08-23 20:53:31.000000', 'Đơn hàng #35 chuyển trạng thái: PENDING → CANCELLED', b'0', 35, 'ORDER_UPDATED', 1),
(17, '2025-08-23 20:53:31.000000', 'Đơn hàng #36 chuyển trạng thái: PENDING → CANCELLED', b'0', 36, 'ORDER_UPDATED', 1),
(18, '2025-08-23 20:58:31.000000', 'Đơn hàng #37 chuyển trạng thái: PENDING → CANCELLED', b'0', 37, 'ORDER_UPDATED', 1),
(19, '2025-08-23 20:58:31.000000', 'Đơn hàng #38 chuyển trạng thái: PENDING → CANCELLED', b'0', 38, 'ORDER_UPDATED', 1),
(20, '2025-08-23 21:03:31.000000', 'Đơn hàng #39 chuyển trạng thái: PENDING → CANCELLED', b'0', 39, 'ORDER_UPDATED', 1),
(21, '2025-08-28 18:24:45.000000', 'Đơn hàng #7 chuyển trạng thái: PROCESSING → SHIPPING', b'0', 7, 'ORDER_UPDATED', 1),
(22, '2025-08-28 18:24:47.000000', 'Đơn hàng #7 chuyển trạng thái: SHIPPING → DELIVERED', b'0', 7, 'ORDER_UPDATED', 1),
(23, '2025-08-28 18:24:55.000000', 'Đơn hàng #7 chuyển trạng thái: DELIVERED → COMPLETED', b'0', 7, 'ORDER_UPDATED', 1),
(24, '2025-08-28 19:08:19.000000', 'Đơn hàng #40 chuyển trạng thái: PENDING → CANCELLED', b'0', 40, 'ORDER_UPDATED', 1),
(25, '2025-09-16 18:37:40.000000', 'Đơn hàng #45 chuyển trạng thái: PAID → PROCESSING', b'0', 45, 'ORDER_UPDATED', 1),
(26, '2025-09-16 18:38:08.000000', 'Đơn hàng #45 chuyển trạng thái: PROCESSING → SHIPPING', b'0', 45, 'ORDER_UPDATED', 1),
(27, '2025-09-16 18:41:11.000000', 'Đơn hàng #45 chuyển trạng thái: SHIPPING → DELIVERED', b'0', 45, 'ORDER_UPDATED', 1),
(28, '2025-09-16 18:41:20.000000', 'Đơn hàng #45 chuyển trạng thái: DELIVERED → COMPLETED', b'0', 45, 'ORDER_UPDATED', 1),
(29, '2025-09-16 18:43:57.000000', 'Đơn hàng #41 chuyển trạng thái: PENDING → CANCELLED', b'0', 41, 'ORDER_UPDATED', 1),
(30, '2025-09-16 18:49:29.000000', 'Đơn hàng #42 chuyển trạng thái: PENDING → CANCELLED', b'0', 42, 'ORDER_UPDATED', 1),
(31, '2025-09-16 18:53:30.000000', 'Đơn hàng #43 chuyển trạng thái: PENDING → CANCELLED', b'0', 43, 'ORDER_UPDATED', 1),
(32, '2025-09-16 18:55:42.000000', 'Đơn hàng #44 chuyển trạng thái: PENDING → CANCELLED', b'0', 44, 'ORDER_UPDATED', 1),
(33, '2025-09-16 19:28:04.000000', 'Đơn hàng #47 chuyển trạng thái: PAID → PROCESSING', b'0', 47, 'ORDER_UPDATED', 1),
(34, '2025-09-16 19:30:11.000000', 'Đơn hàng #47 chuyển trạng thái: PROCESSING → SHIPPING', b'0', 47, 'ORDER_UPDATED', 1),
(35, '2025-09-16 19:30:13.000000', 'Đơn hàng #47 chuyển trạng thái: SHIPPING → DELIVERED', b'0', 47, 'ORDER_UPDATED', 1),
(36, '2025-09-16 19:30:14.000000', 'Đơn hàng #47 chuyển trạng thái: DELIVERED → COMPLETED', b'0', 47, 'ORDER_UPDATED', 1),
(37, '2025-09-16 19:30:31.000000', 'Đơn hàng #46 chuyển trạng thái: PAID → PROCESSING', b'0', 46, 'ORDER_UPDATED', 1),
(38, '2025-09-16 19:52:45.000000', 'Đơn hàng #48 chuyển trạng thái: PAID → PROCESSING', b'0', 48, 'ORDER_UPDATED', 1),
(39, '2025-09-16 19:53:00.000000', 'Đơn hàng #48 chuyển trạng thái: PROCESSING → SHIPPING', b'0', 48, 'ORDER_UPDATED', 1),
(40, '2025-09-16 19:53:10.000000', 'Đơn hàng #48 chuyển trạng thái: SHIPPING → DELIVERED', b'0', 48, 'ORDER_UPDATED', 1),
(41, '2025-09-16 19:53:14.000000', 'Đơn hàng #48 chuyển trạng thái: DELIVERED → COMPLETED', b'0', 48, 'ORDER_UPDATED', 1),
(42, '2025-10-08 07:43:32.000000', 'Đơn hàng #49 chuyển trạng thái: PENDING → CANCELLED', b'0', 49, 'ORDER_UPDATED', 1),
(43, '2025-10-08 08:15:49.000000', 'Đơn hàng #50 chuyển trạng thái: PENDING → CANCELLED', b'0', 50, 'ORDER_UPDATED', 1),
(44, '2025-10-08 10:37:38.000000', 'Đơn hàng #51 chuyển trạng thái: PENDING → CANCELLED', b'0', 51, 'ORDER_UPDATED', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `orders`
--

CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `status` varchar(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `orders`
--

INSERT INTO `orders` (`id`, `created_at`, `status`, `user_id`) VALUES
(7, '2025-08-08 19:17:55.000000', 'COMPLETED', 1),
(9, '2025-08-09 18:58:39.000000', 'CANCELLED', 1),
(10, '2025-08-09 19:00:18.000000', 'CANCELLED', 1),
(11, '2025-08-09 19:00:55.000000', 'PAID', 1),
(12, '2025-08-09 19:14:55.000000', 'CANCELLED', 1),
(13, '2025-08-09 19:35:18.000000', 'CANCELLED', 1),
(14, '2025-08-09 19:35:53.000000', 'CANCELLED', 1),
(15, '2025-08-09 19:38:51.000000', 'CANCELLED', 1),
(16, '2025-08-09 19:56:15.000000', 'CANCELLED', 1),
(17, '2025-08-09 20:03:53.000000', 'CANCELLED', 1),
(18, '2025-08-09 20:05:17.000000', 'PAID', 1),
(19, '2025-08-19 20:47:41.000000', 'PAID', 1),
(20, '2025-08-19 21:05:47.000000', 'PAID', 1),
(21, '2025-08-19 21:09:00.000000', 'PAID', 1),
(22, '2025-08-19 21:11:40.000000', 'CANCELLED', 1),
(23, '2025-08-19 21:14:59.000000', 'PAID', 1),
(24, '2025-08-20 00:12:44.000000', 'PAID', 1),
(25, '2025-08-20 00:17:21.000000', 'PAID', 1),
(26, '2025-08-20 00:20:40.000000', 'PAID', 1),
(27, '2025-08-20 00:21:55.000000', 'PAID', 1),
(28, '2025-08-20 00:29:06.000000', 'CANCELLED', 1),
(29, '2025-08-20 00:44:07.000000', 'CANCELLED', 1),
(30, '2025-08-20 00:45:19.000000', 'PAID', 1),
(31, '2025-08-20 01:01:46.000000', 'PAID', 1),
(32, '2025-08-20 01:02:44.000000', 'PAID', 1),
(33, '2025-08-20 01:03:25.000000', 'PAID', 1),
(34, '2025-08-20 01:17:16.000000', 'PAID', 1),
(35, '2025-08-23 20:19:52.000000', 'CANCELLED', 1),
(36, '2025-08-23 20:22:16.000000', 'CANCELLED', 1),
(37, '2025-08-23 20:24:18.000000', 'CANCELLED', 1),
(38, '2025-08-23 20:25:30.000000', 'CANCELLED', 1),
(39, '2025-08-23 20:31:57.000000', 'CANCELLED', 1),
(40, '2025-08-28 18:36:18.000000', 'CANCELLED', 1),
(41, '2025-09-16 18:12:22.000000', 'CANCELLED', 1),
(42, '2025-09-16 18:19:16.000000', 'CANCELLED', 1),
(43, '2025-09-16 18:23:27.000000', 'CANCELLED', 1),
(44, '2025-09-16 18:25:41.000000', 'CANCELLED', 1),
(45, '2025-09-16 18:37:05.000000', 'COMPLETED', 1),
(46, '2025-09-16 18:48:12.000000', 'PROCESSING', 1),
(47, '2025-09-16 19:00:03.000000', 'COMPLETED', 1),
(48, '2025-09-16 19:51:45.000000', 'COMPLETED', 1),
(49, '2025-10-08 07:10:51.000000', 'CANCELLED', 1),
(50, '2025-10-08 07:44:39.000000', 'CANCELLED', 1),
(51, '2025-10-08 10:02:46.000000', 'CANCELLED', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `order_items`
--

CREATE TABLE `order_items` (
  `id` bigint(20) NOT NULL,
  `price` double NOT NULL,
  `quantity` int(11) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `variant_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `order_items`
--

INSERT INTO `order_items` (`id`, `price`, `quantity`, `order_id`, `product_id`, `variant_id`) VALUES
(1, 15.22, 2, 7, 3, NULL),
(3, 15.22, 2, 9, 3, NULL),
(4, 15.22, 2, 10, 3, NULL),
(5, 15.22, 2, 11, 3, NULL),
(6, 15.22, 2, 12, 3, NULL),
(7, 15.22, 2, 13, 3, NULL),
(8, 15.22, 2, 14, 3, NULL),
(9, 15.22, 2, 15, 3, NULL),
(10, 15.22, 2, 16, 3, NULL),
(11, 0.2, 2, 17, 3, NULL),
(12, 0.2, 2, 18, 3, NULL),
(13, 0.2, 1, 19, 3, NULL),
(14, 0.2, 1, 20, 3, NULL),
(15, 0.2, 1, 21, 3, NULL),
(16, 0.2, 2, 22, 3, NULL),
(17, 0.2, 1, 23, 3, NULL),
(18, 0.2, 1, 24, 3, NULL),
(19, 0.2, 1, 25, 3, NULL),
(20, 0.2, 1, 26, 3, NULL),
(21, 0.2, 1, 27, 3, NULL),
(22, 0.2, 1, 28, 3, NULL),
(23, 0.2, 1, 29, 3, NULL),
(24, 0.2, 1, 30, 3, NULL),
(25, 0.2, 1, 31, 3, NULL),
(26, 0.2, 1, 32, 3, NULL),
(27, 0.2, 1, 33, 3, NULL),
(28, 0.2, 1, 34, 3, NULL),
(29, 0.2, 2, 35, 3, NULL),
(30, 22, 1, 35, 5, NULL),
(31, 0.2, 1, 36, 3, NULL),
(32, 22, 1, 36, 5, NULL),
(33, 0.2, 1, 37, 3, NULL),
(34, 22, 1, 37, 5, NULL),
(35, 0.2, 1, 38, 3, NULL),
(36, 0.2, 1, 39, 3, NULL),
(37, 0.2, 1, 40, 3, NULL),
(38, 0.2, 1, 41, 3, NULL),
(39, 0.2, 1, 42, 3, NULL),
(40, 0.2, 1, 43, 3, NULL),
(41, 0.2, 1, 44, 3, NULL),
(42, 0.2, 1, 45, 3, NULL),
(43, 0.2, 1, 46, 3, NULL),
(44, 0.2, 1, 47, 3, NULL),
(45, 22, 1, 48, 5, NULL),
(46, 0.2, 1, 49, 3, NULL),
(47, 15, 1, 49, 10, NULL),
(48, 15, 1, 49, 10, NULL),
(49, 15, 1, 50, 10, 9),
(50, 15, 1, 51, 10, 10),
(51, 15, 1, 51, 10, 11);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `payments`
--

CREATE TABLE `payments` (
  `id` bigint(20) NOT NULL,
  `amount` double NOT NULL,
  `callback_data` text DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `currency` varchar(255) NOT NULL,
  `method` enum('COD','CREDIT_CARD','DEBIT_CARD','PAYOS','PAYPAL','VNPAY') NOT NULL,
  `status` enum('CANCELLED','COMPLETED','FAILED','PENDING','REFUNDED') NOT NULL,
  `transaction_id` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `order_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `payments`
--

INSERT INTO `payments` (`id`, `amount`, `callback_data`, `created_at`, `currency`, `method`, `status`, `transaction_id`, `updated_at`, `order_id`) VALUES
(1, 30.44, NULL, '2025-08-09 01:19:59.000000', 'VND', 'VNPAY', 'PENDING', '1241252151', '2025-08-09 01:19:59.000000', 7),
(2, 30.44, NULL, '2025-08-09 19:06:27.000000', 'VND', 'PAYOS', 'PENDING', '3f19ed67df1d4651b77c23c08c5cb601', '2025-08-09 19:06:27.000000', 10),
(3, 30.44, NULL, '2025-08-09 19:08:56.000000', 'VND', 'PAYOS', 'PENDING', 'd887bc367a634b148315811e66b8b7e8', '2025-08-09 19:08:56.000000', 11),
(4, 30.44, NULL, '2025-08-09 19:11:23.000000', 'VND', 'PAYOS', 'PENDING', '12fcde215fe54ff3bbaac155db1cc2be', '2025-08-09 19:11:23.000000', 11),
(5, 0, NULL, '2025-08-09 19:38:52.000000', 'VND', 'PAYOS', 'PENDING', 'e22c41b125f243518e5a9dc7ea1b8715', '2025-08-09 19:38:52.000000', 15),
(6, 0, NULL, '2025-08-09 19:56:16.000000', 'VND', 'PAYOS', 'PENDING', '5731d951a90d49aa96fa6ec30a750c4c', '2025-08-09 19:56:16.000000', 16),
(7, 0, NULL, '2025-08-09 20:05:18.000000', 'VND', 'PAYOS', 'COMPLETED', 'aba181005d5940618707c4a2cdae45e8', '2025-08-09 20:05:48.000000', 18),
(8, 0, NULL, '2025-08-19 20:47:41.000000', 'VND', 'PAYOS', 'PENDING', '31fb8ac03b4c4398a4caac42cf70f0d3', '2025-08-19 20:47:41.000000', 19),
(9, 30.44, NULL, '2025-08-19 20:47:42.000000', 'VND', 'PAYOS', 'PENDING', '958a648161ae449289cdce20a871e841', '2025-08-19 20:47:42.000000', 11),
(10, 0, NULL, '2025-08-19 21:05:47.000000', 'VND', 'PAYOS', 'PENDING', 'e2935539f22f4148ab6d8c920897482c', '2025-08-19 21:05:47.000000', 20),
(11, 30.44, NULL, '2025-08-19 21:05:48.000000', 'VND', 'PAYOS', 'PENDING', 'ffde6c8189014a39863dfea013aaa917', '2025-08-19 21:05:48.000000', 11),
(12, 0, NULL, '2025-08-19 21:09:00.000000', 'VND', 'PAYOS', 'PENDING', 'a1581da36f404fe5b080df7aa39808db', '2025-08-19 21:09:00.000000', 21),
(13, 30.44, NULL, '2025-08-19 21:09:01.000000', 'VND', 'PAYOS', 'PENDING', 'e46a62982c7041c1871342b824a1455c', '2025-08-19 21:09:01.000000', 11),
(14, 30.44, NULL, '2025-08-19 21:11:40.000000', 'VND', 'PAYOS', 'PENDING', '41537b4bfd704386a533829e4dc06172', '2025-08-19 21:11:40.000000', 11),
(15, 0, NULL, '2025-08-19 21:14:59.000000', 'VND', 'PAYOS', 'PENDING', '2c93fb9e4ad84b939a8f03a097311e33', '2025-08-19 21:14:59.000000', 23),
(16, 0, NULL, '2025-08-20 00:12:45.000000', 'VND', 'PAYOS', 'PENDING', '0e0c5986d1574c92a7a46364c5ee8160', '2025-08-20 00:12:45.000000', 24),
(17, 30.44, NULL, '2025-08-20 00:12:46.000000', 'VND', 'PAYOS', 'PENDING', '878e41534de54d85a4e5cae4d96d8501', '2025-08-20 00:12:46.000000', 11),
(18, 0, NULL, '2025-08-20 00:17:22.000000', 'VND', 'PAYOS', 'PENDING', '0b2825f98b364eeb96e438f73c5e59a0', '2025-08-20 00:17:22.000000', 25),
(19, 30.44, NULL, '2025-08-20 00:17:23.000000', 'VND', 'PAYOS', 'PENDING', 'f34ec22c1b3845f598a9b727d8248790', '2025-08-20 00:17:23.000000', 11),
(20, 0, NULL, '2025-08-20 00:20:41.000000', 'VND', 'PAYOS', 'PENDING', '4e0949a36486407d91644f94272e1551', '2025-08-20 00:20:41.000000', 26),
(21, 30.44, NULL, '2025-08-20 00:20:41.000000', 'VND', 'PAYOS', 'PENDING', '9c2cc319005141a99f8a66c93ebb01e7', '2025-08-20 00:20:41.000000', 11),
(22, 0, NULL, '2025-08-20 00:21:56.000000', 'VND', 'PAYOS', 'PENDING', '8c69479c5e1c427f81e1babc79ac04d8', '2025-08-20 00:21:56.000000', 27),
(23, 0, NULL, '2025-08-20 00:45:19.000000', 'VND', 'PAYOS', 'PENDING', '46fe054a6a5541fe905365f9b55e458e', '2025-08-20 00:45:19.000000', 30),
(24, 0, NULL, '2025-08-20 01:01:46.000000', 'VND', 'PAYOS', 'PENDING', 'b165022e1aff435a80da3d03c45c4dce', '2025-08-20 01:01:46.000000', 31),
(25, 0, NULL, '2025-08-20 01:02:45.000000', 'VND', 'PAYOS', 'PENDING', '055a05a531754e8bb3e2edb497cfb5fb', '2025-08-20 01:02:45.000000', 32),
(26, 0, NULL, '2025-08-20 01:03:26.000000', 'VND', 'PAYOS', 'PENDING', 'f02395db80f144e2a761cc8b30ed74e4', '2025-08-20 01:03:26.000000', 33),
(27, 0, NULL, '2025-08-20 01:17:16.000000', 'VND', 'PAYOS', 'PENDING', '9edcb6abc8164581bc1545a186143232', '2025-08-20 01:17:16.000000', 34),
(28, 0, NULL, '2025-08-23 20:25:31.000000', 'VND', 'PAYOS', 'PENDING', 'fad0b3082b0648f6a2293a402f0e85f0', '2025-08-23 20:25:31.000000', 38),
(29, 0, NULL, '2025-08-23 20:31:57.000000', 'VND', 'PAYOS', 'PENDING', '4458828e42544549a1f485e359af3a8d', '2025-08-23 20:31:57.000000', 39),
(30, 0, NULL, '2025-08-28 18:36:19.000000', 'VND', 'PAYOS', 'PENDING', '770d92183ab94bb8b0dda76d027eaaa6', '2025-08-28 18:36:19.000000', 40),
(31, 0, NULL, '2025-09-16 18:12:23.000000', 'VND', 'PAYOS', 'COMPLETED', '31b3ee21359241c3838c9e4845ae928e', '2025-09-16 18:13:30.000000', 41),
(32, 0, NULL, '2025-09-16 18:19:17.000000', 'VND', 'PAYOS', 'COMPLETED', '436f11bbb55e463abd1d03c9cd1a29a8', '2025-09-16 18:21:53.000000', 42),
(33, 0, NULL, '2025-09-16 18:23:28.000000', 'VND', 'PAYOS', 'COMPLETED', '88ce887fc22c4c388aa86eab300abde7', '2025-09-16 18:23:40.000000', 43),
(34, 0, NULL, '2025-09-16 18:25:42.000000', 'VND', 'PAYOS', 'COMPLETED', '20d836ae038b40db966c2c380f11030e', '2025-09-16 18:25:55.000000', 44),
(35, 0, NULL, '2025-09-16 18:37:06.000000', 'VND', 'PAYOS', 'COMPLETED', '10b383de64f947f685f325c32bfe47b8', '2025-09-16 18:37:25.000000', 45),
(36, 0, NULL, '2025-09-16 18:48:13.000000', 'VND', 'PAYOS', 'COMPLETED', '734dc17e03f1449cae6cc52b99fd4d0a', '2025-09-16 18:48:36.000000', 46),
(37, 0, NULL, '2025-09-16 19:00:04.000000', 'VND', 'PAYOS', 'COMPLETED', 'f98d374d18c14575a9fcea6ce5a4cee3', '2025-09-16 19:00:22.000000', 47),
(38, 0, NULL, '2025-09-16 19:51:45.000000', 'VND', 'PAYOS', 'COMPLETED', '570ccd76273143169d434884f3aced76', '2025-09-16 19:52:08.000000', 48);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `products`
--

CREATE TABLE `products` (
  `id` bigint(20) NOT NULL,
  `brand` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` text DEFAULT NULL,
  `dimensions` varchar(255) DEFAULT NULL,
  `discount_price` double DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `is_featured` bit(1) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `price` double NOT NULL,
  `quantity` int(11) NOT NULL,
  `rating_avg` double DEFAULT NULL,
  `rating_count` int(11) DEFAULT NULL,
  `sales_count` int(11) DEFAULT NULL,
  `sku` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','DELETED','INACTIVE') DEFAULT NULL,
  `type` enum('DIGITAL','PHYSICAL','SERVICE') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `view_count` int(11) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `seller_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `products`
--

INSERT INTO `products` (`id`, `brand`, `created_at`, `description`, `dimensions`, `discount_price`, `image_url`, `is_featured`, `name`, `price`, `quantity`, `rating_avg`, `rating_count`, `sales_count`, `sku`, `status`, `type`, `updated_at`, `view_count`, `weight`, `seller_id`) VALUES
(3, 'no', '2025-08-08 01:10:56.000000', 'Men\'s 2-button Vest with Shiny Lapels', '12x12x22', 0.05, '04d18808-37d4-403b-926d-5ad57d471bd3.png', b'1', 'Men\'s 2-button Vest with Shiny Lapels', 0.2, 993, 4, 1, NULL, 'ASBFB12BR', 'ACTIVE', NULL, '2025-08-23 20:26:46.000000', NULL, 2, 1),
(5, NULL, '2025-08-20 20:36:55.000000', 'Korean men\'s loose-fit blazer, black and cream', NULL, 12, '7e75da39-73c2-422d-87e5-b114f27be28c.png', b'0', 'Korean men\'s loose-fit blazer, black and cream', 22, 12, NULL, NULL, NULL, NULL, 'ACTIVE', NULL, '2025-08-23 20:29:05.000000', NULL, NULL, 1),
(7, 'sadv', '2025-08-24 01:18:20.000000', 'avsdva', '22', 2, 'a695a9cb-4a52-4b18-beea-ef140ff69f1f.png', b'0', 'yrdy', 1, 22, NULL, NULL, NULL, 'asd', 'ACTIVE', NULL, NULL, NULL, 24, 1),
(10, 'MyBrand', '2025-09-30 20:00:22.000000', 'Áo thun nam nữ form regular, Áo blazer nam nữ kẻ sọc unisex ARMCEO mới nhất chất liệu cotton spandex đứng form', '30x20x3', 110000, NULL, b'0', 'Áo blazer nam nữ kẻ sọc unisex ARMCEO mới nhất chất liệu cotton spandex', 15, 80, NULL, NULL, NULL, 'ARK02', 'ACTIVE', 'PHYSICAL', '2025-10-08 09:43:00.000000', NULL, 0.2, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product_category`
--

CREATE TABLE `product_category` (
  `product_id` bigint(20) NOT NULL,
  `category_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `product_category`
--

INSERT INTO `product_category` (`product_id`, `category_id`) VALUES
(3, 5),
(5, 5),
(7, 5),
(10, 5);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product_variants`
--

CREATE TABLE `product_variants` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `price` double NOT NULL,
  `sku_code` varchar(100) DEFAULT NULL,
  `stock` int(11) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `product_variants`
--

INSERT INTO `product_variants` (`id`, `active`, `image_url`, `price`, `sku_code`, `stock`, `weight`, `product_id`) VALUES
(9, b'1', '5db0b226-1af9-45bd-9731-2634bb552041.png', 130000, 'TSHIRT-RS', 10, NULL, 10),
(10, b'1', 'be30f7de-22d8-475b-90b3-a8a554cfc750.png', 130000, 'TSHIRT-RM', 8, NULL, 10),
(11, b'1', '4fb05b3e-0a89-46a2-b5bc-b98e0e4fb9e6.png', 125000, 'TSHIRT-BS', 12, NULL, 10),
(12, b'1', '212d64ec-6027-45d1-9a10-d5d16db01315.png', 125000, 'TSHIRT-BM', 9, NULL, 10);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product_variant_option`
--

CREATE TABLE `product_variant_option` (
  `variant_id` bigint(20) NOT NULL,
  `option_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `product_variant_option`
--

INSERT INTO `product_variant_option` (`variant_id`, `option_id`) VALUES
(9, 9),
(9, 11),
(10, 9),
(10, 12),
(11, 10),
(11, 11),
(12, 10),
(12, 12);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `ratings`
--

CREATE TABLE `ratings` (
  `id` bigint(20) NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `stars` int(11) NOT NULL,
  `order_item_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `ratings`
--

INSERT INTO `ratings` (`id`, `comment`, `created_at`, `stars`, `order_item_id`, `product_id`, `user_id`) VALUES
(2, 'ok phet', '2025-08-08 19:21:45.000000', 4, 1, 3, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','CUSTOMER','SELLER') NOT NULL,
  `username` varchar(50) NOT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `full_name` varchar(120) DEFAULT NULL,
  `gender` enum('FEMALE','MALE','OTHER') DEFAULT NULL,
  `last_login_at` datetime(6) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `status` enum('ACTIVE','DELETED','INACTIVE','SUSPENDED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `email`, `password`, `role`, `username`, `avatar_url`, `created_at`, `date_of_birth`, `full_name`, `gender`, `last_login_at`, `phone`, `status`, `updated_at`) VALUES
(1, 'tuan122@gmail.com', '$2a$10$HcJEQYuntmu6dNi/d.z7iuJmCyqyS.13W9xOpyj.E/6CP1cir5AJO', 'ADMIN', 'admin', '0ec38bbe-c745-4005-97d6-086ae91e4571.jpg', NULL, '2001-06-12', 'Bui Quang Khai', 'MALE', NULL, '0123455111', 'ACTIVE', NULL),
(2, 'tuan12222@gmail.com', '$2a$10$9UTRKnDDuFAq7jnc/i2vCeKhAsN051l/qzOG4jeWIFL2mgICF86x.', 'CUSTOMER', 'tuan12', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ACTIVE', NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `variant_groups`
--

CREATE TABLE `variant_groups` (
  `id` bigint(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `sort_order` int(11) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `variant_groups`
--

INSERT INTO `variant_groups` (`id`, `name`, `sort_order`, `product_id`) VALUES
(5, 'Color', 1, 10),
(6, 'Size', 2, 10);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `variant_options`
--

CREATE TABLE `variant_options` (
  `id` bigint(20) NOT NULL,
  `value` varchar(50) NOT NULL,
  `group_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `variant_options`
--

INSERT INTO `variant_options` (`id`, `value`, `group_id`) VALUES
(9, 'Black', 5),
(10, 'Gray', 5),
(12, 'M', 6),
(11, 'S', 6);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `addresses`
--
ALTER TABLE `addresses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK1fa36y2oqhao3wgg2rw1pi459` (`user_id`);

--
-- Chỉ mục cho bảng `carts`
--
ALTER TABLE `carts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK64t7ox312pqal3p7fg9o503c2` (`user_id`);

--
-- Chỉ mục cho bảng `cart_items`
--
ALTER TABLE `cart_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKpcttvuq4mxppo8sxggjtn5i2c` (`cart_id`),
  ADD KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  ADD KEY `FK5yyw1o0dor9gmxfra1dqvn4qa` (`variant_id`);

--
-- Chỉ mục cho bảng `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKoul14ho7bctbefv8jywp5v3i2` (`slug`),
  ADD KEY `FKsaok720gsu4u2wrgbk10b5n8d` (`parent_id`);

--
-- Chỉ mục cho bảng `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_room_created_at` (`room_id`,`created_at`),
  ADD KEY `FKgiqeap8ays4lf684x7m0r2729` (`sender_id`);

--
-- Chỉ mục cho bảng `chat_rooms`
--
ALTER TABLE `chat_rooms`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKeju6hcem25gj6r2nv4m0bjfvt` (`user_a_id`,`user_b_id`),
  ADD KEY `FK1tiivtwhxvbinas7fofg0h3yr` (`user_b_id`);

--
-- Chỉ mục cho bảng `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`);

--
-- Chỉ mục cho bảng `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`);

--
-- Chỉ mục cho bảng `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  ADD KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  ADD KEY `FKemq71edpbn9wsxnxncfn1algp` (`variant_id`);

--
-- Chỉ mục cho bảng `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK81gagumt0r8y3rmudcgpbk42l` (`order_id`);

--
-- Chỉ mục cho bảng `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKbgw3lyxhsml3kfqnfr45o0vbj` (`seller_id`);

--
-- Chỉ mục cho bảng `product_category`
--
ALTER TABLE `product_category`
  ADD PRIMARY KEY (`product_id`,`category_id`),
  ADD KEY `FKdswxvx2nl2032yjv609r29sdr` (`category_id`);

--
-- Chỉ mục cho bảng `product_variants`
--
ALTER TABLE `product_variants`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK2yu32kc7oo1hutibfj6jbpd09` (`product_id`,`sku_code`);

--
-- Chỉ mục cho bảng `product_variant_option`
--
ALTER TABLE `product_variant_option`
  ADD PRIMARY KEY (`variant_id`,`option_id`),
  ADD KEY `FKo7gncfutoryea8ddyl7dbgy8g` (`option_id`);

--
-- Chỉ mục cho bảng `ratings`
--
ALTER TABLE `ratings`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKr8xjr3bqlyeosrlkeshbqkpmu` (`order_item_id`),
  ADD KEY `FK228us4dg38ewge41gos8y761r` (`product_id`),
  ADD KEY `FKb3354ee2xxvdrbyq9f42jdayd` (`user_id`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  ADD UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`);

--
-- Chỉ mục cho bảng `variant_groups`
--
ALTER TABLE `variant_groups`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKhkub98y36pe8u4o9v2nk5gvmk` (`product_id`,`name`);

--
-- Chỉ mục cho bảng `variant_options`
--
ALTER TABLE `variant_options`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKtfjfawqdwk08ljxmk2dcwybng` (`group_id`,`value`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `addresses`
--
ALTER TABLE `addresses`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `carts`
--
ALTER TABLE `carts`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=72;

--
-- AUTO_INCREMENT cho bảng `categories`
--
ALTER TABLE `categories`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `chat_messages`
--
ALTER TABLE `chat_messages`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `chat_rooms`
--
ALTER TABLE `chat_rooms`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT cho bảng `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- AUTO_INCREMENT cho bảng `orders`
--
ALTER TABLE `orders`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- AUTO_INCREMENT cho bảng `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- AUTO_INCREMENT cho bảng `payments`
--
ALTER TABLE `payments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT cho bảng `products`
--
ALTER TABLE `products`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT cho bảng `product_variants`
--
ALTER TABLE `product_variants`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT cho bảng `ratings`
--
ALTER TABLE `ratings`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `variant_groups`
--
ALTER TABLE `variant_groups`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `variant_options`
--
ALTER TABLE `variant_options`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `FK1fa36y2oqhao3wgg2rw1pi459` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `carts`
--
ALTER TABLE `carts`
  ADD CONSTRAINT `FKb5o626f86h46m4s7ms6ginnop` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `cart_items`
--
ALTER TABLE `cart_items`
  ADD CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  ADD CONSTRAINT `FK5yyw1o0dor9gmxfra1dqvn4qa` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`),
  ADD CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`);

--
-- Các ràng buộc cho bảng `categories`
--
ALTER TABLE `categories`
  ADD CONSTRAINT `FKsaok720gsu4u2wrgbk10b5n8d` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`);

--
-- Các ràng buộc cho bảng `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD CONSTRAINT `FKgiqeap8ays4lf684x7m0r2729` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKhalwepod3944695ji0suwoqb9` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`);

--
-- Các ràng buộc cho bảng `chat_rooms`
--
ALTER TABLE `chat_rooms`
  ADD CONSTRAINT `FK1tiivtwhxvbinas7fofg0h3yr` FOREIGN KEY (`user_b_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKe34tsbx4b1c8ojau8122j2ke5` FOREIGN KEY (`user_a_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  ADD CONSTRAINT `FKemq71edpbn9wsxnxncfn1algp` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`),
  ADD CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Các ràng buộc cho bảng `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `FK81gagumt0r8y3rmudcgpbk42l` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);

--
-- Các ràng buộc cho bảng `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `FKbgw3lyxhsml3kfqnfr45o0vbj` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`);

--
-- Các ràng buộc cho bảng `product_category`
--
ALTER TABLE `product_category`
  ADD CONSTRAINT `FK5w81wp3eyugvi2lii94iao3fm` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  ADD CONSTRAINT `FKdswxvx2nl2032yjv609r29sdr` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);

--
-- Các ràng buộc cho bảng `product_variants`
--
ALTER TABLE `product_variants`
  ADD CONSTRAINT `FKosqitn4s405cynmhb87lkvuau` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Các ràng buộc cho bảng `product_variant_option`
--
ALTER TABLE `product_variant_option`
  ADD CONSTRAINT `FKasge4owr3mvjk5u5a5nxdti8q` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`),
  ADD CONSTRAINT `FKo7gncfutoryea8ddyl7dbgy8g` FOREIGN KEY (`option_id`) REFERENCES `variant_options` (`id`);

--
-- Các ràng buộc cho bảng `ratings`
--
ALTER TABLE `ratings`
  ADD CONSTRAINT `FK228us4dg38ewge41gos8y761r` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  ADD CONSTRAINT `FKb3354ee2xxvdrbyq9f42jdayd` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKbkoqjbfsgob50aqsir6tvhevv` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`);

--
-- Các ràng buộc cho bảng `variant_groups`
--
ALTER TABLE `variant_groups`
  ADD CONSTRAINT `FKijjkpao39qbdrqtospqpa8ggx` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Các ràng buộc cho bảng `variant_options`
--
ALTER TABLE `variant_options`
  ADD CONSTRAINT `FKnk1b6x3ipihllxxa9cqbsjee0` FOREIGN KEY (`group_id`) REFERENCES `variant_groups` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
