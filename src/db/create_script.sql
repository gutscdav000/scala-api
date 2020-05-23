CREATE TABLE public."user"
(
    id serial primary key,
    username character varying(255) NOT NULL Unique,
    email character varying(255) NOT NULL Unique,
    password_hash character varying(255) NOT NULL,
    is_active boolean DEFAULT true,
    dob date NOT NULL
);

create table public.debt_types
(
	id serial primary key,
	debt_type varchar(255) not null unique
);

create table public.debt
(
	id serial primary key,
	name varchar(255) not null,
	user_id Integer references public.user(id),
	debt_type varchar(255) references public.debt_types(debt_type),
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
	debt_type varchar(255) references public.debt_types(debt_type),
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


insert into public."user"
 values (1, 'gutscdav000', 'gutscdav000@gmail.com', 'pass', true, '1996-02-08')



