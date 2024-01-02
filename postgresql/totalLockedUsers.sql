CREATE OR REPLACE FUNCTION totalLockedUsers()
RETURNS INTEGER AS $$
DECLARE
    contador INTEGER;
BEGIN
    EXECUTE 'SELECT count(*) FROM usuarios u WHERE u.account_non_locked = false' INTO contador;
    RETURN contador;
END;
$$ LANGUAGE plpgsql;
