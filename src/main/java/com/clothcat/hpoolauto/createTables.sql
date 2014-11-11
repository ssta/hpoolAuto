/* Keeps track of account/address relationships */
CREATE TABLE IF NOT EXISTS ADDRESSES 
( 
    ROWID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    ACCOUNT VARCHAR(10) NOT NULL,
    ADDRESS VARCHAR(40) 
);

/* Stores persistent key/value pairs */
CREATE TABLE IF NOT EXISTS KEYVALUES 
( 
    ROWID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, 
    K VARCHAR(10) NOT NULL, /* key */
    V VARCHAR(50) /* value */
);

/* Stores details of receipt transactions */
CREATE TABLE IF NOT EXISTS RECEIPTS 
(
    ROWID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    FROM_ADDRESS VARCHAR(40) NOT NULL, 
    AMOUNT INTEGER, /* amounts are stored in uHYP to avoid rounding errors */
    FOR_POOL VARCHAR(10),
    TRANSACTION_TIME INTEGER  /* unix timestamp */
);

/* Stores a list of txids we have already processed so we don't process stuff 
 * twice */
CREATE TABLE IF NOT EXISTS TRANSACTIONS
(
    ROWID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    TXID VARCHAR(80)
);

/*
INSERT INTO KEYVALUES (K, V) values ('cur_pool', 'pool1');
INSERT INTO KEYVALUES (K, V) values ('tgt_min', '5000000');
INSERT INTO KEYVALUES (K, V) values ('tgt_max', '10000000');
INSERT INTO KEYVALUES (K, V) values ('cur_fill', '0');
*/