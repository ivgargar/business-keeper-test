CREATE TABLE EXCHANGE_RATE(
	ID INTEGER NOT NULL,
	DATE DATE NOT NULL,
	BASE_CURRENCY VARCHAR(3) NOT NULL,
	TARGET_CURRENCY VARCHAR(3) NOT NULL,
	RATE DOUBLE NOT NULL,
	PRIMARY KEY(DATE, BASE_CURRENCY, TARGET_CURRENCY)
);