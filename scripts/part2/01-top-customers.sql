/*
 * Part 2 - SQL
 *
 * Returns the top 10 customers by total amount paid
 * during the last 30 days.
 *
 * Only PROCESSED payments are considered "paid".
 */

SELECT
    c.ID AS CUSTOMER_ID,
    c.NAME AS CUSTOMER_NAME,
    c.COUNTRY,
    SUM(p.AMOUNT) AS TOTAL_PAID,
    COUNT(p.ID) AS PAYMENT_COUNT,
    AVG(p.AMOUNT) AS AVERAGE_TICKET
FROM CUSTOMERS c
JOIN PAYMENTS p
    ON p.CUSTOMER_ID = c.ID
WHERE p.STATUS = 'PROCESSED'
  AND p.CREATED_AT >= SYSTIMESTAMP - INTERVAL '30' DAY
GROUP BY
    c.ID,
    c.NAME,
    c.COUNTRY
ORDER BY
    TOTAL_PAID DESC
FETCH FIRST 10 ROWS ONLY;