CREATE TABLE public."user"
(
    id serial primary key,
    username character varying(255) NOT NULL Unique,
    email character varying(255) NOT NULL Unique,
    password_hash character varying(255) NOT NULL,
    is_active boolean DEFAULT true,
    dob date NOT NULL
);

create table public.debt_type
(
	id serial primary key,
	debt_type varchar(255) not null unique
);


create table public.debt
(
	id serial primary key,
	name varchar(255) not null,
	user_id Integer references public.user(id),
	debt_type varchar(255) references public.debt_type(debt_type),
	lender varchar(255),
	original_balance numeric(13,2),
	balance numeric(13,2),
	rate numeric(5,4),
	interest_paid numeric(13,2),
	periods_to_payoff Integer,
	payoff_date date,
	max_interest numeric(13,2),
	min_payment_value numeric(13,2),
	min_payment_percent numeric(5,4),
	loan_term Integer,
	remaining_term Integer,
	pmi numeric(13,2),
	purchase_price numeric(13,2),
	max_periods Integer,
	escrow numeric(13,2),
	max_loc numeric(13,2),
	constraint debt_constraint unique (name, user_id)
);		 


create table public.debt_hist
(
	id serial primary key,
	name varchar(255) not null,
	user_id Integer references public.user(id),
	debt_type varchar(255) references public.debt_type(debt_type),
	lender varchar(255),
	original_balance numeric(13,2),
	balance numeric(13,2),
	rate numeric(5,4),
	interest_paid numeric(13,2),
	periods_to_payoff Integer,
	payoff_date date,
	max_interest numeric(13,2),
	min_payment_value numeric(13,2),
	min_payment_percent numeric(5,4),
	loan_term Integer,
	remaining_term Integer,
	pmi numeric(13,2),
	purchase_price numeric(13,2),
	max_periods Integer,
	escrow numeric(13,2),
	max_loc numeric(13,2),
	update_stamp timestamp
);

create table public.action
(
	id serial primary key,
	debt_id Integer references public.debt(id),
	principal numeric(13,2) not null,
	interest numeric(13,2) not null,
	pay_date date
);

insert into public."user"
 values (1, 'gutscdav000', 'gutscdav000@gmail.com', 'pass', true, '1996-02-08');

insert into public.debt_type
 values (1, 'Mortgage'),
 		(2, 'Auto Loan'),
		(3, 'Student Loan'),
		(4, 'Credit Card'),
		(5, 'Line of Credit');
		
insert into public.debt
 values (1, 'david''s mortgage', 1, 'Mortgage', 'Fannie Mae', 200000.0, 200000.0, 0.05, 0.0,
		360, '2050-05-01', 186343.11, 1074.0, -1.0, 360, 360,-1.0, 250000.0, 360, -1.0, -1.0),
		(2, 'david''s credit card', 1, 'Credit Card', 'Chase', 0.0, 0.0, 0.18, 0.0, 0, '0001-01-01',
		 0.0, -1.0, 0.02, -1, -1, -1, -1, -1, -1, -1);

insert into public.action
 values (1, 1, 240.67, 833.33, null),
 		(2, 1, 241.67, 832.33, null),
		(3, 1, 242.68, 831.32, null),
		(4, 1, 243.69, 830.31, null),
		(5, 1, 244.70, 829.30, null);
		

drop table public.action;
drop table public.debt;
drop table public.debt_hist;
drop table public.debt_type;
drop table public.user;
