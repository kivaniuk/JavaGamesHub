USE gamehub_db;

-- games
INSERT INTO games (game_name, game_description) VALUES
('Snake', 'Classic snake game with boosters and power-ups'),
('Breakout', 'Brick breaking game with multi-ball and power-ups');

-- users (admin + some players)
INSERT INTO users (username, pin, security_question, security_answer, profile_picture, user_role) VALUES
('admin', '0000', 'What is your pet name?', 'rex', 'avatar1.jpg', 'admin'),
('kirill', '1234', 'What is your pet name?', 'buddy', 'avatar1.jpg', 'player'),
('alex', '1111', 'What city were you born in?', 'boston', 'avatar2.jpg', 'player'),
('maria', '2222', 'What is your mothers name?', 'anna', 'avatar3.png', 'player'),
('john', '3333', 'What is your pet name?', 'max', 'avatar4.png', 'player'),
('sarah', '4444', 'What city were you born in?', 'miami', 'avatar1.jpg', 'player'),
('mike', '5555', 'What is your mothers name?', 'linda', 'avatar2.jpg', 'player');

-- user_stats (one row per user, user_ids 1-7)
INSERT INTO user_stats (user_id, lifetime_xp, weekly_xp, total_sessions, weekly_sessions, week_start_date, week_end_date, last_played) VALUES
(1, 0, 0, 0, 0, '2026-05-04', '2026-05-11', NULL),
(2, 145, 22, 48, 8, '2026-05-04', '2026-05-11', '2026-05-05 14:30:00'),
(3, 98, 15, 32, 5, '2026-05-04', '2026-05-11', '2026-05-05 16:45:00'),
(4, 210, 30, 70, 10, '2026-05-04', '2026-05-11', '2026-05-05 18:00:00'),
(5, 67, 8, 22, 3, '2026-05-04', '2026-05-11', '2026-05-04 20:15:00'),
(6, 180, 25, 60, 9, '2026-05-04', '2026-05-11', '2026-05-05 12:00:00'),
(7, 55, 5, 18, 2, '2026-05-04', '2026-05-11', '2026-05-04 19:30:00');

-- high_scores
INSERT INTO high_scores (user_id, game_id, score, achieved_at) VALUES
(2, 1, 42, '2026-05-03 14:00:00'),
(2, 2, 3200, '2026-05-04 15:00:00'),
(3, 1, 28, '2026-05-02 16:00:00'),
(3, 2, 2100, '2026-05-03 17:00:00'),
(4, 1, 65, '2026-05-01 18:00:00'),
(4, 2, 5400, '2026-05-02 19:00:00'),
(5, 1, 18, '2026-04-30 20:00:00'),
(5, 2, 1200, '2026-05-01 21:00:00'),
(6, 1, 55, '2026-05-03 12:00:00'),
(6, 2, 4800, '2026-05-04 13:00:00'),
(7, 1, 12, '2026-04-29 19:00:00'),
(7, 2, 800, '2026-04-30 20:00:00');

-- game_sessions (mix of snake and breakout, this week and last week)
INSERT INTO game_sessions (user_id, game_id, score, duration_seconds, xp_earned, played_at) VALUES
(2, 1, 42, 180, 2, '2026-05-05 14:00:00'),
(2, 1, 30, 120, 2, '2026-05-05 14:30:00'),
(2, 2, 3200, 300, 4, '2026-05-04 15:00:00'),
(3, 1, 28, 150, 2, '2026-05-05 16:00:00'),
(3, 2, 2100, 260, 4, '2026-05-05 17:00:00'),
(4, 1, 65, 240, 2, '2026-05-05 18:00:00'),
(4, 2, 5400, 420, 4, '2026-05-04 19:00:00'),
(4, 2, 4100, 380, 4, '2026-05-05 18:30:00'),
(5, 1, 18, 90, 2, '2026-05-04 20:00:00'),
(5, 2, 1200, 200, 2, '2026-05-05 21:00:00'),
(6, 1, 55, 210, 2, '2026-05-05 12:00:00'),
(6, 2, 4800, 400, 4, '2026-05-04 13:00:00'),
(6, 1, 40, 160, 2, '2026-05-05 12:30:00'),
(7, 1, 12, 60, 2, '2026-05-04 19:00:00'),
(7, 2, 800, 180, 2, '2026-05-05 20:00:00');

-- session_activity
INSERT INTO session_activity (session_id, key_presses, mouse_clicks, times_paused, boosters_collected, extra_metric_1, extra_metric_2) VALUES
(1, 320, 0, 1, 3, 42, 0),
(2, 280, 0, 0, 2, 30, 1),
(3, 150, 45, 2, 4, 0, 3),
(4, 290, 0, 1, 2, 28, 0),
(5, 160, 50, 1, 5, 0, 2),
(6, 400, 0, 2, 3, 65, 0),
(7, 180, 60, 3, 6, 0, 1),
(8, 170, 55, 1, 4, 0, 2),
(9, 180, 0, 0, 1, 18, 2),
(10, 140, 40, 2, 3, 0, 4),
(11, 350, 0, 1, 4, 55, 0),
(12, 175, 65, 2, 5, 0, 1),
(13, 300, 0, 0, 2, 40, 1),
(14, 120, 0, 0, 0, 12, 3),
(15, 145, 35, 1, 2, 0, 5);

-- weekly_leaderboard_archive (last week's data for report demo)
INSERT INTO weekly_leaderboard_archive (user_id, week_start, week_end, weekly_xp, weekly_sessions, leaderboard_position, best_snake_score, best_breakout_score, avg_session_seconds) VALUES
(4, '2026-04-27', '2026-05-03', 28, 9, 1, 60, 5200, 385.0),
(6, '2026-04-27', '2026-05-03', 22, 8, 2, 50, 4600, 370.0),
(2, '2026-04-27', '2026-05-03', 18, 6, 3, 38, 3000, 280.0),
(3, '2026-04-27', '2026-05-03', 12, 4, 4, 25, 1900, 240.0),
(5, '2026-04-27', '2026-05-03', 6, 2, 5, 15, 1000, 180.0),
(7, '2026-04-27', '2026-05-03', 4, 2, 6, 10, 700, 160.0);