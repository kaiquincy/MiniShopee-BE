
--
-- Cơ sở dữ liệu: `eproject_hk4`
--

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
  `product_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
(20, '2025-08-23 21:03:31.000000', 'Đơn hàng #39 chuyển trạng thái: PENDING → CANCELLED', b'0', 39, 'ORDER_UPDATED', 1);

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
(7, '2025-08-08 19:17:55.000000', 'PROCESSING', 1),
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
(39, '2025-08-23 20:31:57.000000', 'CANCELLED', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `order_items`
--

CREATE TABLE `order_items` (
  `id` bigint(20) NOT NULL,
  `price` double NOT NULL,
  `quantity` int(11) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `order_items`
--

INSERT INTO `order_items` (`id`, `price`, `quantity`, `order_id`, `product_id`) VALUES
(1, 15.22, 2, 7, 3),
(3, 15.22, 2, 9, 3),
(4, 15.22, 2, 10, 3),
(5, 15.22, 2, 11, 3),
(6, 15.22, 2, 12, 3),
(7, 15.22, 2, 13, 3),
(8, 15.22, 2, 14, 3),
(9, 15.22, 2, 15, 3),
(10, 15.22, 2, 16, 3),
(11, 0.2, 2, 17, 3),
(12, 0.2, 2, 18, 3),
(13, 0.2, 1, 19, 3),
(14, 0.2, 1, 20, 3),
(15, 0.2, 1, 21, 3),
(16, 0.2, 2, 22, 3),
(17, 0.2, 1, 23, 3),
(18, 0.2, 1, 24, 3),
(19, 0.2, 1, 25, 3),
(20, 0.2, 1, 26, 3),
(21, 0.2, 1, 27, 3),
(22, 0.2, 1, 28, 3),
(23, 0.2, 1, 29, 3),
(24, 0.2, 1, 30, 3),
(25, 0.2, 1, 31, 3),
(26, 0.2, 1, 32, 3),
(27, 0.2, 1, 33, 3),
(28, 0.2, 1, 34, 3),
(29, 0.2, 2, 35, 3),
(30, 22, 1, 35, 5),
(31, 0.2, 1, 36, 3),
(32, 22, 1, 36, 5),
(33, 0.2, 1, 37, 3),
(34, 22, 1, 37, 5),
(35, 0.2, 1, 38, 3),
(36, 0.2, 1, 39, 3);

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
(29, 0, NULL, '2025-08-23 20:31:57.000000', 'VND', 'PAYOS', 'PENDING', '4458828e42544549a1f485e359af3a8d', '2025-08-23 20:31:57.000000', 39);

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
(7, 'sadv', '2025-08-24 01:18:20.000000', 'avsdva', '22', 2, 'a695a9cb-4a52-4b18-beea-ef140ff69f1f.png', b'0', 'yrdy', 1, 22, NULL, NULL, NULL, 'asd', 'ACTIVE', NULL, NULL, NULL, 24, 1);

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
(7, 5);

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
  `username` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `email`, `password`, `role`, `username`) VALUES
(1, 'tuan122@gmail.com', '$2a$10$HcJEQYuntmu6dNi/d.z7iuJmCyqyS.13W9xOpyj.E/6CP1cir5AJO', 'ADMIN', 'admin'),
(2, 'tuan12222@gmail.com', '$2a$10$9UTRKnDDuFAq7jnc/i2vCeKhAsN051l/qzOG4jeWIFL2mgICF86x.', 'CUSTOMER', 'tuan12');

--
-- Chỉ mục cho các bảng đã đổ
--

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
  ADD KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`);

--
-- Chỉ mục cho bảng `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`),
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
  ADD KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`);

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
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `carts`
--
ALTER TABLE `carts`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

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
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT cho bảng `orders`
--
ALTER TABLE `orders`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=40;

--
-- AUTO_INCREMENT cho bảng `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT cho bảng `payments`
--
ALTER TABLE `payments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT cho bảng `products`
--
ALTER TABLE `products`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

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
-- Các ràng buộc cho các bảng đã đổ
--

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
-- Các ràng buộc cho bảng `ratings`
--
ALTER TABLE `ratings`
  ADD CONSTRAINT `FK228us4dg38ewge41gos8y761r` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  ADD CONSTRAINT `FKb3354ee2xxvdrbyq9f42jdayd` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKbkoqjbfsgob50aqsir6tvhevv` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
