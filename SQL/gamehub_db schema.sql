use gamehub_db;
create table users (
	user_id INT primary key auto_increment ,
	username varchar(50) unique not null,
    birth_date date,
	pin varchar(10) not null,
	security_question varchar(255),
	security_answer varchar(255),
	profile_picture varchar(100) DEFAULT 'avatar1.jpg',
	role ENUM('admin','player') default 'player',
	is_active tinyint(1) default 1,
	created_at datetime default current_timestamp
);

create table games (
	game_id int primary key auto_increment,
	game_name varchar(50) unique not null,
	game_description varchar(255),
	is_active tinyint(1) default 1
);

create table user_stats (
	stat_id int primary key auto_increment,
    user_id int unique,
    lifetime_xp int default 0,
    weekly_xp int default 0,
    total_sessions int default 0,
    weekly_sessions int default 0,
    week_start_date date,
    week_end_date date,
    last_played datetime,
    foreign key (user_id)
    references users(user_id)
);

create table game_sessions (
session_id int primary key auto_increment,
user_id int, 
game_id int,
score int default 0,
duration_seconds int default 0,
xp_earned int default 0,
played_at datetime default current_timestamp,
foreign key (user_id)
references users(user_id),
foreign key (game_id)
references games(game_id)
);

create table high_scores (
highscore_id int primary key auto_increment,
user_id int,
game_id int,
score int default 0,
achieved_at datetime,
unique(user_id, game_id),
foreign key(user_id)
references users(user_id),
foreign key(game_id)
references games(game_id)
);

create table achievments (
achievment_id int primary key auto_increment,
achievment_name varchar(100) not null,
a_description varchar(255),
game_id int null,
a_type enum('game_specific','platform_wide'),
metric varchar(50),
threshold int,
foreign key(game_id)
references games(game_id)
);

create table user_achievments (
id int primary key auto_increment,
user_id int,
achievment_id int,
earned_at datetime default current_timestamp,
unique(user_id,achievment_id),
foreign key (user_id)
references users(user_id),
foreign key (achievment_id)
references achievments(achievment_id)
);

create table session_activity (
activity_id int primary key auto_increment,
session_id int,
key_presses int default 0,
mouse_clicks int default 0,
times_paused int default 0,
boosters_collected int default 0,
extra_metric_1 int default 0,
extra_metric_2 int default 0,
foreign key (session_id)
references game_sessions(session_id) 
);

create table weekly_leaderboard_archive (
archive_id int primary key auto_increment,
user_id int,
week_start date,
week_end date,
weekly_xp int default 0,
weekly_sessions int default 0,
leaderboard_postition int,
best_snake_score int,
best_breakout_score int,
avg_session_seconds decimal(8,2),
foreign key(user_id)
references users(user_id)
);

create table xp_log (
log_id int primary key auto_increment,
user_id int,
old_lvl int,
new_lvl int,
xp_at_time int,
logged_at datetime default current_timestamp,
foreign key(user_id)
references users(user_id)
);

create table event_log(
log_id int primary key auto_increment,
event_name varchar(100),
run_at datetime default current_timestamp,
notes varchar(255)
);