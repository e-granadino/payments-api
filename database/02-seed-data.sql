-- ============================================
-- Seed data for Payments API
-- ============================================

ALTER SESSION SET CONTAINER = FREEPDB1;

ALTER SESSION SET CURRENT_SCHEMA = ORG_BDO;

-- ============================================
-- Customers
-- ============================================

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-001', 'Acme Corporation', 'USA');

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-002', 'Tech Solutions', 'El Salvador');

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-003', 'Global Systems', 'Mexico');

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-004', 'Digital Works', 'Guatemala');

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-005', 'Innovation Labs', 'Costa Rica');

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-006', 'Enterprise Services', 'Colombia');

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-007', 'Cloud Partners', 'Panama');

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-008', 'Software Factory', 'Chile');

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-009', 'Data Solutions', 'Peru');

INSERT INTO ORG_BDO.CUSTOMERS (id, name, country)
VALUES ('CUS-010', 'Business Systems', 'Spain');

-- ============================================
-- Payments - recent / processed
-- ============================================

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-001',
    150.75,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '2' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-001',
    250.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '5' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-002',
    500.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '3' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-002',
    200.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '10' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-002',
    100.00,
    'USD',
    'PENDING',
    SYSTIMESTAMP - INTERVAL '1' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-003',
    1000.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '7' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-003',
    750.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '12' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-004',
    350.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '8' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-005',
    1250.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '4' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-005',
    400.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '15' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-006',
    800.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '6' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-006',
    350.00,
    'USD',
    'PENDING',
    SYSTIMESTAMP - INTERVAL '2' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-007',
    950.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '9' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-008',
    600.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '11' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-009',
    450.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '13' DAY
);

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-010',
    300.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '20' DAY
);

-- ============================================
-- Older payment
-- Should NOT be included in the last-30-day query
-- ============================================

INSERT INTO ORG_BDO.PAYMENTS (
    id,
    customer_id,
    amount,
    currency,
    status,
    created_at
)
VALUES (
    SYS_GUID(),
    'CUS-001',
    5000.00,
    'USD',
    'PROCESSED',
    SYSTIMESTAMP - INTERVAL '45' DAY
);

COMMIT;
