use gamehub_db;
delimiter $$
create trigger validate_sesh
before insert on game_sessions
for each row
begin
if new.score < 0 then signal sqlstate '45000' set message_text = 'Score cannot be less then 0';
	end if;
if new.duration_seconds <= 0 then signal sqlstate '45000' set message_text = 'Duration cannot be less or equal to 0';
	end if;
end $$

create trigger log_lvl_up
after update on user_stats
for each row
begin
if calculate_level(new.lifetime_xp) > calculate_level(old.lifetime_xp) then 
insert into xp_log (user_id, old_lvl, new_lvl, xp_at_time)
values(new.user_id,calculate_level(old.lifetime_xp),calculate_level(new.lifetime_xp), new.lifetime_xp);
end if;
end $$

delimiter ;