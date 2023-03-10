create or replace procedure CUST_BOOK_RENTAL_DETAILS(customer_id in NUMBER,getCusBookRen out sys_refcursor)
as
begin
  open getCusBookRen for select rental_serv.RENTAL_ID,rental_serv.RENTAL_STATUS,rental_serv.BORROW_DATE,rental_serv.ACT_RET_DATE,book_inv.BOOK_INV_ID,rental_serv.CUS_ID,rental_serv.EXP_RET_DATE,books.book_id,books.b_name,invoice.invoice_id,invoice.amount,(case when rental_serv.act_ret_date is null then 'N' else 'Y' end) IS_RETURNED from rental_serv left join invoice on invoice.rental_id=rental_serv.rental_id join book_inv on book_inv.book_inv_id=rental_serv.book_inv_id join books on books.book_id=book_inv.book_id where cus_id=customer_id and rental_serv.act_ret_date is null order by rental_serv.BORROW_DATE;
end;
/
create or replace procedure get_Customer(getCus out sys_refcursor)
as
begin

  open getCus for select customer.Cus_ID, C_FNAME,C_LNAME,C_PH_NO,C_EMAIL,tb_cmb.has_room_booked,tb_cmb.has_book_rented from customer join
(select book_rented.cus_id as cus_id,book_rented.has_book_rented as has_book_rented,Room_reserved.has_room_booked as has_room_booked from(select cus_id,(case when sum(no_of_books)=0 then 'N' else'Y' end) as has_book_rented 
from
(select customer.cus_id as cus_id,(case when RENTAL_ID is null or act_ret_date is not null then 0 else 1 end) as no_of_books 
from rental_serv right join customer on customer.cus_id=rental_serv.cus_id)  tmpcus
group by cus_id) book_rented
join
(select cus_id,(case when sum(no_of_rooms)=0 then 'N' else'Y' end) as has_room_booked 
from
(select customer.cus_id as cus_id,(case when reservation_id is null or res_date<sysdate then 0 else 1 end) as no_of_rooms from reservation right join customer on customer.cus_id=reservation.cus_id)  tmpcus
group by cus_id) Room_reserved on book_rented.cus_id=room_reserved.cus_id) tb_cmb on customer.cus_id=tb_cmb.cus_id;

end;
/
create or replace procedure get_free_rooms(date_req in varchar2, no_of_people in number,getRoom out sys_refcursor)
as
begin
open getRoom for select room_id as room_id,LISTAGG(unique(slot), ',') as slot,max(max_cap) MAX_CAP,max(is_inservice)IS_INSERVICE 
from (select room_id,slot,max_cap,rooms.is_inservice from rooms join slots on 1=1 where rooms.is_inservice=1 and   rooms.max_cap>=no_of_people and (room_id||' '||slot) 
not in (select room_id||' '||slot from reservation where Trunc(res_date) = Trunc(To_date(date_req, 'YYYY-MM-DD')))
) group by room_id;
end;
/
create or replace procedure GET_RESERVED_ROOMS(customerId in number, getResRoom out SYS_REFCURSOR)
as
begin
open getResRoom for select * from reservation join rooms on reservation.room_id=rooms.room_id where cus_id=customerId;
end;
/
create or replace procedure reserve_room(indate in varchar2,inslot in varchar2,inroom in varchar2,incus in varchar2, outres_id out Number)
as
begin
insert into reservation(res_date,slot,cus_id,room_id) values(to_date(indate,'YYYY-MM-DD'),inslot,incus,inroom);
select reservation_id into outres_id from reservation where cus_id=incus and trunc(res_date)=trunc(to_date(indate,'YYYY-MM-DD')) and slot=inslot and room_id=inroom;
end;
/

create or replace procedure get_events(reqestorType in varchar2,getevents out SYS_REFCURSOR)
as
reqestorTypetmp varchar2(32);
cusVar varchar2(32);
empVar varchar2(32);
begin
reqestorTypetmp:=reqestorType;
cusVar:='CUS';
empVar:='EMP';

if reqestorTypetmp=empVar then
open getevents for select eve.event_id,eve.event_type,eve.topic_id,
TO_CHAR (TO_DATE (eve.event_start, 'DD-MON-YY'), 'MM/DD/YYYY') event_start,TO_CHAR (TO_DATE (eve.event_stop, 'DD-MON-YY'), 'MM/DD/YYYY')event_stop,eve.event_name,topi.topic_name,exh.expense,semspon.amount,semspon.sponsor_id,spon.s_fname,spon.s_lname,spon.s_type
from events eve join topics topi on topi.topic_id=eve.topic_id 
left join exhibition exh on eve.event_id=exh.event_id 
left join sem_spon semspon on eve.event_id=semspon.event_id left join sponsors spon on semspon.sponsor_id=spon.sponsor_id order by eve.Event_start;

ELSIf reqestorTypetmp=cusVar then

open getevents for select eve.event_id,eve.event_type,eve.topic_id,
TO_CHAR (TO_DATE (eve.event_start, 'DD-MON-YY'), 'MM/DD/YYYY')event_start,TO_CHAR (TO_DATE (eve.event_stop, 'DD-MON-YY'), 'MM/DD/YYYY')event_stop,eve.event_name,topi.topic_name,exh.expense
from events eve join topics topi on topi.topic_id=eve.topic_id 
left join exhibition exh on eve.event_id=exh.event_id where eve.event_type='E' and trunc(eve.event_start)>=trunc(sysdate) order by eve.Event_start;

end if;
end;
/

create or replace procedure get_cus_exhibition(customer_id in varchar2, getCusExh out SYS_REFCURSOR)
as
Begin
open getCusExh for select eve.event_id,eve.event_type,eve.topic_id,
TO_CHAR (TO_DATE (eve.event_start, 'DD-MON-YY'), 'MM/DD/YYYY') event_start,TO_CHAR (TO_DATE (eve.event_stop, 'DD-MON-YY') , 'MM/DD/YYYY') event_stop,eve.event_name,topi.topic_name,exh.expense
from events eve join topics topi on topi.topic_id=eve.topic_id 
left join exhibition exh on eve.event_id=exh.event_id  join cus_exh cuseh on exh.event_id=cuseh.event_id where eve.event_type='E' and cuseh.cus_id=customer_id order by eve.Event_start;
END;
/

create or replace procedure reserve_Exhibition(customerid in varchar2,eventid in varchar2,reservationid out Number)
as
begin
insert into cus_exh(cus_id,event_id) values(customerid,eventid);
select REGISTRATION_ID into reservationid from cus_exh where cus_id=customerid and event_id=eventid;
end;
/

create or replace procedure cancel_reserve_Exhibition(customerid in varchar2,eventid in varchar2,reservationid out Number)
as
begin
select REGISTRATION_ID into reservationid from cus_exh where cus_id=customerid and event_id=eventid;
delete from cus_exh  where cus_id=customerid and event_id=eventid;
end;
/

create or replace procedure reg_user(fname in varchar2,lname in varchar2,
phone in number,email in varchar2,street in varchar2,state in varchar2,
zipcode in varchar2,idtype in varchar2,idnumber in number,password in varchar2,getdetails out sys_refcursor)
as
begin
insert into customer (
C_FNAME ,
C_LNAME ,
C_PH_NO ,
C_EMAIL ,
C_STREET ,
C_CITY ,
C_STATE ,
C_ZIPCODE ,
C_IDENTIY_TYPE ,
C_IDENTITY_NO,
PASSWORD)
VALUES
(fname,lname,phone,email,street,state,state,zipcode,idtype,idnumber,password);

open getdetails for select *  from customer where C_EMAIL=email;

end;
/

create or replace procedure verify_email(email in varchar2, getotpdetails out sys_refcursor)
as
count1 number(1);
count2 number(1);
user_otp number(6);
BEGIN
count1:=0;
count2:=0;

select count(*) into count1 from customer where c_email=email;
if count1=1
    then
    select trunc(dbms_random.value(100000,999999),0) into user_otp from dual;

    DBMS_OUTPUT.PUT_LINE('user_otp = ' || user_otp);

    UPDATE customer
    SET C_OTP=user_otp
    WHERE C_EMAIL=email;
    open getotpdetails for select CUS_ID,C_EMAIL,C_OTP,'CUS' as user_type  from customer where C_EMAIL=email;

end if;

select count(*) into count2 from employee where e_email=email;
if count2=1
    then
    select trunc(dbms_random.value(100000,999999),0) into user_otp from dual;
    DBMS_OUTPUT.PUT_LINE('user_otp = ' || user_otp);

    UPDATE EMPLOYEE
    SET E_OTP=user_otp
    WHERE E_EMAIL=email;
    open getotpdetails for select EMP_ID,E_EMAIL,E_OTP,'EMP' as user_type  from EMPLOYEE where E_EMAIL=email;

end if;

end;
/


create or replace PROCEDURE update_pwd(inputotp in varchar2,
newpwd in varchar2, useremail in varchar2, getpwdresetrdetails out sys_refcursor)
as
count1 number(1);
count2 number(1);
sys_otp varchar2(32);
BEGIN

count1:=0;
count2:=0;

select count(*) into count1 from customer where C_EMAIL=useremail;
if count1=1
    then
    select c_otp into sys_otp from customer where C_EMAIL=useremail;
    DBMS_OUTPUT.PUT_LINE('sys_otp = ' || sys_otp);
    if sys_otp=inputotp
        then
        UPDATE customer
        SET PASSWORD=newpwd, C_Otp =null
        WHERE C_EMAIL=useremail;
        open getpwdresetrdetails for select CUS_ID,C_EMAIL  from customer where C_EMAIL=useremail;
    end if;

end if;

select count(*) into count2 from employee where e_email=useremail;
if count2=1
    then
    select E_OTP into sys_otp from employee where E_EMAIL=useremail;
    DBMS_OUTPUT.PUT_LINE('sys_otp = ' || sys_otp);
    if sys_otp=inputotp
        then
        UPDATE EMPLOYEE
        SET E_PASSWORD=newpwd, e_otp= null
        WHERE E_EMAIL=useremail;
        open getpwdresetrdetails for select EMP_ID,E_EMAIL  from EMPLOYEE where E_EMAIL=useremail;
    end if;

end if;


END;
/

create or replace PROCEDURE get_authors(eventId IN VARCHAR2,getauthors out SYS_REFCURSOR)
as
begin
open getauthors for select * from AUTHAOUR_VIEW where author_id not in (select author_id from auth_sem where event_id=eventId);
end;
/

create or replace procedure get_Sponsors(eventId in varchar2, getspon out SYS_REFCURSOR)
as
begin
open getspon for select sponsors.sponsor_id,trim(s_fname||' '||sponsors.s_lname) as Sponsor_name,(case when lower(s_type)='o' then 'Organization' else 'Person' end) as Sponsor_Type,event_id,amount
from sem_spon join sponsors on sem_spon.sponsor_id=sponsors.sponsor_id where event_id=eventId order by event_id,rank() over (partition by event_id order by amount desc);
end;
/


create or replace procedure VALIDATE_USER (user_name IN varchar2, password out VARCHAR2,userTyp out varchar2)
as
pass varchar2(256);
count1 number(1);
userT varchar(3);
BEGIN
count1:=0;
pass:=null;
userT:='NA';

select count(*) into count1 from customer where c_email=user_name;
if count1=1
    then
    select password into pass from customer where c_email=user_name;
    userT:='CUS';
end if;

select count(*) into count1 from employee where e_email=user_name;
if count1=1
    then
    select e_password into pass from employee where e_email=user_name;
        userT:='EMP';
end if;
password:=pass;
userTyp:=userT;
END;
/

create or replace procedure SEMINAR_INVITATION(eventId in varchar2,authorId IN VARCHAR2, semInv OUT SYS_REFCURSOR)
as
BEGIN
insert into AUTH_SEM(EVENT_ID,AUTHOR_ID) values(eventId,authorId);
open semInv for select auth_sem.invitation_id,auth_sem.event_id,events.event_name,events.event_start,events.event_stop,authors.author_id,authors.a_fname||' '||authors.a_lname as auth_name,authors.a_email,authors.a_ph_no 
from events inner join auth_sem on events.event_id=auth_sem.event_id join authors on auth_sem.author_id=authors.author_id where auth_sem.event_id=eventId and auth_sem.author_id=authorId;
END;
/
create or replace procedure get_popular_books(getPopularBooks out SYS_REFCURSOR)
as
begin
open getPopularBooks for select Dense_Rank() over(partition by topic_name order by no_of_rentals desc) as Popularity,BOOK_NAME,TOPIC_ID,TOPIC_NAME,no_of_rentals from (
select book_id,max(book_aname) as BOOK_NAME,max(topic_id) as TOPIC_ID,max(topic_name) as TOPIC_NAME,count(rental_id) no_of_rentals from (
select * from  (select book_topic.book_id as BOOK_ID,book_topic.b_name as Book_ANAME,book_topic.topic_id as topic_id,book_topic.topic_name as topic_name,books_inv.book_inv_id as book_inv_id from 
(select books.book_id,books.b_name,books.topic_id,topics.topic_name from books join topics on books.topic_id=topics.topic_id)  book_topic 
join
(select book_inv.book_id,book_inv_id book_inv_id from book_inv) books_inv on book_topic.book_id=books_inv.book_id) book_details left join rental_serv  on  rental_serv.book_inv_id=book_details.book_inv_id ) group by book_id order by Book_id) order by no_of_rentals desc fetch first 5 rows only;

end;
/

create or replace procedure get_popular_bookgenre(getPopularBbookgenre out SYS_REFCURSOR)
as
begin
open getPopularBbookgenre for select Dense_Rank() over( order by sum(no_of_rental) desc) as Popularity,TOPIC_ID,Max(TOPIC_NAME) Genre,sum(no_of_rental) as No_of_rentals from (
select book_id,max(book_aname) as BOOK_NAME,max(topic_id) as TOPIC_ID,max(topic_name) as TOPIC_NAME,count(rental_id) no_of_rental from (
select * from  (select book_topic.book_id as BOOK_ID,book_topic.b_name as Book_ANAME,book_topic.topic_id as topic_id,book_topic.topic_name as topic_name,books_inv.book_inv_id as book_inv_id from 
(select books.book_id,books.b_name,books.topic_id,topics.topic_name from books join topics on books.topic_id=topics.topic_id)  book_topic 
join
(select book_inv.book_id,book_inv_id book_inv_id from book_inv) books_inv on book_topic.book_id=books_inv.book_id) book_details left join rental_serv  on  rental_serv.book_inv_id=book_details.book_inv_id ) group by book_id order by Book_id) group by topic_id fetch first 5 rows only;

end;
/