/*
 * Part 2 - PL/SQL
 *
 * Processes all payments currently in PENDING status.
 *
 * Successful payments:
 *     PENDING -> PROCESSED
 *
 * Failed payments:
 *     Remain PENDING
 *     Error is written to PAYMENT_PROCESSING_LOG
 */

CREATE OR REPLACE PROCEDURE PROCESS_PENDING_PAYMENTS
AS
BEGIN

    FOR payment_rec IN (
        SELECT ID
        FROM PAYMENTS
        WHERE STATUS = 'PENDING'
        FOR UPDATE
    )
    LOOP

        SAVEPOINT PAYMENT_PROCESSING;

        BEGIN

            UPDATE PAYMENTS
            SET STATUS = 'PROCESSED'
            WHERE ID = payment_rec.ID;

        EXCEPTION
            WHEN OTHERS THEN

                ROLLBACK TO PAYMENT_PROCESSING;

                INSERT INTO PAYMENT_PROCESSING_LOG (
                    PAYMENT_ID,
                    ERROR_MESSAGE,
                    ERROR_TIMESTAMP
                )
                VALUES (
                    payment_rec.ID,
                    SQLERRM,
                    SYSTIMESTAMP
                );

        END;

    END LOOP;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END PROCESS_PENDING_PAYMENTS;
/