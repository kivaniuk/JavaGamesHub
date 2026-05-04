delimiter $$
create procedure complete_game_session
(
	user_id int,
    game_id int,
    score int,
    duration_seconds int,
    xp_earned int,
    key_presses int,
    mouse_clicks int,
    times_paused int, 
    boosters_collected int
    )
begin
declare new_session_id int;
insert into game_sessions(user_id,game_id,score,duration_seconds,xp_earned)
values (user_id, game_id, score, duration_seconds, xp_earned);

set new_session_id = last_insert_id();
insert into session_activity (session_id,key_presses,mouse_clicks,times_paused,boosters_collected)
values(new_session_id,key_presses,mouse_clicks,times_paused,boosters_collected,0,0);

update user_stats
set lifetime_xp = lifetime_xp + xp_earned,
	weekly_xp = weekly_xp + xp_earned,
    total_sessions = total_sessions + 1,
    weekly_sessions = weekly_sessions + 1,
    last_played = NOW()
where user_stats.user_id = user_id;

insert into high_scores(user_id, game_id, score, achieved_at)
values (user_id, game_id, score, now())
on duplicate key update
score = if(values(score) > score, values(score), score),
achieved_at = if(values(score) > score, now(), achieved_at);
end$$

create procedure generate_weekly_report
(
week_start_date date
)
begin
select users.username, wla.weekly_xp,wla.weekly_sessions,wla.leaderboard_position, wla.best_snake_score,wla.best_breakout_score,wla.avg_session_seconds
from weekly_leaderboard_archive wla
join users on wla.user_id = users.user_id
where wla.week_start = week_start_date
order by wla.leaderboard_position;
end$$


DELIMITER ;