CREATE TABLE INDO_CMS_ROLE (
ROW_ID SERIAL NOT NULL,
ROLE_ID VARCHAR(100),
ROLENAME VARCHAR(100),
ROLEDESCRIPTION VARCHAR(100)
);

CREATE TABLE INDO_CMS_MENU (
ROW_ID SERIAL NOT NULL,
MENU_ID VARCHAR(100),
MENU_PARENT_ID VARCHAR(100),
MENU_TITLE VARCHAR(100),
MENU_SUBTITLE VARCHAR(100),
MENU_BREADCRUMB VARCHAR(200),
MENU_URL_TITLE VARCHAR(200),
MENU_URL VARCHAR(200),
MENU_SEQUENCE INTEGER
);

CREATE TABLE INDO_CMS_MENU_PERMISSION (
ROW_ID SERIAL NOT NULL,
MENU_ID VARCHAR(100),
ROLE_ID VARCHAR(100),
CAN_INSERT VARCHAR(1),
CAN_UPDATE VARCHAR(1),
CAN_DELETE VARCHAR(1),
CAN_EXPORT VARCHAR(1),
CAN_IMPORT VARCHAR(1)
);

CREATE TABLE INDO_CMS_MENU_PERMISSION (
ROW_ID SERIAL NOT NULL,
MENU_ID VARCHAR(100),
ROLE_ID VARCHAR(100),
CAN_INSERT VARCHAR(1),
CAN_UPDATE VARCHAR(1),
CAN_DELETE VARCHAR(1),
CAN_EXPORT VARCHAR(1),
CAN_IMPORT VARCHAR(1)
);

CREATE TABLE INDO_CMS_BANK (
ROW_ID SERIAL NOT NULL,
BANK_CODE VARCHAR(100),
BANK_NAME VARCHAR(100),
BANK_ADDRESS TEXT
);

CREATE TABLE INDO_CMS_BANK_CUSTOMER (
ROW_ID SERIAL NOT NULL,
BANK_CODE VARCHAR(100),
CUSTOMER_CODE VARCHAR(100),
CUSTOMER_NAME VARCHAR(100),
CUSTOMER_EMAIL VARCHAR(100),
CUSTOMER_GENDER VARCHAR(1),
CUSTOMER_ADDRESS TEXT
);

CREATE TABLE INDO_CMS_BANK_TRANSACTION (
ROW_ID SERIAL NOT NULL,
BANK_CODE VARCHAR(100),
CUSTOMER_CODE VARCHAR(100),
TRANSACTION_TYPE VARCHAR(100),
TRANSACTION_VALUE INTEGER,
TRANSACTION_OPEN_DATE DATE,
TRANSACTION_MAT_DATE DATE
);

CREATE TABLE INDO_CMS_APPROVAL_HEADER (
ROW_ID SERIAL NOT NULL,
APPROVAL_STATUS VARCHAR(20),
APPROVAL_TYPE VARCHAR(10),
TEMPLATE_CODE VARCHAR(100),
APPROVAL_CREATED_BY VARCHAR(100),
APPROVAL_CREATED_DATE TIMESTAMP,
APPROVAL_DATA_BEFORE TEXT,
APPROVAL_DATA_AFTER TEXT,
APPROVAL_DATA_IMPORT TEXT,
APPROVAL_QUERY TEXT
)

CREATE TABLE INDO_CMS_USER_APPROVER (
ROW_ID SERIAL NOT NULL,
USERNAME VARCHAR(100),
ROLE_ID VARCHAR(100),
APPROVER_USERNAME VARCHAR(100),
APPROVER_ROLE VARCHAR(100),
APPROVER_SEQUENCE INTEGER
);

CREATE TABLE INDO_CMS_APPROVAL_DETAIL (
ROW_ID SERIAL NOT NULL,
APPROVAL_HEADER_ID INTEGER,
APPROVAL_STATUS VARCHAR(20),
APPROVAL_USERNAME VARCHAR(100),
APPROVAL_ROLE VARCHAR(100),
APPROVAL_DATE TIMESTAMP,
APPROVAL_MESSAGE TEXT
)

CREATE TABLE INDO_CMS_JOB_HEADER
(
ROW_ID SERIAL NOT NULL,
JOB_ID VARCHAR(100),
JOB_DESCRIPTION TEXT
)

CREATE TABLE INDO_CMS_JOB_DETAIL
(
ROW_ID SERIAL NOT NULL,
JOB_ID VARCHAR(100),
DATABASE_SERVICE VARCHAR(100),
DATABASE_CLASSNAME VARCHAR(100),
DATABASE_CONNECTION_STRING VARCHAR(100),
DATABASE_USERNAME VARCHAR(100),
DATABASE_PASSWORD VARCHAR(100)
)

CREATE TABLE INDO_CMS_JOB_HISTORY
(
ROW_ID SERIAL NOT NULL,
JOB_ID VARCHAR(100),
JOB_STATUS VARCHAR(100),
JOB_MESSAGE TEXT,
JOB_START_BY VARCHAR(100),
JOB_START_DATE TIMESTAMP,
JOB_END_DATE TIMESTAMP
)
