DELIMITER $$
create function calculate_level(total_xp int)
returns int
deterministic
begin
	declare user_level int default 0;
    declare threshold int default 4;
    declare xp_left int default total_xp;
    while xp_left >= threshold do 
	set xp_left = xp_left - threshold;
    set user_level = user_level + 1;
    set threshold = floor(threshold * 1.5);
	end while;     
	return user_level;
end$$

delimiter ;