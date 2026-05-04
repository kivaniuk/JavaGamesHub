use gamehub_db;
create role hub_admin;
grant all on gamehub_db.* to hub_admin;
grant select (user_id,username,birth_date,profile_picture,user_role,is_active, created_at) on gamehub_db.users to hub_admin;


create role hub_user;
grant select on gamehub_db.users to hub_user;
grant select on gamehub_db.games to hub_user;
grant select on gamehub_db.high_scores to hub_user;
grant select on gamehub_db.user_stats to hub_user;
grant select on gamehub_db.user_achievments to hub_user;
grant select on gamehub_db.achievments to hub_user;
grant select on gamehub_db.weekly_leaderboard_archive to hub_user;
grant insert,update on gamehub_db.game_sessions to hub_user;
grant insert,update on gamehub_db.session_activity to hub_user;

create user 'admin_user'@'localhost' identified by 'adminPass!';
grant hub_admin to 'admin_user'@'localhost';
set default role hub_admin to 'admin_user'@'localhost';

create user 'player_user'@'localhost' identified by 'userPass!';
grant hub_user to 'player_user'@'localhost';
set default role hub_user to 'player_user'@'localhost';

