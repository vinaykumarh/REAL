alter table 
  AUTHORS modify AUTHOR_ID default SEQ_AUTHORS.NEXTVAL;
alter table 
  EVENTS modify EVENT_ID default  SEQ_EVENTS.NEXTVAL;
alter table 
  SPONSORS modify SPONSOR_ID default SEQ_SPONSORS.NEXTVAL;
alter table 
  ROOMS modify ROOM_ID default SEQ_ROOMS.NEXTVAL;
alter table 
  CUS_EXH modify REGISTRATION_ID default SEQ_CUS_EXH.NEXTVAL;
alter table 
  AUTH_SEM modify INVITATION_ID default SEQ_AUTH_SEM.NEXTVAL;
alter table 
  BOOK_INV modify BOOK_INV_ID default SEQ_BOOK_INV.NEXTVAL;
alter table 
  BOOKS modify BOOK_ID default SEQ_BOOKS.NEXTVAL;
alter table 
  RESERVATION modify RESERVATION_ID default SEQ_RESERVATION.NEXTVAL;
alter table 
  RENTAL_SERV modify RENTAL_ID default SEQ_RENTAL_SERV.NEXTVAL;
alter table 
  TOPICS modify TOPIC_ID default SEQ_TOPICS.NEXTVAL;
alter table 
  INVOICE modify INVOICE_ID default SEQ_INVOICE.NEXTVAL;
alter table 
  PAYMENTS modify PAYMENT_ID default SEQ_PAYMENTS.NEXTVAL;
alter table 
  CUSTOMER modify CUS_ID default SEQ_CUSTOMER.NEXTVAL;
alter table 
  EMPLOYEE modify EMP_ID default SEQ_EMPLOYEE.NEXTVAL;
ALTER TABLE 
rental_serv MODIFY RENTAL_STATUS DEFAULT 'Borrowed';
ALTER TABLE
rental_serv MODIFY BORROW_DATE DEFAULT sysdate;
ALTER TABLE 
rental_serv MODIFY EXP_RET_DATE DEFAULT sysdate+30;


CREATE OR REPLACE FORCE EDITIONABLE VIEW "AUTHAOUR_VIEW" ("AUTHOR_ID", "AUTHOR_NAME", "AUTHOr_PH", "EMAIL", "AUTHOR_ADDR") AS 
select author_id,a_fname||' '||a_lname,a_ph_no,a_email, a_street||' '||a_city||' '||a_state||' '||a_zipcode from authors;

alter table employee add  E_OTP number(6);

alter table customer add  C_OTP number(6);