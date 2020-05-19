CREATE TABLE public."user"
(
    id integer NOT NULL DEFAULT nextval('user_id_seq'::regclass),
    username character varying(255) COLLATE pg_catalog."default" NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    password_hash character varying(255) COLLATE pg_catalog."default" NOT NULL,
    is_active boolean DEFAULT true,
    dob date NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT user_email_key UNIQUE (email),
    CONSTRAINT user_username_key UNIQUE (username)
);

create table public.debt_types
(
	id serial primary key,
	type varchar(255) not null unique
);

create table public.debt
(
	id serial primary key,
	name varchar(255) not null,
	user_id Integer references public.user(id),
	type varchar(255) references public.debt_types(type),
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
	max_loc numeric(13,2)
);

create table public.debt_hist
(
	id serial primary key,
	name varchar(255) not null,
	user_id Integer references public.user(id),
	type varchar(255) references public.debt_types(type),
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



