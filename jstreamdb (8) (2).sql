-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : dim. 19 avr. 2026 à 05:18
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `jstreamdb`
--

-- --------------------------------------------------------

--
-- Structure de la table `actor`
--

CREATE TABLE `actor` (
  `actor_id` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `photo_url` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `actor`
--

INSERT INTO `actor` (`actor_id`, `name`, `photo_url`) VALUES
(1, 'Ryan Reynolds', '/assets/images/actors/rayenr.jpg'),
(2, 'Scarlett Johansson', '/assets/images/actors/scarlette.jpg'),
(3, 'Tom Hanks', '/assets/images/actors/tomhanks.jpg'),
(4, 'Margot Robbie', '\\assets\\images\\actors\\Margot Robbie.jpg'),
(5, 'Leonardo DiCaprio', '\\assets\\images\\actors\\Leonardo DiCaprio.jpg'),
(6, 'Natalie Portman', '\\assets\\images\\actors\\Natalie Portman.jpg'),
(7, 'Brad Pitt', '/assets/images/actors/Brad Pitt.jpg'),
(8, 'Angelina Jolie', '\\assets\\images\\actors\\Angelina Jolie.jpg'),
(9, 'Chris Evans', '\\assets\\images\\actors\\Chris Evans.jpg'),
(10, 'Emma Stone', '\\assets\\images\\actors\\Emma Stone.jpg'),
(11, 'Winona Ryder', '\\assets\\images\\actors\\Winona Ryder.jpg'),
(12, 'David Harbour', '\\assets\\images\\actors\\davidHerbour.jpg'),
(13, 'Millie Bobby Brown', '\\assets\\images\\actors\\MBB.jpg'),
(14, 'Will Smith', '\\assets\\images\\actors\\will.jpg'),
(15, 'Mena Massoud', '\\assets\\images\\actors\\Mena Massoud.jpg'),
(16, 'Naomi Scott', '\\assets\\images\\actors\\Naomi Scott.jpg'),
(17, 'Johnny Depp', '\\assets\\images\\actors\\johnnyDeep.jpg'),
(18, 'Helena Bonham Carter', '\\assets\\images\\actors\\Helena Bonham Carter.jpg'),
(19, 'Chadwick Boseman', '\\assets\\images\\actors\\Chadwick Boseman.jpg'),
(20, 'Michael B. Jordan', '\\assets\\images\\actors\\Michael B. Jordan.jpg'),
(21, 'Lupita Nyongo', '\\assets\\images\\actors\\Lupita Nyongo.jpg'),
(22, 'Jennifer Lawrence', '\\assets\\images\\actors\\Jennifer Lawrence.jpg'),
(23, 'Josh Hutcherson', '\\assets\\images\\actors\\Josh Hutcherson.jpg'),
(24, 'Robert Downey Jr.', '\\assets\\images\\actors\\RDJ.jpg'),
(25, 'Gwyneth Paltrow', '\\assets\\images\\actors\\Gwyneth Paltrow.jpg'),
(26, 'Dwayne Johnson', '\\assets\\images\\actors\\the rock.jpg'),
(27, 'Kevin Hart', '\\assets\\images\\actors\\Kevin Hart.jpg'),
(28, 'Karen Gillan', '\\assets\\images\\actors\\Karen Gillan.jpg'),
(29, 'Ryan Gosling', '\\assets\\images\\actors\\Ryan Gosling.jpg'),
(30, 'Elle Fanning', '\\assets\\images\\actors\\Elle Fanning.jpg'),
(31, 'Keanu Reeves', '\\assets\\images\\actors\\Keanu Reeves.jpg'),
(32, 'Laurence Fishburne', '\\assets\\images\\actors\\Laurence Fishburne.jpg'),
(33, 'Tom Cruise', '\\assets\\images\\actors\\tomCruise.jpg'),
(34, 'Orlando Bloom', '\\assets\\images\\actors\\Orlando Bloom.jpg'),
(35, 'Keira Knightley', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Keira_Knightley_2014.jpg/440px-Keira_Knightley_2014.jpg'),
(36, 'Tom Holland', '\\assets\\images\\actors\\tomHollend.jpg'),
(37, 'Zendaya', '\\assets\\images\\actors\\zendeya.jpg'),
(38, 'Patrick Wilson', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/97/Patrick_Wilson_2019.jpg/440px-Patrick_Wilson_2019.jpg'),
(39, 'Vera Farmiga', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Vera_Farmiga_2014.jpg/440px-Vera_Farmiga_2014.jpg'),
(40, 'Daniel Radcliffe', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Daniel_Radcliffe_2013.jpg/440px-Daniel_Radcliffe_2013.jpg'),
(41, 'Emma Watson', '\\assets\\images\\actors\\emmaWeston.jpg'),
(42, 'Rupert Grint', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Rupert_Grint_2010.jpg/440px-Rupert_Grint_2010.jpg'),
(43, 'Dan Stevens', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e2/Dan_Stevens_2018.jpg/440px-Dan_Stevens_2018.jpg'),
(44, 'Sam Worthington', '\\assets\\images\\actors\\Sam Worthington.jpg'),
(45, 'Zoe Saldana', '\\assets\\images\\actors\\Zoe Saldana.jpg'),
(46, 'Emily Blunt', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Emily_Blunt_2016.jpg/440px-Emily_Blunt_2016.jpg'),
(47, 'Cillian Murphy', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Cillian_Murphy_2018.jpg/440px-Cillian_Murphy_2018.jpg'),
(48, 'Amy Adams', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4b/Amy_Adams_2014_Toronto.jpg/440px-Amy_Adams_2014_Toronto.jpg'),
(49, 'Jeremy Renner', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Jeremy_Renner_2015.jpg/440px-Jeremy_Renner_2015.jpg'),
(50, 'Dylan OBrien', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Dylan_O%27Brien_at_SDCC.jpg/440px-Dylan_O%27Brien_at_SDCC.jpg'),
(51, 'Rose Byrne', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Rose_Byrne_2011.jpg/440px-Rose_Byrne_2011.jpg'),
(52, 'Emilia Clarke', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/Emilia_Clarke_2018.jpg/440px-Emilia_Clarke_2018.jpg'),
(53, 'Kit Harington', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Kit_Harington_2016.jpg/440px-Kit_Harington_2016.jpg'),
(54, 'Peter Dinklage', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/14/Peter_Dinklage_2014.jpg/440px-Peter_Dinklage_2014.jpg'),
(55, 'Travis Fimmel', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Travis_Fimmel_2015.jpg/440px-Travis_Fimmel_2015.jpg'),
(56, 'Katheryn Winnick', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Katheryn_Winnick_SDCC_2015.jpg/440px-Katheryn_Winnick_SDCC_2015.jpg'),
(57, 'Henry Cavill', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Henry_Cavill_2013.jpg/440px-Henry_Cavill_2013.jpg'),
(58, 'Freya Allan', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Freya_Allan_2020.jpg/440px-Freya_Allan_2020.jpg'),
(59, 'Anya Chalotra', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Anya_Chalotra_2019.jpg/440px-Anya_Chalotra_2019.jpg'),
(60, 'Lee Jung-jae', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Lee_Jung-jae_%2801%29.jpg/440px-Lee_Jung-jae_%2801%29.jpg'),
(61, 'Park Hae-soo', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Park_Hae-soo_2022.jpg/440px-Park_Hae-soo_2022.jpg'),
(62, 'Alvaro Morte', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5b/%C3%81lvaro_Morte_2019.jpg/440px-%C3%81lvaro_Morte_2019.jpg'),
(63, 'Ursula Corbero', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/%C3%9Arsula_Corber%C3%B3_2019.jpg/440px-%C3%9Arsula_Corber%C3%B3_2019.jpg'),
(64, 'Inaki Godoy', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/I%C3%B1aki_Godoy_2023.jpg/440px-I%C3%B1aki_Godoy_2023.jpg'),
(65, 'Mackenyu', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8b/Mackenyu_2018.jpg/440px-Mackenyu_2018.jpg'),
(66, 'Emily Rudd', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/Emily_Rudd_2023.jpg/440px-Emily_Rudd_2023.jpg'),
(67, 'Gong Yoo', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/77/Gong_Yoo_2013.jpg/440px-Gong_Yoo_2013.jpg'),
(68, 'Lee Dong-wook', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Lee_Dong-wook_2016.jpg/440px-Lee_Dong-wook_2016.jpg'),
(69, 'James McAvoy', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/James_McAvoy_2018.jpg/440px-James_McAvoy_2018.jpg'),
(70, 'Daisy Ridley', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Daisy_Ridley_2016.jpg/440px-Daisy_Ridley_2016.jpg'),
(71, 'Willem Dafoe', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/81/Willem_Dafoe_by_Gage_Skidmore.jpg/440px-Willem_Dafoe_by_Gage_Skidmore.jpg'),
(72, 'Mia Wasikowska', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Mia_Wasikowska_2012.jpg/440px-Mia_Wasikowska_2012.jpg'),
(73, 'Meg Ryan', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a0/Meg_Ryan_2011.jpg/440px-Meg_Ryan_2011.jpg'),
(74, 'John Cusack', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/54/John_Cusack_2012.jpg/440px-John_Cusack_2012.jpg'),
(75, 'Angela Lansbury', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/Angela_Lansbury_2009.jpg/440px-Angela_Lansbury_2009.jpg'),
(76, 'Diana Kaarina', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/Diana_Kaarina.jpg/440px-Diana_Kaarina.jpg'),
(77, 'Ashleigh Ball', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/Ashleigh_Ball_2013.jpg/440px-Ashleigh_Ball_2013.jpg'),
(78, 'Ryan Potter', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/Ryan_Potter_2014.jpg/440px-Ryan_Potter_2014.jpg'),
(79, 'Scott Adsit', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Scott_Adsit_2014.jpg/440px-Scott_Adsit_2014.jpg'),
(80, 'T.J. Miller', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/TJ_Miller_2014.jpg/440px-TJ_Miller_2014.jpg'),
(81, 'Kelly Macdonald', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/Kelly_Macdonald_2012.jpg/440px-Kelly_Macdonald_2012.jpg'),
(82, 'Billy Connolly', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/Billy_Connolly_2011.jpg/440px-Billy_Connolly_2011.jpg'),
(83, 'Emma Thompson', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Emma_Thompson_2018.jpg/440px-Emma_Thompson_2018.jpg'),
(84, 'Freddie Highmore', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/Freddie_Highmore_2019.jpg/440px-Freddie_Highmore_2019.jpg'),
(85, 'Vin Diesel', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/06/Vin_Diesel_2014.jpg/440px-Vin_Diesel_2014.jpg'),
(86, 'Paul Walker', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Paul_Walker_2013.jpg/440px-Paul_Walker_2013.jpg'),
(87, 'Michelle Rodriguez', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/Michelle_Rodriguez_2013.jpg/440px-Michelle_Rodriguez_2013.jpg'),
(88, 'Cole Sprouse', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Cole_Sprouse_2019.jpg/440px-Cole_Sprouse_2019.jpg'),
(89, 'Haley Lu Richardson', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e8/Haley_Lu_Richardson_2019.jpg/440px-Haley_Lu_Richardson_2019.jpg'),
(90, 'Anika Noni Rose', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/Anika_Noni_Rose_2013.jpg/440px-Anika_Noni_Rose_2013.jpg'),
(91, 'Bruno Campos', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Bruno_Campos_2009.jpg/440px-Bruno_Campos_2009.jpg'),
(92, 'Idina Menzel', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/85/Idina_Menzel_2014.jpg/440px-Idina_Menzel_2014.jpg'),
(93, 'Kristen Bell', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Kristen_Bell_2019.jpg/440px-Kristen_Bell_2019.jpg'),
(94, 'Josh Gad', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/Josh_Gad_2015.jpg/440px-Josh_Gad_2015.jpg'),
(95, 'Jay Baruchel', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Jay_Baruchel_2014.jpg/440px-Jay_Baruchel_2014.jpg'),
(96, 'Gerard Butler', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f4/Gerard_Butler_2012.jpg/440px-Gerard_Butler_2012.jpg'),
(97, 'America Ferrera', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/America_Ferrera_2019.jpg/440px-America_Ferrera_2019.jpg'),
(98, 'Ray Romano', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Ray_Romano_2012.jpg/440px-Ray_Romano_2012.jpg'),
(99, 'John Leguizamo', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8e/John_Leguizamo_2014.jpg/440px-John_Leguizamo_2014.jpg'),
(100, 'Denis Leary', 'https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Denis_Leary_2014.jpg/440px-Denis_Leary_2014.jpg'),
(101, 'Auliia Cravalho', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Auli%27i_Cravalho_2018.jpg/440px-Auli%27i_Cravalho_2018.jpg'),
(102, 'Ming-Na Wen', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Ming-Na_Wen_2019.jpg/440px-Ming-Na_Wen_2019.jpg'),
(103, 'Eddie Murphy', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/Eddie_Murphy_2012.jpg/440px-Eddie_Murphy_2012.jpg'),
(104, 'Donnie Yen', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Donnie_Yen_2016.jpg/440px-Donnie_Yen_2016.jpg'),
(105, 'Eva Green', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/85/Eva_Green_2016.jpg/440px-Eva_Green_2016.jpg'),
(106, 'Asa Butterfield', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Asa_Butterfield_2019.jpg/440px-Asa_Butterfield_2019.jpg'),
(107, 'Samuel L. Jackson', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Samuel_L_Jackson_2019.jpg/440px-Samuel_L_Jackson_2019.jpg'),
(108, 'James Franco', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5b/James_Franco_2013.jpg/440px-James_Franco_2013.jpg'),
(109, 'Andy Serkis', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/Andy_Serkis_2014.jpg/440px-Andy_Serkis_2014.jpg'),
(110, 'Mike Myers', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/58/Mike_Myers_2013.jpg/440px-Mike_Myers_2013.jpg'),
(111, 'Cameron Diaz', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Cameron_Diaz_2013.jpg/440px-Cameron_Diaz_2013.jpg'),
(112, 'Mandy Moore', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Mandy_Moore_2019.jpg/440px-Mandy_Moore_2019.jpg'),
(113, 'Zachary Levi', 'https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Zachary_Levi_2019.jpg/440px-Zachary_Levi_2019.jpg'),
(114, 'Shailene Woodley', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/54/Shailene_Woodley_2014.jpg/440px-Shailene_Woodley_2014.jpg'),
(115, 'Ansel Elgort', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Ansel_Elgort_2014.jpg/440px-Ansel_Elgort_2014.jpg'),
(116, 'Naomi Watts', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Naomi_Watts_2014.jpg/440px-Naomi_Watts_2014.jpg'),
(117, 'Anne Hathaway', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b7/Anne_Hathaway_2014.jpg/440px-Anne_Hathaway_2014.jpg'),
(118, 'Octavia Spencer', 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Octavia_Spencer_2013.jpg/440px-Octavia_Spencer_2013.jpg'),
(119, 'Stanley Tucci', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Stanley_Tucci_2012.jpg/440px-Stanley_Tucci_2012.jpg'),
(120, 'Jung Yu-mi', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Jung_Yu-mi_2016.jpg/440px-Jung_Yu-mi_2016.jpg'),
(121, 'Ma Dong-seok', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/13/Ma_Dong-seok_2019.jpg/440px-Ma_Dong-seok_2019.jpg'),
(122, 'Kaya Scodelario', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Kaya_Scodelario_2014.jpg/440px-Kaya_Scodelario_2014.jpg'),
(123, 'Will Poulter', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9f/Will_Poulter_2019.jpg/440px-Will_Poulter_2019.jpg'),
(124, 'Song Kang-ho', 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/Song_Kang-ho_2019.jpg/440px-Song_Kang-ho_2019.jpg'),
(125, 'Kim Sang-kyung', 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c4/Kim_Sang-kyung_2014.jpg/440px-Kim_Sang-kyung_2014.jpg'),
(126, 'Lola Tung', 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/97/Lola_Tung_2022.jpg/440px-Lola_Tung_2022.jpg'),
(127, 'Christopher Briney', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/Christopher_Briney_2022.jpg/440px-Christopher_Briney_2022.jpg'),
(128, 'Lotfi Abdelli', 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/2b/Lotfi_Abdelli.jpg/440px-Lotfi_Abdelli.jpg'),
(129, 'Ramzi Azaiez', 'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Ramzi_Azaiez.jpg/440px-Ramzi_Azaiez.jpg'),
(130, 'Tom Kenny', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/11/Tom_Kenny_2019.jpg/440px-Tom_Kenny_2019.jpg'),
(131, 'Bill Fagerbakke', 'https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Bill_Fagerbakke_2014.jpg/440px-Bill_Fagerbakke_2014.jpg'),
(132, 'Lee Seung-gi', 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Lee_Seung-gi_2019.jpg/440px-Lee_Seung-gi_2019.jpg'),
(133, 'Lee Hee-jun', 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Lee_Hee-jun_2019.jpg/440px-Lee_Hee-jun_2019.jpg'),
(134, 'Luke Evans', 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/Luke_Evans_2014.jpg/440px-Luke_Evans_2014.jpg'),
(135, 'Ray Winstone', 'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Ray_Winstone_2012.jpg/440px-Ray_Winstone_2012.jpg'),
(136, 'Angela Bassett', '\\assets\\images\\actors\\Angela Bassett.jpg'),
(137, 'Ahn Hyo-seop', '\\assets\\images\\actors\\Ahn Hyo-seop.jpg'),
(138, 'Kim Se-jeong', '\\assets\\images\\actors\\Kim Se-jeong.jpg'),
(139, 'Kim Soo-hyun', '\\assets\\images\\actors\\Kim Soo-hyun.jpg'),
(140, 'Park Shin-hye', '\\assets\\images\\actors\\Park Shin-hye.jpg'),
(141, 'Arden Cho', '\\assets\\images\\actors\\Arden Cho.jpg'),
(142, 'Lee Byung-hun', '\\assets\\images\\actors\\Lee Byung-hun.jpg'),
(143, 'Ken Jeong', '\\assets\\images\\actors\\Ken Jeong.jpg'),
(144, 'Benedict Cumberbatch', '\\assets\\images\\actors\\Benedict Cumberbatch.jpg'),
(145, 'Jacob Batalon', '\\assets\\images\\actors\\Jacob Batalon.jpg'),
(146, 'Marisa Tomei', '\\assets\\images\\actors\\Marisa Tomei.jpg'),
(147, 'Jon Favreau', '\\assets\\images\\actors\\Jon Favreau.jpg'),
(148, 'Michael Keaton', '\\assets\\images\\actors\\Michael Keaton.jpg'),
(149, 'Sigourney Weaver', '\\assets\\images\\actors\\Sigourney Weaver.jpg'),
(150, 'Stephen Lang', '\\assets\\images\\actors\\Stephen Lang.jpg'),
(151, 'Michelle Rodriguez', '\\assets\\images\\actors\\Michelle Rodriguez.jpg'),
(152, 'CCH Pounder', '\\assets\\images\\actors\\CCH.jpeg'),
(153, 'Oona Chaplin', '\\assets\\images\\actors\\Oona Chaplin.jpg'),
(154, 'Cliff Curtis', '\\assets\\images\\actors\\Cliff Curtis.jpg'),
(155, 'Namkoong Min', '\\assets\\images\\actors\\Namkoong Min.jpg'),
(156, 'Ahn Eun-jin', '\\assets\\images\\actors\\Ahn Eun-jin.jpg'),
(157, 'Lee Hak-joo', '\\assets\\images\\actors\\Lee Hak-joo.jpg'),
(158, 'Kim Yun-ji', '\\assets\\images\\actors\\Kim Yun-ji.jpg');

-- --------------------------------------------------------

--
-- Structure de la table `category`
--

CREATE TABLE `category` (
  `category_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `category`
--

INSERT INTO `category` (`category_id`, `name`, `description`, `created_at`) VALUES
(1, 'Action', 'Fast-paced movies with stunts and combat.', '2026-03-21 20:33:30'),
(2, 'Adventure', 'Journeys and exploration of unknown worlds.', '2026-03-21 20:33:30'),
(3, 'Animation', 'Stylized visual storytelling for all ages.', '2026-03-21 20:33:30'),
(4, 'Comedy', 'Lighthearted content designed to make you laugh.', '2026-03-21 20:33:30'),
(5, 'Crime', 'Heists, detectives, and the criminal underworld.', '2026-03-21 20:33:30'),
(6, 'Documentary', 'Real-life stories and history.', '2026-03-21 20:33:30'),
(7, 'Drama', 'Emotional, character-driven narratives.', '2026-03-21 20:33:30'),
(8, 'Fantasy', 'Magic and mythical creatures.', '2026-03-21 20:33:30'),
(9, 'Horror', 'Spooky and terrifying stories.', '2026-03-21 20:33:30'),
(10, 'Mystery', 'Suspense and puzzles.', '2026-03-21 20:33:30'),
(11, 'Romance', 'Love and relationships.', '2026-03-21 20:33:30'),
(12, 'Sci-Fi', 'Future tech and space travel.', '2026-03-21 20:33:30'),
(13, 'Thriller', 'High-tension plots.', '2026-03-21 20:33:30'),
(14, 'Critically Acclaimed', 'Award-winning and top-rated hits.', '2026-03-21 20:33:30'),
(15, 'Kids & Family', 'Safe for children-oriented content.', '2026-03-21 20:33:30'),
(16, 'Anime', 'Japanese animation style.', '2026-03-21 20:33:30'),
(17, 'Trending Now', 'The most-watched titles this week.', '2026-03-21 20:33:30'),
(18, 'Musical', 'Films where music and song are central.', '2026-03-21 20:33:30');

-- --------------------------------------------------------

--
-- Structure de la table `comments`
--

CREATE TABLE `comments` (
  `comment_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `film_id` int(11) NOT NULL DEFAULT 0,
  `ep_id` int(11) DEFAULT NULL,
  `content` text NOT NULL,
  `flagged` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `comments`
--

INSERT INTO `comments` (`comment_id`, `user_id`, `film_id`, `ep_id`, `content`, `flagged`, `created_at`, `updated_at`) VALUES
(39, 1, 0, 1, 'djhsdlkfjqehfkjhsdnd', 0, '2026-04-07 18:31:14', '2026-04-07 18:31:14'),
(40, 1, 0, 1, 'djdkjchsjdcbjsdhc', 0, '2026-04-07 18:31:18', '2026-04-14 17:01:39'),
(42, 1, 10, 0, 'emna', 0, '2026-04-13 15:36:37', '2026-04-13 15:36:37'),
(47, 1, 8, 0, 'wowwwww', 0, '2026-04-13 15:50:48', '2026-04-13 15:50:48'),
(48, 1, 5, 0, 'sdsdsj', 0, '2026-04-13 15:51:34', '2026-04-13 15:51:34'),
(49, 1, 6, 0, 'wow', 0, '2026-04-13 15:52:52', '2026-04-13 15:52:52'),
(52, 6, 26, 0, 'kkjlkjljlk', 0, '2026-04-17 23:05:50', '2026-04-17 23:05:50'),
(53, 6, 26, 0, ',;n,n', 0, '2026-04-17 23:05:55', '2026-04-17 23:05:55'),
(54, 6, 26, 0, 'kj,lkjlk', 0, '2026-04-17 23:05:59', '2026-04-17 23:05:59');

-- --------------------------------------------------------

--
-- Structure de la table `episode`
--

CREATE TABLE `episode` (
  `ep_id` int(11) NOT NULL,
  `season_id` int(11) DEFAULT NULL,
  `num_episode` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `resume` text DEFAULT NULL,
  `video_url` varchar(255) DEFAULT NULL,
  `covert_url` varchar(255) DEFAULT NULL,
  `rating` float NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `released_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `episode`
--

INSERT INTO `episode` (`ep_id`, `season_id`, `num_episode`, `title`, `duration`, `resume`, `video_url`, `covert_url`, `rating`, `created_at`, `released_at`) VALUES
(1, 1, 1, 'Chapter One: The Vanishing of Will Byers', 47, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 4, '2026-03-22 00:54:40', '2026-03-02 00:27:44'),
(2, 1, 2, 'Chapter Two: The Weirdo on Maple Street', 55, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 5, '2026-03-22 00:54:40', '2026-03-01 00:27:49'),
(3, 1, 3, 'Chapter Three: Holly, Jolly', 51, NULL, '/assets/videos/films/trailors/moana.mp4', 'dfbd', 2, '2026-03-22 00:54:40', '2026-03-10 00:27:53'),
(4, 1, 4, 'Chapter Four: The Body', 50, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 00:54:40', '2026-03-17 00:27:59'),
(5, 2, 1, 'Chapter One: MADMAX', 48, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 00:54:40', NULL),
(7, 1, 5, 'Chapter Five: The Flea and the Acrobat', 53, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 02:29:45', NULL),
(8, 1, 6, 'Chapter Six: The Monster', 47, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 02:29:45', NULL),
(9, 1, 7, 'Chapter Seven: The Bathtub', 42, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 02:29:45', NULL),
(10, 1, 8, 'Chapter Eight: The Upside Down', 55, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 02:29:45', NULL),
(11, 1, 9, 'Turing\'s Legacy', 52, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 02:50:33', NULL),
(12, 1, 10, 'The Silent Server', 49, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 02:50:33', NULL),
(13, 1, 11, 'Digital Consciousness', 58, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 02:50:33', NULL),
(14, 1, 12, 'Arrival (Season Finale)', 65, NULL, '/assets/videos/films/trailors/kpopdemonhunters.mp4', 'dfbd', 0, '2026-03-22 02:50:33', NULL),
(100, 10, 1, 'Winter Is Coming', 62, 'Lord Eddard Stark is visited by his old friend King Robert Baratheon, who asks him to serve as the King\'s Hand.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 5, '2026-04-18 11:26:09', '2011-04-17 00:00:00'),
(101, 10, 2, 'The Kingsroad', 56, 'Ned heads south with the king while Jon and his party travel north to the Wall.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 5, '2026-04-18 11:26:09', '2011-04-24 00:00:00'),
(102, 10, 3, 'Lord Snow', 58, 'Jon Snow arrives at Castle Black and begins his training as a member of the Night\'s Watch.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 4, '2026-04-18 11:26:09', '2011-05-01 00:00:00'),
(103, 10, 4, 'Cripples, Bastards, and Broken Things', 56, 'Eddard investigates the death of his predecessor. Jon takes Samwell Tarly under his wing.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 4, '2026-04-18 11:26:09', '2011-05-08 00:00:00'),
(104, 10, 5, 'The Wolf and the Lion', 55, 'Catelyn has captured Tyrion and they arrive at the Eyrie. Ned confronts the queen.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 5, '2026-04-18 11:26:09', '2011-05-15 00:00:00'),
(105, 10, 6, 'A Golden Crown', 56, 'Ned rules as the King\'s Hand while Robert goes hunting. Viserys loses his patience with Khal Drogo.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 5, '2026-04-18 11:26:09', '2011-05-22 00:00:00'),
(106, 10, 7, 'You Win or You Die', 58, 'Ned uncovers the truth about Jon Arryn\'s murder. Drogo makes a vow to his wife.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 5, '2026-04-18 11:26:09', '2011-05-29 00:00:00'),
(107, 10, 8, 'The Pointy End', 54, 'The Lannisters act against the Starks. Robb Stark calls his father\'s bannermen to war.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 5, '2026-04-18 11:26:09', '2011-06-05 00:00:00'),
(108, 10, 9, 'Baelor', 56, 'Robb goes to war in the Riverlands. Ned is brought before the Great Sept of Baelor.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 5, '2026-04-18 11:26:09', '2011-06-12 00:00:00'),
(109, 10, 10, 'Fire and Blood', 53, 'Robb is named King in the North. Daenerys says farewell to Drogo and lights a funeral pyre.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', 5, '2026-04-18 11:26:09', '2011-06-19 00:00:00'),
(110, 11, 1, 'Rites of Passage', 44, 'Ragnar Lothbrok takes part in a raid on England and dreams of exploring to the West.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/couvert.jpg', 5, '2026-04-18 11:26:09', '2013-03-03 00:00:00'),
(111, 11, 2, 'Wrath of the Northmen', 44, 'Ragnar\'s daring plan to sail West gains him allies and dangerous enemies.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/couvert.jpg', 5, '2026-04-18 11:26:09', '2013-03-10 00:00:00'),
(112, 11, 3, 'Dispossessed', 44, 'The Vikings raid the monastery at Lindisfarne, bringing back riches and an English monk.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-03-17 00:00:00'),
(113, 11, 4, 'Trial', 44, 'Ragnar is put on trial for his unauthorized raid on England.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-03-24 00:00:00'),
(114, 11, 5, 'Raid', 44, 'Ragnar and his crew make a second raid on England against Earl Haraldson\'s wishes.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/couvert.jpg', 5, '2026-04-18 11:26:09', '2013-03-31 00:00:00'),
(115, 11, 6, 'Burial of the Dead', 44, 'Ragnar challenges Earl Haraldson to personal combat to settle their dispute.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/couvert.jpg', 5, '2026-04-18 11:26:09', '2013-04-07 00:00:00'),
(116, 11, 7, 'A King\'s Ransom', 44, 'Ragnar becomes the new Earl and travels to King Aelle to ransom a captured Viking.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/couvert.jpg', 5, '2026-04-18 11:26:09', '2013-04-14 00:00:00'),
(117, 11, 8, 'Sacrifice', 44, 'The Norse make a pilgrimage to the temple at Uppsala for the great sacrificial ceremony.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/couvert.jpg', 5, '2026-04-18 11:26:09', '2013-04-21 00:00:00'),
(118, 11, 9, 'All Change', 44, 'Ragnar plans his next raid on Paris. His home life grows complicated.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/couvert.jpg', 5, '2026-04-18 11:26:09', '2013-04-28 00:00:00'),
(119, 12, 1, 'The End\'s Beginning', 60, 'Geralt of Rivia is hired to lift a curse on the beast-cursed princess Renfri of Creyden.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitcher/couvert.jpg', 4, '2026-04-18 11:26:09', '2019-12-20 00:00:00'),
(120, 12, 2, 'Four Marks', 60, 'Yennefer endures brutal magical training at Aretuza while Geralt hunts a djinn.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitcher/couvert.jpg', 4, '2026-04-18 11:26:09', '2019-12-20 00:00:00'),
(121, 12, 3, 'Betrayer Moon', 60, 'Geralt is hired to lift the curse of a striga haunting the king of Temeria.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitcher/couvert.jpg', 4, '2026-04-18 11:26:09', '2019-12-20 00:00:00'),
(122, 12, 4, 'Of Banquets, Bastards and Burials', 60, 'Geralt meets young Ciri and invokes the Law of Surprise at a royal banquet.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitcher/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-12-20 00:00:00'),
(123, 12, 5, 'Bottled Appetites', 60, 'Geralt and Yennefer hunt a djinn; their relationship intensifies.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitcher/couvert.jpg', 4, '2026-04-18 11:26:09', '2019-12-20 00:00:00'),
(124, 12, 6, 'Rare Species', 60, 'Geralt joins a party hunting a dragon; Ciri finds allies in the forest.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitcher/couvert.jpg', 4, '2026-04-18 11:26:09', '2019-12-20 00:00:00'),
(125, 12, 7, 'Before a Fall', 60, 'Nilfgaard\'s army advances; Yennefer and Tissaia prepare the defense of Sodden Hill.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitcher/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-12-20 00:00:00'),
(126, 12, 8, 'Much More', 60, 'The Battle of Sodden Hill. Geralt and Ciri finally find each other.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitcher/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-12-20 00:00:00'),
(127, 13, 1, 'Red Light, Green Light', 60, '456 players enter the deadly competition and face the iconic first game.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-09-17 00:00:00'),
(128, 13, 2, 'Hell', 63, 'Players discover the identity of who runs the games and some try to escape.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-09-17 00:00:00'),
(129, 13, 3, 'The Man with the Umbrella', 60, 'The deadly honeycomb (dalgona) game tests the players\' patience and nerves.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-09-17 00:00:00'),
(130, 13, 4, 'Stick to the Team', 60, 'Players must form teams of ten for the next deadly game: tug-of-war.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-09-17 00:00:00'),
(131, 13, 5, 'A Fair World', 60, 'The remaining players face the marble game, pairing up to compete against each other.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-09-17 00:00:00'),
(132, 13, 6, 'Gganbu', 60, 'The marble game concludes with devastating emotional losses.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-09-17 00:00:00'),
(133, 13, 7, 'VIPS', 60, 'VIP guests arrive to watch the glass bridge game.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/couvert.jpg', 4, '2026-04-18 11:26:09', '2021-09-17 00:00:00'),
(134, 13, 8, 'Front Man', 60, 'Gi-hun investigates the games with a police officer; the final game is revealed.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-09-17 00:00:00'),
(135, 13, 9, 'One Lucky Day', 60, 'The final squid game. Gi-hun faces the ultimate choice after winning.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-09-17 00:00:00'),
(136, 14, 1, 'Episode 1', 53, 'Cha Hyun-soo moves into a rundown apartment and witnesses the first monstrous transformation.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(137, 14, 2, 'Episode 2', 55, 'Survivors barricade themselves inside the building as more residents transform.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(138, 14, 3, 'Episode 3', 52, 'The group discovers Hyun-soo\'s strange resistance to the monstrous virus.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(139, 14, 4, 'Episode 4', 54, 'Survivors attempt to gather supplies and deal with a growing internal conflict.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(140, 14, 5, 'Episode 5', 56, 'A mysterious soldier arrives and changes the power dynamic inside the building.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(141, 14, 6, 'Episode 6', 55, 'The monsters evolve and the survivors face their deadliest night yet.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(142, 14, 7, 'Episode 7', 54, 'Hyun-soo struggles to control his monster side as the situation grows desperate.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(143, 14, 8, 'Episode 8', 56, 'A desperate plan is formed to escape the building before the military arrives.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(144, 14, 9, 'Episode 9', 57, 'Final preparations and sacrifices are made as the escape plan is put into motion.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(145, 14, 10, 'Episode 10', 58, 'The survivors make their final stand in the building\'s lobby.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-18 00:00:00'),
(146, 15, 1, 'Episode 1', 50, 'The Professor reveals his plan to rob the Royal Mint of Spain to his hand-picked crew.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-02 00:00:00'),
(147, 15, 2, 'Episode 2', 50, 'The crew enters the mint, takes 67 hostages, and the heist begins.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-09 00:00:00'),
(148, 15, 3, 'Episode 3', 47, 'Negotiations with police begin while the crew prints money.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-16 00:00:00'),
(149, 15, 4, 'Episode 4', 50, 'Tokyo and Rio\'s relationship causes tension. The Inspector gets close to the Professor.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-23 00:00:00'),
(150, 15, 5, 'Episode 5', 49, 'A hostage is accidentally shot, raising the pressure on the crew.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-30 00:00:00'),
(151, 15, 6, 'Episode 6', 50, 'Berlin takes brutal control of the hostages as the crew faces internal conflict.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-06-06 00:00:00'),
(152, 15, 7, 'Episode 7', 50, 'The Inspector discovers who the Professor really is.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-06-13 00:00:00'),
(153, 15, 8, 'Episode 8', 50, 'The police storm the mint. The crew must improvise to survive.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-06-20 00:00:00'),
(154, 15, 9, 'Episode 9', 51, 'Berlin reveals a shocking secret about himself and the plan.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-06-27 00:00:00'),
(155, 15, 10, 'Episode 10', 51, 'The crew\'s escape route is in danger as police close in on the Professor.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-07-04 00:00:00'),
(156, 15, 11, 'Episode 11', 50, 'The crew makes a desperate final move to complete the heist.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-07-11 00:00:00'),
(157, 15, 12, 'Episode 12', 51, 'A member of the crew makes the ultimate sacrifice.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-07-18 00:00:00'),
(158, 15, 13, 'Episode 13', 58, 'The Professor\'s plan reaches its climax. The crew escapes — or do they?', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-07-25 00:00:00'),
(159, 16, 1, 'Episode 1', 50, 'Tokyo narrates the origins of the heist crew assembled by The Professor.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-02 00:00:00'),
(160, 16, 2, 'Episode 2', 50, 'Tensions rise inside the mint as hostages begin to resist the robbers.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-09 00:00:00'),
(161, 16, 3, 'Episode 3', 48, 'The Professor begins his cat-and-mouse game with the police inspector.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-16 00:00:00'),
(162, 16, 4, 'Episode 4', 50, 'A hostage situation escalates as Berlin asserts brutal authority.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-23 00:00:00'),
(163, 16, 5, 'Episode 5', 49, 'The printing of money continues while alliances inside the mint shift.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-05-30 00:00:00'),
(164, 16, 6, 'Episode 6', 50, 'The Professor\'s relationship with the inspector deepens dangerously.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-06-06 00:00:00'),
(165, 16, 7, 'Episode 7', 50, 'A police assault on the building forces the crew to adapt.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-06-13 00:00:00'),
(166, 16, 8, 'Episode 8', 51, 'Berlin\'s secrets are revealed and the crew questions his leadership.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-06-20 00:00:00'),
(167, 16, 9, 'Episode 9', 50, 'Tokyo makes a desperate move to save Rio from Berlin.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-06-27 00:00:00'),
(168, 16, 10, 'Episode 10', 51, 'The Professor is cornered but executes a daring counter-move.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-07-04 00:00:00'),
(169, 16, 11, 'Episode 11', 50, 'The crew prepares its final escape as police tighten the net.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-07-11 00:00:00'),
(170, 16, 12, 'Episode 12', 51, 'A member of the team makes a sacrifice so others can escape.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-07-18 00:00:00'),
(171, 16, 13, 'Episode 13', 58, 'The crew\'s fate is decided as the heist reaches its explosive conclusion.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-07-25 00:00:00'),
(172, 17, 1, 'Romance Dawn', 60, 'Luffy and Zoro begin their journey after recruiting the world\'s greatest swordsman.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/onepiece/couvert.jpg', 5, '2026-04-18 11:26:09', '2023-08-31 00:00:00'),
(173, 17, 2, 'The Man in the Straw Hat', 59, 'Luffy and Zoro face their first real threat from pirate hunter Helmeppo\'s father.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/onepiece/couvert.jpg', 5, '2026-04-18 11:26:09', '2023-08-31 00:00:00'),
(174, 17, 3, 'Tell No Tales', 57, 'The crew reaches the Baratie restaurant at sea and meets Sanji.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/onepiece/couvert.jpg', 5, '2026-04-18 11:26:09', '2023-08-31 00:00:00'),
(175, 17, 4, 'The Pirates Are Coming', 60, 'The crew arrives at Nami\'s village, controlled by the fishman pirate Arlong.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/onepiece/couvert.jpg', 5, '2026-04-18 11:26:09', '2023-08-31 00:00:00'),
(176, 17, 5, 'Eat at Baratie!', 60, 'A battle erupts at the Baratie. Nami\'s true mission is revealed.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/onepiece/couvert.jpg', 5, '2026-04-18 11:26:09', '2023-08-31 00:00:00'),
(177, 17, 6, 'The Chef and the Chore Boy', 60, 'Luffy discovers the depth of Nami\'s pain and vows to free her village.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/onepiece/couvert.jpg', 5, '2026-04-18 11:26:09', '2023-08-31 00:00:00'),
(178, 17, 7, 'The Girl with the Sawfish Tattoo', 60, 'The crew storms Arlong Park to free Nami\'s village in the explosive finale.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/onepiece/couvert.jpg', 5, '2026-04-18 11:26:09', '2023-08-31 00:00:00'),
(179, 17, 8, 'Worst in the East', 60, 'The Straw Hats celebrate their victory and set sail for the Grand Line.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/onepiece/couvert.jpg', 5, '2026-04-18 11:26:09', '2023-08-31 00:00:00'),
(180, 18, 1, 'Queen to Be', 57, 'A young Charlotte arrives in England to marry King George, the man she\'s never met.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/queencharlotte/couvert.jpg', 4, '2026-04-18 11:26:09', '2023-05-04 00:00:00'),
(181, 18, 2, 'Diamond of First Water', 52, 'Charlotte tries to understand her new husband\'s mysterious absences from their marriage.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/queencharlotte/couvert.jpg', 4, '2026-04-18 11:26:09', '2023-05-04 00:00:00'),
(182, 18, 3, 'Even Days', 51, 'Charlotte discovers the truth about George\'s mental condition and must decide her path.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/queencharlotte/couvert.jpg', 4, '2026-04-18 11:26:09', '2023-05-04 00:00:00'),
(183, 18, 4, 'Holding the King', 55, 'Charlotte fights to stay by George\'s side while the court schemes around her.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/queencharlotte/couvert.jpg', 4, '2026-04-18 11:26:09', '2023-05-04 00:00:00'),
(184, 18, 5, 'Gardens in Bloom', 52, 'George and Charlotte find common ground in their shared love of science and nature.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/queencharlotte/couvert.jpg', 4, '2026-04-18 11:26:09', '2023-05-04 00:00:00'),
(185, 18, 6, 'Crown Yourself', 59, 'Charlotte must choose between her husband and the throne in the season finale.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/queencharlotte/couvert.jpg', 4, '2026-04-18 11:26:09', '2023-05-04 00:00:00'),
(186, 19, 1, 'Diamond of the First Water', 60, 'The social season opens and Lady Whistledown names Daphne Bridgerton the diamond of the season.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bridgerton/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-25 00:00:00'),
(187, 19, 2, 'Shock and Delight', 57, 'Daphne\'s prospects improve dramatically after a chance encounter with the Duke of Hastings.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bridgerton/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-25 00:00:00'),
(188, 19, 3, 'Art of the Swoon', 56, 'Daphne and the Duke deepen their fake courtship while genuine feelings begin to surface.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bridgerton/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-25 00:00:00'),
(189, 19, 4, 'An Affair of Honor', 63, 'The Duke and Daphne\'s arrangement is tested when a real suitor enters the picture.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bridgerton/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-25 00:00:00'),
(190, 19, 5, 'The Duke and I', 66, 'A duel forces the Duke\'s hand and a shocking proposal follows.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bridgerton/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-25 00:00:00'),
(191, 19, 6, 'Swish', 65, 'Daphne and Simon\'s honeymoon reveals secrets that threaten their new marriage.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bridgerton/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-25 00:00:00'),
(192, 19, 7, 'Oceans Apart', 60, 'Daphne makes a drastic decision that will change her marriage forever.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bridgerton/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-25 00:00:00'),
(193, 19, 8, 'After the Rain', 68, 'The season\'s mysteries are resolved as Lady Whistledown\'s identity hangs in the balance.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bridgerton/couvert.jpg', 4, '2026-04-18 11:26:09', '2020-12-25 00:00:00'),
(194, 20, 1, 'The Lonely, Shining Goblin', 70, 'A 939-year-old goblin meets the girl said to be his human bride.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2016-12-02 00:00:00'),
(195, 20, 2, 'Episode 2', 65, 'The goblin tries to deny his growing feelings for the young woman.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2016-12-03 00:00:00'),
(196, 20, 3, 'Episode 3', 68, 'The grim reaper and the goblin\'s roommate situation grows increasingly complicated.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2016-12-09 00:00:00'),
(197, 20, 4, 'Episode 4', 65, 'Ji-eun learns the truth about her connection to the goblin\'s past life.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2016-12-10 00:00:00'),
(198, 20, 5, 'Episode 5', 67, 'The goblin begins to embrace his feelings while a dark force stirs.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2016-12-16 00:00:00'),
(199, 20, 6, 'Episode 6', 66, 'A threat from the past returns to endanger the goblin\'s bride.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2016-12-17 00:00:00'),
(200, 20, 7, 'Episode 7', 68, 'The grim reaper uncovers painful memories from his own past life.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2016-12-23 00:00:00'),
(201, 20, 8, 'Episode 8', 67, 'The goblin sword\'s true power is revealed as the truth about Ji-eun deepens.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2016-12-24 00:00:00'),
(202, 20, 9, 'Episode 9', 70, 'The goblin faces a heartbreaking decision about his immortality.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-01-06 00:00:00'),
(203, 20, 10, 'Episode 10', 65, 'Ji-eun struggles to accept the painful truth of what being the goblin\'s bride means.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-01-07 00:00:00'),
(204, 20, 11, 'Episode 11', 67, 'The goblin prepares to face his fate as enemies close in.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-01-13 00:00:00'),
(205, 20, 12, 'Episode 12', 66, 'A devastating event changes everything for the goblin and his bride.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-01-14 00:00:00'),
(206, 20, 13, 'Episode 13', 69, 'The aftermath of loss forces Ji-eun to carry on alone.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-01-20 00:00:00'),
(207, 20, 14, 'Episode 14', 68, 'The goblin returns from the void but nothing is the same.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-01-21 00:00:00'),
(208, 20, 15, 'Episode 15', 70, 'Old wounds are reopened as the final confrontation approaches.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-01-27 00:00:00'),
(209, 20, 16, 'Episode 16', 79, 'The goblin and his bride face their final destiny together in an emotional finale.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/couvert.jpg', 5, '2026-04-18 11:26:09', '2017-01-28 00:00:00'),
(210, 21, 1, 'Episode 1', 60, 'Park Si-on arrives at Bukwang University Hospital with an extraordinary gift for surgery.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-08-05 00:00:00'),
(211, 21, 2, 'Episode 2', 60, 'Si-on battles prejudice from colleagues while saving a child\'s life in the ER.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-08-06 00:00:00'),
(212, 21, 3, 'Episode 3', 60, 'A difficult surgery tests the hospital team\'s trust in Si-on\'s abilities.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-08-12 00:00:00'),
(213, 21, 4, 'Episode 4', 60, 'Si-on forms an unlikely bond with a young terminal patient.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-08-13 00:00:00'),
(214, 21, 5, 'Episode 5', 60, 'A hospital board review threatens Si-on\'s position at the hospital.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-08-19 00:00:00'),
(215, 21, 6, 'Episode 6', 60, 'Si-on must perform under extreme pressure when a mass casualty event arrives.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-08-20 00:00:00'),
(216, 21, 7, 'Episode 7', 60, 'Yoon-seo begins to see Si-on\'s true character beyond his disability.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-08-26 00:00:00'),
(217, 21, 8, 'Episode 8', 60, 'A politically sensitive patient case puts the entire pediatric department at risk.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-08-27 00:00:00'),
(218, 21, 9, 'Episode 9', 60, 'Si-on advocates boldly for a child whose parents want to give up on treatment.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-09-02 00:00:00'),
(219, 21, 10, 'Episode 10', 60, 'The hospital faces a funding crisis that could shut down the pediatric ward.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-09-03 00:00:00'),
(220, 21, 11, 'Episode 11', 60, 'Si-on risks his career to operate on a child against the director\'s orders.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-09-09 00:00:00'),
(221, 21, 12, 'Episode 12', 60, 'The bond between Si-on and Yoon-seo deepens as a new crisis hits the hospital.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-09-10 00:00:00'),
(222, 21, 13, 'Episode 13', 60, 'Si-on makes a major breakthrough in a seemingly hopeless surgical case.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-09-16 00:00:00'),
(223, 21, 14, 'Episode 14', 60, 'The hospital faces an ethical dilemma involving organ donation and family consent.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-09-17 00:00:00'),
(224, 21, 15, 'Episode 15', 60, 'Si-on confronts his own past trauma while treating a patient with a similar history.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-09-23 00:00:00'),
(225, 21, 16, 'Episode 16', 60, 'A hospital scandal threatens to destroy everything Si-on has worked for.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-09-24 00:00:00'),
(226, 21, 17, 'Episode 17', 60, 'The pediatric department\'s future hangs in the balance as the board votes.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-09-30 00:00:00'),
(227, 21, 18, 'Episode 18', 60, 'Si-on performs the most dangerous operation of his career.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-10-01 00:00:00'),
(228, 21, 19, 'Episode 19', 60, 'Final preparations are made as the season\'s central conflict reaches its peak.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-10-07 00:00:00'),
(229, 21, 20, 'Episode 20', 60, 'Si-on\'s journey comes full circle in an emotional and uplifting finale.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', 4, '2026-04-18 11:26:09', '2013-10-08 00:00:00'),
(230, 22, 1, 'Episode 1', 70, 'Hotelier Jang Man-wol coerces Goo Chan-sung into working at her peculiar hotel for ghosts.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-13 00:00:00'),
(231, 22, 2, 'Episode 2', 68, 'Chan-sung encounters his first ghost guest and must help them find peace.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-14 00:00:00'),
(232, 22, 3, 'Episode 3', 67, 'Man-wol\'s dark past begins to surface through mysterious and painful visions.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-20 00:00:00'),
(233, 22, 4, 'Episode 4', 68, 'Chan-sung learns the tragic origin story of Hotel del Luna and its owner.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-21 00:00:00'),
(234, 22, 5, 'Episode 5', 68, 'A high-profile ghost checks into the hotel, stirring up trouble.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-27 00:00:00'),
(235, 22, 6, 'Episode 6', 68, 'Man-wol confronts a painful memory from her thousand-year-old past.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-28 00:00:00'),
(236, 22, 7, 'Episode 7', 69, 'Chan-sung discovers the magical tree\'s connection to Man-wol\'s curse.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-03 00:00:00'),
(237, 22, 8, 'Episode 8', 68, 'Man-wol and Chan-sung grow closer as dangerous forces target the hotel.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-04 00:00:00'),
(238, 22, 9, 'Episode 9', 69, 'A ghost from Man-wol\'s past life returns and upends everything.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-10 00:00:00'),
(239, 22, 10, 'Episode 10', 68, 'Chan-sung\'s feelings for Man-wol deepen despite knowing her fate.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-11 00:00:00'),
(240, 22, 11, 'Episode 11', 69, 'The truth about who betrayed Man-wol in her past life is revealed.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-17 00:00:00'),
(241, 22, 12, 'Episode 12', 68, 'Man-wol prepares to face her destiny and leave the hotel forever.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-18 00:00:00'),
(242, 22, 13, 'Episode 13', 69, 'Chan-sung fights to find a way to save Man-wol from her predetermined end.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-24 00:00:00'),
(243, 22, 14, 'Episode 14', 68, 'The hotel staff says farewell as Man-wol\'s time draws near.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-25 00:00:00'),
(244, 22, 15, 'Episode 15', 69, 'Man-wol and Chan-sung spend their final days together as the curse nears its end.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-31 00:00:00'),
(245, 22, 16, 'Episode 16', 75, 'Man-wol\'s story reaches its beautiful and heartbreaking conclusion.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-09-01 00:00:00'),
(246, 23, 1, 'Episode 1', 70, 'Chef Bong-hwan\'s soul is accidentally transported into the body of Queen Cheorin in the Joseon era.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2020-12-12 00:00:00'),
(247, 23, 2, 'Episode 2', 68, 'The chef-turned-queen tries to adapt to the restrictive life of the Joseon court.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2020-12-13 00:00:00'),
(248, 23, 3, 'Episode 3', 69, 'Political intrigue inside the palace threatens the Queen\'s survival.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2020-12-19 00:00:00'),
(249, 23, 4, 'Episode 4', 68, 'The Queen\'s modern instincts clash hilariously with Joseon customs.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2020-12-20 00:00:00'),
(250, 23, 5, 'Episode 5', 69, 'A power struggle within the royal family puts the Queen at the center of danger.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2020-12-26 00:00:00'),
(251, 23, 6, 'Episode 6', 68, 'The Queen discovers a secret about the King\'s true intentions.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2020-12-27 00:00:00'),
(252, 23, 7, 'Episode 7', 69, 'Bong-hwan begins to feel genuine emotion for the people around him.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-02 00:00:00'),
(253, 23, 8, 'Episode 8', 68, 'A life-threatening event forces the Queen to act with real courage.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-03 00:00:00'),
(254, 23, 9, 'Episode 9', 70, 'The Queen and King\'s complicated relationship deepens unexpectedly.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-09 00:00:00'),
(255, 23, 10, 'Episode 10', 68, 'A palace conspiracy is uncovered that shocks everyone.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-10 00:00:00'),
(256, 23, 11, 'Episode 11', 69, 'The Queen must choose between returning to her old life or staying in Joseon.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-16 00:00:00'),
(257, 23, 12, 'Episode 12', 68, 'Shocking truths about the Queen\'s possession are revealed.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-17 00:00:00'),
(258, 23, 13, 'Episode 13', 69, 'The palace is shaken by a rebellion attempt against the King.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-23 00:00:00'),
(259, 23, 14, 'Episode 14', 68, 'The Queen puts herself in harm\'s way to protect those she has come to love.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-24 00:00:00'),
(260, 23, 15, 'Episode 15', 70, 'The final conspiracy is exposed as loyalties are tested.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-30 00:00:00'),
(261, 23, 16, 'Episode 16', 69, 'The battle for the throne reaches its peak with the Queen at the center.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-01-31 00:00:00'),
(262, 23, 17, 'Episode 17', 68, 'Bong-hwan confronts the possibility of never returning to the present.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-02-06 00:00:00'),
(263, 23, 18, 'Episode 18', 70, 'The King reveals his deepest feelings in a moving scene.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-02-07 00:00:00'),
(264, 23, 19, 'Episode 19', 68, 'The penultimate episode delivers stunning twists as the series rushes to its end.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-02-13 00:00:00'),
(265, 23, 20, 'Episode 20', 80, 'A bittersweet and satisfying conclusion to the Queen\'s extraordinary journey.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/couvert.jpg', 5, '2026-04-18 11:26:09', '2021-02-14 00:00:00'),
(286, 25, 1, 'Cruelty', 23, 'Tanjiro returns home to find his family slaughtered and sister Nezuko turned into a demon.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-04-06 00:00:00'),
(287, 25, 2, 'Trainer Sakonji Urokodaki', 23, 'Tanjiro trains under the former Water Pillar to become a Demon Slayer.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-04-13 00:00:00'),
(288, 25, 3, 'Sabito and Makomo', 23, 'During his final training Tanjiro meets two mysterious children in a foggy forest.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-04-20 00:00:00'),
(289, 25, 4, 'Final Selection', 23, 'Tanjiro enters the deadly Final Selection exam on Mt. Fujikasane.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-04-27 00:00:00'),
(290, 25, 5, 'My Own Steel', 23, 'Tanjiro receives his Nichirin Blade and begins his first mission.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-05-04 00:00:00'),
(291, 25, 6, 'Swordsman Accompanying a Demon', 23, 'Tanjiro protects Nezuko from a Demon Slayer who wants her dead.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-05-11 00:00:00'),
(292, 25, 7, 'Muzan Kibutsuji', 23, 'Tanjiro encounters Muzan Kibutsuji, the first demon and root of all evil.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-05-18 00:00:00'),
(293, 25, 8, 'The smell of Enchanting Blood', 23, 'Tanjiro fights two demons in Asakusa who were spawned by Muzan.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-05-25 00:00:00'),
(294, 25, 9, 'Temari Demon and Arrow Demon', 23, 'Tanjiro, Zenitsu and Inosuke battle the two demons in the swamp.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-06-01 00:00:00'),
(295, 25, 10, 'Together Forever', 23, 'Tanjiro defeats the Swamp Demon and rescues the kidnapped girls.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-06-08 00:00:00'),
(296, 25, 11, 'Tsuzumi Mansion', 23, 'The trio enters a tsuzumi mansion that rearranges its rooms to trap them.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-06-15 00:00:00'),
(297, 25, 12, 'The Boar Bares Its Fangs, Zenitsu Sleeps', 23, 'Inosuke attacks Tanjiro while Zenitsu fights a demon alone in his sleep.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-06-22 00:00:00');
INSERT INTO `episode` (`ep_id`, `season_id`, `num_episode`, `title`, `duration`, `resume`, `video_url`, `covert_url`, `rating`, `created_at`, `released_at`) VALUES
(298, 25, 13, 'Something More Important Than Life', 23, 'Zenitsu\'s backstory is revealed as he unleashes his true power.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-06-29 00:00:00'),
(299, 25, 14, 'The House with the Wisteria Family Crest', 23, 'The trio recovers from injuries at a hidden wisteria house.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-06 00:00:00'),
(300, 25, 15, 'Mount Natagumo', 23, 'The trio arrives at Mt. Natagumo, overrun by spider demons.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-13 00:00:00'),
(301, 25, 16, 'Letting Someone Else Go First', 23, 'Inosuke fights the spider demon mother alone on the mountain.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-20 00:00:00'),
(302, 25, 17, 'You Must Master a Single Thing', 23, 'Tanjiro faces the spider demon father in an intense battle.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-07-27 00:00:00'),
(303, 25, 18, 'A Forged Bond', 23, 'Tanjiro uses the Hinokami Kagura dance form in battle for the first time.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-03 00:00:00'),
(304, 25, 19, 'Hinokami', 23, 'The Flame Hashira Rengoku and Sound Hashira Tengen arrive on Mt. Natagumo.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-10 00:00:00'),
(305, 25, 20, 'Pretend Family', 23, 'Tanjiro confronts the spider demon Rui, a member of the Twelve Kizuki.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-17 00:00:00'),
(306, 25, 21, 'Against Corps Rules', 23, 'Tanjiro and Nezuko are brought before the Demon Slayer leader Kagaya.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-24 00:00:00'),
(307, 25, 22, 'Master of the Mansion', 23, 'Tanjiro recovers and learns more about the Hashira and Demon Slayer Corps.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-08-31 00:00:00'),
(308, 25, 23, 'Hashira Meeting', 23, 'The powerful Hashira meet and argue over Tanjiro and Nezuko\'s fate.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-09-07 00:00:00'),
(309, 25, 24, 'Rehabilitation Training', 23, 'Tanjiro and Inosuke begin their intense total concentration breathing training.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-09-14 00:00:00'),
(310, 25, 25, 'Tsuguko, Kanao Tsuyuri', 23, 'Tanjiro trains alongside Kanao, a quiet but extraordinarily gifted Demon Slayer.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-09-21 00:00:00'),
(311, 25, 26, 'New Mission', 23, 'Fully recovered, Tanjiro and friends are assigned a new mission on the Mugen Train.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/couvert.jpg', 5, '2026-04-18 11:26:09', '2019-09-28 00:00:00'),
(312, 26, 1, 'Episode 1', 65, 'Im Sol wakes up in 2008 after making a wish to save her idol Ryu Sun-jae from death.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-04-08 00:00:00'),
(313, 26, 2, 'Episode 2', 62, 'Sol tries to understand the rules of her time travel and meets the young Sun-jae.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-04-09 00:00:00'),
(314, 26, 3, 'Episode 3', 63, 'Sol begins interfering with Sun-jae\'s future to prevent his tragic destiny.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-04-15 00:00:00'),
(315, 26, 4, 'Episode 4', 62, 'Sun-jae\'s music career begins to take shape as Sol guides events from the shadows.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-04-16 00:00:00'),
(316, 26, 5, 'Episode 5', 63, 'Sol realizes her feelings for Sun-jae are becoming dangerously real.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-04-22 00:00:00'),
(317, 26, 6, 'Episode 6', 62, 'Sun-jae\'s growing feelings for Sol complicate her mission to save him.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-04-23 00:00:00'),
(318, 26, 7, 'Episode 7', 64, 'A new threat to Sun-jae\'s life emerges despite Sol\'s interventions.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-04-29 00:00:00'),
(319, 26, 8, 'Episode 8', 62, 'Sol makes a painful decision about the limits of what she can change.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-04-30 00:00:00'),
(320, 26, 9, 'Episode 9', 63, 'The two leads finally confront their feelings for one another.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-05-06 00:00:00'),
(321, 26, 10, 'Episode 10', 62, 'A tragic revelation about the original timeline shakes Sol to her core.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-05-07 00:00:00'),
(322, 26, 11, 'Episode 11', 64, 'Sol must choose between her mission and her love for Sun-jae.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-05-13 00:00:00'),
(323, 26, 12, 'Episode 12', 62, 'Sun-jae discovers Sol\'s true origins and the reason she came to him.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-05-14 00:00:00'),
(324, 26, 13, 'Episode 13', 63, 'The timeline begins to shift in unexpected ways as fate resists being changed.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-05-20 00:00:00'),
(325, 26, 14, 'Episode 14', 62, 'Sol and Sun-jae race against time to find a solution that saves them both.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-05-21 00:00:00'),
(326, 26, 15, 'Episode 15', 64, 'Everything converges in an emotionally devastating penultimate episode.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-05-27 00:00:00'),
(327, 26, 16, 'Episode 16', 70, 'Love across time finds its conclusion in a heartfelt and hopeful finale.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', 5, '2026-04-18 11:26:09', '2024-05-28 00:00:00'),
(354, 28, 1, 'Loop 1: The Night Begins', 48, 'A man arrives home to find a police officer who kills him; he wakes at the start of the same evening.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/twelveminutes/couvert.jpg', 4, '2026-04-18 11:26:09', '2021-08-19 00:00:00'),
(355, 28, 2, 'Loop 2: Desperate Attempts', 47, 'He tries different approaches to stop the intruder and save his wife across multiple loops.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/twelveminutes/couvert.jpg', 4, '2026-04-18 11:26:09', '2021-08-26 00:00:00'),
(356, 28, 3, 'Loop 3: The Truth', 46, 'A shocking revelation about his wife and the cop changes everything the man believed he knew.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/twelveminutes/couvert.jpg', 4, '2026-04-18 11:26:09', '2021-09-02 00:00:00'),
(2701, 271, 1, 'Departure × and × Friends', 23, 'Gon leaves Whale Island to take the Hunter Exam and befriends Leorio and Kurapika on the ship.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-10-02 00:00:00'),
(2702, 271, 2, 'Test × of × Tests', 23, 'The exam applicants must follow Satotz to the exam site through a dangerous fog forest.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-10-09 00:00:00'),
(2703, 271, 3, 'Rivals × for × Survival', 23, 'The first phase continues as many applicants attempt to take down Hisoka in the fog.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-10-16 00:00:00'),
(2704, 271, 4, 'Hope × and × Ambition', 23, 'The second phase begins: gourmet hunters Menchi and Buhara task applicants with cooking a wild pig.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-10-23 00:00:00'),
(2705, 271, 5, 'Hisoka × Is × Sneaky', 23, 'Menchi forces applicants to make sushi. Gon befriends Killua on the way to the next phase.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-10-30 00:00:00'),
(2706, 271, 6, 'A × Surprising × Proposal', 23, 'Chairman Netero challenges Gon and Killua to a game with the prize of passing the exam.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-11-06 00:00:00'),
(2707, 271, 7, 'Decided × by × a Coin', 23, 'The third phase begins atop Trick Tower. Gon faces Bodoro in a coin-flip battle of wills.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-11-13 00:00:00'),
(2708, 271, 8, 'Desperate × and × Cornered', 23, 'The group faces a majority vote path and Leorio duels a criminal in a gambling match.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-11-20 00:00:00'),
(2709, 271, 9, 'Beware × of × Prisoners', 23, 'Leorio\'s match continues. Killua faces a serial killer in a lethal fight to the death.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-11-27 00:00:00'),
(2710, 271, 10, 'A × Trick × and × True Ability', 23, 'The group finishes Trick Tower and proceeds. Gon\'s instincts shine against a veteran.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-12-04 00:00:00'),
(2711, 271, 11, 'Closely Knit Friends × and × the Nen Ability', 23, 'The fourth phase begins on Zevil Island — applicants must steal each other\'s number plates.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-12-11 00:00:00'),
(2712, 271, 12, 'Last Test × of × Resolve', 23, 'The island phase concludes. The final tournament bracket is announced.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-12-18 00:00:00'),
(2713, 271, 13, 'Letter × from × Gon', 23, 'The final phase tournament begins. Gon faces Hanzo in a painful one-sided battle.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2011-12-25 00:00:00'),
(2714, 271, 14, 'Hit × and × Conclusion', 23, 'Kurapika defeats Hisoka in their match with a bold declaration about the Phantom Troupe.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-01-08 00:00:00'),
(2715, 271, 15, 'Explosion × of × Deception', 23, 'Killua shockingly kills his opponent and is disqualified from the exam.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-01-15 00:00:00'),
(2716, 271, 16, 'Defeat × and × Reunion', 23, 'The Hunter Exam concludes. Gon passes and resolves to rescue Killua from his family.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-01-22 00:00:00'),
(2717, 271, 17, 'Beginners × and × Masters', 23, 'The Zoldyck arc: Gon, Leorio and Kurapika arrive at Kukuroo Mountain to rescue Killua.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-01-29 00:00:00'),
(2718, 271, 18, 'Rush × and × Restrictions', 23, 'The group faces the Zoldyck family\'s guard dogs and gate in their attempt to enter.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-02-05 00:00:00'),
(2719, 271, 19, 'Can\'t Win × and × Can\'t Lose', 23, 'The butler Gotoh tests the group\'s worthiness with a deadly coin game.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-02-12 00:00:00'),
(2720, 271, 20, 'Fake × and × Real', 23, 'Killua is finally freed by his father Zeno and Silva and reunited with his friends.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-02-19 00:00:00'),
(2721, 271, 21, 'Risky Business × Gon\'s Party', 23, 'Gon, Killua, Kurapika and Leorio celebrate before parting ways for their individual goals.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-02-26 00:00:00'),
(2722, 271, 22, 'Reality × and × Rarity', 23, 'Gon and Killua arrive at Heavens Arena and encounter its strange layout and fighters.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-03-04 00:00:00'),
(2723, 271, 23, 'The × Grim × Reaper', 23, 'Gon and Killua meet Wing, who reveals the existence of Nen and refuses to teach them.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-03-11 00:00:00'),
(2724, 271, 24, 'Condition × and × Condition', 23, 'Wing opens Gon and Killua\'s aura nodes to save their lives before a Nen user attacks.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-03-18 00:00:00'),
(2725, 271, 25, 'Defeat × and × Fury', 23, 'Gon and Killua begin learning the four major principles of Nen under Wing\'s guidance.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-03-25 00:00:00'),
(2726, 271, 26, 'Infiltration × of × Greed Island', 23, 'Gon battles Gido with his newly learned Nen. Killua also demonstrates his electric Nen.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-04-01 00:00:00'),
(2727, 272, 1, 'A × Heated × Showdown', 23, 'Gon fights Riehlvelt and wins using clever strategy and his developing Nen skills.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-04-08 00:00:00'),
(2728, 272, 2, 'Killua × and × Illumi', 23, 'Killua faces Sadaso who threatens Gon\'s life. Killua resolves the fight without fighting.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-04-15 00:00:00'),
(2729, 272, 3, 'Nen × and × Nen', 23, 'Wing teaches Gon and Killua advanced Nen concepts: Ten, Zetsu, Ren, and Hatsu.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-04-22 00:00:00'),
(2730, 272, 4, 'The × True × Pass', 23, 'Gon and Killua discover their individual Nen types through the water divination test.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-04-29 00:00:00'),
(2731, 272, 5, 'The × Rose × Blooms', 23, 'Gon develops his special ability Jajanken — a Nen-powered rock-paper-scissors attack.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-05-06 00:00:00'),
(2732, 272, 6, 'Hisoka × and × Gon', 23, 'Gon faces Hisoka at Heavens Arena floor 200 in a spectacular and brutal battle.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-05-13 00:00:00'),
(2733, 272, 7, 'The × Invisible × Hand', 23, 'Gon\'s fight with Hisoka concludes — Hisoka returns Gon\'s tag to him and passes.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-05-20 00:00:00'),
(2734, 272, 8, 'Ging × and × Gon', 23, 'Gon earns his Hunter License. Gon and Killua leave Heavens Arena for Yorknew City.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-05-27 00:00:00'),
(2735, 272, 9, 'September × and × Greed Island', 23, 'Gon and Killua arrive at Yorknew City for the underground auction to find Greed Island.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-06-03 00:00:00'),
(2736, 272, 10, 'Chasing × and × Waiting', 23, 'Kurapika tracks the Phantom Troupe who have slaughtered dozens at the underground auction.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-06-10 00:00:00'),
(2737, 272, 11, 'Gathering × of × Heroes', 23, 'The Phantom Troupe massacre Mafia members. Gon and Killua witness their overwhelming power.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-06-17 00:00:00'),
(2738, 272, 12, 'September 2nd × Part 1', 23, 'The second underground auction begins. Kurapika puts his chain trap plan into motion.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-06-24 00:00:00'),
(2739, 273, 1, 'September 2nd × Part 2', 23, 'Kurapika captures Uvogin of the Phantom Troupe using his powerful Chain Jail ability.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-07-01 00:00:00'),
(2740, 273, 2, 'Condition × and × Condition', 23, 'The Troupe leader Chrollo uses Neon\'s fortune telling ability to predict the future.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-07-08 00:00:00'),
(2741, 273, 3, 'Uvogin × and × Kurapika', 23, 'Kurapika confronts and defeats Uvogin in a one-on-one battle in the fields.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-07-15 00:00:00'),
(2742, 273, 4, 'The × Squadron × Leader', 23, 'The Phantom Troupe devises a plan to find and capture the chain user who killed Uvogin.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-07-22 00:00:00'),
(2743, 273, 5, 'Gon × and × Killua', 23, 'Gon and Killua are captured by the Phantom Troupe while investigating Kurapika.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-07-29 00:00:00'),
(2744, 273, 6, 'A Big Debt × and × a Small Kick', 23, 'Gon and Killua are held prisoner by the Phantom Troupe and must endure Nobunaga\'s tests.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-08-05 00:00:00'),
(2745, 273, 7, 'Gon × and × Killua', 23, 'Gon and Killua escape from the Phantom Troupe hideout through a narrow window.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-08-12 00:00:00'),
(2746, 273, 8, 'Pirates × and × Fetters', 23, 'The Phantom Troupe prepares to sell the stolen auction items while Kurapika plots.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-08-19 00:00:00'),
(2747, 273, 9, 'September 3rd × Part 1', 23, 'Chrollo\'s fortune reveals many Troupe members will die — he accepts the prophecy calmly.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-08-26 00:00:00'),
(2748, 273, 10, 'September 3rd × Part 2', 23, 'Kurapika captures Chrollo using his Judgment Chain, rendering him unable to use Nen.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-09-02 00:00:00'),
(2749, 273, 11, 'September 4th × Part 1', 23, 'The Troupe mourns Chrollo and plans to recover him. Gon is furious at Kurapika\'s choices.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-09-09 00:00:00'),
(2750, 273, 12, 'September 4th × Part 2', 23, 'Pakunoda\'s memories are extracted. She makes the ultimate sacrifice for her leader.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-09-16 00:00:00'),
(2751, 273, 13, 'September 6th × Part 1', 23, 'Gon, Killua, and Leorio attend the final day of the Yorknew City auction.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-09-23 00:00:00'),
(2752, 273, 14, 'September 6th × Part 2', 23, 'Gon and Killua use their winnings to buy a Greed Island game card at auction.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-09-30 00:00:00'),
(2753, 273, 15, 'Some × Rules × and × Restrictions', 23, 'Gon and Killua enter Greed Island and receive a tutorial from a player named Goreinu.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-10-07 00:00:00'),
(2754, 273, 16, 'Ging × and × Gon', 23, 'The boys explore Greed Island and discover a beginner\'s town to learn the game\'s rules.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-10-14 00:00:00'),
(2755, 273, 17, 'The × First × Card', 23, 'Gon collects his first specified slot card and faces off against other veteran players.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-10-21 00:00:00'),
(2756, 273, 18, 'Resolve × and × Awakening', 23, 'Razor, one of Ging\'s former companions, challenges Gon to a deadly dodgeball game.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-10-28 00:00:00'),
(2757, 273, 19, 'The × Heated × Battle', 23, 'Gon\'s team fights Razor\'s team in an intense dodgeball match. Hisoka joins their side.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-11-04 00:00:00'),
(2758, 273, 20, 'An × Exhausting × Battle', 23, 'The dodgeball match reaches its climax as Gon channels all his Nen into a final throw.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-11-11 00:00:00'),
(2759, 274, 1, 'A × Dangerous × Meet', 23, 'Gon and Killua begin collecting the 100 specified slot cards needed to complete Greed Island.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-11-18 00:00:00'),
(2760, 274, 2, 'Biscuit × and × Training', 23, 'Biscuit Krueger joins Gon and Killua as their new trainer — hiding her true power level.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-11-25 00:00:00'),
(2761, 274, 3, 'Strengthen × and × Threaten', 23, 'Biscuit reveals her real identity and begins brutally training Gon and Killua in Nen.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-12-02 00:00:00'),
(2762, 274, 4, 'Power × and × Games', 23, 'The trio competes in various Greed Island games to collect cards while growing stronger.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-12-09 00:00:00'),
(2763, 274, 5, 'Enhancement × and × Alteration', 23, 'Killua\'s Nen type is confirmed as Transmutation. His lightning ability begins to develop.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-12-16 00:00:00'),
(2764, 274, 6, 'A × Fated × Awakening', 23, 'Gon develops a new secret attack: Jajanken Stone, Scissors, and Paper in combination.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-12-23 00:00:00'),
(2765, 274, 7, 'Ging × Freecss', 23, 'Gon is stunned to hear a voice recording left by his father Ging inside the game.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2012-12-30 00:00:00'),
(2766, 274, 8, 'Reunion × and × Understanding', 23, 'Gon and Killua meet Gon\'s father\'s old friends and learn more about Ging\'s personality.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-01-06 00:00:00'),
(2767, 274, 9, 'Duty × and × Question', 23, 'Gon completes Greed Island\'s final challenge and encounters Killua\'s brother Illumi.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-01-13 00:00:00'),
(2768, 274, 10, 'An × Absurd × Joker', 23, 'Gon and company clear Greed Island and are given three free Bless cards as a reward.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-01-20 00:00:00'),
(2769, 274, 11, 'Divide × and × Conquer', 23, 'The NGL arc begins: Gon and Killua head to Neo-Green Life to investigate Chimera Ants.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-01-27 00:00:00'),
(2770, 274, 12, 'Captain × and × Yell', 23, 'The Chimera Ant Queen is introduced — a terrifying creature giving birth to soldiers.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-02-03 00:00:00'),
(2771, 274, 13, 'Gon × and × Killua', 23, 'Gon and Killua enter NGL on their mission and encounter ant scouts in the forest.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-02-10 00:00:00'),
(2772, 274, 14, 'Hit × and × Conclusion', 23, 'Kite arrives to assist Gon and Killua. The group fights increasingly powerful ant soldiers.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-02-17 00:00:00'),
(2773, 274, 15, 'Light × and × Darkness', 23, 'The most powerful Royal Guards are born — Neferpitou, Shaiapouf, and Menthuthuyoupi.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-02-24 00:00:00'),
(2774, 274, 16, 'Kite × and × Slots', 23, 'Kite demonstrates his remarkable Crazy Slots ability against a powerful ant soldier.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-03-03 00:00:00'),
(2775, 274, 17, 'Insanity × and × Sanity', 23, 'Kite is defeated and captured by Neferpitou. Gon and Killua barely escape with their lives.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-03-10 00:00:00'),
(2776, 275, 1, 'A × Shocking × Tragedy', 23, 'Gon is devastated by Kite\'s defeat and vows to become strong enough to save him.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-03-17 00:00:00'),
(2777, 275, 2, 'Revenge × and × Recovery', 23, 'Gon and Killua train under Knuckle and Shoot to unlock Nen\'s highest level: Hatsu mastery.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-03-24 00:00:00'),
(2778, 275, 3, 'Charge × and × Invade', 23, 'The Chimera Ant King Meruem is born in a terrifying and bloody emergence from the Queen.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-03-31 00:00:00'),
(2779, 275, 4, 'Divide × and × Conquer', 23, 'Meruem declares himself king. The Royal Guards begin selecting humans to become soldiers.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-04-07 00:00:00'),
(2780, 275, 5, 'Lunge × and × Confiscation', 23, 'The Hunter Association prepares a mass infiltration mission into the ant kingdom.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-04-14 00:00:00'),
(2781, 275, 6, 'Awakening × and × Potential', 23, 'Gon beats Knuckle in their challenge, winning a spot on the invasion squad.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-04-21 00:00:00'),
(2782, 275, 7, 'The × Siege × Begins', 23, 'The invasion of the palace begins. Chairman Netero leads the operation personally.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-04-28 00:00:00'),
(2783, 275, 8, 'Breakdown × and × Awakening', 23, 'Meruem plays Gungi with Komugi — a blind girl prodigy — and cannot defeat her.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-05-05 00:00:00'),
(2784, 275, 9, 'Gungi × of × Komugi', 23, 'Meruem\'s interest in Komugi grows as she defeats him over and over at her own game.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-05-12 00:00:00'),
(2785, 275, 10, 'Return × and × Retire', 23, 'Gon\'s team scouts the ant palace perimeter as the clock ticks on the invasion plan.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-05-19 00:00:00'),
(2786, 275, 11, 'Duel × and × Determination', 23, 'Palm uses her new ant form to gather intelligence inside the palace for the invasion.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-05-26 00:00:00'),
(2787, 275, 12, 'Confusion × and × Expectation', 23, 'The invasion team splits up to distract the Royal Guards while Netero targets Meruem.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-06-02 00:00:00'),
(2788, 275, 13, 'Admission × and × Acceptance', 23, 'Gon confronts Neferpitou to demand they heal Kite. The answer crushes him.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-06-09 00:00:00'),
(2789, 275, 14, 'Anger × and × Light', 23, 'Netero and Meruem begin their legendary fight. Each exchange is explosive and shocking.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-06-16 00:00:00'),
(2790, 275, 15, 'Flash × and × Forge', 23, 'Netero reveals his secret Nen ability: the 100-Type Guanyin Bodhisattva.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-06-23 00:00:00'),
(2791, 275, 16, 'Insult × and × Payback', 23, 'Netero is defeated. He detonates the Miniature Rose — a bomb infused with Nen poison.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-06-30 00:00:00'),
(2792, 275, 17, 'Salvation × and × Future', 23, 'Meruem survives the bomb but is fatally poisoned. He spends his last hours with Komugi.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-07-07 00:00:00'),
(2793, 275, 18, 'Defeat × and × Courage', 23, 'Gon\'s grief and rage overwhelm him. He transforms himself using a forbidden Nen vow.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-07-14 00:00:00'),
(2794, 275, 19, 'Anger × and × Sadness', 23, 'Adult Gon faces Neferpitou. His monstrous power crushes the Royal Guard completely.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-07-21 00:00:00'),
(2795, 275, 20, 'Hostility × and × Determination', 23, 'The full cost of Gon\'s transformation is revealed — he has sacrificed his life force.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-07-28 00:00:00'),
(2796, 275, 21, 'Separation × and × Reunion', 23, 'Killua learns the truth about Gon\'s condition and makes a desperate choice to save him.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-08-04 00:00:00'),
(2797, 275, 22, 'Release × and × Reunion', 23, 'Killua removes the needle Illumi placed in his brain, fully freeing himself at last.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-08-11 00:00:00'),
(2798, 275, 23, 'Past × and × Future', 23, 'Killua rushes Alluka to save Gon\'s life while Illumi and the Zoldycks pursue them.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-08-18 00:00:00'),
(2799, 275, 24, 'Activation × and × Spiders', 23, 'Alluka\'s ability Nanika is explained — it can grant any wish at a terrible price.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-08-25 00:00:00'),
(2800, 275, 25, 'Wrath × and × Savior', 23, 'Killua uses Alluka\'s power to heal Gon, restoring him completely at great personal cost.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-09-01 00:00:00'),
(2801, 275, 26, 'Defeat × and × Conclusion', 23, 'Meruem and Komugi play Gungi together one final time as the poison claims them both.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-09-08 00:00:00'),
(2802, 275, 27, 'Connected × Fates', 23, 'The surviving members of the infiltration team regroup in the aftermath of the battle.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-09-15 00:00:00'),
(2803, 275, 28, 'Revenge × and × Recovery', 23, 'Gon reunites with Killua and learns what his friend sacrificed to save his life.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-09-22 00:00:00'),
(2804, 275, 29, 'Limits × and × Regularity', 23, 'The Hunter Association begins processing the aftermath of the Chimera Ant crisis.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-09-29 00:00:00'),
(2805, 275, 30, 'Confusion × and × Expectation', 23, 'The world begins to learn about the Chimera Ants and the battle that took place.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-10-06 00:00:00'),
(2806, 275, 31, 'Spit × and × Bluff', 23, 'The Hunter Association elects an interim leader following Netero\'s death in battle.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-10-13 00:00:00'),
(2807, 275, 32, 'Preparations × for × Invasion', 23, 'Candidates for the next Hunter Association chairman are announced to the membership.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-10-20 00:00:00'),
(2808, 275, 33, 'Resolve × and × Awakening', 23, 'Leorio reappears and makes a stunning entrance at the chairman election proceedings.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-10-27 00:00:00'),
(2809, 275, 34, 'Initiative × and × Law', 23, 'The election\'s rules are set and campaigning among the Hunters begins in earnest.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-11-03 00:00:00'),
(2810, 275, 35, 'Close × to × Parting', 23, 'Gon wakes up fully healed in the hospital, unaware of the cost of his recovery.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-11-10 00:00:00'),
(2811, 275, 36, 'Parting × and × Departure', 23, 'Gon speaks to Ging\'s voice once more. Killua makes a heartbreaking farewell decision.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-11-17 00:00:00'),
(2812, 275, 37, 'Smile × and × Resolve', 23, 'Killua parts ways with Gon to take Alluka somewhere safe, ending their partnership.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-11-24 00:00:00'),
(2813, 275, 38, 'Gon × and × Ging', 23, 'Gon finally meets his father Ging at the top of the World Tree.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-12-01 00:00:00'),
(2814, 275, 39, 'Past × and × Future', 23, 'Father and son speak honestly about Ging\'s choices and his dreams for the future.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-12-08 00:00:00'),
(2815, 275, 40, 'Hostility × and × Determination', 23, 'Ging reveals his true goal: to venture to the Dark Continent beyond the known world.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-12-15 00:00:00'),
(2816, 275, 41, 'For × You × Only', 23, 'The Hunter chairman election reaches its final rounds with surprising results.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-12-22 00:00:00'),
(2817, 275, 42, 'Confusion × and × Expectation', 23, 'The final two candidates battle for the title of Hunter Association chairman.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2013-12-29 00:00:00'),
(2818, 275, 43, 'Departure × and × Friends', 23, 'Leorio\'s passionate speech about Gon moves every hunter present at the election.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-01-05 00:00:00'),
(2819, 275, 44, 'Magician × and × Empiricist', 23, 'Pariston and Cheadle clash in the final vote for the chairman position.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-01-12 00:00:00'),
(2820, 275, 45, 'Approval × and × Dissent', 23, 'The new Hunter Association chairman is elected in a surprising conclusion.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-01-19 00:00:00'),
(2821, 275, 46, 'Victor × and × Loser', 23, 'The Dark Continent expedition is revealed as the next great arc is introduced.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-01-26 00:00:00'),
(2822, 275, 47, 'Resolve × and × Proposal', 23, 'Preparations for the Dark Continent expedition begin among the top Hunters.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-02-02 00:00:00'),
(2823, 275, 48, 'Lightning × and × Anger', 23, 'The five dangers of the Dark Continent are explained to the world\'s leaders.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-02-09 00:00:00'),
(2824, 275, 49, 'Friend × and × Journey', 23, 'Gon says goodbye and begins preparing for whatever his next adventure will be.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-02-16 00:00:00'),
(2825, 275, 50, 'Change × and × Ability', 23, 'The Zodiacs — the twelve elite Hunters — are introduced and assigned expedition roles.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-02-23 00:00:00'),
(2826, 275, 51, 'Resolve × and × Awakening', 23, 'Kurapika returns, secretly pursuing his own mission within the expedition.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-03-02 00:00:00'),
(2827, 275, 52, 'Limits × and × Regularity', 23, 'The V5 world leaders debate whether to allow the Hunter expedition to proceed.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-03-09 00:00:00'),
(2828, 275, 53, 'Initiative × and × Law', 23, 'The expedition crew is assembled aboard a massive ship preparing to depart.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-03-16 00:00:00'),
(2829, 275, 54, 'Activation × and × Departure', 23, 'The ship sets sail. Old and new characters interact in complex political power plays.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-03-23 00:00:00'),
(2830, 275, 55, 'Mafia × and × Standoff', 23, 'Aboard the ship, the Phantom Troupe boards for their own mysterious reasons.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-03-30 00:00:00'),
(2831, 275, 56, 'Feast × and × Famine', 23, 'Kurapika reveals a shocking plan about the Princes and their bodyguards.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-04-06 00:00:00'),
(2832, 275, 57, 'Initiative × and × Law', 23, 'The Princes\' deadly game of succession begins aboard the ship headed to the Dark Continent.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-04-13 00:00:00'),
(2833, 275, 58, 'Confusion × and × Expectation', 23, 'The Chimera Ant arc officially closes as the ship arc storyline takes center stage.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-04-20 00:00:00'),
(2834, 275, 59, 'Negotiation × and × Scheme', 23, 'Kurapika negotiates dangerous alliances among the rival Princes to survive.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-04-27 00:00:00'),
(2835, 275, 60, 'Activation × and × Trapping', 23, 'The nen beasts guarding each Prince are analyzed. Kurapika teaches Nen to the guards.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-05-04 00:00:00'),
(2836, 275, 61, 'Past × and × Parting', 23, 'The Chimera Ant arc reaches its emotional conclusion as the series approaches its finale.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-05-11 00:00:00'),
(2837, 276, 1, 'Approval × and × Dissent', 23, 'The Hunter election moves into its final phase as political tensions peak.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-05-18 00:00:00'),
(2838, 276, 2, 'Victor × and × Loser', 23, 'Pariston shocks everyone by withdrawing from the election and conceding to Cheadle.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-05-25 00:00:00'),
(2839, 276, 3, 'Resolve × and × Proposal', 23, 'Cheadle becomes the new Hunter Association chairman and immediately faces challenges.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-06-01 00:00:00'),
(2840, 276, 4, 'Lightning × and × Anger', 23, 'Leorio reunites with Gon in hospital and confronts his feelings about everything that happened.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-06-08 00:00:00'),
(2841, 276, 5, 'Friend × and × Journey', 23, 'Gon wakes fully recovered. Killua has already left to protect Alluka in secret.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-06-15 00:00:00'),
(2842, 276, 6, 'Change × and × Ability', 23, 'Killua defeats Illumi\'s assassin squad to protect Alluka from being used as a weapon.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-06-22 00:00:00'),
(2843, 276, 7, 'Limits × and × Regularity', 23, 'Killua makes a final vow to Nanika about the terms of using her power.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-06-29 00:00:00');
INSERT INTO `episode` (`ep_id`, `season_id`, `num_episode`, `title`, `duration`, `resume`, `video_url`, `covert_url`, `rating`, `created_at`, `released_at`) VALUES
(2844, 276, 8, 'Initiative × and × Law', 23, 'Gon meets Ging at the top of the World Tree for a long-awaited father-son conversation.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-07-06 00:00:00'),
(2845, 276, 9, 'Wish × and × Promise', 23, 'Ging tells Gon about the Dark Continent and explains his real reason for leaving him.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-07-13 00:00:00'),
(2846, 276, 10, 'Activation × and × Departure', 23, 'The preparations for the Dark Continent expedition officially begin under Cheadle.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-07-20 00:00:00'),
(2847, 276, 11, 'Mafia × and × Standoff', 23, 'The Kakin royals and the Beyond Netero expedition mission come into conflict.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-07-27 00:00:00'),
(2848, 276, 12, 'Past × and × Future', 23, 'The series ends as Gon heads to his next adventure, and the Dark Continent awaits.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/couvert.jpg', 5, '2026-04-18 11:00:00', '2014-09-23 00:00:00'),
(3000, 24, 1, 'Episode 1', 60, 'A mysterious stranger named Lee Jang-hyun arrives in the peaceful village of Neunggun-ri and turns the world of the lively noblewoman Yoo Gil-chae upside down.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep1.jpg', 5, '2026-04-18 21:44:24', '2023-08-04 21:50:00'),
(3001, 24, 2, 'Episode 2', 75, 'Gil-chae asks Jang-hyun to help her learn about the people of Hanyang. He takes her to fascinating places and their uneasy alliance deepens.', '/assets/images/filmsandseries/Mydearest/ep2.mp4', '/assets/images/filmsandseries/Mydearest/ep2.jpg', 5, '2026-04-18 21:44:24', '2023-08-05 21:50:00'),
(3002, 24, 3, 'Episode 3', 75, 'Yeon-jun and the scholars volunteer to fight for the country as rumors of a Qing invasion spread. Jang-hyun quietly prepares to evacuate.', '/assets/images/filmsandseries/Mydearest/ep3.mp4', '/assets/images/filmsandseries/Mydearest/ep3.jpg', 5, '2026-04-18 21:44:24', '2023-08-11 21:50:00'),
(3003, 24, 4, 'Episode 4', 75, 'The Qing army invades Joseon. Amid the chaos of war, Gil-chae and Jang-hyun find themselves thrown together in desperate circumstances.', '/assets/images/filmsandseries/Mydearest/ep4.mp4', '/assets/images/filmsandseries/Mydearest/ep4.jpg', 5, '2026-04-18 21:44:24', '2023-08-12 21:50:00'),
(3004, 24, 5, 'Episode 5', 75, 'Refugees flee south as the Manchu forces advance. Jang-hyun risks his life to protect Gil-chae, revealing unexpected depths to his character.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep5.jpg', 5, '2026-04-18 21:44:24', '2023-08-18 21:50:00'),
(3005, 24, 6, 'Episode 6', 75, 'Trapped in the middle of the war, Gil-chae must make an impossible choice. Jang-hyun\'s true identity and motives begin to come to light.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep6.jpg', 5, '2026-04-18 21:44:24', '2023-08-19 21:50:00'),
(3006, 24, 7, 'Episode 7', 75, 'The survivors of the invasion struggle to rebuild their lives. Gil-chae confronts her growing feelings and the harsh realities of her new situation.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep7.jpg', 5, '2026-04-18 21:44:24', '2023-08-25 21:50:00'),
(3007, 24, 8, 'Episode 8', 75, 'Jang-hyun makes a shocking move that changes the fate of those around him. Gil-chae begins to see him in a completely different light.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep8.jpg', 5, '2026-04-18 21:44:24', '2023-08-26 21:50:00'),
(3008, 24, 9, 'Episode 9', 75, 'As the war reaches its climax, Gil-chae faces the most devastating loss of her life. Jang-hyun fights desperately to protect her from the horror surrounding them.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep9.jpg', 5, '2026-04-18 21:44:24', '2023-09-01 21:50:00'),
(3009, 24, 10, 'Episode 10', 75, 'Part 1 finale. The Joseon king surrenders to the Qing. Gil-chae and Jang-hyun are torn apart as countless captives are taken north — and a devastating separation begins.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep10.jpg', 5, '2026-04-18 21:44:24', '2023-09-02 21:50:00'),
(3010, 24, 11, 'Episode 11', 75, 'Part 2 begins. Months have passed. Gil-chae struggles to survive and move forward, while Jang-hyun endures captivity in Shenyang fighting to return to her.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep11.jpg', 5, '2026-04-18 21:44:24', '2023-10-13 21:50:00'),
(3011, 24, 12, 'Episode 12', 75, 'Jang-hyun takes great risks to protect Korean prisoners of war whom even the king has abandoned, vowing to fulfill their wish to return home.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep12.jpg', 5, '2026-04-18 21:44:24', '2023-10-14 21:50:00'),
(3012, 24, 13, 'Episode 13', 75, 'Escaping Shenyang proves to be a minefield of danger. A new romantic rival appears on the scene and she is not accustomed to being refused.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep13.jpg', 5, '2026-04-18 21:44:24', '2023-10-20 21:50:00'),
(3013, 24, 14, 'Episode 14', 75, 'The King tortures the Crown Prince, his own son, driven by unjustified suspicion. Jang-hyun is framed as a traitor and faces a deadly crisis.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep14.jpg', 5, '2026-04-18 21:44:24', '2023-10-21 21:50:00'),
(3014, 24, 15, 'Episode 15', 75, 'Gil-chae and Jang-hyun reunite after their long painful separation, but joy is short-lived as powerful forces conspire to keep them apart.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep15.jpg', 5, '2026-04-18 21:44:24', '2023-10-27 21:50:00'),
(3015, 24, 16, 'Episode 16', 75, 'After a devastating breakup, the leads go their sad separate ways. Both struggle to carry on as their lives diverge in painful directions.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep16.jpg', 5, '2026-04-18 21:44:24', '2023-10-28 21:50:00'),
(3016, 24, 17, 'Episode 17', 75, 'Jang-hyun lies wounded with Gil-chae keeping a ceaseless vigil by his side. Their bond is tested to its absolute limit as enemies close in.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep17.jpg', 5, '2026-04-18 21:44:24', '2023-11-03 21:50:00'),
(3017, 24, 18, 'Episode 18', 75, 'Jang-hyun regains his memory and discovers the Crown Prince and Princess have died, leaving him a letter. Meanwhile, Gak-hwa travels all the way to Joseon to find him.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep18.jpg', 5, '2026-04-18 21:44:24', '2023-11-04 21:50:00'),
(3018, 24, 19, 'Episode 19', 75, 'Gil-chae steps up as the sole provider for her family, running a business with fierce determination. Jang-hyun watches from the shadows, unable to stay away.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep19.jpg', 5, '2026-04-18 21:44:24', '2023-11-10 21:50:00'),
(3019, 24, 20, 'Episode 20', 75, 'The final obstacles between Gil-chae and Jang-hyun crumble as the truth of his past and his sacrifices for her are fully revealed. A long-awaited choice must be made.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep20.jpg', 5, '2026-04-18 21:44:24', '2023-11-11 21:50:00'),
(3020, 24, 21, 'Episode 21', 80, 'Series finale (extended episode). After all the war, heartbreak, and separation, Jang-hyun and Gil-chae finally find their way back to each other in a deeply moving conclusion.', '/assets/images/filmsandseries/Mydearest/ep1.mp4', '/assets/images/filmsandseries/Mydearest/ep21.jpg', 5, '2026-04-18 21:44:24', '2023-11-18 21:50:00');

-- --------------------------------------------------------

--
-- Structure de la table `episode_progress`
--

CREATE TABLE `episode_progress` (
  `user_id` int(11) NOT NULL,
  `ep_id` int(11) NOT NULL,
  `status` enum('NOT_STARTED','IN_PROGRESS','COMPLETED') NOT NULL DEFAULT 'NOT_STARTED',
  `last_position` int(11) DEFAULT 0,
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `episode_progress`
--

INSERT INTO `episode_progress` (`user_id`, `ep_id`, `status`, `last_position`, `updated_at`) VALUES
(6, 3000, 'COMPLETED', 43, '2026-04-19 03:02:21'),
(6, 3001, 'IN_PROGRESS', 209, '2026-04-19 03:08:29'),
(6, 3002, 'IN_PROGRESS', 13, '2026-04-19 02:53:03'),
(6, 3003, 'IN_PROGRESS', 6, '2026-04-19 02:53:16'),
(7, 3000, 'IN_PROGRESS', 4, '2026-04-19 03:22:44'),
(7, 3020, 'COMPLETED', 149, '2026-04-19 00:03:16'),
(10, 3000, 'COMPLETED', 149, '2026-04-19 02:36:03');

-- --------------------------------------------------------

--
-- Structure de la table `film`
--

CREATE TABLE `film` (
  `film_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `synopsis` text DEFAULT NULL,
  `casting` text DEFAULT NULL,
  `director` varchar(255) DEFAULT NULL,
  `video_url` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `title_image_url` varchar(255) DEFAULT NULL,
  `poster_url` varchar(255) DEFAULT NULL,
  `release_date` datetime DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `duration` double DEFAULT NULL,
  `age_rating` varchar(10) DEFAULT NULL,
  `rating` float NOT NULL DEFAULT 0,
  `trailer_url` varchar(500) DEFAULT NULL,
  `poster_v_url` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `film`
--

INSERT INTO `film` (`film_id`, `title`, `synopsis`, `casting`, `director`, `video_url`, `image_url`, `title_image_url`, `poster_url`, `release_date`, `updated_at`, `duration`, `age_rating`, `rating`, `trailer_url`, `poster_v_url`) VALUES
(100, 'Aladdin', 'A street urchin named Aladdin falls in love with Princess Jasmine and, with the help of a Genie, must thwart the plans of the sorcerer Jafar.', 'Mena Massoud, Naomi Scott, Will Smith', 'Guy Ritchie', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/Aladdin/couvert.jpg', '/assets/images/filmsandseries/Aladdin/title.png', '/assets/images/filmsandseries/Aladdin/poster.jpg', '2019-05-24 00:00:00', '2026-04-18 12:26:08', 128, 'PG', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/Aladdin/posterv.jpg'),
(101, 'Alice in Wonderland', 'Alice, an imaginative girl, follows a rabbit down a hole into a fantastical world where nothing is quite what it seems.', 'Mia Wasikowska, Johnny Depp, Helena Bonham Carter', 'Tim Burton', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/aliceinwonderland/couvert.png', '/assets/images/filmsandseries/aliceinwonderland/title.png', '/assets/images/filmsandseries/aliceinwonderland/poster.jpeg', '2010-03-05 00:00:00', '2026-04-18 17:37:40', 108, 'PG', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/aliceinwonderland/posterv.png'),
(102, 'Anastasia', 'An 18-year-old girl with no past teams up with two con men to find her real identity.', 'Meg Ryan, John Cusack, Angela Lansbury', 'Don Bluth', '/assets/images/filmsandseries/anastazia/video.mp4', '/assets/images/filmsandseries/anastazia/couvert.png', '/assets/images/filmsandseries/anastazia/title.png', '/assets/images/filmsandseries/anastazia/poster.jpeg', '1997-11-21 00:00:00', '2026-04-18 14:33:13', 94, 'G', 4, '/assets/images/filmsandseries/anastazia/trailer.mp4', '/assets/images/filmsandseries/anastazia/posterv.png'),
(103, 'Barbie: Princess and Popstar', 'Princess Keira and pop star Tori swap lives and discover that dreams can come true when you follow your heart.', 'Diana Kaarina, Ashleigh Ball', 'Ezekiel Norton', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/Barbie_princess_and_popstar/couvert.jpg', '/assets/images/filmsandseries/Barbie_princess_and_popstar/title.png', '/assets/images/filmsandseries/Barbie_princess_and_popstar/poster.jpg', '2012-07-03 00:00:00', '2026-04-18 12:26:08', 83, 'G', 3, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/Barbie_princess_and_popstar/posterv.jpg'),
(104, 'Big Hero 6', 'A brilliant robotics prodigy and his inflatable robot friend Baymax fight to save the city of San Fransokyo.', 'Ryan Potter, Scott Adsit, T.J. Miller', 'Don Hall, Chris Williams', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bighero6/couvert.jpg', '/assets/images/filmsandseries/bighero6/title.png', '/assets/images/filmsandseries/bighero6/poster.jpeg', '2014-11-07 00:00:00', '2026-04-18 19:19:30', 102, 'PG', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/bighero6/posterv.jpg'),
(105, 'Black Panther', 'T\'Challa returns home to Wakanda to take the throne, but his claim is challenged by a powerful enemy.', 'Chadwick Boseman, Michael B. Jordan, Lupita Nyong\'o', 'Ryan Coogler', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/BlackPanther/couvert.jpg', '/assets/images/filmsandseries/BlackPanther/title.png', '/assets/images/filmsandseries/BlackPanther/poster.jpg', '2018-02-16 00:00:00', '2026-04-18 12:26:08', 134, 'PG-13', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/BlackPanther/posterv.jpg'),
(106, 'Brave', 'Scottish princess Merida defies an ancient custom and must undo a beastly curse before it\'s too late.', 'Kelly Macdonald, Billy Connolly, Emma Thompson', 'Mark Andrews, Brenda Chapman', '/assets/images/filmsandseries/brave/couvert.png', '/assets/images/filmsandseries/brave/couvert.png', '/assets/images/filmsandseries/brave/title.png', '/assets/images/filmsandseries/brave/poster.jpeg', '2012-06-22 00:00:00', '2026-04-18 18:44:51', 93, 'PG', 4, '/assets/images/filmsandseries/brave/couvert.png', '/assets/images/filmsandseries/brave/posterv.png'),
(107, 'Charlie and the Chocolate Factory', 'Charlie Bucket wins a golden ticket to visit the extraordinary factory owned by the reclusive Willy Wonka.', 'Johnny Depp, Freddie Highmore, Helena Bonham Carter', 'Tim Burton', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/chocolatefactory/couvert.jpeg', '/assets/images/filmsandseries/chocolatefactory/title.png', '/assets/images/filmsandseries/chocolatefactory/poster.jpeg', '2005-07-15 00:00:00', '2026-04-18 19:38:17', 115, 'PG', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/chocolatefactory/posterv.jpeg'),
(108, 'Damsel', 'A dutiful damsel agrees to marry a handsome prince, only to find the royal family has recruited her as a sacrifice.', 'Millie Bobby Brown, Ray Winstone, Angela Bassett', 'Juan Carlos Fresnadillo', '/assets/images/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/damsel/couvert.jpeg', '/assets/images/filmsandseries/damsel/title.png', '/assets/images/filmsandseries/damsel/poster.jpeg', '2024-03-08 00:00:00', '2026-04-18 18:49:59', 110, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/damsel/posterv.jpeg'),
(109, 'Fast & Furious', 'Los Angeles police officer Brian O\'Conner goes undercover to investigate a gang of truck hijackers led by Dominic Toretto.', 'Vin Diesel, Paul Walker, Michelle Rodriguez', 'Rob Cohen', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/fastandfurious/couvert.jpg', '/assets/images/filmsandseries/fastandfurious/title.png', '/assets/images/filmsandseries/fastandfurious/poster.jpeg', '2001-06-22 00:00:00', '2026-04-18 19:43:15', 106, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/fastandfurious/posterv.jpg'),
(110, 'Five Feet Apart', 'Two cystic fibrosis patients meet in a hospital and fall in love, despite being required to stay apart.', 'Cole Sprouse, Haley Lu Richardson', 'Justin Baldoni', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/fiveFeetAway/couvert.jpg', '/assets/images/filmsandseries/fiveFeetAway/title.png', '/assets/images/filmsandseries/fiveFeetAway/poster.jpg', '2019-03-15 00:00:00', '2026-04-18 12:26:08', 116, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/fiveFeetAway/posterv.jpg'),
(111, 'Frog Princess', 'A hardworking girl named Tiana kisses a frog prince and must help break the voodoo spell to restore him.', 'Anika Noni Rose, Bruno Campos', 'Ron Clements, John Musker', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/frogprincees/couvert.webp', '/assets/images/filmsandseries/frogprincees/title.png', '/assets/images/filmsandseries/frogprincees/poster.jpeg', '2009-12-11 00:00:00', '2026-04-18 18:53:20', 97, 'G', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/frogprincees/posterv.jpeg'),
(112, 'Frozen', 'When Queen Elsa\'s powers plunge Arendelle into eternal winter, her sister Anna embarks on a journey to save her.', 'Idina Menzel, Kristen Bell, Josh Gad', 'Chris Buck, Jennifer Lee', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/Frozen/couvert.jpg', '/assets/images/filmsandseries/Frozen/title.png', '/assets/images/filmsandseries/Frozen/poster.jpg', '2013-11-27 00:00:00', '2026-04-18 12:26:08', 102, 'PG', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/Frozen/posterv.jpg'),
(113, 'Harry Potter', 'A young boy discovers he is a wizard and begins his education at Hogwarts School of Witchcraft and Wizardry.', 'Daniel Radcliffe, Emma Watson, Rupert Grint', 'Chris Columbus', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/harrypotter/couvert.jpg', '/assets/images/filmsandseries/harrypotter/title.png', '/assets/images/filmsandseries/harrypotter/poster.jpeg', '2001-11-16 00:00:00', '2026-04-18 19:09:27', 152, 'PG', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/harrypotter/posterv.png'),
(114, 'How to Train Your Dragon', 'A young Viking who aspires to hunt dragons befriends one instead, and the unlikely pair must fight together to save both their worlds.', 'Jay Baruchel, Gerard Butler, America Ferrera', 'Chris Sanders, Dean DeBlois', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/dragontrainer/couvert.jpg', '/assets/images/filmsandseries/dragontrainer/title.png', '/assets/images/filmsandseries/dragontrainer/poster.jpeg', '2010-03-26 00:00:00', '2026-04-18 19:42:49', 98, 'PG', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/dragontrainer/posterv.jpg'),
(115, 'The Hunger Games', 'In a dystopian future, teenager Katniss Everdeen volunteers to take her sister\'s place in the lethal Hunger Games.', 'Jennifer Lawrence, Josh Hutcherson, Liam Hemsworth', 'Gary Ross', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hungergames/couvert.jpeg', '/assets/images/filmsandseries/hungergames/title.png', '/assets/images/filmsandseries/hungergames/poster.jpeg', '2012-03-23 00:00:00', '2026-04-18 19:13:23', 142, 'PG-13', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/hungergames/posterv.jpeg'),
(116, 'Ice Age 2', 'Manny, Sid, and Diego discover that the Ice Age is ending and embark on a wild journey through a rapidly changing world.', 'Ray Romano, John Leguizamo, Denis Leary', 'Carlos Saldanha', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/iceage2/couvert.jpg', '/assets/images/filmsandseries/iceage2/title.png', '/assets/images/filmsandseries/iceage2/poster.jpeg', '2006-03-31 00:00:00', '2026-04-18 19:46:18', 91, 'PG', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/iceage2/posterv.jpg'),
(117, 'Insidious', 'A family discovers that dark forces have attached themselves to their comatose son, setting off a chain of terrifying events.', 'Patrick Wilson, Rose Byrne, Ty Simpkins', 'James Wan', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/insidious/couvert.jpg', '/assets/images/filmsandseries/insidious/title.png', '/assets/images/filmsandseries/insidious/poster.jpg', '2011-04-01 00:00:00', '2026-04-18 19:46:30', 103, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/insidious/posterv.jpeg'),
(118, 'Iron Man', 'Billionaire Tony Stark builds a powered suit of armor and becomes the superhero Iron Man.', 'Robert Downey Jr., Gwyneth Paltrow, Jeff Bridges', 'Jon Favreau', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/IronMan/couvert.jpg', '/assets/images/filmsandseries/IronMan/title.png', '/assets/images/filmsandseries/IronMan/poster.jpg', '2008-05-02 00:00:00', '2026-04-18 12:26:08', 126, 'PG-13', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/IronMan/posterv.jpg'),
(119, 'Iron Man 2', 'Tony Stark faces pressure from the government, his own demons, and a new enemy named Whiplash.', 'Robert Downey Jr., Gwyneth Paltrow, Mickey Rourke', 'Jon Favreau', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/IronMan2/couvert.jpg', '/assets/images/filmsandseries/IronMan2/title.png', '/assets/images/filmsandseries/IronMan2/poster.jpg', '2010-05-07 00:00:00', '2026-04-18 12:26:08', 124, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/IronMan2/posterv.jpg'),
(120, 'Iron Man 3', 'Tony Stark battles anxiety attacks and a powerful new enemy calling himself the Mandarin.', 'Robert Downey Jr., Gwyneth Paltrow, Guy Pearce', 'Shane Black', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/IronMan3/couvert.jpg', '/assets/images/filmsandseries/IronMan3/title.png', '/assets/images/filmsandseries/IronMan3/poster.jpg', '2013-05-03 00:00:00', '2026-04-18 12:26:08', 130, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/IronMan3/posterv.jpg'),
(121, 'Jumanji', 'When two kids find and play a magical board game, they release a man trapped for decades and must finish the game.', 'Dwayne Johnson, Kevin Hart, Karen Gillan', 'Jake Kasdan', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/jumanji1/couvert.jpg', '/assets/images/filmsandseries/jumanji1/title.png', '/assets/images/filmsandseries/jumanji1/poster.jpg', '2017-12-20 00:00:00', '2026-04-18 12:26:08', 119, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/jumanji1/posterv.jpg'),
(122, 'Jumanji: The Next Level', 'The gang returns to Jumanji but in a very different world — they have to brave parts of the game they\'ve never seen before.', 'Dwayne Johnson, Kevin Hart, Jack Black, Karen Gillan', 'Jake Kasdan', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/jumanji2/couvert.jpg', '/assets/images/filmsandseries/jumanji2/title.png', '/assets/images/filmsandseries/jumanji2/poster.jpg', '2019-12-13 00:00:00', '2026-04-18 12:26:08', 123, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/jumanji2/posterv.jpg'),
(123, 'La La Land', 'A jazz musician and an aspiring actress fall in love while both struggle to make their dreams come true in Los Angeles.', 'Ryan Gosling, Emma Stone', 'Damien Chazelle', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/la la land/couvert.jpg', '/assets/images/filmsandseries/la la land/title.png', '/assets/images/filmsandseries/la la land/poster.jpg', '2016-12-09 00:00:00', '2026-04-18 12:26:08', 128, 'PG', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/la la land/posterv.jpg'),
(124, 'Life of Pi', 'A young man survives a disaster at sea and is hurtled into an epic journey of adventure and discovery while adrift in the ocean.', 'Suraj Sharma, Irrfan Khan', 'Ang Lee', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lfe of pi/couvert.jpg', '/assets/images/filmsandseries/lfe of pi/title.png', '/assets/images/filmsandseries/lfe of pi/poster.jpg', '2012-11-21 00:00:00', '2026-04-18 12:26:08', 127, 'PG', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/lfe of pi/posterv.jpg'),
(125, 'Maleficent', 'A vengeful fairy is driven to curse an infant princess, only to discover that the child may be the one person who can restore peace.', 'Angelina Jolie, Elle Fanning', 'Robert Stromberg', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/maleficent/couvert.jpg', '/assets/images/filmsandseries/maleficent/title.png', '/assets/images/filmsandseries/maleficent/poster.jpeg', '2014-05-30 00:00:00', '2026-04-18 20:04:12', 97, 'PG', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/maleficent/posterv.jpg'),
(126, 'The Matrix', 'A computer hacker learns the nature of his reality and joins a rebellion against the machines that control the world.', 'Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss', 'The Wachowskis', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/Matrix/couvert.jpg', '/assets/images/filmsandseries/Matrix/title.png', '/assets/images/filmsandseries/Matrix/poster.jpg', '1999-03-31 00:00:00', '2026-04-18 12:26:08', 136, 'R', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/Matrix/posterv.jpg'),
(127, 'Mission: Impossible', 'A CIA agent goes rogue after being framed for the murder of his entire team and tries to uncover the real enemy.', 'Tom Cruise, Jon Voight, Jean Reno', 'Brian De Palma', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/missionimpossible/couvert.jpg', '/assets/images/filmsandseries/missionimpossible/title.png', '/assets/images/filmsandseries/missionimpossible/poster.jpg', '1996-05-22 00:00:00', '2026-04-18 12:26:08', 110, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/missionimpossible/posterv.jpg'),
(128, 'Moana', 'An adventurous teenager sails out on a daring mission to save her people, guided by the demigod Maui.', 'Auli\'i Cravalho, Dwayne Johnson', 'Ron Clements, John Musker', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/Moana/couvert.jpg', '/assets/images/filmsandseries/Moana/title.png', '/assets/images/filmsandseries/Moana/poster.jpeg', '2016-11-23 00:00:00', '2026-04-18 20:41:17', 107, 'PG', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/Moana/posterv.jpg'),
(129, 'Mulan', 'A young Chinese woman disguises herself as a male warrior to take her father\'s place in the army.', 'Ming-Na Wen, Eddie Murphy, Donnie Yen', 'Tony Bancroft, Barry Cook', '/assets/images/filmsandseries/Mulan/trailer.mp4', '/assets/images/filmsandseries/Mulan/covert.jpg', '/assets/images/filmsandseries/Mulan/title.png', '/assets/images/filmsandseries/Mulan/poster.jpg', '1998-06-19 00:00:00', '2026-04-18 21:01:55', 88, 'G', 4, '/assets/images/filmsandseries/Mulan/trailer.mp4', '/assets/images/filmsandseries/Mulan/posterv.jpg'),
(130, 'Miss Peregrine\'s Home for Peculiar Children', 'A teenager discovers a hidden island and the magical orphanage it shelters, whose children have unique abilities.', 'Eva Green, Asa Butterfield, Samuel L. Jackson', 'Tim Burton', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/missperegrinehome/couvert.jpg', '/assets/images/filmsandseries/missperegrinehome/title.png', '/assets/images/filmsandseries/missperegrinehome/poster.jpeg', '2016-09-30 00:00:00', '2026-04-18 20:52:42', 127, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/missperegrinehome/posterv.jpg'),
(131, 'Pirates of the Caribbean', 'Blacksmith Will Turner teams up with eccentric pirate Captain Jack Sparrow to rescue his love from the clutches of the undead.', 'Johnny Depp, Orlando Bloom, Keira Knightley', 'Gore Verbinski', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/piratesofcaribbean/couvert.jpg', '/assets/images/filmsandseries/piratesofcaribbean/title.png', '/assets/images/filmsandseries/piratesofcaribbean/poster.jpeg', '2003-07-09 00:00:00', '2026-04-18 21:03:50', 143, 'PG-13', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/piratesofcaribbean/posterv.jpg'),
(132, 'Purple Hearts', 'A struggling singer and a Marine agree to a marriage of convenience that begins to feel real.', 'Sofia Carson, Nicholas Galitzine', 'Elizabeth Allen Rosenbaum', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/purpleHearts/couvert.jpg', '/assets/images/filmsandseries/purpleHearts/title.png', '/assets/images/filmsandseries/purpleHearts/poster.jpg', '2022-07-29 00:00:00', '2026-04-18 12:26:08', 122, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/purpleHearts/posterv.jpg'),
(133, 'The Rise of the Planet of the Apes', 'A scientist\'s experiments inadvertently lead to the development of hyper-intelligent apes who rise up against humanity.', 'James Franco, John Lithgow, Andy Serkis', 'Rupert Wyatt', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/rizeofplanet/couvert.jpg', '/assets/images/filmsandseries/rizeofplanet/title.png', '/assets/images/filmsandseries/rizeofplanet/poster.jpeg', '2011-08-05 00:00:00', '2026-04-18 21:12:12', 105, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/rizeofplanet/posterv.jpg'),
(134, 'Shrek', 'An ogre\'s swamp is overrun by fairy tale creatures. To get his home back, Shrek must rescue Princess Fiona.', 'Mike Myers, Eddie Murphy, Cameron Diaz', 'Andrew Adamson, Vicky Jenson', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/shrek/couvert.jpg', '/assets/images/filmsandseries/shrek/title.png', '/assets/images/filmsandseries/shrek/poster.jpg', '2001-05-18 00:00:00', '2026-04-18 12:26:08', 90, 'PG', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/shrek/posterv.jpg'),
(135, 'Spider-Man: Homecoming', 'Peter Parker tries to balance his superhero alter-ego with his high school life while facing the Vulture.', 'Tom Holland, Michael Keaton, Zendaya', 'Jon Watts', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/SpiderMan_HomeComing/couvert.jpg', '/assets/images/filmsandseries/SpiderMan_HomeComing/title.png', '/assets/images/filmsandseries/SpiderMan_HomeComing/poster.jpg', '2017-07-07 00:00:00', '2026-04-18 12:26:08', 133, 'PG-13', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/SpiderMan_HomeComing/posterv.jpg'),
(136, 'Spider-Man: Brand New Day', 'After years of being Spider-Man, Peter Parker faces new threats in a world that no longer remembers who he is.', 'Tom Holland, Zendaya, Benedict Cumberbatch', 'Jon Watts', '/assets/images/filmsandseries/spidermanbrandNewDay/video.mp4', '/assets/images/filmsandseries/spidermanbrandNewDay/couvert.png', '/assets/images/filmsandseries/spidermanbrandNewDay/title.png', '/assets/images/filmsandseries/spidermanbrandNewDay/poster.jpg', '2025-01-01 00:00:00', '2026-04-18 16:38:14', 130, 'PG-13', 4, '/assets/images/filmsandseries/spidermanbrandNewDay/trailer.mp4', '/assets/images/filmsandseries/spidermanbrandNewDay/posterv.png'),
(137, 'Tangled', 'A princess with magical 70 feet of hair escapes her tower with the help of a charming thief.', 'Mandy Moore, Zachary Levi', 'Nathan Greno, Byron Howard', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/Tangled/couvert.jpg', '/assets/images/filmsandseries/Tangled/title.png', '/assets/images/filmsandseries/Tangled/poster.jpg', '2010-11-24 00:00:00', '2026-04-18 12:26:08', 100, 'PG', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/Tangled/posterv.jpg'),
(138, 'The Conjuring', 'Paranormal investigators Ed and Lorraine Warren help a family terrorized by a dark presence in their home.', 'Patrick Wilson, Vera Farmiga, Ron Livingston', 'James Wan', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/the conjuring/couvert.jpg', '/assets/images/filmsandseries/the conjuring/title.png', '/assets/images/filmsandseries/the conjuring/poster.jpg', '2013-07-19 00:00:00', '2026-04-18 12:26:08', 112, 'R', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/the conjuring/posterv.jpg'),
(139, 'The Conjuring 2', 'Paranormal investigators travel to North London to help a single mother and her four children terrorized by a demonic nun.', 'Patrick Wilson, Vera Farmiga', 'James Wan', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/the conjuring2/couvert.jpg', '/assets/images/filmsandseries/the conjuring2/title.png', '/assets/images/filmsandseries/the conjuring2/poster.jpg', '2016-06-10 00:00:00', '2026-04-18 12:26:08', 134, 'R', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/the conjuring2/posterv.jpg'),
(140, 'The Fault in Our Stars', 'Two teens with cancer fall deeply in love despite knowing their time together may be limited.', 'Shailene Woodley, Ansel Elgort', 'Josh Boone', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/theFaultinourStars/couvert.jpg', '/assets/images/filmsandseries/theFaultinourStars/title.png', '/assets/images/filmsandseries/theFaultinourStars/poster.jpg', '2014-06-06 00:00:00', '2026-04-18 12:26:08', 126, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/theFaultinourStars/posterv.jpg'),
(141, 'The Ring', 'A journalist investigates a mysterious videotape and finds that everyone who watches it dies seven days later.', 'Naomi Watts, Martin Henderson', 'Gore Verbinski', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/the ring/couvert.jpg', '/assets/images/filmsandseries/the ring/title.png', '/assets/images/filmsandseries/the ring/poster.jpg', '2002-10-18 00:00:00', '2026-04-18 12:26:08', 115, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/the ring/posterv.jpg'),
(142, 'The Witches', 'A young boy discovers a secret witch convention and must stop their plan to turn all children into mice.', 'Anne Hathaway, Octavia Spencer, Stanley Tucci', 'Robert Zemeckis', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitches/couvert.jpg', '/assets/images/filmsandseries/thewitches/title.png', '/assets/images/filmsandseries/thewitches/poster.jpg', '2020-10-22 00:00:00', '2026-04-18 12:26:08', 106, 'PG', 3, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/thewitches/posterv.jpg'),
(143, 'Top Gun: Maverick', 'After 30 years, Maverick is still pushing the envelope as a top naval aviator while confronting ghosts from his past.', 'Tom Cruise, Miles Teller, Jennifer Connelly', 'Joseph Kosinski', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/top_gun/couvert.jpg', '/assets/images/filmsandseries/top_gun/title.png', '/assets/images/filmsandseries/top_gun/poster.jpg', '2022-05-27 00:00:00', '2026-04-18 12:26:08', 130, 'PG-13', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/top_gun/posterv.jpg'),
(144, 'Toys Story', 'A cowboy doll is profoundly threatened and jealous when a new spaceman toy supplants him as top toy in a boy\'s room.', 'Tom Hanks, Tim Allen', 'John Lasseter', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/toys story/couvert.jpg', '/assets/images/filmsandseries/toys story/title.png', '/assets/images/filmsandseries/toys story/poster.jpg', '1995-11-22 00:00:00', '2026-04-18 12:26:08', 81, 'G', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/toys story/posterv.jpg'),
(145, 'Train to Busan', 'While a zombie virus breaks out in South Korea, a group of passengers fight for survival on a speeding train.', 'Gong Yoo, Jung Yu-mi, Ma Dong-seok', 'Yeon Sang-ho', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/train to busan/couvert.jpg', '/assets/images/filmsandseries/train to busan/title.png', '/assets/images/filmsandseries/train to busan/poster.jpeg', '2016-07-20 00:00:00', '2026-04-18 19:36:41', 118, 'NR', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/train to busan/posterv.jpg'),
(146, 'Your Name', 'Two strangers find they are sharing dreams — and bodies — in an incredible connection that transcends time and space.', 'Ryunosuke Kamiki, Mone Kamishiraishi', 'Makoto Shinkai', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/yourName/couvert.jpg', '/assets/images/filmsandseries/yourName/title.png', '/assets/images/filmsandseries/yourName/poster.jpg', '2016-08-26 00:00:00', '2026-04-18 12:26:08', 106, 'PG', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/yourName/posterv.jpg'),
(147, 'A Quiet Place 2', 'Following the deadly events at home, the Abbott family must now face the terrors of the outside world.', 'Emily Blunt, Cillian Murphy, Millicent Simmonds', 'John Krasinski', '/assets/images/filmsandseries/a quitePlace2/video.mp4', '/assets/images/filmsandseries/a quitePlace2/couvert.jpg', '/assets/images/filmsandseries/a quitePlace2/title.png', '/assets/images/filmsandseries/a quitePlace2/poster.jpg', '2021-05-28 00:00:00', '2026-04-18 15:46:41', 97, 'PG-13', 4, '/assets/images/filmsandseries/a quitePlace2/video.mp4', '/assets/images/filmsandseries/a quitePlace2/posterv.jpg'),
(148, 'A Quiet Place', 'A family struggles to survive in a post-apocalyptic world inhabited by deadly monsters with ultra-sensitive hearing.', 'Emily Blunt, John Krasinski, Millicent Simmonds', 'John Krasinski', '/assets/images/filmsandseries/a_quitePlace/video.mp4', '/assets/images/filmsandseries/a_quitePlace/couvert.jpg', '/assets/images/filmsandseries/a_quitePlace/title.png', '/assets/images/filmsandseries/a_quitePlace/poster.jpg', '2018-04-06 00:00:00', '2026-04-18 15:46:18', 90, 'PG-13', 5, '/assets/images/filmsandseries/a_quitePlace/trailer.mp4', '/assets/images/filmsandseries/a_quitePlace2/posterv.jpg'),
(149, 'A Silent Voice', 'A young man who bullied a deaf girl in school seeks redemption by reconnecting with her years later.', 'Miyu Irino, Saori Hayami', 'Naoko Yamada', '/assets/images/filmsandseries/A_Silent_voice/video.mp4', '/assets/images/filmsandseries/A_Silent_voice/couvert.jpg', '/assets/images/filmsandseries/A_Silent_voice/title.png', '/assets/images/filmsandseries/A_Silent_voice/poster.jpg', '2016-09-17 00:00:00', '2026-04-18 15:57:02', 130, 'PG-13', 5, '/assets/images/filmsandseries/A_Silent_voice/trailer.mp4', '/assets/images/filmsandseries/A_Silent_voice/posterv.png'),
(151, 'All of Us Are Dead', 'A high school becomes the epicenter of a zombie virus outbreak and students must fight for survival.', 'Yoon Chan-young, Park Ji-hu, Cho Yi-hyun', 'Lee Jae-kyoo', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/allofusaredead/couvert.jpg', '/assets/images/filmsandseries/allofusaredead/title.png', '/assets/images/filmsandseries/allofusaredead/poster.jpg', '2022-01-28 00:00:00', '2026-04-18 12:26:08', 53, '18+', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/allofusaredead/posterv.jpg'),
(152, 'Arrival', 'A linguist is recruited by the military to communicate with aliens who arrive on Earth and must prevent global war.', 'Amy Adams, Jeremy Renner, Forest Whitaker', 'Denis Villeneuve', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/Arrival/couvert.jpeg', '/assets/images/filmsandseries/Arrival/title.png', '/assets/images/filmsandseries/Arrival/poster.jpg', '2016-11-11 00:00:00', '2026-04-18 16:44:53', 116, 'PG-13', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/Arrival/posterv.jpeg'),
(153, 'Avatar 1', 'A paraplegic Marine dispatched to the moon Pandora falls in love with a Na\'vi woman and fights to protect her people.', 'Sam Worthington, Zoe Saldana, Sigourney Weaver', 'James Cameron', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/avatar1/couvert.jpg', '/assets/images/filmsandseries/avatar1/title.png', '/assets/images/filmsandseries/avatar1/poster.jpeg', '2009-12-18 00:00:00', '2026-04-18 17:31:40', 162, 'PG-13', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/avatar1/posterv.jpg'),
(154, 'Avatar: Fire and Ash', 'The Sully family faces new threats from a fiery volcanic region of Pandora and a relentless new enemy.', 'Sam Worthington, Zoe Saldana, Oona Chaplin', 'James Cameron', '/assets/images/filmsandseries/avatarFireAnd Ash/video.mp4', '/assets/images/filmsandseries/avatarFireAnd Ash/couvert.jpg', '/assets/images/filmsandseries/avatarFireAnd Ash/title.png', '/assets/images/filmsandseries/avatarFireAnd Ash/poster.jpg', '2025-12-19 00:00:00', '2026-04-18 14:28:01', 160, 'PG-13', 4, '/assets/images/filmsandseries/avatarFireAnd Ash/trailer.mp4', '/assets/images/filmsandseries/avatarFireAnd Ash/posterv.jpg'),
(155, 'Broer', 'A heartfelt drama about the bond between two brothers navigating family secrets and personal sacrifice.', 'Adewale Akinnuoye-Agbaje, Michael James Shaw', 'Various', '', 'C:\\Users\\user\\OneDrive\\Desktop\\filmsemna\\broer\\télécharger (53).jpeg', 'C:\\Users\\user\\OneDrive\\Desktop\\filmsemna\\broer\\télécharger (53).jpeg', 'C:\\Users\\user\\OneDrive\\Desktop\\filmsemna\\broer\\télécharger (53).jpeg', '2024-01-01 00:00:00', '2026-04-18 17:11:08', 95, 'PG-13', 4, '', 'C:\\Users\\user\\OneDrive\\Desktop\\filmsemna\\broer\\télécharger (53).jpeg'),
(156, 'Business Proposal', 'An office employee goes on a blind date disguised as her friend and falls for her CEO boss.', 'Ahn Hyo-seop, Kim Se-jeong', 'Park Sun-ho', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/businessproposel/couvert.jpg', '/assets/images/filmsandseries/businessproposel/title.png', '/assets/images/filmsandseries/businessproposel/poster.jpeg', '2022-02-28 00:00:00', '2026-04-18 19:33:18', 70, '12+', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/businessproposel/posterv.jpg'),
(158, 'The Maze Runner', 'A boy wakes in a mysterious community of boys with no memory of his past and discovers a dangerous labyrinth.', 'Dylan O\'Brien, Kaya Scodelario, Will Poulter', 'Wes Ball', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mazerunner/couvert.jpg', '/assets/images/filmsandseries/mazerunner/title.png', '/assets/images/filmsandseries/mazerunner/poster.jpeg', '2014-09-19 00:00:00', '2026-04-18 20:46:46', 113, 'PG-13', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/mazerunner/posterv.jpg'),
(159, 'The Memories of Murder', 'A detective from Seoul and a local investigator clash while hunting a serial killer in 1986 Korea.', 'Song Kang-ho, Kim Sang-kyung', 'Bong Joon-ho', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/memoriesofmurder/couvert.jpg', '/assets/images/filmsandseries/memoriesofmurder/title.png', '/assets/images/filmsandseries/memoriesofmurder/poster.jpeg', '2003-05-02 00:00:00', '2026-04-18 20:51:30', 132, 'R', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/memoriesofmurder/posterv.jpg'),
(160, 'The Summer I Turned Pretty', 'A girl spends a transformative summer at a beach house where she finds herself torn between two brothers.', 'Lola Tung, Christopher Briney, Gavin Casalegno', 'Shannon Murphy', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thesummeriturnedpretty/couvert.jpg', '/assets/images/filmsandseries/thesummeriturnedpretty/title.png', '/assets/images/filmsandseries/thesummeriturnedpretty/poster.jpg', '2022-06-17 00:00:00', '2026-04-18 12:26:08', 60, 'TV-14', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/thesummeriturnedpretty/posterv.jpg'),
(161, 'Sa7bek Rajel 1', 'A Tunisian comedy following two best friends navigating love, friendship and the chaos of daily life.', 'Lotfi Abdelli, Ramzi Azaiez', 'Moncef Dhouib', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sa7bek rajel 1/couvert.jpg', '/assets/images/filmsandseries/sa7bek rajel 1/title.png', '/assets/images/filmsandseries/sa7bek rajel 1/poster.jpg', '2014-01-01 00:00:00', '2026-04-18 12:26:08', 100, 'PG', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/sa7bek rajel 1/posterv.jpg'),
(162, 'Sa7bek Rajel 2', 'The beloved Tunisian comedy duo returns for more misadventures in love and friendship.', 'Lotfi Abdelli, Ramzi Azaiez', 'Moncef Dhouib', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sa7bek rajel 2/couvert.jpg', '/assets/images/filmsandseries/sa7bek rajel 2/title.png', '/assets/images/filmsandseries/sa7bek rajel 2/poster.jpg', '2017-01-01 00:00:00', '2026-04-18 12:26:08', 100, 'PG', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/sa7bek rajel 2/posterv.jpg'),
(163, 'Spongbob', 'SpongeBob SquarePants must save Bikini Bottom from an evil plankton scheme threatening the entire underwater city.', 'Tom Kenny, Bill Fagerbakke', 'Stephen Hillenburg', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/spongbob/couvert.jpg', '/assets/images/filmsandseries/spongbob/title.png', '/assets/images/filmsandseries/spongbob/poster.jpg', '2004-11-19 00:00:00', '2026-04-18 12:26:08', 87, 'G', 4, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/spongbob/posterv.jpg'),
(164, 'Tarazan', 'A Tunisian tragicomedy following a street-smart man trying to find his place in modern society.', 'Lotfi Abdelli', 'Moncef Dhouib', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/tarazan/couvert.jpg', '/assets/images/filmsandseries/tarazan/title.png', '/assets/images/filmsandseries/tarazan/poster.jpg', '2016-01-01 00:00:00', '2026-04-18 12:26:08', 98, 'PG', 3, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/tarazan/posterv.jpg'),
(165, 'Mouse', 'A psychopathic serial killer\'s murder spree is investigated by two detectives who discover a shocking truth.', 'Lee Seung-gi, Lee Hee-jun', 'Choi Joon-bae', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mouse/couvert.jpg', '/assets/images/filmsandseries/mouse/title.png', '/assets/images/filmsandseries/mouse/poster.jpeg', '2021-03-03 00:00:00', '2026-04-18 20:54:39', 60, '18+', 5, '/assets/videos/films/trailors/moana.mp4', '/assets/images/filmsandseries/mouse/posterv.jpg'),
(166, 'Beauty and the Beast', 'A young woman is taken prisoner by a beast in his castle and discovers his true nature.', 'Emma Watson, Dan Stevens, Luke Evans', 'Bill Condon', '/assets/videos/films/trailors/beautyandthebeast.mp4', '/assets/images/filmsandseries/beautyandthebeast/cover.jpg', '/assets/images/filmsandseries/beautyandthebeast/title.png', '/assets/images/filmsandseries/beautyandthebeast/poster.jpg', '2017-03-17 00:00:00', '2026-04-18 17:55:27', 129, 'PG', 4, '/assets/videos/films/trailors/beautyandthebeast.mp4', '/assets/images/filmsandseries/beautyandthebeast/poster_v.jpg'),
(167, 'K-Pop Demon Hunters', 'A fictional story about K-pop idols who secretly fight demons while maintaining their fame.', 'Arden Cho, May Hong, Ji-young Yoo, Ahn Hyo-seop, Lee Byung-hun, Ken Jeong, Daniel Dae Kim\',\n', 'Maggie Kang, Chris Appelhans', '/assets/images/filmsandseries/kpopdemonhunters/video.mp4', '/assets/images/filmsandseries/kpopdemonhunters/couvert.jpg', '/assets/images/filmsandseries/kpopdemonhunters/title.png', '/assets/images/filmsandseries/kpopdemonhunters/poster.jpeg', '2025-06-20 00:00:00', '2026-04-18 18:50:25', 100, 'PG-13', 0, '/assets/images/filmsandseries/kpopdemonhunters/trailer.mp4', '/assets/images/filmsandseries/kpopdemonhunters/posterv.jpg'),
(169, 'Moana 2', 'Moana returns to the ocean on a new journey after receiving a mysterious call from her ancestors.', 'Auliʻi Cravalho, Dwayne Johnson', 'David G. Derrick Jr.', '/assets/videos/films/trailors/moana2.mp4', '/assets/images/filmsandseries/moana/couvert.jpg', '/assets/images/filmsandseries/moana/title.png', '/assets/images/filmsandseries/moana/poster2.jpg', '2024-11-27 00:00:00', '2026-04-18 20:44:09', 100, 'PG', 0, '/assets/videos/films/trailors/moana2.mp4', '/assets/images/filmsandseries/moana2/poster_v.jpg');

-- --------------------------------------------------------

--
-- Structure de la table `film_actor`
--

CREATE TABLE `film_actor` (
  `film_id` int(11) NOT NULL,
  `actor_id` int(11) NOT NULL,
  `role_name` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `film_actor`
--

INSERT INTO `film_actor` (`film_id`, `actor_id`, `role_name`) VALUES
(100, 14, 'Genie'),
(100, 15, 'Aladdin'),
(100, 16, 'Princess Jasmine'),
(101, 17, 'Mad Hatter'),
(101, 18, 'Red Queen'),
(101, 72, 'Alice'),
(102, 73, 'Anastasia (voice)'),
(102, 74, 'Dmitri (voice)'),
(102, 75, 'Sophie (voice)'),
(103, 76, 'Princess Keira (voice)'),
(103, 77, 'Tori (voice)'),
(104, 78, 'Hiro Hamada (voice)'),
(104, 79, 'Baymax (voice)'),
(104, 80, 'Fred (voice)'),
(105, 19, 'T\'Challa / Black Panther'),
(105, 20, 'Erik Killmonger'),
(105, 21, 'Nakia'),
(106, 81, 'Merida (voice)'),
(106, 82, 'King Fergus (voice)'),
(106, 83, 'Queen Elinor (voice)'),
(107, 17, 'Willy Wonka'),
(107, 18, 'Mrs. Bucket'),
(107, 84, 'Charlie Bucket'),
(108, 13, 'Elodie'),
(108, 135, 'King Roderick'),
(108, 136, 'Queen Isabelle'),
(109, 85, 'Dominic Toretto'),
(109, 86, 'Brian OConner'),
(109, 87, 'Letty Ortiz'),
(110, 88, 'Will Newman'),
(110, 89, 'Stella Grant'),
(111, 90, 'Tiana (voice)'),
(111, 91, 'Prince Naveen (voice)'),
(112, 92, 'Elsa (voice)'),
(112, 93, 'Anna (voice)'),
(112, 94, 'Olaf (voice)'),
(113, 40, 'Harry Potter'),
(113, 41, 'Hermione Granger'),
(113, 42, 'Ron Weasley'),
(114, 95, 'Hiccup (voice)'),
(114, 96, 'Stoick (voice)'),
(114, 97, 'Astrid (voice)'),
(115, 22, 'Katniss Everdeen'),
(115, 23, 'Peeta Mellark'),
(116, 98, 'Manny (voice)'),
(116, 99, 'Sid (voice)'),
(116, 100, 'Diego (voice)'),
(117, 38, 'Josh Lambert'),
(117, 51, 'Renai Lambert'),
(118, 24, 'Tony Stark / Iron Man'),
(118, 25, 'Pepper Potts'),
(119, 24, 'Tony Stark / Iron Man'),
(119, 25, 'Pepper Potts'),
(120, 24, 'Tony Stark / Iron Man'),
(120, 25, 'Pepper Potts'),
(121, 26, 'Dr. Smolder Bravestone'),
(121, 27, 'Franklin Finbar'),
(121, 28, 'Ruby Roundhouse'),
(122, 26, 'Dr. Smolder Bravestone'),
(122, 27, 'Franklin Finbar'),
(122, 28, 'Ruby Roundhouse'),
(123, 10, 'Mia Dolan'),
(123, 29, 'Sebastian Wilder'),
(125, 8, 'Maleficent'),
(125, 30, 'Aurora'),
(126, 31, 'Neo'),
(126, 32, 'Morpheus'),
(127, 33, 'Ethan Hunt'),
(128, 26, 'Maui (voice)'),
(128, 101, 'Moana (voice)'),
(129, 102, 'Fa Mulan (voice)'),
(129, 103, 'Mushu (voice)'),
(129, 104, 'Commander Tung'),
(130, 105, 'Miss Peregrine'),
(130, 106, 'Jake'),
(130, 107, 'Barron'),
(131, 17, 'Captain Jack Sparrow'),
(131, 34, 'Will Turner'),
(131, 35, 'Elizabeth Swann'),
(133, 108, 'Will Rodman'),
(133, 109, 'Caesar (performance capture)'),
(134, 103, 'Donkey (voice)'),
(134, 110, 'Shrek (voice)'),
(134, 111, 'Princess Fiona (voice)'),
(135, 36, 'Peter Parker / Spider-Man'),
(135, 37, 'MJ'),
(135, 144, 'Doctor Strange'),
(135, 145, 'Ned Leeds'),
(135, 146, 'Aunt May'),
(135, 147, 'Happy Hogan'),
(135, 148, 'Vulture'),
(136, 36, 'Peter Parker / Spider-Man'),
(136, 37, 'MJ'),
(136, 144, 'Doctor Strange'),
(136, 145, 'Ned Leeds'),
(136, 146, 'Aunt May'),
(136, 147, 'Happy Hogan'),
(137, 112, 'Rapunzel (voice)'),
(137, 113, 'Flynn Rider (voice)'),
(138, 38, 'Ed Warren'),
(138, 39, 'Lorraine Warren'),
(139, 38, 'Ed Warren'),
(139, 39, 'Lorraine Warren'),
(140, 114, 'Hazel Grace Lancaster'),
(140, 115, 'Augustus Waters'),
(141, 116, 'Rachel Keller'),
(142, 117, 'The Grand High Witch'),
(142, 118, 'Grandma'),
(142, 119, 'Mr. Stringer'),
(143, 33, 'Pete Maverick Mitchell'),
(144, 3, 'Woody (voice)'),
(145, 67, 'Seok-woo'),
(145, 120, 'Seong-kyeong'),
(145, 121, 'Sang-hwa'),
(147, 46, 'Evelyn Abbott'),
(147, 47, 'Emmett'),
(148, 46, 'Evelyn Abbott'),
(152, 48, 'Louise Banks'),
(152, 49, 'Ian Donnelly'),
(153, 44, 'Jake Sully'),
(153, 45, 'Neytiri'),
(153, 149, 'Dr. Grace Augustine'),
(153, 150, 'Colonel Quaritch'),
(153, 151, 'Trudy Chacon'),
(153, 152, 'Mo at Ike'),
(154, 44, 'Jake Sully'),
(154, 45, 'Neytiri'),
(154, 149, 'Dr. Grace Augustine'),
(154, 150, 'Colonel Quaritch'),
(154, 153, 'Varang'),
(154, 154, 'Tonowari'),
(156, 137, 'Kang Tae-moo'),
(156, 138, 'Shin Ha-ri'),
(157, 139, 'Kim Shin'),
(157, 140, 'Park Bo-hyun'),
(158, 50, 'Thomas'),
(158, 122, 'Teresa Agnes'),
(158, 123, 'Gally'),
(159, 124, 'Detective Park Doo-man'),
(159, 125, 'Detective Seo Tae-yoon'),
(160, 126, 'Belly'),
(160, 127, 'Conrad Fisher'),
(161, 128, 'Lotfi'),
(161, 129, 'Ramzi'),
(162, 128, 'Lotfi'),
(162, 129, 'Ramzi'),
(163, 130, 'SpongeBob SquarePants (voice)'),
(163, 131, 'Patrick Star (voice)'),
(164, 128, 'Tarazan'),
(165, 132, 'Jung Ba-reum'),
(165, 133, 'Go Moo-chi'),
(166, 41, 'Belle'),
(166, 43, 'The Beast'),
(166, 134, 'Gaston'),
(167, 141, 'Ahyeon'),
(167, 142, 'Agent Lee'),
(167, 143, 'Dr. Park');

-- --------------------------------------------------------

--
-- Structure de la table `film_category`
--

CREATE TABLE `film_category` (
  `film_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `film_category`
--

INSERT INTO `film_category` (`film_id`, `category_id`) VALUES
(100, 2),
(100, 3),
(100, 15),
(100, 18),
(101, 2),
(101, 8),
(101, 15),
(102, 3),
(102, 11),
(102, 18),
(103, 3),
(103, 15),
(103, 18),
(104, 1),
(104, 3),
(104, 12),
(104, 15),
(105, 1),
(105, 12),
(105, 14),
(105, 17),
(106, 2),
(106, 3),
(106, 15),
(107, 4),
(107, 8),
(107, 15),
(108, 1),
(108, 2),
(108, 8),
(109, 1),
(109, 13),
(110, 7),
(110, 11),
(111, 3),
(111, 11),
(111, 15),
(111, 18),
(112, 3),
(112, 8),
(112, 15),
(112, 18),
(113, 2),
(113, 8),
(113, 14),
(114, 2),
(114, 3),
(114, 8),
(114, 14),
(114, 15),
(115, 1),
(115, 2),
(115, 12),
(115, 13),
(116, 2),
(116, 3),
(116, 4),
(116, 15),
(117, 9),
(117, 10),
(117, 13),
(118, 1),
(118, 12),
(118, 14),
(119, 1),
(119, 12),
(120, 1),
(120, 12),
(121, 1),
(121, 2),
(121, 4),
(122, 1),
(122, 2),
(122, 4),
(123, 7),
(123, 11),
(123, 14),
(123, 18),
(124, 2),
(124, 7),
(124, 14),
(125, 2),
(125, 8),
(125, 15),
(126, 1),
(126, 12),
(126, 13),
(126, 14),
(127, 1),
(127, 13),
(128, 2),
(128, 3),
(128, 15),
(128, 18),
(129, 1),
(129, 3),
(129, 15),
(130, 2),
(130, 8),
(130, 9),
(131, 1),
(131, 2),
(131, 8),
(132, 7),
(132, 11),
(133, 1),
(133, 12),
(133, 13),
(134, 2),
(134, 3),
(134, 4),
(134, 15),
(135, 1),
(135, 4),
(135, 12),
(136, 1),
(136, 12),
(137, 2),
(137, 3),
(137, 15),
(137, 18),
(138, 9),
(138, 10),
(138, 13),
(139, 9),
(139, 13),
(140, 7),
(140, 11),
(141, 9),
(141, 10),
(141, 13),
(142, 8),
(142, 9),
(142, 15),
(143, 1),
(143, 7),
(143, 14),
(144, 2),
(144, 3),
(144, 4),
(144, 15),
(145, 1),
(145, 9),
(145, 13),
(145, 14),
(146, 3),
(146, 7),
(146, 11),
(146, 16),
(147, 9),
(147, 12),
(147, 13),
(148, 9),
(148, 12),
(148, 13),
(149, 3),
(149, 7),
(149, 11),
(149, 16),
(151, 1),
(151, 9),
(151, 13),
(151, 17),
(152, 7),
(152, 10),
(152, 12),
(152, 14),
(153, 1),
(153, 2),
(153, 12),
(153, 14),
(154, 1),
(154, 2),
(154, 12),
(155, 5),
(155, 7),
(156, 4),
(156, 7),
(156, 11),
(158, 1),
(158, 2),
(158, 12),
(159, 5),
(159, 10),
(159, 13),
(159, 14),
(160, 7),
(160, 11),
(161, 4),
(162, 4),
(163, 3),
(163, 4),
(163, 15),
(164, 4),
(164, 7),
(165, 5),
(165, 9),
(165, 10),
(165, 13);

-- --------------------------------------------------------

--
-- Structure de la table `film_progress`
--

CREATE TABLE `film_progress` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `film_id` int(11) NOT NULL,
  `last_position` int(11) DEFAULT 0,
  `watch_status` enum('NOT_STARTED','IN_PROGRESS','COMPLETED') DEFAULT 'NOT_STARTED',
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `film_progress`
--

INSERT INTO `film_progress` (`id`, `user_id`, `film_id`, `last_position`, `watch_status`, `updated_at`) VALUES
(40, 6, 159, 2, 'IN_PROGRESS', '2026-04-18 12:32:05'),
(41, 6, 123, 4, 'IN_PROGRESS', '2026-04-18 15:48:12'),
(43, 6, 103, 2, 'IN_PROGRESS', '2026-04-18 13:33:48'),
(45, 6, 154, 389, 'COMPLETED', '2026-04-19 00:50:06'),
(51, 6, 102, 8, 'IN_PROGRESS', '2026-04-18 14:34:19'),
(55, 6, 148, 119, 'COMPLETED', '2026-04-19 00:49:33'),
(57, 6, 149, 97, 'COMPLETED', '2026-04-19 00:49:01'),
(60, 6, 136, 1, 'IN_PROGRESS', '2026-04-18 16:39:57'),
(61, 6, 167, 2, 'IN_PROGRESS', '2026-04-18 18:50:54'),
(62, 1, 107, 1, 'IN_PROGRESS', '2026-04-18 19:38:27'),
(63, 1, 154, 0, 'COMPLETED', '2026-04-18 20:22:54'),
(64, 1, 102, 0, 'COMPLETED', '2026-04-18 20:20:27'),
(65, 1, 129, 2, 'IN_PROGRESS', '2026-04-18 21:02:58'),
(67, 7, 167, 109, 'IN_PROGRESS', '2026-04-19 01:11:58'),
(79, 10, 154, 245, 'IN_PROGRESS', '2026-04-19 01:37:37');

-- --------------------------------------------------------

--
-- Structure de la table `latest_search`
--

CREATE TABLE `latest_search` (
  `search_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `searched_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `latest_search`
--

INSERT INTO `latest_search` (`search_id`, `user_id`, `title`, `searched_at`) VALUES
(183, 6, 'Five Feet Apart', '2026-04-18 18:51:35'),
(184, 6, 'Frog Princess', '2026-04-18 18:54:18'),
(185, 6, 'Game of Thrones', '2026-04-18 18:57:44'),
(228, 1, 'Mr. Queen', '2026-04-18 20:56:05'),
(230, 1, 'Mulan', '2026-04-18 21:02:04'),
(231, 1, 'Queen Charlotte', '2026-04-18 21:05:44'),
(232, 1, 'Miss Peregrine\'s Home for Peculiar Children', '2026-04-18 21:08:28'),
(234, 1, 'The Rise of the Planet of the Apes', '2026-04-18 21:14:37'),
(235, 6, 'The Witches', '2026-04-18 23:29:37'),
(236, 7, 'K-Pop Demon Hunters', '2026-04-19 01:10:36'),
(238, 6, 'My Dearest', '2026-04-19 02:02:52'),
(239, 10, 'Moana', '2026-04-19 02:54:06');

-- --------------------------------------------------------

--
-- Structure de la table `my_list`
--

CREATE TABLE `my_list` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `film_id` int(11) DEFAULT 0,
  `serie_id` int(11) DEFAULT 0,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `my_list`
--

INSERT INTO `my_list` (`id`, `user_id`, `film_id`, `serie_id`, `added_at`) VALUES
(38, 4, 11, 0, '2026-03-26 23:48:29'),
(41, 4, 2, 0, '2026-03-26 23:51:50'),
(137, 1, 0, 2, '2026-04-07 18:32:30'),
(138, 1, 2, 0, '2026-04-09 14:47:53'),
(139, 6, 0, 2, '2026-04-09 22:02:40'),
(140, 1, 10, 0, '2026-04-13 20:36:40'),
(148, 6, 148, 0, '2026-04-18 15:48:02'),
(149, 6, 154, 0, '2026-04-18 15:48:04'),
(150, 1, 129, 0, '2026-04-18 21:02:03'),
(151, 6, 0, 24, '2026-04-19 02:06:07'),
(152, 7, 0, 24, '2026-04-19 02:22:33');

-- --------------------------------------------------------

--
-- Structure de la table `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `body` text DEFAULT NULL,
  `type` varchar(50) DEFAULT 'INFO',
  `is_read` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `notifications`
--

INSERT INTO `notifications` (`id`, `user_id`, `title`, `body`, `type`, `is_read`, `created_at`) VALUES
(35, 8, '👋 Welcome to Raksha!', 'Your account is set up. Start exploring movies and series.', 'WELCOME', 0, '2026-04-18 23:20:58'),
(36, 9, '👋 Welcome to Raksha!', 'Your account is set up. Start exploring movies and series.', 'WELCOME', 0, '2026-04-18 23:21:42'),
(38, 11, '👋 Welcome to Raksha!', 'Your account is set up. Start exploring movies and series.', 'WELCOME', 0, '2026-04-18 23:23:26');

-- --------------------------------------------------------

--
-- Structure de la table `ratings`
--

CREATE TABLE `ratings` (
  `rating_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `film_id` int(11) NOT NULL DEFAULT 0,
  `serie_id` int(11) NOT NULL DEFAULT 0,
  `season_id` int(11) NOT NULL DEFAULT 0,
  `episode_id` int(11) NOT NULL DEFAULT 0,
  `note` float NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `ratings`
--

INSERT INTO `ratings` (`rating_id`, `user_id`, `film_id`, `serie_id`, `season_id`, `episode_id`, `note`, `created_at`, `updated_at`) VALUES
(44, 7, 135, 0, 0, 0, 4, '2026-04-18 22:27:58', '2026-04-18 22:27:58'),
(45, 8, 135, 0, 0, 0, 5, '2026-04-18 22:30:48', '2026-04-18 22:30:48'),
(46, 8, 154, 0, 0, 0, 4, '2026-04-18 22:31:51', '2026-04-18 22:31:51');

-- --------------------------------------------------------

--
-- Structure de la table `season`
--

CREATE TABLE `season` (
  `season_id` int(11) NOT NULL,
  `serie_id` int(11) DEFAULT NULL,
  `season_num` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `synopsis` text DEFAULT NULL,
  `trailer_url` varchar(255) DEFAULT NULL,
  `poster_url` varchar(255) DEFAULT NULL,
  `title_url` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `planned_episodes` int(11) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `rating` float NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `season`
--

INSERT INTO `season` (`season_id`, `serie_id`, `season_num`, `title`, `synopsis`, `trailer_url`, `poster_url`, `title_url`, `image_url`, `created_at`, `planned_episodes`, `status`, `rating`) VALUES
(10, 10, 1, 'Season 1', 'The great Houses of Westeros fight for the Iron Throne as a dark threat looms beyond the Wall.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gameofthrones/poster.jpeg', '/assets/images/filmsandseries/gameofthrones/title.png', '/assets/images/filmsandseries/gameofthrones/couvert.jpg', '2011-04-16 23:00:00', 10, 'completed', 5),
(11, 11, 1, 'Season 1', 'Ragnar Lothbrok rises from farmer to Viking legend.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/vikings/poster.jpg', '/assets/images/filmsandseries/vikings/title.png', '/assets/images/filmsandseries/vikings/couvert.jpg', '2013-03-02 23:00:00', 9, 'completed', 5),
(12, 12, 1, 'Season 1', 'Geralt of Rivia hunts monsters across the Continent.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/thewitcher/poster.jpg', '/assets/images/filmsandseries/thewitcher/title.png', '/assets/images/filmsandseries/thewitcher/couvert.jpg', '2019-12-19 23:00:00', 8, 'completed', 4),
(13, 13, 1, 'Season 1', 'Desperate people gamble their lives in deadly children\'s games for a massive cash prize.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/squidgame/poster.jpg', '/assets/images/filmsandseries/squidgame/title.png', '/assets/images/filmsandseries/squidgame/couvert.jpg', '2021-09-16 23:00:00', 9, 'completed', 5),
(14, 14, 1, 'Season 1', 'Residents of Green Home apartment complex battle monstrous transformations.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/sweethome/poster.jpg', '/assets/images/filmsandseries/sweethome/title.png', '/assets/images/filmsandseries/sweethome/couvert.jpg', '2020-12-17 23:00:00', 10, 'completed', 4),
(15, 15, 1, 'Part 1', 'The Professor assembles a crew to take on the Royal Mint of Spain.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/moneyheist/poster.jpg', '/assets/images/filmsandseries/moneyheist/title.png', '/assets/images/filmsandseries/moneyheist/couvert.jpg', '2017-05-01 23:00:00', 13, 'completed', 5),
(16, 16, 1, 'Part 1', 'The heist on the Royal Mint of Spain begins.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lacasadepapel/poster.jpeg', '/assets/images/filmsandseries/lacasadepapel/title.png', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', '2017-05-01 23:00:00', 13, 'completed', 5),
(17, 17, 1, 'Season 1', 'Luffy and his crew set sail toward the Grand Line.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/onepiece/poster.jpg', '/assets/images/filmsandseries/onepiece/title.png', '/assets/images/filmsandseries/onepiece/couvert.jpg', '2023-08-30 23:00:00', 8, 'completed', 5),
(18, 18, 1, 'Season 1', 'The young Queen Charlotte navigates the pressures of her new life at court.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/queencharlotte/poster.jpeg', '/assets/images/filmsandseries/queencharlotte/title.png', '/assets/images/filmsandseries/queencharlotte/covert.jpg', '2023-05-03 23:00:00', 6, 'completed', 4),
(19, 19, 1, 'Season 1', 'The Bridgerton family\'s eldest daughter enters high society.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/bridgerton/poster.jpeg', '/assets/images/filmsandseries/bridgerton/title.png', '/assets/images/filmsandseries/bridgerton/couvert.jpg', '2020-12-24 23:00:00', 8, 'completed', 4),
(20, 20, 1, 'Season 1', 'The lonely goblin seeks his human bride to break an ancient curse.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/goblin/poster.jpeg', '/assets/images/filmsandseries/goblin/title.jpeg', '/assets/images/filmsandseries/goblin/couvert.jpg', '2016-12-01 23:00:00', 16, 'completed', 5),
(21, 21, 1, 'Season 1', 'A savant doctor joins a prestigious hospital and begins saving lives.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/gooddoctor/poster.jpeg', '/assets/images/filmsandseries/gooddoctor/title.png', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', '2013-08-04 23:00:00', 20, 'completed', 4),
(22, 22, 1, 'Season 1', 'A mortal man reluctantly takes over the management of Hotel del Luna.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hoteldelluna/poster.jpeg', '/assets/images/filmsandseries/hoteldelluna/title.png', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', '2019-07-12 23:00:00', 16, 'completed', 5),
(23, 23, 1, 'Season 1', 'A chef\'s soul travels to Joseon and inhabits the Queen\'s body.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/mrqueen/poster.jpeg', '/assets/images/filmsandseries/mrqueen/title.png', '/assets/images/filmsandseries/mrqueen/couvert.jpg', '2020-12-11 23:00:00', 20, 'completed', 5),
(24, 24, 1, 'Season 1', 'A noblewoman survives the Manchu invasion and finds unexpected love.', '/assets/images/filmsandseries/Mydearest/trailer.mp4', '/assets/images/filmsandseries/Mydearest/poster.jpeg', '/assets/images/filmsandseries/Mydearest/title.png', '/assets/images/filmsandseries/Mydearest/posterv.jpeg', '2023-08-03 23:00:00', 21, 'completed', 5),
(25, 25, 1, 'Season 1', 'Tanjiro begins his journey as a Demon Slayer to cure his sister.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/demonslayer/poster.jpeg', '/assets/images/filmsandseries/demonslayer/title.png', '/assets/images/filmsandseries/demonslayer/couvert.jpg', '2019-04-05 23:00:00', 26, 'completed', 5),
(26, 26, 1, 'Season 1', 'A devoted fan travels back in time to her idol\'s youth.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/lovelyrunner/poster.jpeg', '/assets/images/filmsandseries/lovelyrunner/title.png', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', '2024-04-07 23:00:00', 16, 'completed', 5),
(28, 28, 1, 'Season 1', 'A man trapped in a time loop tries to break free before the night ends.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/twelveminutes/poster.jpeg', '/assets/images/filmsandseries/twelveminutes/title.png', '/assets/images/filmsandseries/twelveminutes/couvert.jpg', '2021-08-18 23:00:00', 3, 'completed', 4),
(271, 27, 1, 'Hunter Exam Arc', 'Gon Freecss leaves his island home to take the brutal Hunter Exam and find his legendary father, befriending Killua, Kurapika, and Leorio along the way.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/poster.jpeg', '/assets/images/filmsandseries/hxh/title.png', '/assets/images/filmsandseries/hxh/couvert.jpg', '2011-10-01 23:00:00', 26, 'completed', 5),
(272, 27, 2, 'Heavens Arena Arc', 'Gon and Killua train at the massive Heavens Arena fighting tower and learn the fundamentals of Nen from Wing.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/poster.jpeg', '/assets/images/filmsandseries/hxh/title.png', '/assets/images/filmsandseries/hxh/couvert.jpg', '2012-03-17 23:00:00', 12, 'completed', 5),
(273, 27, 3, 'Phantom Troupe Arc', 'In Yorknew City, Kurapika uses his chain abilities to pursue the Phantom Troupe and avenge his clan, while Gon and Killua get caught in the crossfire.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/poster.jpeg', '/assets/images/filmsandseries/hxh/title.png', '/assets/images/filmsandseries/hxh/couvert.jpg', '2012-06-23 23:00:00', 20, 'completed', 5),
(274, 27, 4, 'Greed Island Arc', 'Gon and Killua enter the legendary video game Greed Island to find clues about Gon\'s father Ging, competing against deadly players for rare cards.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/poster.jpeg', '/assets/images/filmsandseries/hxh/title.png', '/assets/images/filmsandseries/hxh/couvert.jpg', '2012-10-13 23:00:00', 17, 'completed', 5),
(275, 27, 5, 'Chimera Ant Arc', 'The most intense arc: Gon and Killua confront the terrifying Chimera Ants, a species that devours humans and evolves rapidly, led by the all-powerful Meruem.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/poster.jpeg', '/assets/images/filmsandseries/hxh/title.png', '/assets/images/filmsandseries/hxh/couvert.jpg', '2013-01-05 23:00:00', 61, 'completed', 5),
(276, 27, 6, 'Election Arc', 'After the climactic battle with the Ants, the Hunter Association holds a chairman election, and Gon faces the consequences of his choices.', '/assets/videos/films/trailors/kpopdemonhunters.mp4', '/assets/images/filmsandseries/hxh/poster.jpeg', '/assets/images/filmsandseries/hxh/title.png', '/assets/images/filmsandseries/hxh/couvert.jpg', '2014-06-14 23:00:00', 12, 'completed', 5);

-- --------------------------------------------------------

--
-- Structure de la table `serie`
--

CREATE TABLE `serie` (
  `serie_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `title_url` varchar(255) DEFAULT NULL,
  `synopsis` text DEFAULT NULL,
  `casting` text DEFAULT NULL,
  `director` varchar(255) DEFAULT NULL,
  `covert_url` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `rating` float NOT NULL DEFAULT 0,
  `age_rating` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `serie`
--

INSERT INTO `serie` (`serie_id`, `title`, `title_url`, `synopsis`, `casting`, `director`, `covert_url`, `created_at`, `updated_at`, `rating`, `age_rating`) VALUES
(10, 'Game of Thrones', '/assets/images/filmsandseries/gameofthrones/title.png', 'Nine noble families fight for control over the lands of Westeros while an ancient enemy rises beyond the North.', 'Emilia Clarke, Kit Harington, Peter Dinklage', 'David Benioff, D.B. Weiss', '/assets/images/filmsandseries/gameofthrones/couvert.png', '2011-04-16 23:00:00', '2026-04-18 18:56:39', 5, '18+'),
(11, 'Vikings', '/assets/images/filmsandseries/vikings/title.png', 'The Vikings follow legendary Norse hero Ragnar Lothbrok on his rise to power as a fierce chieftain.', 'Travis Fimmel, Katheryn Winnick, Clive Standen', 'Michael Hirst', '/assets/images/filmsandseries/vikings/couvert.jpg', '2013-03-02 23:00:00', '2026-04-17 23:00:00', 5, '18+'),
(12, 'The Witcher', '/assets/images/filmsandseries/thewitcher/title.png', 'Geralt of Rivia, a mutated monster-hunter, struggles to find his place in a world where people often prove more wicked than beasts.', 'Henry Cavill, Freya Allan, Anya Chalotra', 'Lauren Schmidt Hissrich', '/assets/images/filmsandseries/thewitcher/couvert.jpg', '2019-12-19 23:00:00', '2026-04-17 23:00:00', 4, '16+'),
(13, 'Squid Game', '/assets/images/filmsandseries/squidgame/title.png', 'Hundreds of cash-strapped contestants accept a strange invitation to compete in children\'s games for a tempting prize.', 'Lee Jung-jae, Park Hae-soo, Wi Ha-jun', 'Hwang Dong-hyuk', '/assets/images/filmsandseries/squidgame/couvert.jpg', '2021-09-16 23:00:00', '2026-04-17 23:00:00', 5, '18+'),
(14, 'Sweet Home', '/assets/images/filmsandseries/sweethome/title.png', 'A reclusive high schooler who moves into a new apartment complex finds humans turning into monsters.', 'Song Kang, Lee Jin-uk, Lee Si-young', 'Lee Eung-bok', '/assets/images/filmsandseries/sweethome/couvert.jpg', '2020-12-17 23:00:00', '2026-04-17 23:00:00', 4, '18+'),
(15, 'Money Heist', '/assets/images/filmsandseries/moneyheist/title.png', 'A criminal mastermind who goes by \"The Professor\" recruits a band of thieves to carry out an elaborate heist.', 'Álvaro Morte, Úrsula Corberó, Itziar Ituño', 'Álex Pina', '/assets/images/filmsandseries/moneyheist/couvert.jpg', '2017-05-01 23:00:00', '2026-04-17 23:00:00', 5, '16+'),
(16, 'La Casa de Papel', '/assets/images/filmsandseries/lacasadepapel/title.png', 'An enigmatic man known as the Professor plans and executes the biggest bank heist in history.', 'Álvaro Morte, Úrsula Corberó, Itziar Ituño', 'Álex Pina', '/assets/images/filmsandseries/lacasadepapel/couvert.jpg', '2017-05-01 23:00:00', '2026-04-17 23:00:00', 5, '16+'),
(17, 'One Piece', '/assets/images/filmsandseries/onepiece/title.png', 'Monkey D. Luffy sets off on a journey to find the legendary treasure known as One Piece and become the Pirate King.', 'Iñaki Godoy, Mackenyu, Emily Rudd', 'Matt Owens, Steven Maeda', '/assets/images/filmsandseries/onepiece/couvert.jpg', '2023-08-30 23:00:00', '2026-04-17 23:00:00', 5, '12+'),
(18, 'Queen Charlotte', '/assets/images/filmsandseries/queencharlotte/title.png', 'The story of a young Queen Charlotte\'s marriage to King George and her rise to power in society.', 'India Amarteifio, Corey Mylchreest, Golda Rosheuvel', 'Shonda Rhimes', '/assets/images/filmsandseries/queencharlotte/couvert.jpg', '2023-05-03 23:00:00', '2026-04-17 23:00:00', 4, 'TV-14'),
(19, 'Bridgerton', '/assets/images/filmsandseries/bridgerton/title.png', 'Set in a Regency-era London society, the eight close-knit Bridgerton siblings look for love and happiness.', 'Adjoa Andoh, Jonathan Bailey, Simone Ashley', 'Chris Van Dusen', '/assets/images/filmsandseries/bridgerton/couvert.jpg', '2020-12-24 23:00:00', '2026-04-17 23:00:00', 4, 'TV-MA'),
(20, 'Goblin', '/assets/images/filmsandseries/goblin/title.png', 'An immortal goblin needs a human bride to end his immortality. He meets a girl who claims to be his bride.', 'Gong Yoo, Kim Go-eun, Lee Dong-wook', 'Lee Eung-bok', '/assets/images/filmsandseries/goblin/couvert.jpg', '2016-12-01 23:00:00', '2026-04-17 23:00:00', 5, '15+'),
(21, 'Good Doctor', '/assets/images/filmsandseries/gooddoctor/title.png', 'A young autistic savant doctor joins a hospital where his extraordinary abilities clash with personal challenges.', 'Joo Won, Moon Chae-won', 'Kim Min-soo', '/assets/images/filmsandseries/gooddoctor/couvert.jpg', '2013-08-04 23:00:00', '2026-04-17 23:00:00', 4, '12+'),
(22, 'Hotel del Luna', '/assets/images/filmsandseries/hoteldelluna/title.png', 'A man becomes the manager of a hotel that exclusively caters to ghosts, overseen by a 1,000-year-old owner.', 'IU, Yeo Jin-goo', 'Oh Choong-hwan', '/assets/images/filmsandseries/hoteldelluna/couvert.jpg', '2019-07-12 23:00:00', '2026-04-17 23:00:00', 5, '15+'),
(23, 'Mr. Queen', '/assets/images/filmsandseries/mrqueen/title.png', 'A modern chef\'s soul is transported back to the Joseon era and ends up in the body of a queen.', 'Shin Hye-sun, Kim Jung-hyun', 'Yoon Sung-shik', '/assets/images/filmsandseries/mrqueen/couvert.jpg', '2020-12-11 23:00:00', '2026-04-17 23:00:00', 5, '12+'),
(24, 'My Dearest', '/assets/images/filmsandseries/Mydearest/title.png', 'Set in 1627, a noblewoman\'s life is turned upside down when the Manchu army invades Joseon.', 'Namkoong Min, Ahn Eun-jin', 'Kim Sung-yong', '/assets/images/filmsandseries/Mydearest/posterv.jpeg', '2025-08-03 23:00:00', '2026-04-18 21:25:31', 5, '15+'),
(25, 'Demon Slayer', '/assets/images/filmsandseries/demonslayer/title.png', 'A young boy becomes a demon slayer after his family is slaughtered and his sister is turned into a demon.', 'Natsuki Hanae, Zach Aguilar', 'Haruo Sotozaki', '/assets/images/filmsandseries/demonslayer/couvert.jpg', '2019-04-05 23:00:00', '2026-04-17 23:00:00', 5, '16+'),
(26, 'Lovely Runner', '/assets/images/filmsandseries/lovelyrunner/title.png', 'A paralyzed fan travels back in time to save her idol from suicide and discovers unexpected feelings along the way.', 'Im Sol, Byeon Woo-seok', 'Yoon Jong-ho', '/assets/images/filmsandseries/lovelyrunner/couvert.jpg', '2024-04-07 23:00:00', '2026-04-17 23:00:00', 5, '12+'),
(27, 'Hunter x Hunter', '/assets/images/filmsandseries/hxh/title.png', 'Gon Freecss dreams of becoming a Hunter like his father, embarking on a dangerous adventure to find him.', 'Megumi Han, Mariya Ise', 'Hiroshi Kōjina', '/assets/images/filmsandseries/hxh/couvert.jpg', '2011-10-01 23:00:00', '2026-04-17 23:00:00', 5, '13+'),
(28, 'Twelve Minutes', '/assets/images/filmsandseries/twelveminutes/title.png', 'A man is caught in a time loop on the night a police officer breaks into his apartment and kills him.', 'James McAvoy, Daisy Ridley, Willem Dafoe', 'Luis Antonio', '/assets/images/filmsandseries/twelveminutes/couvert.jpg', '2021-08-18 23:00:00', '2026-04-17 23:00:00', 4, '18+'),
(29, 'Alchemy of Souls', '/assets/images/filmsandseries/alchemyofsouls/title.png', 'In a magical kingdom, powerful mages can swap souls between bodies, leading to forbidden love and dangerous power struggles.', 'Lee Jae-wook, Jung So-min, Go Youn-jung, Hwang Min-hyun', 'Park Joon-hwa', '/assets/images/filmsandseries/alchemyofsouls/cover.jpg', '2022-06-17 23:00:00', '2026-04-18 20:14:16', 5, '16+');

-- --------------------------------------------------------

--
-- Structure de la table `serie_actor`
--

CREATE TABLE `serie_actor` (
  `serie_id` int(11) NOT NULL,
  `actor_id` int(11) NOT NULL,
  `role_name` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `serie_actor`
--

INSERT INTO `serie_actor` (`serie_id`, `actor_id`, `role_name`) VALUES
(10, 52, 'Daenerys Targaryen'),
(10, 53, 'Jon Snow'),
(10, 54, 'Tyrion Lannister'),
(11, 55, 'Ragnar Lothbrok'),
(11, 56, 'Lagertha'),
(12, 57, 'Geralt of Rivia'),
(12, 58, 'Ciri'),
(12, 59, 'Yennefer of Vengerberg'),
(13, 60, 'Seong Gi-hun Player 456'),
(13, 61, 'Cho Sang-woo Player 218'),
(15, 62, 'The Professor'),
(15, 63, 'Tokyo'),
(16, 62, 'The Professor'),
(16, 63, 'Tokyo'),
(17, 64, 'Monkey D. Luffy'),
(17, 65, 'Roronoa Zoro'),
(17, 66, 'Nami'),
(20, 67, 'Kim Shin The Goblin'),
(20, 68, 'Wang Yeo Grim Reaper'),
(24, 155, 'Jang Hyun'),
(24, 156, 'Yoo Gil-chae'),
(24, 157, 'Ryu Sung-il'),
(24, 158, 'Gu Won-mu'),
(28, 69, 'The Husband'),
(28, 70, 'The Wife'),
(28, 71, 'The Cop');

-- --------------------------------------------------------

--
-- Structure de la table `serie_category`
--

CREATE TABLE `serie_category` (
  `serie_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `serie_category`
--

INSERT INTO `serie_category` (`serie_id`, `category_id`) VALUES
(2, 9),
(2, 12),
(2, 17),
(10, 1),
(10, 7),
(10, 8),
(10, 14),
(10, 17),
(11, 1),
(11, 2),
(11, 7),
(12, 1),
(12, 7),
(12, 8),
(13, 7),
(13, 13),
(13, 14),
(13, 17),
(14, 9),
(14, 12),
(14, 13),
(15, 5),
(15, 7),
(15, 13),
(15, 17),
(16, 5),
(16, 7),
(16, 13),
(17, 1),
(17, 2),
(17, 16),
(18, 7),
(18, 11),
(19, 7),
(19, 11),
(20, 7),
(20, 8),
(20, 11),
(21, 7),
(22, 8),
(22, 10),
(22, 11),
(23, 4),
(23, 8),
(23, 11),
(24, 2),
(24, 7),
(24, 11),
(25, 1),
(25, 8),
(25, 16),
(26, 7),
(26, 11),
(26, 12),
(27, 1),
(27, 2),
(27, 16),
(28, 10),
(28, 12),
(28, 13);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('USER','ADMIN') NOT NULL DEFAULT 'USER',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `profile_photo` varchar(255) DEFAULT '/assets/images/profile.png'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`user_id`, `username`, `email`, `password`, `role`, `created_at`, `profile_photo`) VALUES
(1, 'test', '', '$2a$12$BwEiUPrFdnPgTvzvubd/EeYsbD0ASVhrqJ2H4UFRLmiH/IG40UAhm', 'USER', '2026-03-22 01:52:41', 'file:/C:/Users/user/git/ProjectS2/JStream/src/main/resources/assets/images/filmsandseries/maleficent/poster.jpeg'),
(6, 'clovo', 'baliemna2222@gmail.com', '$2a$12$FJn3aQEUfMpW/8wKBWzp.e4esXLEjfJhdFiHkBmlRAaw4zXhmEVJm', 'USER', '2026-04-09 22:02:10', 'file:/C:/Users/user/OneDrive/Desktop/users/Capture%20d\'écran%202026-04-19%20013225.png'),
(7, 'emna', 'emna@gmail.com', '$2a$12$.BWU2E9pKMdFGkZHmQvD8OOh3NOobnSQipILCcOHqoWw6K5BIHzmq', 'USER', '2026-04-18 22:20:11', 'file:/C:/Users/user/OneDrive/Desktop/users/Capture%20d\'écran%202026-04-19%20013209.png'),
(8, 'noura', 'noura@gmail.com', '$2a$12$1LBKMhQym.EkDmky9y.8j.BwFssHRDS2gerxoEU0p1nT8Uu72aTMK', 'USER', '2026-04-18 22:20:58', '/assets/images/profile.png'),
(9, 'sirin', 'sirin@gmail.com', '$2a$12$E2bLGApcVKk6.CDQ.YG/heaQn75B6O0QqDbHOJ7WwoMbweyqj5r/.', 'USER', '2026-04-18 22:21:41', '/assets/images/profile.png'),
(10, 'adem', 'adem@gmail.com', '$2a$12$dSpwgJM9mbvaIhrL1q93U.q1SZ0GaE5sy/BdOZXd6J1WnsH6hLtsq', 'USER', '2026-04-18 22:22:39', 'file:/C:/Users/user/OneDrive/Desktop/users/Capture%20d\'écran%202026-04-19%20013139.png'),
(11, 'admin', 'admin@gmail.com', '$2a$12$5j0myScBZdemxprT0ZvT3OvVktv9P4Ka.Cn9W1UODuymDK56fNSpC', 'ADMIN', '2026-04-18 22:23:26', '/assets/images/profile.png');

-- --------------------------------------------------------

--
-- Structure de la table `user_meta`
--

CREATE TABLE `user_meta` (
  `user_id` int(11) NOT NULL,
  `first_login_done` tinyint(1) DEFAULT 0,
  `last_ep_check` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `user_meta`
--

INSERT INTO `user_meta` (`user_id`, `first_login_done`, `last_ep_check`) VALUES
(1, 1, '2026-04-18 23:18:29'),
(6, 1, '2026-04-19 04:16:56'),
(7, 1, '2026-04-19 03:37:45'),
(8, 1, '2026-04-18 23:20:58'),
(9, 1, '2026-04-18 23:22:42'),
(10, 1, '2026-04-19 04:06:28'),
(11, 1, '2026-04-18 23:23:36');

-- --------------------------------------------------------

--
-- Structure de la table `user_notified_episodes`
--

CREATE TABLE `user_notified_episodes` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `ep_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `user_notified_episodes`
--

INSERT INTO `user_notified_episodes` (`id`, `user_id`, `ep_id`, `created_at`) VALUES
(1, 6, 28, '2026-04-14 20:17:28'),
(2, 6, 29, '2026-04-14 20:19:13');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `actor`
--
ALTER TABLE `actor`
  ADD PRIMARY KEY (`actor_id`);

--
-- Index pour la table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`category_id`);

--
-- Index pour la table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`comment_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `episode`
--
ALTER TABLE `episode`
  ADD PRIMARY KEY (`ep_id`),
  ADD KEY `season_id` (`season_id`);

--
-- Index pour la table `episode_progress`
--
ALTER TABLE `episode_progress`
  ADD PRIMARY KEY (`user_id`,`ep_id`),
  ADD KEY `ep_id` (`ep_id`);

--
-- Index pour la table `film`
--
ALTER TABLE `film`
  ADD PRIMARY KEY (`film_id`);

--
-- Index pour la table `film_actor`
--
ALTER TABLE `film_actor`
  ADD PRIMARY KEY (`film_id`,`actor_id`),
  ADD KEY `actor_id` (`actor_id`);

--
-- Index pour la table `film_category`
--
ALTER TABLE `film_category`
  ADD PRIMARY KEY (`film_id`,`category_id`),
  ADD KEY `fk_category` (`category_id`);

--
-- Index pour la table `film_progress`
--
ALTER TABLE `film_progress`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_film_unique` (`user_id`,`film_id`),
  ADD KEY `fk_film_progress_film` (`film_id`);

--
-- Index pour la table `latest_search`
--
ALTER TABLE `latest_search`
  ADD PRIMARY KEY (`search_id`);

--
-- Index pour la table `my_list`
--
ALTER TABLE `my_list`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `film_id` (`film_id`),
  ADD KEY `serie_id` (`serie_id`);

--
-- Index pour la table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `ratings`
--
ALTER TABLE `ratings`
  ADD PRIMARY KEY (`rating_id`),
  ADD UNIQUE KEY `uc_user_content` (`user_id`,`film_id`,`serie_id`,`season_id`,`episode_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `film_id` (`film_id`);

--
-- Index pour la table `season`
--
ALTER TABLE `season`
  ADD PRIMARY KEY (`season_id`),
  ADD KEY `serie_id` (`serie_id`);

--
-- Index pour la table `serie`
--
ALTER TABLE `serie`
  ADD PRIMARY KEY (`serie_id`);

--
-- Index pour la table `serie_actor`
--
ALTER TABLE `serie_actor`
  ADD PRIMARY KEY (`serie_id`,`actor_id`),
  ADD KEY `actor_id` (`actor_id`);

--
-- Index pour la table `serie_category`
--
ALTER TABLE `serie_category`
  ADD PRIMARY KEY (`serie_id`,`category_id`),
  ADD KEY `fk_serie_category_category` (`category_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- Index pour la table `user_meta`
--
ALTER TABLE `user_meta`
  ADD PRIMARY KEY (`user_id`);

--
-- Index pour la table `user_notified_episodes`
--
ALTER TABLE `user_notified_episodes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`ep_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `actor`
--
ALTER TABLE `actor`
  MODIFY `actor_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=159;

--
-- AUTO_INCREMENT pour la table `category`
--
ALTER TABLE `category`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT pour la table `comments`
--
ALTER TABLE `comments`
  MODIFY `comment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=55;

--
-- AUTO_INCREMENT pour la table `episode`
--
ALTER TABLE `episode`
  MODIFY `ep_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3021;

--
-- AUTO_INCREMENT pour la table `film`
--
ALTER TABLE `film`
  MODIFY `film_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=170;

--
-- AUTO_INCREMENT pour la table `film_progress`
--
ALTER TABLE `film_progress`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=80;

--
-- AUTO_INCREMENT pour la table `latest_search`
--
ALTER TABLE `latest_search`
  MODIFY `search_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=240;

--
-- AUTO_INCREMENT pour la table `my_list`
--
ALTER TABLE `my_list`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=153;

--
-- AUTO_INCREMENT pour la table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT pour la table `ratings`
--
ALTER TABLE `ratings`
  MODIFY `rating_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT pour la table `season`
--
ALTER TABLE `season`
  MODIFY `season_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=277;

--
-- AUTO_INCREMENT pour la table `serie`
--
ALTER TABLE `serie`
  MODIFY `serie_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT pour la table `user_notified_episodes`
--
ALTER TABLE `user_notified_episodes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `comments`
--
ALTER TABLE `comments`
  ADD CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `episode_progress`
--
ALTER TABLE `episode_progress`
  ADD CONSTRAINT `episode_progress_ibfk_1` FOREIGN KEY (`ep_id`) REFERENCES `episode` (`ep_id`),
  ADD CONSTRAINT `episode_progress_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Contraintes pour la table `film_category`
--
ALTER TABLE `film_category`
  ADD CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_film` FOREIGN KEY (`film_id`) REFERENCES `film` (`film_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `film_progress`
--
ALTER TABLE `film_progress`
  ADD CONSTRAINT `fk_film_progress_film` FOREIGN KEY (`film_id`) REFERENCES `film` (`film_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_film_progress_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `ratings`
--
ALTER TABLE `ratings`
  ADD CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `serie_actor`
--
ALTER TABLE `serie_actor`
  ADD CONSTRAINT `serie_actor_ibfk_1` FOREIGN KEY (`serie_id`) REFERENCES `serie` (`serie_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `serie_actor_ibfk_2` FOREIGN KEY (`actor_id`) REFERENCES `actor` (`actor_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `serie_category`
--
ALTER TABLE `serie_category`
  ADD CONSTRAINT `fk_serie_category_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
