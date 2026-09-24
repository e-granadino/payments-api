WHENEVER SQLERROR EXIT SQL.SQLCODE;

ALTER SESSION SET CONTAINER = FREEPDB1;

-- =========================================================
-- Users
-- =========================================================

CREATE USER ORG_BDO IDENTIFIED BY OrgBdoPass123;
GRANT CONNECT, RESOURCE TO ORG_BDO;

ALTER USER ORG_BDO QUOTA UNLIMITED ON USERS;

CREATE USER APP_USER IDENTIFIED BY AppPass123;
GRANT CONNECT TO APP_USER;

-- ALTER SESSION SET CURRENT_SCHEMA = ORG_BDO;

-- =========================================================
-- CUSTOMERS
-- =========================================================

CREATE TABLE ORG_BDO.CUSTOMERS (
    ID VARCHAR2(50 CHAR) NOT NULL,
    NAME VARCHAR2(100 CHAR) NOT NULL,
    COUNTRY VARCHAR2(100 CHAR) NOT NULL
);

-- =========================================================
-- PAYMENTS
-- =========================================================

CREATE TABLE ORG_BDO.PAYMENTS (
    ID RAW(16) NOT NULL,
    CUSTOMER_ID VARCHAR2(50 CHAR) NOT NULL,
    AMOUNT NUMBER(19,4) NOT NULL,
    CURRENCY VARCHAR2(3 CHAR) NOT NULL,
    STATUS VARCHAR2(20 CHAR) NOT NULL,
    CREATED_AT TIMESTAMP WITH TIME ZONE NOT NULL
);

-- =========================================================
-- PAYMENT PROCESSING LOG
-- =========================================================

CREATE TABLE ORG_BDO.PAYMENT_PROCESSING_LOG (
    ID NUMBER GENERATED ALWAYS AS IDENTITY,
    PAYMENT_ID RAW(16),
    ERROR_CODE NUMBER,
    ERROR_MESSAGE VARCHAR2(4000 CHAR) NOT NULL,
    ERROR_TIMESTAMP TIMESTAMP WITH TIME ZONE
        DEFAULT SYSTIMESTAMP NOT NULL
);

-- =========================================================
-- Primary keys
-- =========================================================

ALTER TABLE ORG_BDO.CUSTOMERS
    ADD CONSTRAINT PK_CUSTOMERS
    PRIMARY KEY (ID);

ALTER TABLE ORG_BDO.PAYMENTS
    ADD CONSTRAINT PK_PAYMENTS
    PRIMARY KEY (ID);

ALTER TABLE ORG_BDO.PAYMENT_PROCESSING_LOG
    ADD CONSTRAINT PK_PAYMENT_PROCESSING_LOG
    PRIMARY KEY (ID);

-- =========================================================
-- Foreign key
-- =========================================================

ALTER TABLE ORG_BDO.PAYMENTS
    ADD CONSTRAINT FK_PAYMENTS_CUSTOMER
    FOREIGN KEY (CUSTOMER_ID)
    REFERENCES ORG_BDO.CUSTOMERS (ID);

-- =========================================================
-- Index required by the SQL challenge
-- =========================================================

CREATE INDEX ORG_BDO.IDX_PAYMENTS_CREATED_STATUS_CUSTOMER
    ON ORG_BDO.PAYMENTS (CREATED_AT, STATUS, CUSTOMER_ID);

-- =========================================================
-- APP_USER permissions
-- =========================================================

GRANT SELECT, INSERT, UPDATE, DELETE
    ON ORG_BDO.CUSTOMERS
    TO APP_USER;

GRANT SELECT, INSERT, UPDATE, DELETE
    ON ORG_BDO.PAYMENTS
    TO APP_USER;

GRANT SELECT, INSERT, UPDATE, DELETE
    ON ORG_BDO.PAYMENT_PROCESSING_LOG
    TO APP_USER;
