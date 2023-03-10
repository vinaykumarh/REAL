create or replace TRIGGER TU_RENTAL_SERV
AFTER UPDATE OF ACT_RET_DATE
ON RENTAL_SERV
FOR EACH ROW
DECLARE
    rentalID NUMBER(10);
    actualRetDate Date;
    expRetDate Date;
    borrowDate Date;
BEGIN
    SELECT :new.ACT_RET_DATE, :old.EXP_RET_DATE, :old.BORROW_DATE, :new.RENTAL_ID into actualRetDate, expRetDate, borrowDate, rentalID from dual;
    IF(actualRetDate>expRetDate) 
    THEN
        insert into invoice(INVOICE_ID, AMOUNT, RENTAL_ID) values (SEQ_INVOICE.nextvaL, ((expRetDate - borrowDate) * 0.2)+(actualRetDate - expRetDate) * 0.4, rentalID);
    ELSE
        insert into invoice(INVOICE_ID, AMOUNT, RENTAL_ID) values (SEQ_INVOICE.nextvaL, (actualRetDate - borrowDate) * 0.2, rentalID);
    END IF;
END;