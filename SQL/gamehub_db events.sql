delimiter $$ 
create event weekly_reset
on schedule every 1 week starts '2026-05-04 00:00:00'
do begin
insert into weekly_leaderboard_archive (user_id, weekly_xp, weekly_sessions, week_start, week_end, leaderboard_position, best_snake_score, best_breakout_score, avg_session_seconds)
select us.user_id,us.weekly_xp, us.weekly_sessions,us.week_start_date, us.week_end_date,
rank() over (order by us.weekly_xp desc),
max(case when gs.game_id = 1 then gs.score else 0 end),
max(case when gs.game_id = 2 then gs.score else 0 end),
avg(gs.duration_seconds)
 
from user_stats us
left join game_sessions gs
on gs.user_id = us.user_id and gs.played_at >= us.week_start_date 
group by us.user_id,us.week_start_date,us.week_end_date,us.weekly_xp,us.weekly_sessions;

update user_stats set
	weekly_xp = 0,
    weekly_sessions = 0,
    week_start_date = curdate(),
    week_end_date = curdate() + interval 7 day;
insert into event_log (event_name,notes)
values ('weekly_reset','Weekly reset completed');
end$$
delimiter ;